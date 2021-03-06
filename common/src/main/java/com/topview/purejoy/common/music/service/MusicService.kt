package com.topview.purejoy.common.music.service

import android.content.IntentFilter
import android.widget.Toast
import com.topview.purejoy.common.IPCItemChangeListener
import com.topview.purejoy.common.IPCListenerController
import com.topview.purejoy.common.IPCModeChangeListener
import com.topview.purejoy.common.IPCPlayerController
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.player.abs.cache.CacheLoader
import com.topview.purejoy.common.music.player.abs.cache.CacheStrategy
import com.topview.purejoy.common.music.player.abs.transformation.IWrapperTransformation
import com.topview.purejoy.common.music.player.abs.transformation.ItemTransformation
import com.topview.purejoy.common.music.player.impl.Operator
import com.topview.purejoy.common.music.player.impl.OperatorCallback
import com.topview.purejoy.common.music.player.impl.ipc.BinderPool
import com.topview.purejoy.common.music.player.impl.ipc.IPCDataControllerImpl
import com.topview.purejoy.common.music.player.impl.ipc.IPCListenerControllerImpl
import com.topview.purejoy.common.music.player.service.MediaService
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.common.music.player.util.DataSource
import com.topview.purejoy.common.music.player.util.cast
import com.topview.purejoy.common.music.player.util.castAs
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.notification.MusicNotification
import com.topview.purejoy.common.music.service.notification.MusicNotificationReceiver
import com.topview.purejoy.common.music.service.recover.MusicPhoto
import com.topview.purejoy.common.music.service.transformation.MusicItemTransformation
import com.topview.purejoy.common.music.service.transformation.WrapperTransformation
import com.topview.purejoy.common.music.service.url.cache.MusicCacheStrategy
import com.topview.purejoy.common.music.service.url.viewmodel.MusicURLViewModel
import com.topview.purejoy.common.music.service.url.viewmodel.MusicURLViewModelImpl
import com.topview.purejoy.common.music.util.SP
import okhttp3.*
import java.io.File
import java.io.IOException

class MusicService : MediaService<MusicItem>() {
    private val musicNotification: MusicNotification by lazy {
        MusicNotification(applicationContext, NOTIFICATION_ID)
    }
    private val receiver: MusicNotificationReceiver by lazy {
        val controller = pool.queryBinder(BinderPool.PLAYER_CONTROL_BINDER).castAs<IPCPlayerController>()!!
        MusicNotificationReceiver(controller = controller,
            listener = object : MusicNotificationReceiver.ClearListener {
            override fun onClear() {
                if (musicNotification.showForeground) {
                    stopForeground(true)
                    musicNotification.showForeground = false
                }
            }
        })
    }
    private val TAG = "MusicService"

    private val viewModel: MusicURLViewModel by lazy {
        MusicURLViewModelImpl()
    }




    private val client: OkHttpClient by lazy {
        OkHttpClient()
    }

    override fun initMediaModeSetting() {
        val position: Int = SP.sp.getInt(POSITION_KEY, MediaModeSetting.INIT_POSITION)
        val max = SP.sp.getInt(MAX_KEY, 0)
        MediaModeSetting.getInstance().init(max, position)
    }

    override fun loadInitMode(): Int {
        val mode: Int = SP.sp.getInt(MODE_KEY, MediaModeSetting.ORDER)
        return mode
    }



    private val dataController: IPCDataControllerImpl<MusicItem> by lazy {
        pool.queryBinder(BinderPool.DATA_CONTROL_BINDER)
            .castAs<IPCDataControllerImpl<MusicItem>>()!!
    }


    private val photo: MusicPhoto by lazy {
        val listenerController: IPCListenerControllerImpl = IPCListenerController.Stub.asInterface(pool.queryBinder(
            BinderPool.LISTENER_CONTROL_BINDER)).castAs()!!
        MusicPhoto(dataController, mainHandler, listenerController)
    }


