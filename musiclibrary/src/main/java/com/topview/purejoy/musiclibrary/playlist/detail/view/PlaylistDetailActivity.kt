package com.topview.purejoy.musiclibrary.playlist.detail.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.music.util.getDisplaySize
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.common.music.view.bottom.MusicController
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.util.DownloadUtil
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.NoBindingActivity
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.util.loadBitmapColor
import com.topview.purejoy.musiclibrary.playlist.detail.adapter.PlaylistDetailAdapter
import com.topview.purejoy.musiclibrary.playlist.detail.viewmodel.PlaylistDetailViewModel
import com.topview.purejoy.musiclibrary.recommendation.music.pop.RecommendPop
import com.topview.purejoy.musiclibrary.router.MusicLibraryRouter
import com.topview.purejoy.video.VideoPlayerLauncher

@Route(path = MusicLibraryRouter.ACTIVITY_PLAYLIST_DETAIL)
class PlaylistDetailActivity : NoBindingActivity<PlaylistDetailViewModel>() {
    private val musicController: MusicController = MusicController()
    private val bottomView: MusicBottomView by lazy {
        MusicBottomView(this, musicController)
    }

    private val popWindow: RecommendPop by lazy {
        val size = getDisplaySize()
        val p = RecommendPop(
            this, width = size.width(),
            height = size.height() * 2 / 3, window
        )
        p.addItemView(R.drawable.next_play_pop_32, R.string.next_play) { item ->
            item?.let {
                val w = it.wrap()
                val items = viewModel.songsResponse.value!!.songs
                if (items.isEmpty()) {
                    musicController.dataController?.add(w)
                    musicController.playerController?.jumpTo(0)
                } else {
                    val index = items.indexOf(musicController.currentItem.value)
                    if (index != -1) {
                        musicController.dataController?.addAfter(w, index)
                    } else {
                        musicController.dataController?.clear()
                        musicController.dataController?.add(w)
                        musicController.playerController?.jumpTo(0)
                    }
                }
                popWindow.window.dismiss()
            }
        }
        p.addItemView(R.drawable.download_32, R.string.download) {
            // ??????
            it?.let { item ->
                if (item.url != null) {
                    downloadMusic(item.name, item.url!!)
                    popWindow.window.dismiss()
                } else {
                    viewModel.requestUrl(item.id)
                }
            }
            popWindow.window.dismiss()
        }
        p
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playlistId: Long? = intent.getBundleExtra(CommonRouter.BUNDLE_EXTRA)?.getLong(
            MusicLibraryRouter.PLAYLIST_EXTRA, -1L
        )
        if (playlistId == null || playlistId == -1L) {
            errorHandle()
            return
        }
        val root: CoordinatorLayout = findViewById(R.id.playlist_root_layout)
        val toolBar: Toolbar = findViewById(R.id.playlist_detail_tool_bar)
        toolBar.setNavigationOnClickListener {
            finish()
        }
        toolBar.setTitle(R.string.playlist_detail)
        val layout: LinearLayout = findViewById(R.id.playlist_detail_msg_layout)
        val coverImg: RoundedCornerImageView = findViewById(R.id.playlist_detail_cover_iv)
        val playlistNameTx: TextView = findViewById(R.id.playlist_detail_name)
        val descriptionTx: TextView = findViewById(R.id.playlist_detail_description_tx)
        val playAllLayout: LinearLayout = findViewById(R.id.playlist_detail_play_all_layout)
        val countTx: TextView = findViewById(R.id.playlist_detail_song_count_tv)
        val recyclerView: RecyclerView = findViewById(R.id.playlist_detail_songs_rv)
        val footLayout: View = LayoutInflater.from(this).inflate(
            R.layout.loading_foot_view, null)
        val adapter = PlaylistDetailAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.response.observe(this) {
            if (it == null) {
                errorHandle()
            } else {
                loadBitmapColor(it.playlist.coverImgUrl) { color ->
                    layout.background = ColorDrawable(color)
                }
                coverImg.loadImageRequest = it.playlist.coverImgUrl
                playlistNameTx.text = it.playlist.name
                descriptionTx.text = it.playlist.description
                val t = "(${it.playlist.trackCount})"
                countTx.text = t
                viewModel.requestPLSongWithPage(playlistId, adapter.data.size)
            }
        }

        viewModel.urlResponse.observe(this) {
            if (it == null) {
                showToast(this, "????????????", Toast.LENGTH_SHORT)
            } else {
                it.data.forEach { item ->
                    var music: MusicItem? = null
                    for (m in adapter.data) {
                        if (m.id == item.id) {
                            music = m
                            break
                        }
                    }
                    if (item.url.isEmpty()) {
                        showToast(this, "??????${music?.name}????????????", Toast.LENGTH_SHORT)
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

        playAllLayout.setOnClickListener {
            handleClick(0, adapter.data)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    !recyclerView.canScrollVertically(1)) {
                    val b = viewModel.loadItems
                    viewModel.requestPLSongWithPage(playlistId, adapter.data.size)
                    if (viewModel.loadItems && !b) {
                        adapter.addFooterView(footLayout)
                    }
                }

            }
        })

        adapter.itemClickListener = object : DataClickListener<MusicItem> {
            override fun onClick(value: MusicItem, position: Int) {
                handleClick(position, adapter.data)
            }
        }

        adapter.buttonClickListener = object : DataClickListener<MusicItem> {
            override fun onClick(value: MusicItem, position: Int) {
                popWindow.data = value
                popWindow.showDownAt(root)
            }
        }

        adapter.mvClickListener = object : DataClickListener<MusicItem> {
            override fun onClick(value: MusicItem, position: Int) {
                if (musicController.playerController?.isPlaying == true) {
                    musicController.playerController?.playOrPause()
                }
                VideoPlayerLauncher.launch(true, value.mv.toString())
            }

        }

        viewModel.songsResponse.observe(this) {
            if (it == null) {
                showToast(context = this, text = "Unknown Error")
            } else {
                val items = ArrayList<MusicItem>(it.songs)
                val iterator = items.listIterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (adapter.data.contains(item)) {
                        iterator.remove()
                    }
                }
                val insert = adapter.data.size
                adapter.data.addAll(items)
                adapter.notifyItemRangeInserted(insert, items.size)
            }
            adapter.removeFooterView(footLayout)
        }
        bottomView.addMusicBottomBar(marginBottom = 0)
        viewModel.getDetails(playlistId)


    }

