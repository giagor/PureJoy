package com.topview.purejoy.home.download

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.topview.purejoy.common.mvvm.activity.MVVMActivity
import com.topview.purejoy.home.R
import com.topview.purejoy.home.databinding.ActivityHomeDownloadManageBinding
import com.topview.purejoy.home.router.HomeRouter
import com.topview.purejoy.home.util.getAndroidViewModelFactory

@Route(path = HomeRouter.ACTIVITY_HOME_DOWNLOAD_MANAGE)
class DownloadManageActivity :
    MVVMActivity<DownloadManageViewModel, ActivityHomeDownloadManageBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        initData()
    }

    private fun observe() {
        viewModel.downloadSongInfoListLiveData.observe(this) {
        }
    }

    private fun initData() {
        viewModel.getDownloadSongInfoList()
    }

    override fun getLayoutId(): Int = R.layout.activity_home_download_manage

    override fun getViewModelClass(): Class<DownloadManageViewModel> =
        DownloadManageViewModel::class.java

    override fun createFactory(): ViewModelProvider.Factory = getAndroidViewModelFactory()
}