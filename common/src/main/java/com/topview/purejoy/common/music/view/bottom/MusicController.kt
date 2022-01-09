package com.topview.purejoy.common.music.view.bottom

import androidx.lifecycle.*
import com.topview.purejoy.common.*
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.instance.BinderPoolClientInstance
import com.topview.purejoy.common.music.player.client.BinderPoolClient
import com.topview.purejoy.common.music.player.impl.ipc.BinderPool
import com.topview.purejoy.common.music.service.MusicService
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.entity.getMusicItem
import com.topview.purejoy.common.music.util.ExecutorInstance

/**
 * 注意事项：在连接服务前，应调用registerServiceConnectListener方法
 * 在不需要使用此对象时，应调用unregisterServiceListener方法
 */
open class MusicController {
    // 播放逻辑控制
    var playerController: IPCPlayerController? = null
    // 数据操作控制
    var dataController: IPCDataController? = null
    // 播放模式控制
    var modeController: IPCModeController? = null
    // IPC回调控制
    var listenerController: IPCListenerController? = null
    // 当前播放歌曲
    val currentItem: MutableLiveData<MusicItem?> = MutableLiveData()
    // 当前播放列表
    val playItems: MutableLiveData<MutableList<MusicItem>> = MutableLiveData()
    // 当前播放状态
    val playState: MutableLiveData<Boolean> = MutableLiveData()
    // 当前播放模式
    val currentMode: MutableLiveData<Int> = MutableLiveData()

    val client: BinderPoolClient by lazy {
        val c = BinderPoolClientInstance.getInstance().getClient(MusicService::class.java)
        c.connectService()
        c
    }

    private val itemChangeListener: IPCItemChangeListener by lazy {
        object : IPCItemChangeListener.Stub() {
            override fun onItemChange(wrapper: Wrapper?) {
                currentItem.postValue(wrapper?.getMusicItem())
            }
        }
    }

    private val dataSetChangeListener: IPCDataSetChangeListener by lazy {
        object : IPCDataSetChangeListener.Stub() {
            override fun onChange(source: MutableList<Wrapper>) {
                if (source.isEmpty()) {
                    currentItem.postValue(null)
                    playItems.postValue(mutableListOf())
                } else {
                    val list = playItems.value ?: mutableListOf()
                    source.forEach { w ->
                        w.getMusicItem()?.let {
                            if (list.contains(it)) {
                                list.remove(it)
                            } else {
                                list.add(it)
                            }
                        }
                    }
                    playItems.postValue(list)
                }
            }
        }
    }

    private val modeChangeListener: IPCModeChangeListener by lazy {
        object : IPCModeChangeListener.Stub() {
            override fun onModeChange(mode: Int) {
                currentMode.postValue(mode)
            }

        }
    }

    private val stateChangeListener: IPCPlayStateChangeListener by lazy {
        object : IPCPlayStateChangeListener.Stub() {
            override fun playStateChange(state: Boolean) {
                playState.postValue(state)
            }
        }
    }

    fun registerServiceListener() {
        client.registerConnectListener(this::onServiceConnected)
        client.registerDisconnectListener(this::onServiceDisconnected)
    }

    fun unregisterServiceListener() {
        playerController = null
        dataController = null
        listenerController = null
        modeController = null
        kotlin.runCatching {
            if (client.isConnected()) {
                listenerController?.apply {
                    removeItemChangeListener(itemChangeListener)
                    removeDataChangeListener(dataSetChangeListener)
                    removePlayStateChangeListener(stateChangeListener)
                    removeModeChangeListener(modeChangeListener)
                }
            }
        }
        client.unregisterConnectListener(this::onServiceConnected)
        client.unregisterDisconnectListener(this::onServiceDisconnected)
    }



    protected open fun onServiceConnected() {
        playerController = IPCPlayerController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.PLAYER_CONTROL_BINDER))
        dataController = IPCDataController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.DATA_CONTROL_BINDER))
        modeController = IPCModeController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.MODE_CONTROL_BINDER))
        listenerController = IPCListenerController.Stub.asInterface(
            client.pool()?.queryBinder(BinderPool.LISTENER_CONTROL_BINDER))
        listenerController?.apply {
            addItemChangeListener(itemChangeListener)
            addDataChangeListener(dataSetChangeListener)
            addPlayStateChangeListener(stateChangeListener)
            addModeChangeListener(modeChangeListener)
        }
        currentItem.postValue(dataController?.current()?.getMusicItem())
        playState.postValue(playerController?.isPlaying ?: false)
        dataController?.let { controller ->
            ExecutorInstance.getInstance().execute {
                val list = playItems.value ?: mutableListOf()
                val data = controller.allItems()
                for (d in data) {
                    d.getMusicItem()?.let {
                        list.add(it)
                    }
                }
                playItems.postValue(list)
            }
        }
        modeController?.currentMode()?.let {
            currentMode.postValue(it)
        }
    }

    protected open fun onServiceDisconnected() {
        playerController = null
        dataController = null
        modeController = null
        listenerController = null
        playItems.postValue(mutableListOf())
        currentItem.postValue(null)
        playState.postValue(false)
    }

}