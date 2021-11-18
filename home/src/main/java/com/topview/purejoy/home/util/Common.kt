package com.topview.purejoy.home.util

import com.topview.purejoy.common.util.dpToPx

object Common {
    /**
     * discover碎片中，RecommendNewSong对应的Item的横向间隔
     * */
    val DISCOVER_RECOMMEND_NEW_SONG_ITEM_HORIZONTAL_SPACING: Int = dpToPx(10)

    /**
     * discover碎片中，RecommendNewSong对应的Item的宽度比屏幕的宽度小多少，即
     * width(item) + DISCOVER_RECOMMEND_NEW_SONG_ITEM_WIDTH_SMALLER_THAN_SCREEN = width(screen)
     * */
    val DISCOVER_RECOMMEND_NEW_SONG_ITEM_WIDTH_SMALLER_THAN_SCREEN: Int = dpToPx(50)
}