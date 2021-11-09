package com.topview.purejoy.musiclibrary.player.abs

import com.topview.purejoy.musiclibrary.data.Item

interface ErrorHandler<T : Item> {
    /**
     * @param value 播放失败的音乐实体
     * 错误处理
     */
    fun onError(value: T)
}