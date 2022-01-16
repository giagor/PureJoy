package com.topview.purejoy.musiclibrary.playlist.square.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistSquareResponse
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistTagResponse
import com.topview.purejoy.musiclibrary.playlist.square.repo.IPlaylistSquareRepository
import com.topview.purejoy.musiclibrary.playlist.square.repo.PlaylistSquareRepository

class PlaylistSquareViewModel(
    private val repository: IPlaylistSquareRepository = PlaylistSquareRepository(),
    val playlistTagsResponse: MutableLiveData<PlaylistTagResponse?> = MutableLiveData(),
    val playlistResponse: MutableLiveData<PlaylistSquareResponse?> = MutableLiveData()
) : MVVMViewModel() {


    fun requireTags() {
        viewModelScope.rxLaunch<PlaylistTagResponse> {
            onRequest = {
                repository.requireTags().awaitAsync()
            }
            onSuccess = {
                playlistTagsResponse.postValue(it)
            }
            onError = {
                playlistTagsResponse.postValue(null)
            }
            onEmpty = {
                playlistTagsResponse.postValue(null)
            }
        }
    }

    fun requirePlaylists(limit: Int, order: String, cat: String, offset: Int) {
        viewModelScope.rxLaunch<PlaylistSquareResponse> {
            onRequest = {
                repository.requirePlaylists(limit, order, cat, offset).awaitAsync()
            }
            onEmpty = {
                playlistResponse.postValue(null)
            }
            onSuccess = {
                playlistResponse.postValue(it)
            }
            onError = {
                playlistResponse.postValue(null)
            }
        }
    }


}