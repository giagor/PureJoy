package com.topview.purejoy.home.search.content.song

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.Song
import com.topview.purejoy.home.entity.SongPagerWrapper

/**
 * 分页加载，默认每页的大小
 * */
private const val DEFAULT_SEARCH_SONG_PAGER_SIZE = 20

class SearchContentSongViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    /**
     * 存储已搜索到的歌曲
     * */
    val searchSongsLiveData: MutableLiveData<MutableList<Song>> by lazy {
        MutableLiveData<MutableList<Song>>()
    }

    /**
     * 由于搜索使用分页加载，这里记录歌曲的总数量
     * */
    val searchSongCountLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun getSearchSongByFirst(keyword: String, limit: Int = DEFAULT_SEARCH_SONG_PAGER_SIZE) {
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
                    searchSongsLiveData.value = searchResult
                }
            }
        }
    }
}