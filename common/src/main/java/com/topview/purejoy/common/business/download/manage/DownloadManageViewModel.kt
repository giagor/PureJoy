package com.topview.purejoy.common.business.download.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.data.repo.CommonRepository
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel

class DownloadManageViewModel : MVVMViewModel() {
    private val repository = CommonRepository

    private val _downloadSongsLiveData: MutableLiveData<List<DownloadSongInfo>> by lazy {
        MutableLiveData<List<DownloadSongInfo>>()
    }
    val downloadSongsLiveData: LiveData<List<DownloadSongInfo>> = _downloadSongsLiveData

    private val _taskEmptyLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val taskEmptyLiveData: LiveData<Boolean> = _taskEmptyLiveData

    fun getDownloadSongInfoList() {
        viewModelScope.rxLaunch<List<DownloadSongInfo>> {
            onRequest = {
                repository.getDownloadSongInfoList()
            }

            onEmpty = {
                _taskEmptyLiveData.value = true
            }

            onSuccess = {
                _downloadSongsLiveData.value = it
            }
        }
    }
}