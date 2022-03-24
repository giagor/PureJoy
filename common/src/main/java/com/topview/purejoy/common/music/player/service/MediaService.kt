package com.topview.purejoy.common.music.player.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.topview.purejoy.common.IPCDataController
import com.topview.purejoy.common.IPCPlayerController
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.ErrorHandler
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.player.abs.core.MusicPlayer
import com.topview.purejoy.common.music.player.abs.core.Position
import com.topview.purejoy.common.music.player.impl.MediaListenerMangerImpl
import com.topview.purejoy.common.music.player.impl.controller.MediaControllerImpl
import com.topview.purejoy.common.music.player.impl.controller.ModeControllerImpl
import com.topview.purejoy.common.music.player.impl.core.DefaultMusicPlayer
import com.topview.purejoy.common.music.player.impl.ipc.*
import com.topview.purejoy.common.music.player.impl.listener.*
import com.topview.purejoy.common.music.player.impl.position.InitPosition
import com.topview.purejoy.common.music.player.setting.ErrorSetting
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.common.music.player.setting.PlayerSetting
import com.topview.purejoy.common.music.player.util.DataSource
import com.topview.purejoy.common.music.player.util.cast
import com.topview.purejoy.common.music.player.util.castAs
import com.topview.purejoy.common.music.player.util.ensureSecurity
import com.topview.purejoy.common.music.util.ExecutorInstance
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

