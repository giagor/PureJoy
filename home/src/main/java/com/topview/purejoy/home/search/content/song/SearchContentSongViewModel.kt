package com.topview.purejoy.home.search.content.song

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
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

    fun getSearchSongByFirst(keyword: String, limit: Int) {
        viewModelScope.rxLaunch<SongPagerWrapper> {
            onRequest = {
                repository.getSearchSongByFirst(keyword, limit)
            }

            onSuccess = {
                it.songCount?.let { songCount ->
                    searchSongCountLiveData.value = songCount
                }

                it.songs?.let { songs ->
                    val searchResult = mutableListOf<Song>()
                    searchResult.addAll(songs)
                    searchSongsByFirstRequestLiveData.value = searchResult
                }
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

            onEmpty = {
                status.value = Status.SEARCH_SONG_LOAD_MORE_NET_EMPTY
            }
        }
    }
}