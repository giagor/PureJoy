package com.topview.purejoy.musiclibrary.recommendation.music.view

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.recommendation.music.adapter.DailyRecommendAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.viemmodel.DailySongsViewModel

class DailyRecommendActivity : CommonActivity() {

    override fun getLayoutId(): Int = R.layout.coordinatorlayout_activity_daily_recommend
    private val viewModel by lazy {
        ViewModelProvider(this, DefaultFactory.getInstance()).get(DailySongsViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageView = findViewById<ImageView>(R.id.music_daily_recommend_iv)
        val toolbar = findViewById<Toolbar>(R.id.music_daily_recommend_tool_bar)
        toolbar.setTitle(R.string.daily_recommend)
        val layout = findViewById<LinearLayout>(R.id.music_daily_recommend_play_all_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.music_daily_recommend_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = DailyRecommendAdapter()
        recyclerView.adapter = adapter
        viewModel.data.observe(this, {
            adapter.dailySongs = it
            adapter.notifyItemRangeChanged(0, it.dailySongs.size)
            Glide.with(this).asBitmap().error(R.drawable.white_holder)
                .placeholder(R.drawable.white_holder).load(it.dailySongs[0].al.picUrl).into(imageView)
        })
        layout.setOnClickListener {
            // 播放全部歌曲
        }
        viewModel.requestDailySongs()
    }
}