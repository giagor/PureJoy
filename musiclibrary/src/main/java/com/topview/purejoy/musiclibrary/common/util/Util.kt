package com.topview.purejoy.musiclibrary.common.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.instance.BinderPoolClientInstance
import com.topview.purejoy.musiclibrary.player.client.BinderPoolClient
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * 获取图片主题色的方法
 * @param bitmap 从这个Bitmap获取主题色
 * @param view 要设置背景色的View
 */
fun buildSwatch(bitmap: Bitmap, action: (Int) -> Unit) {
    Palette.Builder(bitmap).generate {
        it?.apply {
            val s = when {
                lightMutedSwatch != null -> {
                    lightMutedSwatch
                }
                lightVibrantSwatch != null -> {
                    lightVibrantSwatch
                }
                vibrantSwatch != null -> {
                    vibrantSwatch
                }
                vibrantSwatch != null -> {
                    vibrantSwatch
                }
                darkMutedSwatch != null -> {
                    darkMutedSwatch
                }
                darkVibrantSwatch != null -> {
                    darkVibrantSwatch
                }
                else -> {
                    mutedSwatch
                }
            }
            val c = s?.rgb ?: R.color.wathet
//                    view.setBackgroundColor(c)
            action.invoke(c)

        }
    }

}

/**
 * 使用经过高斯模糊处理的图片做背景
 */
fun loadBlurBackground(view: View, url: String, blurRadius: Int = 25, blurSample: Int = 16) {
    Glide.with(view.context).asDrawable().load(url)
        .apply(RequestOptions.bitmapTransform(BlurTransformation(blurRadius, blurSample)))
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                view.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        })
}

fun loadBitmapColor(url: String, action: (Int) -> Unit) {
    Glide.with(CommonApplication.getContext()).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            buildSwatch(resource, action)
        }

        override fun onLoadCleared(placeholder: Drawable?) {

        }

    })
}

/**
 * CommonActivity的拓展方法，为了方便连接音乐服务和获取服务客户端的单例
 */
fun CommonActivity.getAndConnectService(clazz: Class<*>): BinderPoolClient {
    val client = BinderPoolClientInstance.getInstance().getClient(clazz)
    client.connectService()
    return client
}


fun CommonActivity.getDisplaySize(): Rect {
    val rect = Rect()
    rect.left = 0
    rect.top = 0
    val metrics = resources.displayMetrics
    rect.right = metrics.widthPixels
    rect.bottom = metrics.heightPixels
    return rect
}

/**
 * 将数字转换为时间格式的字符串
 */
fun numToString(num: Int): String {
    val minute = num / 1000 / 60
    var n = num - minute * 1000 * 60
    val second = n / 1000
    n -= second * 1000
    val builder = StringBuilder()
    if (minute < 10) {
        builder.append(0)
    }
    builder.append(minute).append(":")
    if (second < 10) {
        builder.append(0)
    }
    builder.append(second)

    return builder.toString()

}


fun loadBitmap(view: ImageView, url: String?,
               holder: Int = R.drawable.white_holder,
               error: Int = R.drawable.white_holder) {
    Glide.with(view.context).asBitmap().placeholder(holder).error(error).load(url).into(view)
}