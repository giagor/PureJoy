package com.topview.purejoy.musiclibrary.playing.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.playing.entity.LrcResponse
import com.topview.purejoy.musiclibrary.playing.repository.PlayingRepository

/**
 * 使用这个ViewModel作为Activity与Fragment的交互对象
 */
class PlayingViewModel(private val repository: PlayingRepository = PlayingRepository()) : MVVMViewModel() {
    val playingItems: MutableLiveData<MutableList<MusicItem>> = MutableLiveData(mutableListOf())
    val lrcResponse: MutableLiveData<LrcResponse> = MutableLiveData()

    // lrc fragment需监听，以与Activity交互
    val currentItem: MutableLiveData<MusicItem> = MutableLiveData()
    val progress: MutableLiveData<Int> = MutableLiveData(0)
    val duration: MutableLiveData<Int> = MutableLiveData(0)
    val playState: MutableLiveData<Boolean> = MutableLiveData()

    fun requestLrc(id: Long) {
        viewModelScope.rxLaunch<LrcResponse> {
            onRequest = {
                repository.requestLrc(id)
            }
            onSuccess = {
                lrcResponse.value = it
            }
            onError = {
                lrcResponse.value = LrcResponse(null, id)
            }
        }
    }


}