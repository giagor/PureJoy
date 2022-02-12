package com.topview.purejoy.common.mvvm.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("commonLoadImg")
fun loadImg(iv: ImageView, url: String?) {
    url?.let {
        Glide.with(iv.context)
            .load(it)
            .apply(RequestOptions().override(iv.width, iv.height))
            .into(iv)
    }
}