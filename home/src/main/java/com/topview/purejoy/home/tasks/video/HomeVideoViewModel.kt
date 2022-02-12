package com.topview.purejoy.home.tasks.video

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.video.HomeVideoScreenState
import com.topview.purejoy.home.data.repo.HomeVideoRepository
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.entity.VideoCategoryTab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeVideoViewModel: MVVMViewModel() {

    private val repository = HomeVideoRepository()

    private var _screenState: MutableStateFlow<HomeVideoScreenState> = MutableStateFlow(
        HomeVideoScreenState(
            pagerState = PageState.Empty,
            tabArray = emptyArray()
        )
    )
    val screenState: StateFlow<HomeVideoScreenState> = _screenState

    fun getVideoByCategory(
        categoryId: Long
    ): Flow<PagingData<ExternVideo>> = repository.getVideoFlowByCategory(
        categoryId
    )

    /**
     * 加载视频类别列表
     */
    fun loadVideoCategory() {
        viewModelScope.rxLaunch<Array<VideoCategoryTab>> {
            onRequest = {
                _screenState.value = getEmptyState(PageState.Loading)
                val array = repository.getCategoryArray()
                if (array.isEmpty()) {
                    error("cannot get category")
                } else {
                    array
                }
            }
            onSuccess = {
                _screenState.value = HomeVideoScreenState(
                    pagerState = PageState.Success,
                    tabArray = it
                )
            }
            onError = {
                _screenState.value = getEmptyState(
                    PageState.Error
                )
            }
        }
    }

    private fun getEmptyState(
        pagerState: PageState,
    ): HomeVideoScreenState = HomeVideoScreenState(
        pagerState = pagerState,
        tabArray = emptyArray()
    )
}