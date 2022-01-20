package com.topview.purejoy.musiclibrary.recommendation.music.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonBindingAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.entity.SongWithReason


class DailyRecommendAdapter(
    var itemClickListener: DailyRecommendItemClickListener? = null,
    var buttonClickListener: DailyRecommendItemClickListener? = null,
    var mvClickListener: DailyRecommendItemClickListener? = null) :
    CommonBindingAdapter<SongWithReason,
            DailyRecommendAdapter.DailyHolder>(layoutId = R.layout.music_item) {



    class DailyHolder(viewDataBinding: ViewDataBinding) :
        CommonBindingAdapter.BindingHolder(viewDataBinding) {
        val button = itemView.findViewById<ImageView>(R.id.music_item_more_bt)
        val reasonTx = itemView.findViewById<TextView>(R.id.music_item_reason_tx)


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
            buttonClickListener?.onClick(item)
        }
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(item)
        }
        val iv = holder.getView<ImageView>(R.id.music_item_mv_bt)
        if (item.item.mv != -1L) {
            iv.visibility = View.VISIBLE
            iv.setOnClickListener {
                mvClickListener?.onClick(item)
            }
        } else {
            iv.visibility = View.GONE
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

    interface DailyRecommendItemClickListener {
        fun onClick(item: SongWithReason)
    }
}

