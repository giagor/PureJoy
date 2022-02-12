package com.topview.purejoy.common.business.download.manage

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.R
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.download.manage.adapter.DownloadManageAdapter
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.databinding.ActivityCommonDownloadManageBinding
import com.topview.purejoy.common.music.view.bottom.MusicBottomView
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.util.DownloadUtil

@Route(path = CommonRouter.ACTIVITY_COMMON_DOWNLOAD_MANAGE)
class DownloadManageActivity :
    MVVMActivity<DownloadManageViewModel, ActivityCommonDownloadManageBinding>(),
    DownloadManageAdapter.OnClickListener {

    private val bottomMusicBar: MusicBottomView by lazy {
        MusicBottomView(activity = this)
    }
    
    private val downloadListener = object : UserDownloadListener {
        override fun onPrepareDownload(downloadTask: DownloadTask) {
            updateItem(downloadTask)
        }

        override fun onStarted(downloadTask: DownloadTask) {
            updateItem(downloadTask)
        }

        override fun onProgress(downloadTask: DownloadTask, progress: Int) {
            updateItem(downloadTask)
        }

        override fun onPaused(downloadTask: DownloadTask) {
            updateItem(downloadTask)
        }

        override fun onResumed(downloadTask: DownloadTask) {
            updateItem(downloadTask)
        }

        override fun onFailure(downloadTask: DownloadTask, msg: String) {
            removeItem(downloadTask)
        }

        override fun onCancelled(downloadTask: DownloadTask) {
            removeItem(downloadTask)
        }

        override fun onSuccess(downloadTask: DownloadTask) {
            removeItem(downloadTask)
        }

        override fun alreadyDownloaded(downloadTask: DownloadTask) {
            removeItem(downloadTask)
        }
    }

    private val adapter: DownloadManageAdapter = DownloadManageAdapter().apply {
        setStatusButtonClickListener(this@DownloadManageActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.downloadManageViewModel = viewModel
        initView()
        observe()
        initData()
        initEvent()
    }

    private fun observe() {
        viewModel.downloadSongsLiveData.observe(this) {
            for (songInfo in it) {
                DownloadManager.getTask(songInfo.tag)?.registerObserver(downloadListener)
            }
        }
    }

    private fun initView() {
        initRecyclerView()
        bottomMusicBar.addMusicBottomBar()
    }

    private fun initData() {
        viewModel.getDownloadSongInfoList()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.downloadTaskLayoutManager = layoutManager
        binding.downloadTaskAdapter = adapter
    }

    private fun initEvent() {
        binding.backListener = View.OnClickListener {
            finish()
        }

        // 点击监听
        adapter.setOnItemClickListener { adapter, _, position ->
            val songInfo: DownloadSongInfo = adapter.getItem(position) as DownloadSongInfo
            if (songInfo.status == DownloadStatus.INITIAL) {
                DownloadUtil.downloadMusic(this, songInfo, downloadListener)
            }
        }
    }

    private fun updateItem(downloadTask: DownloadTask) {
        val songInfoList = adapter.data
        for ((index, songInfo) in songInfoList.withIndex()) {
            if (songInfo.tag == downloadTask.tag) {
                songInfo.status = downloadTask.getStatus()
                songInfo.progress = downloadTask.getProgress()
                adapter.notifyItemChanged(index)
                break
            }
        }
    }

    private fun removeItem(downloadTask: DownloadTask) {
        val songInfoList = adapter.data
        for (songInfo in songInfoList) {
            if (songInfo.tag == downloadTask.tag) {
                songInfo.status = downloadTask.getStatus()
                adapter.remove(songInfo)
                break
            }
        }
    }

    /**
     * 状态按钮的点击事件
     * */
    override fun onStatusButtonClick(songInfo: DownloadSongInfo) {
        when (songInfo.status) {
            DownloadStatus.DOWNLOADING -> {
                DownloadManager.getTask(songInfo.tag)?.pauseDownload()
            }

            DownloadStatus.PAUSED -> {
                DownloadManager.getTask(songInfo.tag)?.resumeDownload()
            }

            else -> {}
        }
    }

    override fun onCancelTaskClick(songInfo: DownloadSongInfo) {
        when (songInfo.status) {
            DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED, DownloadStatus.PREPARE_DOWNLOAD -> {
                DownloadManager.getTask(songInfo.tag)?.let {
                    it.cancelDownload()
                    updateItem(it)
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_common_download_manage

    override fun getViewModelClass(): Class<DownloadManageViewModel> =
        DownloadManageViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory =
        ViewModelProvider.AndroidViewModelFactory.getInstance(
            CommonApplication.getContext() as Application
        )

    override fun onDestroy() {
        for (songInfo in adapter.data) {
            DownloadManager.getTask(songInfo.tag)?.unregisterObserver(downloadListener)
        }
        super.onDestroy()
    }
}