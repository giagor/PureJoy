package com.topview.purejoy.home.search

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.home.entity.Song
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


