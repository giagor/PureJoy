package com.topview.purejoy.common.music.player.abs.controller

import com.topview.purejoy.common.music.player.abs.MediaListenerManger

interface ModeController {
    // 当前的播放模式
    var current: Int
    val listenerManager: MediaListenerManger
    // 切换到下一个播放模式
    fun nextMode(): Int
    fun setMode(mode: Int)
}