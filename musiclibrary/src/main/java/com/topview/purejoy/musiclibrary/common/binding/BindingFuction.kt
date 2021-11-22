package com.topview.purejoy.musiclibrary.common.binding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.topview.purejoy.musiclibrary.common.util.buildSwatch
import jp.wasabeef.glide.transformations.BlurTransformation

@BindingAdapter(value = ["imageUrl", "holder", "error"])
fun setImageUrl(view: ImageView, imageUrl: String, holder: Drawable?, error: Drawable?) {
    Glide.with(view.context).asBitmap().load(imageUrl).placeholder(holder).error(error).into(view)
}

@BindingAdapter(value = ["colorUrl"])
fun setColorUrl(view: View, colorUrl: String) {
    Glide.with(view.context).asBitmap().load(colorUrl).into(object : CustomTarget<Bitmap> () {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            buildSwatch(resource) {
                view.setBackgroundColor(it)
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {

        }

    })

}

@BindingAdapter(value = ["blurUrl", "blurRadius", "blurSample"])
fun setBlurUrlBg(view: View, blurUrl: String, blurRadius: Int = 13, blurSample: Int = 1) {
    Glide.with(view.context).asDrawable().apply(
        RequestOptions.bitmapTransform(BlurTransformation(25, 1)))
        .load(blurUrl).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                view.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        })
}

