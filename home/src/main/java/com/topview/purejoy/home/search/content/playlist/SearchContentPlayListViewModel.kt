package com.topview.purejoy.home.search.content.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.Status
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.PlayListPagerWrapper

class SearchContentPlayListViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    /**
     * 记录初次搜索到的歌单
     * */
    private val _searchPlayListByFirstRequestLiveData: MutableLiveData<List<PlayList>> by lazy {
        MutableLiveData<List<PlayList>>()
    }
    val searchPlayListByFirstRequestLiveData: LiveData<List<PlayList>> =
        _searchPlayListByFirstRequestLiveData


    /**
     * 记录本次"加载更多"，获取到的歌单列表
     * */
    private val _searchPlayListsLoadMoreLiveData: MutableLiveData<List<PlayList>> by lazy {
        MutableLiveData<List<PlayList>>()
    }
    val searchPlayListsLoadMoreLiveData: LiveData<List<PlayList>> = _searchPlayListsLoadMoreLiveData

    /**
     * 由于搜索使用分页加载，这里记录歌单的总数量
     * */
    private val _searchPlayListCountLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val searchPlayListCountLiveData: LiveData<Int> = _searchPlayListCountLiveData

    private val _loadingLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData


    fun getSearchPlayListByFirst(keyword: String, limit: Int) {
        viewModelScope.rxLaunch<PlayListPagerWrapper> {
            onRequest = {
                _loadingLiveData.value = true
                repository.getSearchPlayListByFirst(keyword, limit)
            }

            onSuccess = {
                it.playlistCount?.let {
                    _searchPlayListCountLiveData.value = it
                }

                it.playlists?.let { playlists ->
                    val searchResult = mutableListOf<PlayList>()
                    searchResult.addAll(playlists)
                    _searchPlayListByFirstRequestLiveData.value = searchResult
                }
                _loadingLiveData.value = false
            }

            onError = {
                _loadingLiveData.value = false
                status.value = Status.SEARCH_PLAYLIST_FIRST_ERROR
            }

            onEmpty = {
                _loadingLiveData.value = false
            }
        }
    }

    fun loadMorePlayLists(keyword: String, offset: Int, limit: Int) {
        viewModelScope.rxLaunch<List<PlayList>> {
            onRequest = {
                repository.loadMorePlayLists(keyword, offset, limit)
            }

            onSuccess = {
                _searchPlayListsLoadMoreLiveData.value = it
            }

            onError = {
                status.value = Status.SEARCH_PLAYLIST_LOAD_MORE_NET_ERROR
            }
        }
    }
}