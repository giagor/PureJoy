package com.topview.purejoy.home.discover.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.common.util.getWindowWidth
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeRecommendNewSongBinding
import com.topview.purejoy.home.entity.Song

class RecommendNewSongAdapter :
    BaseQuickAdapter<Song, BaseDataBindingHolder<ItemHomeRecommendNewSongBinding>>(
        R.layout.item_home_recommend_new_song
    ) {

    private var clickListener: ClickListener? = null

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
                val itemWidthSmallerThanScreen = it.root.context.resources
                    .getDimension(R.dimen.home_discover_recommend_new_song_item_width_smaller_than_screen)
                width =
                    (getWindowWidth(it.root.context) - itemWidthSmallerThanScreen).toInt()
            }
        }
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeRecommendNewSongBinding>,
        item: Song
    ) {
        // 获取Binding
        val binding: ItemHomeRecommendNewSongBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.recommendNewSong = item
            // 设置监听器
            clickListener?.let { listener ->
                it.songClickListener = View.OnClickListener {
                    listener.onRecommendNewSongClick(item)
                }
            }
        }
    }

    /**
     * 设置数据，调用该方法，会清除之前List的内容，并设置新内容
     * */
    fun setData(list: List<Song>) {
        setList(list)
    }

    fun setClickListener(listener: ClickListener) {
        this.clickListener = listener
    }

    interface ClickListener {
        fun onRecommendNewSongClick(song: Song)
    }
}