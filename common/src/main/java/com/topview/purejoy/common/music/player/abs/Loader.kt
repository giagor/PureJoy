package com.topview.purejoy.common.music.player.abs

import com.topview.purejoy.common.music.data.Item

interface Loader {
    /**
     * @param itemIndex 要加载URL的音乐实体在集合中的位置
     * @param item 要加载URL的音乐实体
     */
    fun onLoadItem(itemIndex: Int, item: Item)
}