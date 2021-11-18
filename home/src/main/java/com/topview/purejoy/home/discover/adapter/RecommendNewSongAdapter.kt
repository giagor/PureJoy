package com.topview.purejoy.home.discover.adapter

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.common.util.getWindowWidth
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeRecommendNewSongBinding
import com.topview.purejoy.home.entity.RecommendNewSong
import com.topview.purejoy.home.util.Common.DISCOVER_RECOMMEND_NEW_SONG_ITEM_WIDTH_SMALLER_THAN_SCREEN

class RecommendNewSongAdapter :
    BaseQuickAdapter<RecommendNewSong, BaseDataBindingHolder<ItemHomeRecommendNewSongBinding>>(
        R.layout.item_home_recommend_new_song
    ) {

    override fun onItemViewHolderCreated(
        viewHolder: BaseDataBindingHolder<ItemHomeRecommendNewSongBinding>,
        viewType: Int
    ) {
        val binding: ItemHomeRecommendNewSongBinding? = viewHolder.dataBinding
        binding?.let {
            // 获取原来的View的Root的布局参数
            val sourcePrams = it.root.layoutParams
            // 使得item的宽度比屏幕宽度小一些
            it.root.layoutParams = RecyclerView.LayoutParams(sourcePrams).apply {
                width =
                    getWindowWidth(it.root.context) - DISCOVER_RECOMMEND_NEW_SONG_ITEM_WIDTH_SMALLER_THAN_SCREEN
            }
        }
    }

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