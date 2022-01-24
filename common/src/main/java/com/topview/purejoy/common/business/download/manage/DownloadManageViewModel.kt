package com.topview.purejoy.common.business.download.manage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.data.repo.CommonRepository
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

class DownloadManageViewModel : MVVMViewModel() {
    private val repository = CommonRepository

    val downloadSongsLiveData: MutableLiveData<List<DownloadSongInfo>> by lazy {
        MutableLiveData<List<DownloadSongInfo>>()
    }

    fun getDownloadSongInfoList() {
        viewModelScope.rxLaunch<List<DownloadSongInfo>> {
            onRequest = {
                repository.getDownloadSongInfoList()
            }

            onSuccess = {
                downloadSongsLiveData.value = it
            }
        }
    }
}