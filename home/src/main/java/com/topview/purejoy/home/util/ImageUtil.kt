package com.topview.purejoy.home.util

object ImageUtil {
    /**
     * 为图片URL添加大小限制
     */
    fun limitImageSize(url: String?, size: Int): String = limitImageSize(url, size, size)

    fun limitImageSize(url: String?, width: Int, height: Int) = "$url?param${width}y$height"
}