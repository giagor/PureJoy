package com.topview.purejoy.musiclibrary.service

import android.content.IntentFilter
import android.widget.Toast
import com.topview.purejoy.musiclibrary.IPCListenerController
import com.topview.purejoy.musiclibrary.IPCModeController
import com.topview.purejoy.musiclibrary.IPCPlayerController
import com.topview.purejoy.musiclibrary.common.transformation.MusicItemTransformation
import com.topview.purejoy.musiclibrary.common.transformation.WrapperTransformation
import com.topview.purejoy.musiclibrary.common.util.ExecutorInstance
import com.topview.purejoy.musiclibrary.common.util.SP
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.*
import com.topview.purejoy.musiclibrary.player.abs.Loader
import com.topview.purejoy.musiclibrary.player.abs.cache.CacheStrategy
import com.topview.purejoy.musiclibrary.player.abs.transformation.IWrapperTransformation
import com.topview.purejoy.musiclibrary.player.abs.transformation.ItemTransformation
import com.topview.purejoy.musiclibrary.player.impl.cache.CacheStrategyImpl
import com.topview.purejoy.musiclibrary.player.impl.ipc.BinderPool
import com.topview.purejoy.musiclibrary.player.impl.ipc.IPCDataControllerImpl
import com.topview.purejoy.musiclibrary.player.impl.ipc.IPCListenerControllerImpl
import com.topview.purejoy.musiclibrary.player.impl.ipc.IPCModeControllerImpl
import com.topview.purejoy.musiclibrary.player.service.MediaService
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting
import com.topview.purejoy.musiclibrary.player.util.DataSource
import com.topview.purejoy.musiclibrary.player.util.cast
import com.topview.purejoy.musiclibrary.player.util.castAs
import com.topview.purejoy.musiclibrary.service.cache.CacheCallback
import com.topview.purejoy.musiclibrary.service.notification.MusicNotification
import com.topview.purejoy.musiclibrary.service.notification.MusicNotificationReceiver
import com.topview.purejoy.musiclibrary.service.recover.db.RecoverDatabase
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverALData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverARData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverMusicData
import com.topview.purejoy.musiclibrary.service.recover.db.initDB
import com.topview.purejoy.musiclibrary.service.url.viewmodel.MusicURLViewModel
import com.topview.purejoy.musiclibrary.service.url.viewmodel.MusicURLViewModelImpl
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

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

    private val rmPhoto = CopyOnWriteArrayList<RecoverMusicData>()
    private val rlPhoto = CopyOnWriteArrayList<RecoverALData>()
    private val rrPhoto = CopyOnWriteArrayList<RecoverARData>()

    private val recoverDB = "recover"

    private val db: RecoverDatabase by lazy {
        initDB(RecoverDatabase::class.java, recoverDB)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient()
    }

    override fun initMediaModeSetting() {
        val position: Int = SP.sp.getInt(POSITION_KEY, MediaModeSetting.INIT_POSITION)
        val max = SP.sp.getInt(MAX_KEY, 0)
        SP.sp.edit().apply {
            putInt(POSITION_KEY, MediaModeSetting.INIT_POSITION)
            putInt(MODE_KEY, MediaModeSetting.ORDER)
            putInt(MAX_KEY, 0)
            apply()
        }
        MediaModeSetting.getInstance().init(max, position)
    }

    override fun loadInitMode(): Int {
        val mode: Int = SP.sp.getInt(MODE_KEY, MediaModeSetting.ORDER)
        return mode
    }


    private val storeDuration = 60000L

    private val dataController: IPCDataControllerImpl<MusicItem> by lazy {
        pool.queryBinder(BinderPool.DATA_CONTROL_BINDER)
            .castAs<IPCDataControllerImpl<MusicItem>>()!!
    }

    private val listenerController: IPCListenerControllerImpl by lazy {
        IPCListenerController.Stub.asInterface(pool.queryBinder(
            BinderPool.LISTENER_CONTROL_BINDER)).castAs()!!
    }


    override fun onCreate() {
        super.onCreate()
        dataController.source.changeListeners.add(object : DataSource.DataSetChangeListener<Wrapper> {
            override fun onChange(changes: MutableList<Wrapper>) {
                if (changes.isEmpty()) {
                    // 清除通知
                    if (musicNotification.showForeground) {
                        stopForeground(true)
                        musicNotification.showForeground = false
                    }
                }
            }
        })

        readLocal()

        val storeTask = object : Runnable {
            override fun run() {
                if (rmPhoto.size != dataController.mediaSource.size) {
                    storePlayerState()
                } else {
                    var changed = false

                    for(item in dataController.mediaSource) {
                        if (!rmPhoto.contains(item.toRecoverMusic())) {
                            changed = true
                            break
                        }
                    }

                    if (changed) {
                        storePlayerState()
                    }
                }
                mainHandler.postDelayed(this, storeDuration)
            }

        }

        mainHandler.postDelayed(storeTask, storeDuration)

        val filter = IntentFilter()
        filter.addAction(MusicNotificationReceiver.CLEAR_ACTION)
        filter.addAction(MusicNotificationReceiver.STATE_ACTION)
        filter.addAction(MusicNotificationReceiver.NEXT_ACTION)
        filter.addAction(MusicNotificationReceiver.PREVIOUS_ACTION)
        registerReceiver(receiver, filter)
    }

    private fun storePlayerState() {
        val dao = db.recoverDao()
        ExecutorInstance.getInstance().execute {
            val tm = ArrayList(rmPhoto)
            val tl = ArrayList(rlPhoto)
            val tr = ArrayList(rrPhoto)
            dao.deleteRecoverData(tm, tl, tr)

            rmPhoto.clear()
            rrPhoto.clear()
            rlPhoto.clear()
            val list = dataController.mediaSource

            val modeController = IPCModeController.Stub.asInterface(pool.queryBinder(
                BinderPool.MODE_CONTROL_BINDER))?.castAs<IPCModeControllerImpl>()
            if (list.isNotEmpty()) {
                translateList(rmPhoto, rrPhoto, rlPhoto)
                tm.clear()
                tm.addAll(rmPhoto)
                tl.clear()
                tl.addAll(rlPhoto)
                tr.clear()
                tr.addAll(rrPhoto)

                dao.insertRecoverData(tm, tl, tr)

                modeController?.let {
                    storeState(it.realController.current, dataController.position.current(), list.size)
                }

            } else {
                storeState(MediaModeSetting.ORDER, MediaModeSetting.INIT_POSITION, 0)
            }
        }
    }

    private fun translateList(recoverMusicList: MutableList<RecoverMusicData>,
                              recoverARList: MutableList<RecoverARData>,
                              recoverALList: MutableList<RecoverALData>) {
        val list = dataController.mediaSource
        for (l in list) {
            recoverMusicList.add(l.toRecoverMusic())
            for (a in l.ar) {
                recoverARList.add(a.toRecoverAR(l.id))
            }
            recoverALList.add(l.al.toRecoverAL(l.id))
        }
    }

    private fun readLocal() {
        ExecutorInstance.getInstance().execute {
            val list = mutableListOf<MusicItem>()
            val dao = db.recoverDao()
            val data = dao.obtainRecoverData()
            val recoverMusic = mutableListOf<RecoverMusicData>()
            val recoverAl = mutableListOf<RecoverALData>()
            val recoverAr = mutableListOf<RecoverARData>()
            for (d in data) {
                list.add(d.toMusicItem())
                recoverMusic.add(d.musicData)
                recoverAl.add(d.alData)
                recoverAr.addAll(d.arDataList)
            }

//            dao.deleteRecoverData(recoverMusic, recoverAl, recoverAr)
            if (list.isNotEmpty()) {
                mainHandler.post {
                    rmPhoto.addAll(recoverMusic)
                    rlPhoto.addAll(recoverAl)
                    rrPhoto.addAll(recoverAr)
                    val source = mutableListOf<Wrapper>()
                    list.forEach {
                        source.add(it.wrap())
                    }
                    dataController.addAll(source)

                    mainHandler.post {
                        val current = dataController.current()
                        if (current != null) {
                            listenerController.invokeItemChangeListener(current)
                        }
                    }
                }

            }

        }
    }

    private fun storeState(mode: Int, position: Int, max: Int) {
        SP.sp.edit().apply {
            putInt(MODE_KEY, mode)
            putInt(POSITION_KEY, position)
            putInt(MAX_KEY, max)
            apply()
        }
    }

    override fun onLoadItem(itemIndex: Int, item: Item, callback: Loader.Callback<Item>) {
        val cacheCallback = CacheCallback(callback, cacheStrategy!!, client)
        viewModel.requestMusicURL(item.cast()!!, itemIndex, cacheCallback)
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
        return CacheStrategyImpl(cacheDirectory = file, maxMemorySize = maxSize.toInt())
    }

    override fun reportOperator(code: Int, value: MusicItem?) {
        if (code == IPCDataControllerImpl.SUCCESS_CODE) {
            Toast.makeText(this.applicationContext,
                "${value?.name}成功添加到播放列表中", Toast.LENGTH_SHORT).show()
        } else {
            value?.let {
                Toast.makeText(this.applicationContext,
                    "添加歌曲失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        storePlayerState()
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