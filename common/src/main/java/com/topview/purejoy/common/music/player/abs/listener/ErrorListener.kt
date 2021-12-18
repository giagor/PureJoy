package com.topview.purejoy.common.music.player.abs.listener

/**
 * 音乐播放错误时的回调
 */
interface ErrorListener<T> {
    fun onError(value: T): Boolean
}