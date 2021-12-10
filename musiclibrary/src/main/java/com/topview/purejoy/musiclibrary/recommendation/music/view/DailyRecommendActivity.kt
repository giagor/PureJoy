package com.topview.purejoy.musiclibrary.recommendation.music.view

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.musiclibrary.IPCDataSetChangeListener
import com.topview.purejoy.musiclibrary.IPCItemChangeListener
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.MusicCommonActivity
import com.topview.purejoy.musiclibrary.common.factory.DefaultFactory
import com.topview.purejoy.musiclibrary.common.instance.BinderPoolClientInstance
import com.topview.purejoy.musiclibrary.common.util.buildSwatch
import com.topview.purejoy.musiclibrary.common.util.getDisplaySize
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.wrap
import com.topview.purejoy.musiclibrary.recommendation.music.adapter.DailyRecommendAdapter
import com.topview.purejoy.musiclibrary.recommendation.music.entity.SongWithReason
import com.topview.purejoy.musiclibrary.recommendation.music.entity.toWrapperList
import com.topview.purejoy.musiclibrary.recommendation.music.pop.RecommendPop
import com.topview.purejoy.musiclibrary.recommendation.music.viemmodel.DailySongsViewModel
import com.topview.purejoy.musiclibrary.service.MusicService

class DailyRecommendActivity : MusicCommonActivity<DailySongsViewModel>() {

    override fun getLayoutId(): Int = R.layout.coordinatorlayout_activity_daily_recommend

    private val popWindow: RecommendPop by lazy {
        val size = getDisplaySize()
        val p = RecommendPop(this, size.width(), size.height() * 2/ 3)
        p.addItemView(R.drawable.next_play_pop_32, R.string.next_play) {
            item ->
            item?.let {
                val w = it.wrap()
                if (currentItem.value == null) {
                    dataController?.add(w)
                    playerController?.jumpTo(0)
                } else {
                    val index = playingList.value!!.indexOf(currentItem.value)
                    if (index == -1) {
                        dataController?.add(w)
                    } else {
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


    private val playingList = MutableLiveData<MutableList<Wrapper>>(mutableListOf())

    private var currentItem: MutableLiveData<Wrapper?> = MutableLiveData(null)
    
    private val TAG = "DailyRecommend"


    private val itemChangeListener: IPCItemChangeListener =
        object : IPCItemChangeListener.Stub() {
            override fun onItemChange(wrapper: Wrapper?) {
                currentItem.postValue(wrapper)
            }
        }

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val dataSetChangeListener: IPCDataSetChangeListener =
        object : IPCDataSetChangeListener.Stub() {
            override fun onChange(source: MutableList<Wrapper>?) {
                handler.post {
                    if (source!!.isEmpty()) {
                        val v = playingList.value!!
                        v.clear()
                        playingList.postValue(v)
                        currentItem.postValue(null)
                    } else {
                        dataController?.allItems()?.let {
                            val value = playingList.value!!
                            value.clear()
                            value.addAll(it)
                            playingList.postValue(value)
                        }
                    }
                }
            }
        }

    override fun onServiceConnected() {
        super.onServiceConnected()
        dataController?.allItems()?.let {
            val v = playingList.value!!
            v.addAll(it)
            playingList.postValue(v)
        }

        val v = dataController?.current()
        currentItem.postValue(v)

        listenerController?.apply {
            addItemChangeListener(itemChangeListener)
            addDataChangeListener(dataSetChangeListener)
        }
    }




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
        val adapter = DailyRecommendAdapter(itemClickListener = object : DailyRecommendAdapter.DailyRecommendItemClickListener {
            override fun onClick(item: SongWithReason) {
                dataController?.clear()
                dataController?.addAll(viewModel.data.value!!.toWrapperList())
                playerController?.jumpTo(viewModel.data.value!!.indexOf(item))
                playingList.value?.addAll(viewModel.data.value!!.toWrapperList())
            }
        }, buttonClickListener = object : DailyRecommendAdapter.DailyRecommendItemClickListener {
            override fun onClick(item: SongWithReason) {
                popWindow.data = item.item
                popWindow.window.showAtLocation(root, Gravity.BOTTOM, 0, 0)
            }
        })
        recyclerView.adapter = adapter
        viewModel.data.observe(this, {
            if (it != null) {
                adapter.data.clear()
                adapter.data.addAll(it)
                adapter.notifyItemRangeChanged(0, adapter.data.size)
                Glide.with(this).asBitmap().load(it[0].item.al.picUrl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            buildSwatch(resource) {
                                imageView.setImageDrawable(ColorDrawable(it))
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    })
            } else {
                showToast(context = this, text = "Request Error")
            }
        })
        layout.setOnClickListener {
            // 播放全部歌曲
            if (viewModel.data.value?.isNotEmpty() == true) {
                dataController?.clear()
                dataController?.addAll(viewModel.data.value!!.toWrapperList())
                playerController?.jumpTo(0)
            }
        }
        viewModel.requestDailySongs()
    }

    override fun onDestroy() {
        if (popWindow.window.isShowing) {
            popWindow.window.dismiss()
        }
        kotlin.runCatching {
            if (BinderPoolClientInstance.getInstance().getClient(
                    MusicService::class.java).isConnected()) {
                listenerController?.apply {
                    removeItemChangeListener(itemChangeListener)
                    removeDataChangeListener(dataSetChangeListener)
                }
            }
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