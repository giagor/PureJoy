package com.topview.purejoy.home.discover.adapter.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.topview.purejoy.common.widget.banner.BannerView
import com.topview.purejoy.home.discover.RecommendNewSongDecoration
import com.topview.purejoy.home.discover.adapter.DailyRecommendPlayListAdapter
import com.topview.purejoy.home.discover.adapter.RecommendNewSongAdapter
import com.topview.purejoy.home.entity.DailyRecommendPlayList
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem
import com.topview.purejoy.home.entity.RecommendNewSong

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

@BindingAdapter("recommendNewSongAdapter")
fun setRecommendNewSongAdapter(
    recyclerView: RecyclerView,
    adapter: RecommendNewSongAdapter
) {
    recyclerView.adapter = adapter
}

@BindingAdapter("dailyRecommendPlayList")
fun setDailyRecommendPlayList(
    recyclerView: RecyclerView,
    list: List<DailyRecommendPlayList>?
) {
    list?.let {
        val adapter = recyclerView.adapter as DailyRecommendPlayListAdapter
        adapter.setData(it)
    }
}

@BindingAdapter("recommendNewSong")
fun setRecommendNewSong(
    recyclerView: RecyclerView,
    list: List<RecommendNewSong>?
) {
    list?.let {
        val adapter = recyclerView.adapter as RecommendNewSongAdapter
        adapter.setData(it)
    }
}

@BindingAdapter("recommendNewSongDecoration")
fun setRecommendNewSongDecoration(
    recyclerView: RecyclerView,
    decoration: RecommendNewSongDecoration
) {
    recyclerView.addItemDecoration(decoration)
}

@BindingAdapter("attachSnapHelper")
fun attachSnapHelper(
    recyclerView: RecyclerView,
    snapHelper: SnapHelper
) {
    snapHelper.attachToRecyclerView(recyclerView)
}

