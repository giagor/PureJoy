package com.topview.purejoy.home.download

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.component.download.listener.user.SimpleUserDownloadListener
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.common.util.DownloadUtil
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeDownloadManageBinding
import com.topview.purejoy.home.download.adapter.DownloadManageAdapter
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

@Route(path = HomeRouter.ACTIVITY_HOME_DOWNLOAD_MANAGE)
class DownloadManageActivity :
    MVVMActivity<DownloadManageViewModel, ActivityHomeDownloadManageBinding>(),
    DownloadManageAdapter.OnClickListener {

    private val downloadListener = object : SimpleUserDownloadListener() {
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
            super.alreadyDownloaded(downloadTask)
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
        // TODO 如果列表过大，就在子线程中添加监听器
        viewModel.downloadTasksLiveData.observe(this) {
            // TODO 对于未开始下载的任务，考虑开始下载时才加监听
            for (task in it) {
                task.registerObserver(downloadListener)
            }
        }
    }

    private fun initView() {
        initRecyclerView()
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
        // 点击监听
        adapter.setOnItemClickListener { adapter, _, position ->
            val task: DownloadTask = adapter.getItem(position) as DownloadTask
            if (task.getStatus() == DownloadStatus.INITIAL) {
                DownloadUtil.downloadMusic(this@DownloadManageActivity, task)
            }
        }
    }

    private fun updateItem(downloadTask: DownloadTask) {
        adapter.notifyItemChanged(adapter.getItemPosition(downloadTask))
    }

    private fun removeItem(downloadTask: DownloadTask) {
        adapter.remove(downloadTask)
    }

    /**
     * 状态按钮的点击事件
     * */
    override fun onStatusButtonClick(downloadTask: DownloadTask) {
        when (downloadTask.getStatus()) {
            DownloadStatus.DOWNLOADING -> {
                downloadTask.pauseDownload()
            }

            DownloadStatus.PAUSED -> {
                downloadTask.resumeDownload()
            }

            else -> {}
        }
    }

    override fun onCancelTaskClick(downloadTask: DownloadTask) {
        when (downloadTask.getStatus()) {
            DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED, DownloadStatus.PREPARE_DOWNLOAD -> {
                downloadTask.cancelDownload()
                updateItem(downloadTask)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_home_download_manage

    override fun getViewModelClass(): Class<DownloadManageViewModel> =
        DownloadManageViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()
}