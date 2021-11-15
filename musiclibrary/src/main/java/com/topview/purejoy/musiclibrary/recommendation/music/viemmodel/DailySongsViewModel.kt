package com.topview.purejoy.musiclibrary.recommendation.music.viemmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.await
import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongs
import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongsWrapper
import com.topview.purejoy.musiclibrary.recommendation.music.entity.SongWithReason
import com.topview.purejoy.musiclibrary.recommendation.music.repository.DailySongsRepository
import com.topview.purejoy.musiclibrary.recommendation.music.repository.DailySongsRepositoryImpl

class DailySongsViewModel(
    private val repository: DailySongsRepository = DailySongsRepositoryImpl()
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
                data.value = list
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
}