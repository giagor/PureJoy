package com.topview.purejoy.home.discover

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.topview.purejoy.common.widget.banner.BannerView
import com.topview.purejoy.home.discover.adapter.DailyRecommendPlayListAdapter
import com.topview.purejoy.home.discover.adapter.RecommendNewSongAdapter
import com.topview.purejoy.home.entity.HomeDiscoverBannerItem
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.Song

@BindingAdapter("loadBanners")
fun loadBanners(bannerView: BannerView, items: List<HomeDiscoverBannerItem>?) {
    items?.let {
        bannerView.setBanners(it)
    }
}

@BindingAdapter("dailyRecommendPlayList")
fun setDailyRecommendPlayList(
    recyclerView: RecyclerView,
    list: List<PlayList>?
) {
    list?.let {
        val adapter = recyclerView.adapter as DailyRecommendPlayListAdapter
        adapter.setData(it)
    }
}

@BindingAdapter("recommendNewSong")
fun setRecommendNewSong(
    recyclerView: RecyclerView,
    list: List<Song>?
) {
    list?.let {
        val adapter = recyclerView.adapter as RecommendNewSongAdapter
        adapter.setData(it)
    }
}

@BindingAdapter("itemDecoration")
fun setItemDecoration(
    recyclerView: RecyclerView,
    decoration: RecyclerView.ItemDecoration
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

@BindingAdapter("compoundDrawable")
fun setTextViewIc(
    textView: TextView,
    drawable: Drawable
) {
    textView.setCompoundDrawables(drawable, null, null, null)
}
