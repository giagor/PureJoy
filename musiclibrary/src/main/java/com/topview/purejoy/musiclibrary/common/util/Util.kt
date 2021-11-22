package com.topview.purejoy.musiclibrary.common.util

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.topview.purejoy.musiclibrary.R

/**
 * 获取图片主题色的方法
 * @param bitmap 从这个Bitmap获取主题色
 * @param view 要设置背景色的View
 */
fun buildSwatch(bitmap: Bitmap, action: (Int) -> Unit) {
    Palette.Builder(bitmap).generate {
        it?.apply {
            val s = when {
                lightMutedSwatch != null -> {
                    lightMutedSwatch
                }
                lightVibrantSwatch != null -> {
                    lightVibrantSwatch
                }
                vibrantSwatch != null -> {
                    vibrantSwatch
                }
                vibrantSwatch != null -> {
                    vibrantSwatch
                }
                darkMutedSwatch != null -> {
                    darkMutedSwatch
                }
                darkVibrantSwatch != null -> {
                    darkVibrantSwatch
                }
                else -> {
                    mutedSwatch
                }
            }
            val c = s?.rgb ?: R.color.wathet
//                    view.setBackgroundColor(c)
            action.invoke(c)

        }
    }

}