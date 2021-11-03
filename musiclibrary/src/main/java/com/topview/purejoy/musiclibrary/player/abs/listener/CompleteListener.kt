package com.topview.purejoy.musiclibrary.player.abs.listener

/**
 * 音乐播放完成的回调
 */
interface CompleteListener<T> {
    fun onComplete(value: T)
}