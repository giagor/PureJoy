package com.topview.purejoy.home.search.content.song

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.bean.SongDetailJson
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.entity.SongPagerWrapper

class SearchContentSongViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    /**
     * 记录初次搜索到的歌曲
     * */
    val searchSongsByFirstRequestLiveData: MutableLiveData<List<Song>> by lazy {
        MutableLiveData<List<Song>>()
    }

    /**
     * 记录本次"加载更多"，获取到的歌曲列表
     * */
    val searchSongsLoadMoreLiveData: MutableLiveData<List<Song>> by lazy {
        MutableLiveData<List<Song>>()
    }

    /**
     * 由于搜索使用分页加载，这里记录歌曲的总数量
     * */
    val searchSongCountLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val requestUrlLiveData: MutableLiveData<Song> by lazy {
        MutableLiveData<Song>()
    }

    val loadingLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun getSearchSongByFirst(keyword: String, limit: Int) {
        viewModelScope.rxLaunch<SongPagerWrapper> {
            onRequest = {
                loadingLiveData.value = true
                repository.getSearchSongByFirst(keyword, limit)
            }

            onSuccess = {
                loadingLiveData.value = false
                it.songCount?.let { songCount ->
                    searchSongCountLiveData.value = songCount
                }

                it.songs?.let { songs ->
                    val searchResult = mutableListOf<Song>()
                    searchResult.addAll(songs)
                    searchSongsByFirstRequestLiveData.value = searchResult
                }
            }

            onError = {
                loadingLiveData.value = false
                status.value = Status.SEARCH_SONG_FIRST_ERROR
            }

            onEmpty = {
                loadingLiveData.value = false
            }
        }
    }

    fun loadMoreSongs(keyword: String, offset: Int, limit: Int) {
        viewModelScope.rxLaunch<List<Song>> {
            onRequest = {
                repository.loadMoreSongs(keyword, offset, limit)
            }

            onSuccess = {
                searchSongsLoadMoreLiveData.value = it
            }

            onError = {
                status.value = Status.SEARCH_SONG_LOAD_MORE_NET_ERROR
            }
        }
    }

    fun requestSongUrl(song: Song) {
        if (song.id == null) {
            status.value = Status.SEARCH_SONG_REQUEST_URL_ID_EMPTY
            return
        }

        song.id?.let {
            viewModelScope.rxLaunch<SongDetailJson> {
                onRequest = {
                    repository.requestSongUrl(it)
                }

                onSuccess = {
                    song.url = it.data?.get(0)?.url
                    requestUrlLiveData.value = song
                }

                onError = {
                    status.value = Status.SEARCH_SONG_REQUEST_URL_ERROR
                }
            }
        }


    }
}