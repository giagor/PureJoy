package com.topview.purejoy.common.music.player.impl.ipc

import com.topview.purejoy.common.IPCListenerController
import com.topview.purejoy.common.music.data.Wrapper

abstract class AbsIPCListenerController : IPCListenerController.Stub() {
    /**
     * @param change 数据集合中变化的数据
     * 通知客户端数据集合的变化
     */
    abstract fun invokeDataSetChangeListener(change: List<Wrapper>?)

    /**
     * @param change 当前准备播放的音乐实体
     * 通知客户端播放的音乐实体发生变化
     */
    abstract fun invokeItemChangeListener(change: Wrapper)

    /**
     * @param mode 播放模式
     * 通知客户端播放模式发生变化
     */
    abstract fun invokeModeChangeListener(mode: Int)

    /**
     * @param state 播放状态
     * 通知客户端音乐播放器的播放状态发生变化
     */
    abstract fun invokePlayStateChangeListener(state: Boolean)
}