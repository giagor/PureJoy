package com.topview.purejoy.home.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.repo.HomeRepository

class DownloadManageViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    val downloadSongInfoListLiveData: MutableLiveData<List<DownloadSongInfo>> by lazy {
        MutableLiveData()
    }

    fun getDownloadSongInfoList() {
        viewModelScope.rxLaunch<List<DownloadSongInfo>> {
            onRequest = {
                repository.getDownloadSongInfoList()
            }

            onSuccess = {
                downloadSongInfoListLiveData.value = it
            }
        }
    }
}