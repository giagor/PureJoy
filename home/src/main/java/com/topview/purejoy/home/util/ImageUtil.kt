package com.topview.purejoy.home.util

import android.graphics.drawable.ColorDrawable

object ImageUtil {

    /**
     * 为图片URL添加大小限制
     */
    fun limitImageSize(url: String?, size: Int): String = limitImageSize(url, size, size)

    fun limitImageSize(url: String?, width: Int, height: Int) = "$url?param=${width}y$height"

    fun getRandomColorDrawable(): ColorDrawable {
        val red = Math.random() * 256
        val green = Math.random() * 256
        val blue = Math.random() * 256
        return ColorDrawable(toArgb(
            red = red.toFloat(),
            green = green.toFloat(),
            blue = blue.toFloat()
        ))
    }

    private fun toArgb( alpha: Float = 1F, red: Float, green: Float, blue: Float): Int {
        return ((alpha * 255.0f + 0.5f).toInt() shl 24) or
               ((red   * 255.0f + 0.5f).toInt() shl 16) or
               ((green * 255.0f + 0.5f).toInt() shl  8) or
                (blue  * 255.0f + 0.5f).toInt()
    }
}