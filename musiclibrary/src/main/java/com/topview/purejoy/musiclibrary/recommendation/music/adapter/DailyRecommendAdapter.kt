package com.topview.purejoy.musiclibrary.recommendation.music.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonBindingAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.entity.SongWithReason



class DailyRecommendAdapter() :
    CommonBindingAdapter<SongWithReason,
            DailyRecommendAdapter.DailyHolder>(layoutId = R.layout.layout_music_recommend_item) {

    class DailyHolder(viewDataBinding: ViewDataBinding) :
        CommonBindingAdapter.BindingHolder(viewDataBinding) {
        val button = itemView.findViewById<ImageButton>(R.id.music_daily_recommend_item_more_bt)
        val reasonTx = itemView.findViewById<TextView>(R.id.music_daily_recommend_item_reason_tx)


        override fun variableId(): Int {
            return BR.musicItem
        }
    }

    override fun convert(holder: DailyHolder, item: SongWithReason) {
        if (item.reason == null) {
            holder.reasonTx.visibility = View.GONE
        } else {
            holder.reasonTx.text = item.reason
            holder.reasonTx.visibility = View.VISIBLE
        }
        holder.button.setOnClickListener {
            // 弹出弹窗
        }
        holder.bind(item.item)
    }

    override fun createBindingHolder(
        parent: ViewGroup,
        layoutResId: Int,
        binding: ViewDataBinding
    ): DailyHolder {
        return DailyHolder(binding)
    }
}