    private fun downloadMusic(fileName: String, url: String) {
        DownloadUtil.downloadMusic(
            activity = this,
            name = fileName,
            url = url
        )
    }

    private fun handleClick(position: Int, data: List<MusicItem>) {
        if (data.isNotEmpty()) {
            musicController.dataController?.clear()
            val list = mutableListOf<Wrapper>()
            if (data.size > viewModel.limit) {
                val start = if (position + viewModel.limit / 2 >= data.size) {
                    data.size - viewModel.limit
                } else {
                    Math.max(0, position - viewModel.limit / 2)
                }
                for (i in start until start + viewModel.limit) {
                    list.add(data[i].wrap())
                }
            } else {
                for (d in data) {
                    list.add(d.wrap())
                }
            }

            musicController.dataController?.addAll(list)
            musicController.playerController?.jumpTo(position)
            CommonRouter.routeToPlayingActivity()
        }
    }

    private fun errorHandle() {
        showToast(context = this, text = "Error playlist id", duration = Toast.LENGTH_SHORT)
        finish()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_playlist_detail
    }

    override fun getViewModelClass(): Class<PlaylistDetailViewModel> {
        return PlaylistDetailViewModel::class.java
    }

    override fun onDestroy() {
        if (popWindow.window.isShowing) {
            popWindow.window.dismiss()
        }
        super.onDestroy()
    }


    override fun createFactory(): ViewModelProvider.Factory {
        return DefaultFactory.getInstance()
    }
}