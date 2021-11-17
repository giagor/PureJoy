package com.topview.purejoy.home.discover.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeRecommendNewSongBinding
import com.topview.purejoy.home.entity.RecommendNewSong

class RecommendNewSongAdapter :
    BaseQuickAdapter<RecommendNewSong, BaseDataBindingHolder<ItemHomeRecommendNewSongBinding>>(
        R.layout.item_home_recommend_new_song
    ) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeRecommendNewSongBinding>,
        item: RecommendNewSong
    ) {
        // 获取Binding
        val binding: ItemHomeRecommendNewSongBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.recommendNewSong = item
        }
    }

    /**
     * 设置数据，调用该方法，会清除之前List的内容，并设置新内容
     * */
    fun setData(list: List<RecommendNewSong>) {
        setList(list)
    }
}