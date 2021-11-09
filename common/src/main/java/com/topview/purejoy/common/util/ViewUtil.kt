package com.topview.purejoy.common.util

import android.content.res.Resources
import android.util.TypedValue

fun dpToPx(dp: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        Resources.getSystem().displayMetrics
    )