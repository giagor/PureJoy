package com.topview.purejoy.home.search.content.playlist.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeSearchPlaylistBinding
import com.topview.purejoy.home.entity.PlayList

class SearchContentPlayListAdapter :
    BaseQuickAdapter<PlayList, BaseDataBindingHolder<ItemHomeSearchPlaylistBinding>>(R.layout.item_home_search_playlist),
    LoadMoreModule {
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeSearchPlaylistBinding>,
        item: PlayList
    ) {
        // 获取Binding
        val binding: ItemHomeSearchPlaylistBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.playlist = item
        }
    }
}