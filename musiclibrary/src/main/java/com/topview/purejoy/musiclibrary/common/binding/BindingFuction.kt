package com.topview.purejoy.musiclibrary.common.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter(value = ["imageUrl", "holder", "error"])
fun setImageUrl(view: ImageView, imageUrl: String, holder: Drawable? = null, error: Drawable? = null) {
    Glide.with(view.context).asBitmap().load(imageUrl).placeholder(holder).error(error).into(view)
}





