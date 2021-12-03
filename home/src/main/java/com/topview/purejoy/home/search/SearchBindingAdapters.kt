package com.topview.purejoy.home.search

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.search.content.playlist.adapter.SearchContentPlayListAdapter
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter

@BindingAdapter("searchSongAdapter")
fun setSearchContentSongAdapter(recyclerView: RecyclerView, adapter: SearchContentSongAdapter) {
    recyclerView.adapter = adapter
}

@BindingAdapter("searchSongsByFirstRequest")
fun setSearchSongsByFirstRequest(recyclerView: RecyclerView, list: List<Song>?) {
    list?.let {
        val adapter = recyclerView.adapter as SearchContentSongAdapter
        adapter.setList(list)
    }
}

@BindingAdapter("loadMoreSongs")
fun loadMoreSongs(recyclerView: RecyclerView, list: List<Song>?) {
    list?.let {
        val adapter = recyclerView.adapter as SearchContentSongAdapter
        adapter.addData(it)
        // 通知"加载更多"已完成
        adapter.loadMoreModule.loadMoreComplete()
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

@BindingAdapter("searchPlayListsByFirstRequest")
fun setSearchPlayListsByFirstRequest(recyclerView: RecyclerView, list: List<PlayList>?) {
    list?.let {
        val adapter = recyclerView.adapter as SearchContentPlayListAdapter
        adapter.setList(list)
    }
}

@BindingAdapter("loadMorePlayLists")
fun loadMorePlayLists(recyclerView: RecyclerView, list: List<PlayList>?) {
    list?.let {
        val adapter = recyclerView.adapter as SearchContentPlayListAdapter
        adapter.addData(it)
        // 通知"加载更多"已完成
        adapter.loadMoreModule.loadMoreComplete()
    }
}