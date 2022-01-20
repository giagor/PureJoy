package com.topview.purejoy.home.download

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.component.download.listener.user.SimpleUserDownloadListener
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeDownloadManageBinding
import com.topview.purejoy.home.download.adapter.DownloadManageAdapter
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

@Route(path = HomeRouter.ACTIVITY_HOME_DOWNLOAD_MANAGE)
class DownloadManageActivity :
    MVVMActivity<DownloadManageViewModel, ActivityHomeDownloadManageBinding>() {

    private val downloadListener = object : SimpleUserDownloadListener() {
        override fun onProgress(downloadTask: DownloadTask, progress: Int) {
            super.onProgress(downloadTask, progress)
        }

        override fun onPaused(downloadTask: DownloadTask) {
            super.onPaused(downloadTask)
        }

        override fun onResumed(downloadTask: DownloadTask) {
            super.onResumed(downloadTask)
        }

        override fun onFailure(downloadTask: DownloadTask, msg: String) {
            super.onFailure(downloadTask, msg)
        }

        override fun onCancelled(downloadTask: DownloadTask) {
            super.onCancelled(downloadTask)
        }

        override fun onSuccess(downloadTask: DownloadTask) {
            super.onSuccess(downloadTask)
        }

        override fun alreadyDownloaded(downloadTask: DownloadTask) {
            super.alreadyDownloaded(downloadTask)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.downloadManageViewModel = viewModel
        initView()
        observe()
        initData()
    }

    private fun observe() {
        // TODO 如果列表过大，就在子线程中添加监听器
        viewModel.downloadTasksLiveData.observe(this) {

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
        val adapter = DownloadManageAdapter()
        binding.downloadTaskLayoutManager = layoutManager
        binding.downloadTaskAdapter = adapter
    }

    override fun getLayoutId(): Int = R.layout.activity_home_download_manage

    override fun getViewModelClass(): Class<DownloadManageViewModel> =
        DownloadManageViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()
}