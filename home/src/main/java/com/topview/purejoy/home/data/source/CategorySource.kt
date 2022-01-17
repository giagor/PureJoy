package com.topview.purejoy.home.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.topview.purejoy.common.net.awaitSync
import com.topview.purejoy.home.data.api.HomeVideoService
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.entity.RecommendTabId
import com.topview.purejoy.home.entity.toExternVideos
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

                val json = if (isRecommend) {
                    service.getRecommendVideo(page).awaitSync()
                } else {
                    service.getVideoByCategory(categoryId, page).awaitSync()
                }
                if (json == null) {
                    error("Cannot get json object")
                }
                if (json.code != 200) {
                    error("Response code is ${json.code}! message: " + json.message)
                }
                // 确定是否有下一页
                val nextPage = if (json.hasMore == true) page + 1 else null

                LoadResult.Page(json.toExternVideos(isRecommend), prevPage, nextPage)
            }.getOrElse {
                LoadResult.Error(it)
            }
        }
    }
}