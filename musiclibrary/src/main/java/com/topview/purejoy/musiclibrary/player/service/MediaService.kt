package com.topview.purejoy.musiclibrary.player.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.topview.purejoy.musiclibrary.IPCPlayerController
import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.player.abs.ErrorHandler
import com.topview.purejoy.musiclibrary.player.abs.Loader
import com.topview.purejoy.musiclibrary.player.abs.cache.CacheStrategy
import com.topview.purejoy.musiclibrary.player.abs.core.MusicPlayer
import com.topview.purejoy.musiclibrary.player.abs.core.Position
import com.topview.purejoy.musiclibrary.player.abs.listener.ErrorListener
import com.topview.purejoy.musiclibrary.player.impl.MediaListenerMangerImpl
import com.topview.purejoy.musiclibrary.player.impl.controller.MediaControllerImpl
import com.topview.purejoy.musiclibrary.player.impl.controller.ModeControllerImpl
import com.topview.purejoy.musiclibrary.player.impl.core.MusicPlayerImpl
import com.topview.purejoy.musiclibrary.player.impl.ipc.*
import com.topview.purejoy.musiclibrary.player.impl.listener.*
import com.topview.purejoy.musiclibrary.player.impl.position.InitPosition
import com.topview.purejoy.musiclibrary.player.setting.ErrorSetting
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting
import com.topview.purejoy.musiclibrary.player.util.DataSource
import com.topview.purejoy.musiclibrary.player.util.cast
import java.lang.ref.WeakReference

abstract class MediaService<T : Item> : Service(), Loader {
    protected val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    protected val errorSetting by lazy {
        errorSetting()
    }

    protected val pool by lazy {
        BinderPool()
    }

    protected val cacheStrategy: CacheStrategy? by lazy {
        cacheStrategy()
    }


    override fun onCreate() {
        super.onCreate()
        initMediaModeSetting()
        initIPC()
    }

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
        val local = initDataSet()
        val dataSource = DataSource<T>()

        val wrappers = DataSource<Wrapper>()

        if (local.isNotEmpty()) {
            local.forEach {
                wrappers.add(Wrapper(value = it))
            }
            dataSource.addAll(local)
        }

