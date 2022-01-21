package com.topview.purejoy.common.music.player.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.topview.purejoy.common.IPCPlayerController
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.ErrorHandler
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.player.abs.cache.CacheStrategy
import com.topview.purejoy.common.music.player.abs.core.MusicPlayer
import com.topview.purejoy.common.music.player.abs.core.Position
import com.topview.purejoy.common.music.player.abs.listener.ErrorListener
import com.topview.purejoy.common.music.player.abs.transformation.IWrapperTransformation
import com.topview.purejoy.common.music.player.abs.transformation.ItemTransformation
import com.topview.purejoy.common.music.player.impl.DataInterceptor
import com.topview.purejoy.common.music.player.impl.MediaListenerMangerImpl
import com.topview.purejoy.common.music.player.impl.OperatorCallback
import com.topview.purejoy.common.music.player.impl.controller.MediaControllerImpl
import com.topview.purejoy.common.music.player.impl.controller.ModeControllerImpl
import com.topview.purejoy.common.music.player.impl.core.MusicPlayerImpl
import com.topview.purejoy.common.music.player.impl.ipc.*
import com.topview.purejoy.common.music.player.impl.listener.*
import com.topview.purejoy.common.music.player.impl.position.InitPosition
import com.topview.purejoy.common.music.player.setting.ErrorSetting
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.common.music.player.util.DataSource
import com.topview.purejoy.common.music.player.util.cast
import com.topview.purejoy.common.music.player.util.castAs
import com.topview.purejoy.common.music.player.util.ensureSecurity
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

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

    protected val itemTransformation by lazy {
        transformation()
    }

    protected val wrapperTransformation by lazy {
        wrapperTransformation()
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
        val local = loadLocalRecord()
        val list = CopyOnWriteArrayList<T>()
        local?.let {
            list.addAll(local)
        }
        val dataSource = DataSource<T>(list = CopyOnWriteArrayList())

        val wrappers = DataSource<Wrapper>(list = CopyOnWriteArrayList())

        position.max = dataSource.size
        val interceptor = dataInterceptor()
        return if (interceptor == null) {
            IPCDataControllerImpl(
                handler = mainHandler,
                source = wrappers,
                mediaSource = dataSource,
                position = position,
                transformation = itemTransformation,
                operatorCallback = operatorCallback())
        } else {
            IPCDataControllerImpl(
                handler = mainHandler,
                source = wrappers,
                mediaSource = dataSource,
                position = position,
                transformation = itemTransformation,
                operatorCallback = operatorCallback(),
                interceptor = interceptor)
        }
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
            itemCallback = callback,
            cacheStrategy = cacheStrategy
        )
        player.completeListener = object : MusicPlayer.CompleteListener {
            override fun completed() {
                mainHandler.post {
                    ensureSecurity(source, mediaController.position) {
                        mediaController.completeListener?.onComplete(
                            source[mediaController.position.current()])
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
                        mediaController.preparedListener?.onPrepared(
                            source[mediaController.position.current()])
                    }
                }
            }

        }

        player.errorListener = object : MusicPlayer.ErrorListener {
            override fun onError(what: Int, extra: Int) {
                mainHandler.post {
                    ensureSecurity(source, mediaController.position) {
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
        }
        mediaController.errorListener = object : ErrorListener<T> {
            override fun onError(value: T): Boolean {
                ensureSecurity(source, mediaController.position) {
                    if (source[position.current()] == value) {
                        player.reset()
                        val count = errorSetting.errorCount(value)
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
     * 可重载此方法从磁盘加载播放模式
     */
    open fun loadInitMode(): Int {
        return MediaModeSetting.getInstance().getFirstMode()
    }


    /**
     * 初始化IPC服务(包括[IPCModeControllerImpl], [IPCDataControllerImpl],
     * [IPCPlayerControllerImpl], [IPCListenerControllerImpl])
     * 并将这些服务放入[BinderPool]中
     */
    open fun initIPC() {
        val modeController = IPCModeControllerImpl(handler = mainHandler,
            realController = ModeControllerImpl(current = loadInitMode())
        )

        val dataController = initDataController(
            MediaModeSetting.getInstance().getPosition(modeController.realController.current)!!)
        val source = dataController.source

        val callback = object : Loader.Callback<Item> {
            override fun callback(itemIndex: Int, value: Item) {
                val wrapper = source[itemIndex]
                if (value == itemTransformation.transform(wrapper)) {
                    wrapperTransformation.transform(value.cast()!!)?.let {
                        source[itemIndex] = it
                    }
                } else {
                    for (i in 0 until source.size) {
                        val w = source[i]
                        if (value == itemTransformation.transform(w)) {
                            wrapperTransformation.transform(value.cast()!!)?.let {
                                source[i] = it
                            }
                        }
                    }
                }
            }
        }

        val playerController = initPlayerController(callback = callback,
            source = dataController.mediaSource,
            position = dataController.position)
        val realController = playerController.realController.castAs<MediaControllerImpl<T>>()!!

        val listenerController = IPCListenerControllerImpl()

        source.removeListeners.add(object : DataSource.RemoveListener<Wrapper> {
            override fun onRemoved(element: Wrapper, index: Int) {
                realController.position.max = source.size;
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
                itemTransformation.transform(element)?.let {
                    errorSetting.remove(it)
                }
            }

        })
        source.changeListeners.add(object : DataSource.DataSetChangeListener<Wrapper> {
            override fun onChange(changes: MutableList<Wrapper>) {

                listenerController.invokeDataSetChangeListener(changes)

                if (changes.isEmpty()) {
                    realController.position.with(InitPosition)
                    errorSetting.record.clear()
                    if (realController.isPlaying()) {
                        realController.playOrPause()
                        realController.reset()
                    }
                } else {
                    realController.position.max = source.size
                    changes.forEach {
                        itemTransformation.transform(it)?.let { item ->
                            errorSetting.remove(item)
                        }
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
                    val v = itemTransformation.transform(source[i])
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
                    showForeground(itemTransformation.transform(
                        source[realController.position.current()])!!,
                        realController.isPlaying())
                }
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


    protected open fun operatorCallback(): OperatorCallback? = null


    /**
     * @param value 播放失败的音乐实体
     * 播放失败时的回调
     */
    protected open fun reportPlayError(value: T) {

    }

    /**
     * @param value 当前播放音乐对应的实体
     * @param state 播放状态
     * 应在这个方法中生成通知等
     */
    protected abstract fun showForeground(value: T, state: Boolean)

    protected open fun loadLocalRecord(): MutableList<T>? {
        return null
    }



    /**
     * 初始化缓存策略，默认为null
     */
    protected open fun cacheStrategy(): CacheStrategy? {
        return null
    }

    /**
     * 初始化错误处理设置
     */
    protected open fun errorSetting(): ErrorSetting<T> {
        return ErrorSetting(handler = object : ErrorHandler<T> {
            override fun onError(value: T) {
                reportPlayError(value)
            }
        })
    }

    protected open fun dataInterceptor(): DataInterceptor<T>? = null

    protected abstract fun transformation(): ItemTransformation<T>

    protected abstract fun wrapperTransformation(): IWrapperTransformation<T>

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        (IPCPlayerController.Stub.asInterface(
            pool.queryBinder(BinderPool.PLAYER_CONTROL_BINDER)
        ) as? IPCPlayerControllerImpl)?.realController?.release()
        super.onDestroy()
    }




}