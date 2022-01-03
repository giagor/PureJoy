package com.topview.purejoy.musiclibrary.recommendation.music.viemmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.music.service.url.entity.URLItemWrapper
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepository
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepositoryImpl
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.await
import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongsWrapper
import com.topview.purejoy.musiclibrary.recommendation.music.entity.SongWithReason
import com.topview.purejoy.musiclibrary.recommendation.music.repository.DailySongsRepository
import com.topview.purejoy.musiclibrary.recommendation.music.repository.DailySongsRepositoryImpl

class DailySongsViewModel(
    private val repository: DailySongsRepository = DailySongsRepositoryImpl(),
    private val urlRepo: MusicURLRepository = MusicURLRepositoryImpl(),
    val urlResponse: MutableLiveData<URLItemWrapper?> = MutableLiveData()
) : MVVMViewModel() {
    val data: MutableLiveData<List<SongWithReason>> by lazy {
        MutableLiveData()
    }

    fun requestDailySongs() {
        viewModelScope.rxLaunch<DailySongsWrapper> {
            onRequest = {
                repository.requestDailySongsRepository().await()
            }
            onSuccess = {
                val list = mutableListOf<SongWithReason>()
                var i = 0
                for (item in it.data.dailySongs) {
                    var reason: String? = null
                    for(index in i until it.data.recommendReasons.size) {
                        val r = it.data.recommendReasons[index]
                        if (r.songId == item.id) {
                            reason = r.reason
                            i++
                            break
                        }
                    }
                    list.add(SongWithReason(item, reason))
                }
                data.postValue(list)
            }
            onEmpty = {
                data.value = null
            }
            onError = {
                // push null to notify observer that throws exception
                data.value = null
            }
        }
    }

    fun requestURL(id: Long) {
        viewModelScope.rxLaunch<URLItemWrapper> {
            onRequest = {
                urlRepo.requestMusicURL(id.toString()).await()
            }
            onSuccess = {
                urlResponse.postValue(it)
            }
            onError = {
                urlResponse.postValue(null)
            }
        }
    }
}