package com.topview.purejoy.home.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.home.data.api.HomeVideoService
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.entity.RecommendTabId
import com.topview.purejoy.home.entity.toExternVideos
import com.topview.purejoy.home.util.JsonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategorySource(
    private val categoryId: Long,
    private val service: HomeVideoService
): PagingSource<Int, ExternVideo>() {

    private val isRecommend: Boolean = categoryId == RecommendTabId

    override fun getRefreshKey(state: PagingState<Int, ExternVideo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExternVideo> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val page = params.key ?: 0
                val prevPage = if (page == 0) null else page - 1

                val jsonData = if (isRecommend) {
                    service.getRecommendVideo(page).awaitAsync()
                } else {
                    service.getVideoByCategory(categoryId, page).awaitAsync()
                }
                if (jsonData == null) {
                    error("Cannot get json Data")
                }
                val jsonObject = JsonUtil.parseRecommendVideoJson(jsonData.string())
                if (jsonObject.code != 200) {
                    error("Response code is ${jsonObject.code}! message: " + jsonObject.message)
                }
                // 确定是否有下一页
                val nextPage = if (jsonObject.hasMore == true) page + 1 else null

                LoadResult.Page(jsonObject.toExternVideos(isRecommend), prevPage, nextPage)
            }.getOrElse {
                LoadResult.Error(it)
            }
        }
    }
}