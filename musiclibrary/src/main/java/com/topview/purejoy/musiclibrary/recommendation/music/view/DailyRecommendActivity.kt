package com.topview.purejoy.musiclibrary.recommendation.music.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.common.music.view.bottom.MusicController
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.music.util.getDisplaySize
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.util.DownloadUtil
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.NoBindingActivity
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.util.loadBitmapColor
import com.topview.purejoy.musiclibrary.recommendation.music.adapter.DailyRecommendAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.entity.SongWithReason
import com.topview.purejoy.musiclibrary.recommendation.music.entity.toWrapperList
import com.topview.purejoy.musiclibrary.recommendation.music.pop.RecommendPop
import com.topview.purejoy.musiclibrary.recommendation.music.viemmodel.DailySongsViewModel
import com.topview.purejoy.musiclibrary.router.MusicLibraryRouter
import com.topview.purejoy.video.VideoPlayerLauncher

@Route(path = MusicLibraryRouter.ACTIVITY_MUSIC_LIBRARY_DAILY_RECOMMEND)
class DailyRecommendActivity : NoBindingActivity<DailySongsViewModel>() {

    private val musicController: MusicController = MusicController()

    private val bottomView: MusicBottomView by lazy {
        MusicBottomView(this, musicController)
    }

    override fun getLayoutId(): Int = R.layout.coordinatorlayout_activity_daily_recommend

    private val popWindow: RecommendPop by lazy {
        val size = getDisplaySize()
        val p = RecommendPop(
            this, size.width(),
            size.height() * 2 / 3, window
        )
        p.addItemView(R.drawable.next_play_pop_32, R.string.next_play) { item ->
            item?.let {
                val w = it.wrap()
                if (musicController.currentItem.value == null) {
                    musicController.dataController?.add(w)
                    musicController.playerController?.jumpTo(0)
                } else {
                    val index =
                        musicController.playItems.value?.indexOf(musicController.currentItem.value)
                    if (index != null && index != -1) {
                        musicController.dataController?.addAfter(w, index)
                    }
                }
                popWindow.window.dismiss()
            }
        }
        p.addItemView(R.drawable.download_32, R.string.download) {
            // 下载
            it?.let { item ->
                if (item.url != null) {
                    downloadMusic(item.name, item.url!!)
                    popWindow.window.dismiss()
                } else {
                    viewModel.requestURL(item.id)
                }
            }
            popWindow.window.dismiss()
        }
        p
    }


    private val TAG = "DailyRecommend"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root: CoordinatorLayout = findViewById(R.id.music_daily_recommend_layout)
        val imageView = findViewById<ImageView>(R.id.music_daily_recommend_iv)
        val toolbar = findViewById<Toolbar>(R.id.music_daily_recommend_tool_bar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setTitle(R.string.daily_recommend)
        val layout = findViewById<LinearLayout>(R.id.music_daily_recommend_play_all_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.music_daily_recommend_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = DailyRecommendAdapter(itemClickListener = object :
            DailyRecommendAdapter.DailyRecommendItemClickListener {
            override fun onClick(item: SongWithReason) {
                musicController.dataController?.clear()
                musicController.dataController?.addAll(viewModel.data.value!!.toWrapperList())
                musicController.playerController?.jumpTo(viewModel.data.value!!.indexOf(item))
                CommonRouter.routeToPlayingActivity()
            }
        }, buttonClickListener = object : DailyRecommendAdapter.DailyRecommendItemClickListener {
            override fun onClick(item: SongWithReason) {
                popWindow.data = item.item
                popWindow.showDownAt(root)
            }
        }, mvClickListener = object : DailyRecommendAdapter.DailyRecommendItemClickListener {
            override fun onClick(item: SongWithReason) {
                if (musicController.playerController?.isPlaying == true) {
                    musicController.playerController?.playOrPause()
                }
                VideoPlayerLauncher.launch(true, item.item.mv.toString())
            }

        })
        recyclerView.adapter = adapter
        viewModel.data.observe(this, {
            if (it != null) {
                adapter.data.clear()
                adapter.data.addAll(it)
                adapter.notifyItemRangeChanged(0, adapter.data.size)
                loadBitmapColor(it[0].item.al.picUrl) { color ->
                    imageView.setImageDrawable(ColorDrawable(color))
                }

            } else {
                showToast(context = this, text = "请求错误", duration = Toast.LENGTH_SHORT)
                finish()
            }
        })
        viewModel.urlResponse.observe(this) {
            it?.let { response ->
                response.data.forEach { item ->
                    var music: MusicItem? = null
                    for (m in adapter.data) {
                        if (m.item.id == item.id) {
                            music = m.item
                            break
                        }
                    }
                    if (item.url.isEmpty()) {
                        showToast(this, "歌曲${music?.name}不能下载", Toast.LENGTH_SHORT)
                    } else {
                        music?.url = item.url
                        music?.let { musicItem ->
                            downloadMusic(musicItem.name, item.url)
                        }

                        if (item.id == popWindow.data?.id) {
                            popWindow.window.dismiss()
                        }
                    }
                }
            }
        }
        layout.setOnClickListener {
            // 播放全部歌曲
            if (viewModel.data.value?.isNotEmpty() == true) {
                musicController.dataController?.clear()
                musicController.dataController?.addAll(viewModel.data.value!!.toWrapperList())
                musicController.playerController?.jumpTo(0)
            }
        }
        viewModel.requestDailySongs()
        bottomView.addMusicBottomBar()
    }

    private fun downloadMusic(fileName: String, url: String) {
        DownloadUtil.downloadMusic(
            activity = this,
            name = fileName,
            url = url,
            permissionAllowed = {
                showToast(this, "已加入下载任务队列", Toast.LENGTH_SHORT)
            },
            permissionDenied = {
                showToast(this, "拒绝权限将无法下载", Toast.LENGTH_SHORT)
            })
    }

    override fun onDestroy() {
        if (popWindow.window.isShowing) {
            popWindow.window.dismiss()
        }
        super.onDestroy()
    }

    override fun getViewModelClass(): Class<DailySongsViewModel> {
        return DailySongsViewModel::class.java
    }

    override fun createFactory(): ViewModelProvider.Factory {
        return DefaultFactory.getInstance()
    }
}