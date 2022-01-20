package com.topview.purejoy.home.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.repo.HomeRepository

class DownloadManageViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    val downloadTasksLiveData: MutableLiveData<List<DownloadTask>> by lazy {
        MutableLiveData<List<DownloadTask>>()
    }

    fun getDownloadSongInfoList() {
        viewModelScope.rxLaunch<List<DownloadTask>> {
            onRequest = {
                repository.getDownloadTaskList()
            }

            onSuccess = {
                downloadTasksLiveData.value = it
            }
        }
    }
}