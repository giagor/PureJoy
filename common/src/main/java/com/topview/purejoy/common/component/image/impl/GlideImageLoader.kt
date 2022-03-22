package com.topview.purejoy.common.component.image.impl

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.topview.purejoy.common.component.image.ImageLoader

class GlideImageLoader : ImageLoader {
    override fun loadImage(
        context: Context,
        url: String,
        width: Int?,
        height: Int?,
        iv: ImageView
    ) {
        var builder: RequestBuilder<Drawable> = Glide.with(context).load(url)
        if (width != null && height != null) {
            builder = builder.apply(RequestOptions().override(width, height))
        }
        builder.into(iv)
    }
}