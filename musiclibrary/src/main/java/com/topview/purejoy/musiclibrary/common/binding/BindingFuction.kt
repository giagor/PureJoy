package com.topview.purejoy.musiclibrary.common.binding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.topview.purejoy.musiclibrary.common.util.buildSwatch

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