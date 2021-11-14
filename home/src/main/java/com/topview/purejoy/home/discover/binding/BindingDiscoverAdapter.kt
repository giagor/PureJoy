package com.topview.purejoy.home.discover.binding

import android.util.Log
import androidx.databinding.BindingAdapter
import com.topview.purejoy.common.widget.banner.BannerView
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem

@BindingAdapter("loadBanners")
fun BannerView.loadBanners(items: List<HomeDiscoverBannerItem>?) {
    items?.let { 
        setBanners(it)
    }
}