package com.topview.purejoy.home.entity

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.common.widget.banner.BannerItem
import com.topview.purejoy.home.R
import com.topview.purejoy.home.util.LinkUtil

data class HomeDiscoverBannerItem(val imgUrl: String?, val link: String?) : BannerItem {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): View {
        val view = inflater.inflate(
            R.layout.common_banner_item_card, parent,
            false
        )
        val imageView: ImageView = view.findViewById(R.id.common_iv_banner_card_image)
        imgUrl?.let {
            Glide.with(imageView.context as Activity)
                .load(it)
                .apply(RequestOptions().override(imageView.width, imageView.height))
                .into(imageView)
        }
        // 展示轮播图的网页内容
        imageView.setOnClickListener { iv ->
            LinkUtil.executeLink(link, {
                CommonRouter.routeToWebViewActivity(it)
            }, {
                val invalidUrlTips =
                    iv.context.resources.getString(R.string.home_banner_invalid_url_tips)
                showToast(iv.context, invalidUrlTips)
            })
        }
        return view
    }
}