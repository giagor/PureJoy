package com.topview.purejoy.common.music.player.abs

import com.topview.purejoy.common.music.data.Item

interface ErrorHandler<T : Item> {
    /**
     * @param value 播放失败的音乐实体
     * 错误处理
     */
    fun onError(value: T)
}