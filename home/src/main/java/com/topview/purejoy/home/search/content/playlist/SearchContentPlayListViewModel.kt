package com.topview.purejoy.home.search.content.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.data.repo.HomeRepository
import com.topview.purejoy.home.entity.PlayList
import com.topview.purejoy.home.entity.PlayListPagerWrapper

class SearchContentPlayListViewModel : MVVMViewModel() {
    private val repository = HomeRepository

    /**
     * 记录初次搜索到的歌单
     * */
    val searchPlayListByFirstRequestLiveData: MutableLiveData<List<PlayList>> by lazy {
        MutableLiveData<List<PlayList>>()
    }

    /**
     * 由于搜索使用分页加载，这里记录歌单的总数量
     * */
    val searchPlayListCountLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun getSearchPlayListByFirst(keyword: String, limit: Int) {
        viewModelScope.rxLaunch<PlayListPagerWrapper> {
            onRequest = {
                repository.getSearchPlayListByFirst(keyword, limit)
            }

            onSuccess = {
                it.playlistCount?.let {
                    searchPlayListCountLiveData.value = it
                }

                it.playlists?.let { playlists ->
                    val searchResult = mutableListOf<PlayList>()
                    searchResult.addAll(playlists)
                    searchPlayListByFirstRequestLiveData.value = searchResult
                }
            }


        }
    }
}