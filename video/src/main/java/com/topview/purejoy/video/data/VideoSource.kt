package com.topview.purejoy.video.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.net.awaitSync
import com.topview.purejoy.video.data.api.VideoService
import com.topview.purejoy.video.data.bean.toVideos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VideoSource(
    private val videoService: VideoService,
    private val initialList: List<Video>
): PagingSource<Int, Video>() {

    /**
     * 作为推荐参考的Video的vid
     */
    private var heuristicVideoId: String? = if (initialList.isEmpty()) {
        null
    } else {
        val index: Int = (initialList.size * Math.random()).toInt()
        initialList[index].id
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val page = params.key ?: 1
                val prevPage = if (page == 1) null else page - 1
                if (page == 1) {
                    LoadResult.Page(initialList, prevPage, page + 1)
                } else {
                    val response =
                        videoService.getRelevantVideo(heuristicVideoId ?: "").awaitSync()
                            ?: error("Cannot get json object")
                    if (response.code != 200) {
                        error("Response code is ${response.code}! message: " + response.message)
                    }
                    val result = response.toVideos()
                    val nextPage: Int? = if (result.isEmpty()) {
                        null
                    } else {
                        // 更新推荐值
                        val index: Int = (result.size * Math.random()).toInt()
                        heuristicVideoId = result[index].id
                        page + 1
                    }
                    LoadResult.Page(result, prevPage, nextPage)
                }
            }.getOrElse {
                LoadResult.Error(it)
            }
        }
    }
}
