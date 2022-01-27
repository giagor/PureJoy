package com.topview.purejoy.home.search

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.search.content.playlist.adapter.SearchContentPlayListAdapter
import com.topview.purejoy.home.search.content.song.adapter.SearchContentSongAdapter

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

/**
 * 设置歌单的 歌曲数量、播放数量 信息
 * */
@SuppressLint("SetTextI18n")
@BindingAdapter("playListCountsInfo")
fun setPlayListCountsInfo(textView: TextView, playList: PlayList) {
    textView.text = "${playList.songCounts}首，播放${playList.playCount}次"
}

/**
 * 是否显示MV图标
 * */
@BindingAdapter("mvVisibility")
fun setMvVisibility(iv: ImageView, song: Song) {
    if (song.mvId == null) {
        iv.visibility = View.INVISIBLE
        return
    }

    song.mvId?.let {
        if (it == 0L) {
            iv.visibility = View.INVISIBLE
        } else {
            iv.visibility = View.VISIBLE
        }
    }
}