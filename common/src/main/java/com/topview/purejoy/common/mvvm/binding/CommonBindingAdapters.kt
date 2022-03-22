package com.topview.purejoy.common.mvvm.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.topview.purejoy.common.component.image.ImageUtil

@BindingAdapter("commonLoadImg")
fun loadImg(iv: ImageView, url: String?) {
    url?.let {
        ImageUtil.getImageLoader().loadImage(
            context = iv.context,
            url = it,
            width = iv.width,
            height = iv.height,
            iv = iv
        )
    }
}