package com.topview.purejoy.common.component.image

import android.content.Context
import android.widget.ImageView

interface ImageLoader {
    /**
     * @param context 图片运行的 Context 环境
     * @param url 图片加载的 url
     * @param width 图片的宽度
     * @param height 图片的高度
     * @param iv 加载到哪个 ImageView 上面
     * */
    fun loadImage(
        context: Context,
        url: String,
        width: Int? = null,
        height: Int? = null,
        iv: ImageView
    )
}