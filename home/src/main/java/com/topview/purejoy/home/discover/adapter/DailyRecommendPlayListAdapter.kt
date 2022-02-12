package com.topview.purejoy.home.discover.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ItemHomeDailyRecommendPlaylistBinding
import com.topview.purejoy.home.entity.PlayList

class DailyRecommendPlayListAdapter :
    BaseQuickAdapter<PlayList, BaseDataBindingHolder<ItemHomeDailyRecommendPlaylistBinding>>(
        R.layout.item_home_daily_recommend_playlist
    ) {

    private var clickListener: OnClickListener? = null

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeDailyRecommendPlaylistBinding>,
        item: PlayList
    ) {
        // 获取Binding
        val binding: ItemHomeDailyRecommendPlaylistBinding? = holder.dataBinding
        binding?.let {
            // 设置数据
            it.dailyRecommendPlayList = item
            // 设置监听器
            clickListener?.let { listener ->
                it.playListClickListener = View.OnClickListener {
                    listener.onPlayListClick(item)
                }
            }
        }
    }

    /**
     * 设置数据，调用该方法，会清除之前List的内容，并设置新内容
     * */
    fun setData(list: List<PlayList>) {
        setList(list)
    }

    fun setClickListener(listener: OnClickListener) {
        this.clickListener = listener
    }

    interface OnClickListener {
        fun onPlayListClick(playList: PlayList)
    }
}