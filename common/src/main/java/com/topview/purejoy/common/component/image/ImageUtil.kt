package com.topview.purejoy.common.component.image

import com.topview.purejoy.common.component.image.impl.GlideImageLoader

object ImageUtil {
    private val imageLoader: ImageLoader = GlideImageLoader()

    fun getImageLoader(): ImageLoader = imageLoader
}