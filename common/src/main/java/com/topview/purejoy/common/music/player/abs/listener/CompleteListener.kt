package com.topview.purejoy.common.music.player.abs.listener

/**
 * 音乐播放完成的回调
 */
interface CompleteListener<T> {
    fun onComplete(value: T)
}