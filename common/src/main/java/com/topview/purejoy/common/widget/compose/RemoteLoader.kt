package com.topview.purejoy.common.widget.compose

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import com.bumptech.glide.RequestBuilder
import com.google.accompanist.glide.rememberGlidePainter

class RemoteLoader {

    /**
     * Glide图片加载请求的Builder,[size]是本次图片建议的加载大小（可以认为是ImageView的大小）
     */
    var requestBuilder: (RequestBuilder<Drawable>.(size: IntSize) -> RequestBuilder<Drawable>)? = null

    /**
     * 设置加载图片时的占位图，注意，使用同一个Loader的话只需要设置一次
     */
    fun setPlaceHolder(@DrawableRes id: Int) {
        requestBuilder = requestBuilder?.let { originBuilder ->
            val builder: (RequestBuilder<Drawable>.(size: IntSize) -> RequestBuilder<Drawable>) = {
                originBuilder.invoke(this, it)
                placeholder(id)
            }
            builder
        } ?: {
            placeholder(id)
        }
    }

    @Composable
    fun getRemotePainter(
        request: String
    ) = rememberGlidePainter(request = request, requestBuilder = requestBuilder)
}

