package com.topview.purejoy.common.util

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.drawable.CrossfadeDrawable
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition

/**
 * 简化创建ImageRequest的流程
 * 创建的ImageRequest默认带有CrossFade，但不使用[preferExactIntrinsicSize]<br>
 * [preferExactIntrinsicSize]的含义参见[CrossfadeDrawable.preferExactIntrinsicSize]
 */
@Composable
fun createImageRequestForCoil(
    data: Any?,
    placeholder: Drawable? = null,
    requestBuilder: (ImageRequest.Builder.() -> Unit) = {},
    crossFade: Boolean = true,
    preferExactIntrinsicSize: Boolean = false
): ImageRequest {
    val builder = ImageRequest.Builder(LocalContext.current)
    if (crossFade) {
        builder.transitionFactory(
            CrossfadeTransition.Factory(
            preferExactIntrinsicSize = preferExactIntrinsicSize
        ))
    }
    return builder.data(data)
        .placeholder(placeholder)
        .error(placeholder)
        .apply {
            requestBuilder()
        }
        .build()
}