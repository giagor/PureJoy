package com.topview.purejoy.video.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * 寻找Activity并设置requestedOrientation，从而改变屏幕朝向
 */
internal fun Context.setOrientation(orientation: Int) {
    findActivity()?.let {
        it.requestedOrientation = orientation
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
