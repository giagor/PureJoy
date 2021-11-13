package com.topview.purejoy.musiclibrary.recommendation.music.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topview.purejoy.common.widget.RoundImageView
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonAdapter
import com.topview.purejoy.musiclibrary.common.adapter.DataBindingAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongs

@BindingAdapter(value = ["imageUrl", "holder", "error"])
fun setImageUrl(view: ImageView, imageUrl: String, holder: Drawable?, error: Drawable?) {
    Glide.with(view.context).asBitmap().load(imageUrl).placeholder(holder).error(error).into(view)
}

class DailyRecommendAdapter(
    var dailySongs: DailySongs = DailySongs(dailySongs = mutableListOf(),
        recommendReasons = mutableListOf())) :
    DataBindingAdapter<DailyRecommendAdapter.DailyRecommendHolder>() {
    override fun variableId(viewType: Int): Int {
        return BR.musicItem
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.layout_music_recommend_item
    }

    override fun getItem(position: Int): Any {
        return dailySongs.dailySongs[position]
    }

    override fun getItemCount(): Int {
        return dailySongs.dailySongs.size
    }

    override fun onBindViewHolder(holder: DailyRecommendHolder, position: Int) {
        holder.layout.setOnClickListener {
            // 播放全部歌曲
        }
        holder.button.setOnClickListener {
            // 弹窗
        }
        holder.reasonTx.apply {
            val s = findReason(dailySongs.dailySongs[position].id)
            if (s.isEmpty()) {
                visibility = View.GONE
            } else {
                text = s
                visibility = View.VISIBLE
            }
        }
        super.onBindViewHolder(holder, position)
    }

    private fun findReason(id: Long): String {
        for (r in dailySongs.recommendReasons) {
            if (r.songId == id) {
                return r.reason
            }
        }
        return ""
    }

    class DailyRecommendHolder(viewDataBinding: ViewDataBinding) : BindingHolder(viewDataBinding) {
        val layout = itemView.findViewById<LinearLayout>(R.id.music_daily_recommend_item_layout)
        val reasonTx = itemView.findViewById<TextView>(R.id.music_daily_recommend_item_reason_tx)
        val button = itemView.findViewById<ImageButton>(R.id.music_daily_recommend_item_more_bt)
    }

    override fun createHolder(viewBinding: ViewDataBinding): DailyRecommendHolder {
        return DailyRecommendHolder(viewBinding)
    }
}
