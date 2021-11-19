package com.topview.purejoy.common.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 获取窗口的宽度
 *
 * @return 若获取成功，则返回窗口的宽度；若获取失败，则返回0
 * */
fun getWindowWidth(context: Context): Int {
    val windowManager: WindowManager? =
        context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    if (windowManager != null) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }
    return 0
}

/**
 * 获取窗口的高度
 *
 * @return 若获取成功，则返回窗口的高度；若获取失败，则返回0
 * */
fun getWindowHeight(context: Context): Int {
    val windowManager: WindowManager? =
        context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    if (windowManager != null) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }
    return 0
}