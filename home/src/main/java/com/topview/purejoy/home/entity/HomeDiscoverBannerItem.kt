package com.topview.purejoy.home.entity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.widget.banner.BannerItem
import com.topview.purejoy.home.R

data class HomeDiscoverBannerItem(val imgUrl: String?, val link: String?) : BannerItem {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): View {
        val view = inflater.inflate(
            R.layout.common_banner_item_card, parent,
            false
        )
        val imageView: ImageView = view.findViewById(R.id.common_iv_banner_card_image)
        imgUrl?.let {
            Glide.with(CommonApplication.getContext())
                .load(it)
                .placeholder(R.drawable.home_ic_bottom_navi_mine)
                .error(R.drawable.home_bg_bottom_navi_item)
                .into(imageView)
        }
        return view
    }
}