        val dataController = IPCDataControllerImpl(
            handler = mainHandler,
            source = wrappers,
            mediaSource = dataSource, addCallback = { code, wrapper ->
                reportOperator(code, wrapper?.value?.cast())
            }, position = position)
        return dataController
    }

    /**
     * @param callback 音乐URL错误时使用callback来加载音乐URL
     * @param source 音乐实体集合
     * @param position 初始化MediaController要用到的位置信息
     * 初始化IPCPlayerController
     */
    private fun initPlayerController(callback: Loader.Callback<Item>,
                                     source: MutableList<T>,
                                     position: Position
    ): IPCPlayerControllerImpl {
        val player = MusicPlayerImpl()
        val listenerManger = MediaListenerMangerImpl()
        val mediaController = MediaControllerImpl(
            player = player,
            position = position,
            listenerManger = listenerManger,
            loader = WeakReference(this),
            handler = mainHandler,
            list = source,
            itemCallback = callback
        )
        player.completeListener = object : MusicPlayer.CompleteListener {
            override fun completed() {
                mainHandler.post {
                    mediaController.completeListener?.onComplete(
                        source[mediaController.position.current()])
                    mediaController.listenerManger.invokeChangeListener(false, PlayStateFilter)
                    mediaController.next()
                }
            }
        }
        player.preparedListener = object : MusicPlayer.PreparedListener {
            override fun prepared() {
                mainHandler.post {
                    mediaController.listenerManger.invokeChangeListener(source[position.current()], ItemFilter)
                    mediaController.listenerManger.invokeChangeListener(true, PlayStateFilter)
                    mediaController.preparedListener?.onPrepared(
                        source[mediaController.position.current()])
                }
            }

        }

        player.errorListener = object : MusicPlayer.ErrorListener {
            override fun onError(what: Int, extra: Int) {
                mainHandler.post {
                    val value = source[mediaController.position.current()]
                    val count = errorSetting.errorCount(value)
                    mediaController.listenerManger.invokeChangeListener(false, PlayStateFilter)
                    if (mediaController.errorListener?.onError(value) == false) {
                        player.reset()
                        if (count >= errorSetting.retryCount) {
                            errorSetting.handler.onError(value)
                        } else {
                            errorSetting.record[value] = count + 1
                            mediaController.playOrPause()
                        }
                    }
                }
            }
        }
        mediaController.errorListener = object : ErrorListener<T> {
            override fun onError(value: T): Boolean {
                player.reset()
                val count = errorSetting.errorCount(value)
                if (count >= errorSetting.retryCount) {
                    errorSetting.handler.onError(value)
                } else {
                    errorSetting.record[value] = count + 1
                    mediaController.playOrPause()
                }
                return true
            }

        }

        return IPCPlayerControllerImpl(realController = mediaController, handler = mainHandler)
    }

    /**
     * 设置初始播放模式，默认为MediaModeSetting.getFirstMode()
     * 可重载此方法从磁盘加载播放模式
     */
    open fun loadInitMode(): Int {
        return MediaModeSetting.getInstance().getFirstMode()
    }


    /**
     * 初始化IPC服务(包括IPCModeController, IPCDataController, IPCPlayerController, IPCListenerController)
     * 并将这些服务放入BinderPool中
     */
    open fun initIPC() {
        val modeController = IPCModeControllerImpl(handler = mainHandler,
            realController = ModeControllerImpl(current = loadInitMode()))

        val dataController = initDataController(
            MediaModeSetting.getInstance().getPosition(modeController.realController.current)!!)
        val source = dataController.source

        val callback = object : Loader.Callback<Item> {
            override fun onSuccess(itemIndex: Int, value: Item) {
                val wrapper = source[itemIndex]
                if (wrapper.value?.isSame(value) == true) {
                    wrapper.value = value
                } else {
                    source.forEach { w ->
                        if (w.value?.isSame(value) == true) {
                            w.value = value
                        }
                    }
                }
            }

            override fun onFailure(msg: String?) {

            }
        }

        val playerController = initPlayerController(callback = callback,
            source = dataController.mediaSource,
            position = dataController.position)
        val realController = playerController.realController.cast<MediaControllerImpl<T>>()!!

        val listenerController = IPCListenerControllerImpl()

        source.removeListeners.add(object : DataSource.RemoveListener<Wrapper> {
            override fun onRemoved(element: Wrapper, index: Int) {
                realController.position.max = source.size;
                if (realController.position.current() == index) {
                    if (source.size > 0) {
                        realController.next()
                    } else {
                        realController.reset()
                    }
                }
                errorSetting.remove(element.value?.cast<T>()!!)
            }

        })
        source.changeListeners.add(object : DataSource.DataSetChangeListener<Wrapper> {
            override fun onChange(changes: MutableList<Wrapper>?) {

                listenerController.invokeDataSetChangeListener(changes)

                if (changes == null) {
                    realController.position.with(InitPosition)
                    errorSetting.record.clear()
                } else {
                    realController.position.max = source.size
                    if (realController.position.current() == MediaModeSetting.INIT_POSITION) {
                        realController.next()
                    }
                    changes.forEach {
                        errorSetting.remove(it.value?.cast<T>()!!)
                    }
                }
            }
        })

        (modeController.realController as ModeControllerImpl).
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
                    if (value.isSame(source[i].value)) {
                        listenerController.invokeItemChangeListener(source[i])
                        showForeground(value.cast()!!)
                        break
                    }
                }
            }
        })
        realController.listenerManger.registerChangeListener(object : PlayStateChangeListener {
            override fun onChange(value: Boolean) {
                listenerController.invokePlayStateChangeListener(value)
                showForeground(source[realController.position.current()].value?.cast<T>()!!)
            }
        })

        pool.map[BinderPool.DATA_CONTROL_BINDER] = dataController
        pool.map[BinderPool.LISTENER_CONTROL_BINDER] = listenerController
        pool.map[BinderPool.MODE_CONTROL_BINDER] = modeController
        pool.map[BinderPool.PLAYER_CONTROL_BINDER] = playerController


    }

    override fun onBind(intent: Intent?): IBinder? {
        return pool
    }


    /**
     * @param code 状态码
     * @param value 音乐实体
     * 添加单个音乐实体的回调，当code为0时，添加失败，code为1时，添加成功
     */
    open fun reportOperator(code: Int, value: T?) {

    }


    /**
     * @param value 播放失败的音乐实体
     * 播放失败时的回调
     */
    open fun reportPlayError(value: T) {

    }

    /**
     * @param value 当前播放音乐对应的实体
     * 应在这个方法中生成通知等
     */
    abstract fun showForeground(value: T)

    /**
     * 加载初始化时所用到的音乐数据集合，默认为一个空的MutableList
     * 可在此方法中加载本地数据
     */
    open fun initDataSet(): MutableList<T> {
        return mutableListOf()
    }

    /**
     * 初始化缓存策略，默认为null
     */
    open fun cacheStrategy(): CacheStrategy? {
        return null
    }

    /**
     * 初始化错误处理设置
     */
    open fun errorSetting(): ErrorSetting<T> {
        return ErrorSetting(handler = object : ErrorHandler<T> {
            override fun onError(value: T) {
                reportPlayError(value)
            }
        })
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        (IPCPlayerController.Stub.asInterface(
            pool.queryBinder(BinderPool.PLAYER_CONTROL_BINDER)
        ) as? IPCPlayerControllerImpl)?.realController?.release()
        super.onDestroy()
    }
}