package com.topview.purejoy.common.util

import android.content.res.Resources
import android.util.TypedValue

fun dpToPx(dp: Float): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        Resources.getSystem().displayMetrics
    )

fun dpToPx(dp: Int): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

fun pxToDp(px: Int): Int = pxToDp(px.toFloat()).toInt()

fun pxToDp(px: Float): Float = px / Resources.getSystem().displayMetrics.density