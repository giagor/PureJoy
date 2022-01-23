package com.topview.purejoy.home.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.repo.HomeRepository

class DownloadManageViewModel : MVVMViewModel() {
    private val repository = HomeRepository

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