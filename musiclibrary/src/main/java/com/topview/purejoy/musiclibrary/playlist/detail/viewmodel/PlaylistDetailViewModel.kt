package com.topview.purejoy.musiclibrary.playlist.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.music.service.entity.MusicResponse
import com.topview.purejoy.common.music.service.url.entity.URLItemWrapper
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepository
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepositoryImpl
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.await
import com.topview.purejoy.musiclibrary.playlist.detail.repo.IPlaylistDetailRepository
import com.topview.purejoy.musiclibrary.playlist.detail.repo.PlaylistDetailRepository
import com.topview.purejoy.musiclibrary.playlist.entity.PlaylistResponse

class PlaylistDetailViewModel(
    private val repo: IPlaylistDetailRepository = PlaylistDetailRepository(),
    val response: MutableLiveData<PlaylistResponse?> = MutableLiveData(),
    val songsResponse: MutableLiveData<MusicResponse?> = MutableLiveData(),
    private val urlRepo: MusicURLRepository = MusicURLRepositoryImpl(),
    val urlResponse: MutableLiveData<URLItemWrapper?> = MutableLiveData()
) : MVVMViewModel() {

    private val TAG = "PlaylistVM"

    fun getDetails(id: Long) {
        viewModelScope.rxLaunch<PlaylistResponse> {
            onRequest = {
                repo.getDetails(id).await()
            }
            onError = {
                response.postValue(null)
            }
            onSuccess = {
                response.postValue(it)
                val builder = StringBuilder()
                for (i in it.playlist.trackIds) {
                    if (builder.isNotEmpty()) {
                        builder.append(',')
                    }
                    builder.append(i.id)
                }
                requestSongsDetails(builder.toString())
            }
        }
    }

    fun requestSongsDetails(ids: String) {
        viewModelScope.rxLaunch<MusicResponse> {
            onRequest = {
                repo.requestSongDetails(ids).await()
            }
            onError = {
                songsResponse.postValue(null)
            }
            onSuccess = {
                songsResponse.postValue(it)
            }
        }
    }

    fun requestUrl(id: Long) {
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