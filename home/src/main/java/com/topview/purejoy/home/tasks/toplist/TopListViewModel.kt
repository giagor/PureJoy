package com.topview.purejoy.home.tasks.toplist

import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.data.repo.TopListRepository
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.entity.TopListTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TopListViewModel: MVVMViewModel() {
    private val repository: TopListRepository = TopListRepository

    /**
     * 页面的状态
     */
    private var _pageState: MutableStateFlow<PageState> = MutableStateFlow(PageState.Loading)
    val pageState: StateFlow<PageState> = _pageState

    /**
     * 榜单数据
     */
    private var _topListData: MutableStateFlow<Map<TopListTab, List<TopList>>?> =
        MutableStateFlow(null)
    val topListData: StateFlow<Map<TopListTab, List<TopList>>?> = _topListData

    /**
     * 是否已经加载了封面的地址
     */
    private var _loadedCoverUrl: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadedCoverUrl: StateFlow<Boolean> = _loadedCoverUrl

    fun loadTopListData() {
        _pageState.value = PageState.Loading
        viewModelScope.rxLaunch<Map<TopListTab, List<TopList>>> {
            onRequest = {
                val list = repository.getTopListDetail()
                repository.collectTopListMap(list)
            }
            onSuccess = {
                _topListData.value = it
                _pageState.value = PageState.Success
            }
            onError = {
                _pageState.value = PageState.Error
            }
        }
    }

    /**
     * 加载官方榜单的专辑图片
     */
    fun loadCovers() {
        viewModelScope.rxLaunch<Unit> {
            onRequest = {
                _topListData.value?.get(TopListTab.Official)?.let {
                    repository.getPicUrlForTopList(it)
                }
            }
            onSuccess = {
                _loadedCoverUrl.value = true
            }
        }
    }
}