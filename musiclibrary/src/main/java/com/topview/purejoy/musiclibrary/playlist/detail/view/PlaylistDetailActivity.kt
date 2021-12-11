package com.topview.purejoy.musiclibrary.playlist.detail.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView
import com.topview.purejoy.musiclibrary.IPCDataSetChangeListener
import com.topview.purejoy.musiclibrary.IPCItemChangeListener
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.MusicCommonActivity
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener
import com.topview.purejoy.musiclibrary.common.instance.BinderPoolClientInstance
import com.topview.purejoy.musiclibrary.common.transformation.MusicItemTransformation
import com.topview.purejoy.musiclibrary.common.util.ExecutorInstance
import com.topview.purejoy.musiclibrary.common.util.getDisplaySize
import com.topview.purejoy.musiclibrary.common.util.loadBitmapColor
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.entity.wrap
import com.topview.purejoy.musiclibrary.playing.view.PlayingActivity
import com.topview.purejoy.musiclibrary.playlist.detail.adapter.PlaylistDetailAdapter
import com.topview.purejoy.musiclibrary.playlist.detail.viewmodel.PlaylistDetailViewModel
import com.topview.purejoy.musiclibrary.recommendation.music.pop.RecommendPop
import com.topview.purejoy.musiclibrary.service.MusicService

class PlaylistDetailActivity : MusicCommonActivity<PlaylistDetailViewModel>() {

    private val popWindow: RecommendPop by lazy {
        val size = getDisplaySize()
        val p = RecommendPop(this, width = size.width(),
            height = size.height() * 2 / 3, window)
        p.addItemView(R.drawable.next_play_pop_32, R.string.next_play) {
                item ->
            item?.let {
                val w = it.wrap()
                val items = allItems.value
                if (items == null || items.isEmpty()) {
                    dataController?.add(w)
                    playerController?.jumpTo(0)
                } else {
                    val index = items.indexOf(currentItem.value)
                    if (index != -1) {
                        dataController?.addAfter(w, index)
                    }
                }
                popWindow.window.dismiss()
            }
        }
        p.addItemView(R.drawable.download_32, R.string.download) {
            // 下载

            popWindow.window.dismiss()
        }
        p
    }

    private val allItems: MutableLiveData<MutableList<MusicItem>> = MutableLiveData(mutableListOf())
    private val currentItem: MutableLiveData<MusicItem?> = MutableLiveData()

    private val dataSetChangeListener: IPCDataSetChangeListener by lazy {
        object : IPCDataSetChangeListener.Stub() {
            override fun onChange(source: MutableList<Wrapper>) {
                val data = allItems.value!!
                if (source.isEmpty()) {
                    data.clear()
                } else {
                    for (w in source) {
                        val item = MusicItemTransformation.transform(w)
                        if (data.contains(item)) {
                            data.remove(item)
                        } else {
                            item?.let {
                                data.add(it)
                            }
                        }
                    }
                }
                allItems.postValue(data)
            }

        }
    }

    private val itemChangeListener: IPCItemChangeListener by lazy {
        object : IPCItemChangeListener.Stub() {
            override fun onItemChange(wrapper: Wrapper?) {
                currentItem.postValue(MusicItemTransformation.transform(wrapper!!))
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())



    override fun onServiceConnected() {
        super.onServiceConnected()

        // 防止主线程等待过长时间，将读取数据的操作放到子线程执行
        ExecutorInstance.getInstance().execute {
            val wrapper = dataController?.allItems()
            wrapper?.let {
                val list = mutableListOf<MusicItem>()
                for (w in it) {
                    MusicItemTransformation.transform(w)?.let { item ->
                        list.add(item)
                    }
                }
                val current = dataController?.current()
                if (current == null) {
                    currentItem.postValue(null)
                } else {
                    currentItem.postValue(MusicItemTransformation.transform(current))
                }
                allItems.postValue(list)
            }

        }

        listenerController?.apply {
            addDataChangeListener(dataSetChangeListener)
            addItemChangeListener(itemChangeListener)
        }

    }

    override fun onServiceDisconnected() {
        allItems.value?.clear()
        super.onServiceDisconnected()
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

            handler.postDelayed({ startActivity(Intent(this@PlaylistDetailActivity, PlayingActivity::class.java)) }, 2000)
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
        kotlin.runCatching {
            if (BinderPoolClientInstance.getInstance().getClient(
                    MusicService::class.java).isConnected()) {
                listenerController?.apply {
                    removeDataChangeListener(dataSetChangeListener)
                    removeItemChangeListener(itemChangeListener)
                }
            }
        }
        super.onDestroy()
    }

    companion object {
        const val PLAYLIST_EXTRA = "playlist"
    }
}