package com.topview.purejoy.musiclibrary.playlist.detail.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.util.md5
import com.topview.purejoy.common.music.activity.MusicCommonActivity
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.impl.cache.DiskCache
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.entity.wrap
import com.topview.purejoy.common.music.util.getDisplaySize
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.util.loadBitmapColor
import com.topview.purejoy.musiclibrary.playlist.detail.adapter.PlaylistDetailAdapter
import com.topview.purejoy.musiclibrary.playlist.detail.viewmodel.PlaylistDetailViewModel
import com.topview.purejoy.musiclibrary.recommendation.music.pop.RecommendPop

class PlaylistDetailActivity : MusicCommonActivity<PlaylistDetailViewModel>() {

    private val popWindow: RecommendPop by lazy {
        val size = getDisplaySize()
        val p = RecommendPop(this, width = size.width(),
            height = size.height() * 2 / 3, window)
        p.addItemView(R.drawable.next_play_pop_32, R.string.next_play) {
                item ->
            item?.let {
                val w = it.wrap()
                val items = viewModel.songsResponse.value!!.songs
                if (items.isEmpty()) {
                    dataController?.add(w)
                    playerController?.jumpTo(0)
                } else {
                    val index = items.indexOf(currentItem.value)
                    if (index != -1) {
                        dataController?.addAfter(w, index)
                    } else {
                        dataController?.clear()
                        dataController?.add(w)
                        playerController?.jumpTo(0)
                    }
                }
                popWindow.window.dismiss()
            }
        }
        p.addItemView(R.drawable.download_32, R.string.download) {
            // 下载
            it?.let { item ->
                if (item.url != null) {
                    val name = "${DiskCache.MD5Digest.getInstance()
                        .digest(item.url!!)}/${item.url!!.substring(item.url!!.lastIndexOf('.'))}"
                    DownloadManager.download(item.url!!,
                        CommonApplication.musicPath.absolutePath,
                        name, null)
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
        val playlistId: Long = intent.getLongExtra(PLAYLIST_EXTRA, -1L)
        if (playlistId == -1L) {
            errorHandle()
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
            }
        }

        viewModel.urlResponse.observe(this) {
            if (it == null) {
                showToast(this, "请求错误", Toast.LENGTH_SHORT)
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
                        showToast(this, "歌曲${music?.name}不能下载", Toast.LENGTH_SHORT)
                    } else {
                        music?.url = item.url
                        showToast(this, "已加入下载任务队列",
                            Toast.LENGTH_SHORT)
                        val name = "${md5(item.url)}${item.url.substring(item.url.lastIndexOf('.'))}"
                        DownloadManager.download(item.url,
                            CommonApplication.musicPath.path,
                            name, null)
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

        viewModel.songsResponse.observe(this) {
            if (it == null) {
                showToast(context = this, text = "Unknown Error")
            } else {
                adapter.data.addAll(it.songs)
                adapter.notifyItemRangeInserted(0, adapter.data.size)
            }
        }
        addMusicBottomBar(0)
        viewModel.getDetails(playlistId)




    }

    private fun handleClick(position: Int, data: List<MusicItem>) {
        if (data.isNotEmpty()) {
            dataController?.clear()
            val list = mutableListOf<Wrapper>()
            for (d in data) {
                list.add(d.wrap())
            }
            dataController?.addAll(list)
            playerController?.jumpTo(position)

//            handler.postDelayed({ startActivity(Intent(this@PlaylistDetailActivity, PlayingActivity::class.java)) }, 500)
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

    companion object {
        const val PLAYLIST_EXTRA = "playlist"
    }

    override fun createFactory(): ViewModelProvider.Factory {
        return DefaultFactory.getInstance()
    }
}