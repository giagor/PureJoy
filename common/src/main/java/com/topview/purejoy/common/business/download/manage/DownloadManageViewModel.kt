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

    val taskEmptyLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun getDownloadSongInfoList() {
        viewModelScope.rxLaunch<List<DownloadSongInfo>> {
            onRequest = {
                repository.getDownloadSongInfoList()
            }

            onEmpty = {
                taskEmptyLiveData.value = true
            }

            onSuccess = {
                downloadSongsLiveData.value = it
            }
        }
    }
}