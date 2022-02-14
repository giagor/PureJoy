package com.topview.purejoy.musiclibrary.playlist.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.music.service.entity.MusicResponse
import com.topview.purejoy.common.music.service.url.entity.URLItemWrapper
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepository
import com.topview.purejoy.common.music.service.url.repository.MusicURLRepositoryImpl
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.musiclibrary.playlist.detail.repo.IPlaylistDetailRepository
import com.topview.purejoy.musiclibrary.playlist.detail.repo.PlaylistDetailRepository
import com.topview.purejoy.musiclibrary.playlist.entity.PlaylistResponse

class PlaylistDetailViewModel(
    private val repo: IPlaylistDetailRepository = PlaylistDetailRepository(),
    val response: MutableLiveData<PlaylistResponse?> = MutableLiveData(),
    val songsResponse: MutableLiveData<MusicResponse?> = MutableLiveData(),
    private val urlRepo: MusicURLRepository = MusicURLRepositoryImpl(),
    val urlResponse: MutableLiveData<URLItemWrapper?> = MutableLiveData(),
    val limit: Int = 300,
    @Volatile var loadItems: Boolean = false
) : MVVMViewModel() {

    private val TAG = "PlaylistVM"

    fun getDetails(id: Long) {
        viewModelScope.rxLaunch<PlaylistResponse> {
            onRequest = {
                repo.getDetails(id).awaitAsync()
            }
            onError = {
                response.postValue(null)
            }
            onSuccess = {
                response.postValue(it)
            }
        }
    }

    fun requestSongsDetails(ids: String) {
        viewModelScope.rxLaunch<MusicResponse> {
            onRequest = {
                repo.requestSongDetails(ids).awaitAsync()
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
                urlRepo.requestMusicURL(id.toString()).awaitAsync()
            }
            onSuccess = {
                urlResponse.postValue(it)
            }
            onError = {
                urlResponse.postValue(null)
            }
        }
    }

    fun requestPLSongWithPage(id: Long, size: Int) {
        if (!loadItems && response.value != null) {
            loadItems = true
            val offset = size / limit
            val total = response.value!!.playlist.trackIds.size
            val max = total / limit
            if (size < total) {
                viewModelScope.rxLaunch<MusicResponse> {
                    onRequest = {
                        repo.requestPLSongs(id, limit, offset).awaitAsync()
                    }
                    onSuccess = {
                        loadItems = false
                        songsResponse.postValue(it)
                    }
                    onError = {
                        loadItems = false
                        songsResponse.postValue(null)
                    }
                }
            }
        }

    }

}