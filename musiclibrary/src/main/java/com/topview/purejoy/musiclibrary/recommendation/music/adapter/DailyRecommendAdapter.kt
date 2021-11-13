package com.topview.purejoy.musiclibrary.recommendation.music.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topview.purejoy.common.widget.RoundImageView
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongs

class DailyRecommendAdapter(
    var dailySongs: DailySongs = DailySongs(
        dailySongs = mutableListOf(),
        recommendReasons = mutableListOf())
) : CommonAdapter<DailyRecommendAdapter.DailyRecommendHolder>() {

    class DailyRecommendHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.music_daily_recommend_item_layout)
        val imageView: RoundImageView = view.findViewById(R.id.music_daily_recommend_item_iv)
        val name: TextView = view.findViewById(R.id.music_daily_recommend_item_name_tx)
        val reason: TextView = view.findViewById(R.id.music_daily_recommend_item_reason_tx)
        val author: TextView = view.findViewById(R.id.music_daily_recommend_item_author_tx)
        val button: ImageButton = view.findViewById(R.id.music_daily_recommend_item_more_bt)
    }

    override fun layoutId(): Int {
        return R.layout.linearlayout_music_recommend_item
    }

    override fun createViewHolder(root: View): DailyRecommendHolder {
        return DailyRecommendHolder(root)
    }

    override fun onBindViewHolder(holder: DailyRecommendHolder, position: Int) {
        val item = dailySongs.dailySongs[position]
        if (position >= dailySongs.recommendReasons.size) {
            holder.reason.visibility = View.GONE
        } else {
            holder.reason.text = dailySongs.recommendReasons[position].reason
            holder.reason.visibility = View.VISIBLE
        }
        Glide.with(holder.itemView.context).asBitmap()
            .error(R.drawable.white_holder).placeholder(R.drawable.white_holder)
            .load(item.al.picUrl).into(holder.imageView)
        holder.name.text = item.al.name
        holder.author.text = item.getAuthors()
        holder.layout.setOnClickListener {
            // 播放当前歌曲
        }
        holder.button.setOnClickListener {
            // 弹窗
        }
    }

    override fun getItemCount(): Int {
        return dailySongs.dailySongs.size
    }
}