    override fun onCreate() {
        super.onCreate()
        dataController.source.changeListeners.add(object : DataSource.DataSetChangeListener<Wrapper> {
            override fun onChange(changes: MutableList<Wrapper>) {
                photo.updatePhoto()
                if (changes.isEmpty()) {
                    // ????????????
                    if (musicNotification.showForeground) {
                        stopForeground(true)
                        musicNotification.showForeground = false
                    }
                }
                SP.sp.edit().apply {
                    putInt(MAX_KEY, dataController.position.max)
                    putInt(POSITION_KEY, dataController.position.current())
                    apply()
                }
            }
        })

        photo.readLocal()

        val listenerController: IPCListenerControllerImpl = IPCListenerController.Stub.asInterface(pool.queryBinder(
            BinderPool.LISTENER_CONTROL_BINDER)).castAs()!!
        listenerController.addModeChangeListener(object : IPCModeChangeListener.Stub() {
            override fun onModeChange(mode: Int) {
                SP.sp.edit().apply {
                    putInt(MODE_KEY, mode)
                    apply()
                }
            }
        })
        listenerController.addItemChangeListener(object : IPCItemChangeListener.Stub() {
            override fun onItemChange(wrapper: Wrapper?) {
                SP.sp.edit().apply {
                    putInt(POSITION_KEY, dataController.position.current())
                    apply()
                }
            }
        })

        val filter = IntentFilter()
        filter.addAction(MusicNotificationReceiver.CLEAR_ACTION)
        filter.addAction(MusicNotificationReceiver.STATE_ACTION)
        filter.addAction(MusicNotificationReceiver.NEXT_ACTION)
        filter.addAction(MusicNotificationReceiver.PREVIOUS_ACTION)
        registerReceiver(receiver, filter)
    }



    override fun onLoadItem(itemIndex: Int, item: Item, callback: Loader.Callback<Item>) {
        viewModel.requestMusicURL(item.cast()!!, itemIndex, callback)
    }

    override fun showForeground(value: MusicItem, state: Boolean) {
        musicNotification.updateNotification(value, state)
        if (musicNotification.showForeground) {
            musicNotification.send()
        } else {
            musicNotification.showForeground = true
            startForeground(NOTIFICATION_ID, musicNotification.notification)
        }
    }

    override fun cacheStrategy(): CacheStrategy {
        val file = File(applicationContext.cacheDir, MUSIC_CACHE_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        val maxSize = Runtime.getRuntime().freeMemory() / 8
        val cs = MusicCacheStrategy(cacheDirectory = file, maxMemorySize = maxSize.toInt(),
            downloadDir = CommonApplication.musicPath)
        cs.loader = object : CacheLoader {
            override fun load(url: String) {
                val request = Request.Builder().url(url).build()
                val call = client.newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.body?.byteStream()?.let { stream ->
                            cs.putInDisk(url, stream)
                        }
                    }
                })
            }
        }
        return cs
    }

    override fun operatorCallback(): OperatorCallback {
        return object : OperatorCallback {
            override fun callback(operator: Operator, code: Int, success: Any?, fail: Any?) {
                when(operator) {
                    Operator.ADD -> {
                        if (code == OperatorCallback.SUCCESS_CODE) {
                            itemTransformation.transform(success!!.castAs()!!)?.let {
                                Toast.makeText(applicationContext,
                                    "${it.name}??????????????????????????????", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext,
                                "??????????????????", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {

                    }
                }
            }

        }
    }



    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun transformation(): ItemTransformation<MusicItem> {
        return MusicItemTransformation
    }

    companion object {
        const val MUSIC_CACHE_DIR = "musicCache"
        const val NOTIFICATION_ID = 100
        const val POSITION_KEY = "position"
        const val MODE_KEY = "mode"
        const val MAX_KEY = "max"
    }

    override fun wrapperTransformation(): IWrapperTransformation<MusicItem> {
        return WrapperTransformation
    }
}