abstract class MediaService<T : Item> : Service(), Loader {
    protected val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }


    protected val pool by lazy {
        BinderPool()
    }

    protected val setting by lazy {
        providePlayerSetting()
    }


    override fun onCreate() {
        super.onCreate()
        initMediaModeSetting()
        initIPC()
    }

    protected abstract fun providePlayerSetting(): PlayerSetting<T>



    /**
     * 初始化播放模式
     */
    open fun initMediaModeSetting() {
        MediaModeSetting.getInstance().init(0, MediaModeSetting.INIT_POSITION)
    }

    /**
     * @param position 初始化DataController用的Position
     * 初始化IPCDataController
     */
    private fun initDataController(position: Position): IPCDataControllerImpl<T> {
        val local = loadLocalRecord()
        val list = CopyOnWriteArrayList<T>()
        local?.let {
            list.addAll(local)
        }
        val dataSource = DataSource<T>(list = CopyOnWriteArrayList())

        val wrappers = DataSource<Wrapper>(list = CopyOnWriteArrayList())

        position.max = dataSource.size
        val interceptor = setting.dataInterceptor
        return if (interceptor == null) {
            IPCDataControllerImpl(
                handler = mainHandler,
                source = wrappers,
                mediaSource = dataSource,
                position = position,
                transformation = setting.itemTransformation,
                operatorCallback = setting.operatorCallback)
        } else {
            IPCDataControllerImpl(
                handler = mainHandler,
                source = wrappers,
                mediaSource = dataSource,
                position = position,
                transformation = setting.itemTransformation,
                operatorCallback = setting.operatorCallback,
                interceptor = interceptor)
        }
    }

    /**
     * @param callback 音乐URL错误时使用callback来加载音乐URL
     * @param source 音乐实体集合
     * @param position 初始化MediaController要用到的位置信息
     * 初始化IPCPlayerController
     */
    private fun initPlayerController(source: MutableList<T>,
                                     position: Position
    ): IPCPlayerControllerImpl<T> {
        val player = setting.basePlayer ?: DefaultMusicPlayer()
        val listenerManger = MediaListenerMangerImpl()
        val mediaController = setting.player ?: MediaControllerImpl(
            player = player,
            position = position,
            listenerManger = listenerManger,
            loader = WeakReference(this),
            handler = mainHandler,
            list = source,
            cacheStrategy = setting.cacheStrategy
        )
        val config = setting.errorSetting
        val errorSetting = if (config == null) {
            ErrorSetting(handler = object : ErrorHandler<T> {
                override fun onError(value: T) {
                    showForeground(value, false)
                }
            })
        } else {
            ErrorSetting(config.retryCount, handler = object : ErrorHandler<T> {
                override fun onError(value: T) {
                    showForeground(value, false)
                    config.handler.onError(value)
                }

            }, config.record)
        }
        setting.errorSetting = config
        player.completeListener = object : MusicPlayer.CompleteListener {
            override fun completed() {
                mainHandler.post {
                    ensureSecurity(source, mediaController.position) {
                        mediaController.completeListener?.completed()
                    }
                    mediaController.listenerManger.invokeChangeListener(false, PlayStateFilter)
                    mediaController.next()
                }
            }
        }
        player.preparedListener = object : MusicPlayer.PreparedListener {
            override fun prepared() {
                mainHandler.post {
                    ensureSecurity(source, mediaController.position) {
                        mediaController.listenerManger.invokeChangeListener(true, PlayStateFilter)
                        mediaController.preparedListener?.prepared()
                    }
                }
            }

        }
        player.errorListener = object : MusicPlayer.ErrorListener<String> {
            override fun onError(player: MusicPlayer<String>, what: Int, extra: Int): Boolean {
                ensureSecurity(source, mediaController.position) {
                    val value = source[mediaController.position.current()]
                    val count = errorSetting.errorCount(value)
                    mediaController.listenerManger.invokeChangeListener(false, PlayStateFilter)
                    if (mediaController.errorListener?.onError(mediaController, what, extra) != true) {
                        player.reset()
                        if (count >= errorSetting.retryCount) {
                            errorSetting.handler.onError(value)
                        } else {
                            errorSetting.record[value] = count + 1
                            mediaController.playOrPause()
                        }
                    }
                }
                return true
            }
        }

        return IPCPlayerControllerImpl(realController = mediaController, handler = mainHandler)
    }

    /**
     * 设置初始播放模式，默认为MediaModeSetting.getFirstMode()
     * 可重写此方法从磁盘加载播放模式
     */
    open fun loadInitMode(): Int {
        return MediaModeSetting.getInstance().getFirstMode()
    }

    // 初始化IPCModeController
    private fun initIPCModeController(): IPCModeControllerImpl {
        return IPCModeControllerImpl(handler = mainHandler,
            realController = ModeControllerImpl(current = loadInitMode())
        )
    }

    // 初始化IPCListenerController
    private fun initIPCListenerController(): IPCListenerControllerImpl {
        return IPCListenerControllerImpl()
    }

    override fun onLoadItem(itemIndex: Int, item: Item) {
        ExecutorInstance.getInstance().execute {
            val newItem = loadItem(itemIndex, item)
            handleData(itemIndex, item, newItem)
        }
    }

    /**
     * @param itemIndex 音乐实体在列表中的位置
     * @param item 音乐实体
     * 同步请求音乐实体URL
     */
    protected abstract fun loadItem(itemIndex: Int, item: Item): Item

    /**
     * @param itemIndex 请求的音乐实体在列表中的位置
     * @param oldItem 音乐实体
     * @param newItem 本次请求获取到的音乐实体
     * 将播放列表中[oldItem]换为[newItem]
     */
    protected open fun handleData(itemIndex: Int, oldItem: Item, newItem: Item) {
        mainHandler.post {
            val dataController = IPCDataController.Stub.asInterface(
                pool.queryBinder(BinderPool.DATA_CONTROL_BINDER))?.castAs<IPCDataControllerImpl<T>>()!!
            val playerController = IPCPlayerController.Stub.asInterface(pool.queryBinder(
                BinderPool.PLAYER_CONTROL_BINDER))?.castAs<IPCPlayerControllerImpl<T>>()!!
            val source = dataController.source
            val mediaSource = dataController.mediaSource
            for (i in 0 until mediaSource.size) {
                if (oldItem == mediaSource[i]) {
                    mediaSource[i] = newItem.cast()!!
                    source[i] = setting.wrapperTransformation.transform(mediaSource[i])!!
                    break
                }
            }
            val cur = playerController.realController.position.current()
            if (mediaSource[cur] == newItem && !playerController.isPlaying) {
                playerController.realController.setDataSource(mediaSource[cur])
            }

        }
    }

    /**
     * 初始化IPC服务(包括[IPCModeControllerImpl], [IPCDataControllerImpl],
     * [IPCPlayerControllerImpl], [IPCListenerControllerImpl])
     * 并将这些服务放入[BinderPool]中
     */
    open fun initIPC() {
        val modeController = initIPCModeController()
        pool.map[BinderPool.MODE_CONTROL_BINDER] = modeController


        val dataController = initDataController(
            MediaModeSetting.getInstance().getPosition(modeController.currentMode())!!)
        pool.map[BinderPool.DATA_CONTROL_BINDER] = dataController
        val source = dataController.source


        val playerController = initPlayerController(
            source = dataController.mediaSource,
            position = dataController.position)
        pool.map[BinderPool.PLAYER_CONTROL_BINDER] = playerController
        val realController = playerController.realController.castAs<MediaControllerImpl<T>>()!!

        val listenerController = initIPCListenerController()
        pool.map[BinderPool.LISTENER_CONTROL_BINDER] = listenerController

        source.removeListeners.add(object : DataSource.RemoveListener<Wrapper> {
            override fun onRemoved(element: Wrapper, index: Int) {
                realController.position.max = source.size
                if (realController.position.current() == index) {
                    if (realController.isPlaying()) {
                        realController.playOrPause()
                    }
                    if (source.size > 0) {
                        realController.next()
                    } else {
                        realController.reset()
                    }
                }
                setting.itemTransformation.transform(element)?.let {
                    setting.errorSetting?.remove(it)
                }
            }

        })
        source.changeListeners.add(object : DataSource.DataSetChangeListener<Wrapper> {
            override fun onChange(changes: MutableList<Wrapper>) {

                listenerController.invokeDataSetChangeListener(changes)

                if (changes.isEmpty()) {
                    realController.position.with(InitPosition)
                    setting.errorSetting?.record?.clear()
                    if (realController.isPlaying()) {
                        realController.playOrPause()
                        realController.reset()
                    }
                } else {
                    realController.position.max = source.size
                    changes.forEach {
                        setting.itemTransformation.transform(it)?.let { item ->
                            setting.errorSetting?.remove(item)
                        }
                    }
                }
            }
        })

        (modeController.realController).
        listenerManager.registerChangeListener(object : ModeStateChangeListener {
            override fun onChange(value: Int) {
                val position = realController.position
                realController.position = MediaModeSetting.getInstance().getPosition(value)!!
                realController.position.with(position)
                dataController.position = realController.position
                listenerController.invokeModeChangeListener(value)
            }
        })

        realController.listenerManger.registerChangeListener(object : ItemChangeListener {
            override fun onChange(value: Item) {
                for(i in 0 until source.size) {
                    val v = setting.itemTransformation.transform(source[i])
                    if (value == v) {
                        listenerController.invokeItemChangeListener(source[i])
                        showForeground(v, realController.isPlaying())
                        break
                    }
                }
            }
        })
        realController.listenerManger.registerChangeListener(object : PlayStateChangeListener {
            override fun onChange(value: Boolean) {
                listenerController.invokePlayStateChangeListener(value)
                val p = realController.position.current()
                if (source.isNotEmpty() && p < source.size) {
                    showForeground(setting.itemTransformation.transform(
                        source[realController.position.current()])!!,
                        realController.isPlaying())
                }
            }
        })

    }

    override fun onBind(intent: Intent?): IBinder? {
        return pool
    }




    /**
     * @param value 当前播放音乐对应的实体
     * @param state 播放状态
     * 应在这个方法中生成通知等
     */
    protected abstract fun showForeground(value: T, state: Boolean)

    /**
     * 加载本地记录
     */
    protected open fun loadLocalRecord(): MutableList<T>? {
        return null
    }



    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        (IPCPlayerController.Stub.asInterface(
            pool.queryBinder(BinderPool.PLAYER_CONTROL_BINDER)
        ) as? IPCPlayerControllerImpl<*>)?.realController?.release()
        super.onDestroy()
    }




}