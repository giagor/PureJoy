package com.topview.purejoy.home.discover.adapter.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.topview.purejoy.common.widget.banner.BannerView
import com.topview.purejoy.home.discover.adapter.DailyRecommendPlayListAdapter
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem

@BindingAdapter("loadBanners")
fun loadBanners(bannerView: BannerView, items: List<HomeDiscoverBannerItem>?) {
    items?.let {
        bannerView.setBanners(it)
    }
}

@BindingAdapter("loadImg")
fun loadImg(iv: ImageView, url: String?) {
    url?.let {
        Glide.with(iv.context)
            .load(it)
            .apply(RequestOptions().override(iv.width, iv.height))
            .into(iv)
    }
}

@BindingAdapter("dailyRecommendPlayListAdapter")
fun setDailyRecommendPlayListAdapter(
    recyclerView: RecyclerView,
    adapter: DailyRecommendPlayListAdapter
) {
    recyclerView.adapter = adapter
}

@BindingAdapter("dailyRecommendPlayList")
fun setDailyRecommendRecommendPlayList(
    recyclerView: RecyclerView,
    list: List<DailyRecommendPlayList>?
) {
    list?.let {
        val adapter = recyclerView.adapter as DailyRecommendPlayListAdapter
        adapter.setData(list)
    }
}

