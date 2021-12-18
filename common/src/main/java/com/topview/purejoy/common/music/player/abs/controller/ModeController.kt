package com.topview.purejoy.common.music.player.abs.controller

interface ModeController {
    // 当前的播放模式
    var current: Int
    // 切换到下一个播放模式
    fun nextMode(): Int
}