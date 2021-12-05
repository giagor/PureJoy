package com.topview.purejoy.home.search.content.song.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeSearchSongBinding
import com.topview.purejoy.home.entity.Song

class SearchContentSongAdapter :
    BaseQuickAdapter<Song, BaseDataBindingHolder<ItemHomeSearchSongBinding>>(R.layout.item_home_search_song),
    LoadMoreModule {

    override fun convert(holder: BaseDataBindingHolder<ItemHomeSearchSongBinding>, item: Song) {
        // 获取Binding
        val binding: ItemHomeSearchSongBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.searchSong = item
        }
    }
}