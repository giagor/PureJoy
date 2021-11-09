package com.topview.purejoy.musiclibrary.player.abs

import com.topview.purejoy.musiclibrary.player.impl.listener.ChangeListener
import com.topview.purejoy.musiclibrary.player.impl.listener.ListenerFilter

// 真正的播放器回调管理接口
interface MediaListenerManger {
    fun <T> registerChangeListener(listener: ChangeListener<T>)
    fun <T> removeChangeListener(listener: ChangeListener<T>)
    fun <T> invokeChangeListener(value: T, filter: ListenerFilter<T>)
}