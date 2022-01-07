package com.topview.purejoy.video.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.data.api.VideoService
import com.topview.purejoy.video.data.bean.toVideos
import retrofit2.await

internal class VideoSource(
    private val videoService: VideoService,
    private val initialList: List<Video>,
    private val maxPage: Int = Int.MAX_VALUE
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
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return runCatching {
            val page = params.key ?: 1
            val prevPage = if (page == 1) null else page - 1
            val nextPage = if (page < maxPage) page + 1 else null
            if (page == 1) {
                LoadResult.Page(initialList, prevPage, nextPage)
            } else {
                val response = videoService.getRelevantVideo(heuristicVideoId ?: "").await()
                if (response.code != 200) {
                    error("error http code: ${response.code}")
                }
                val result = response.toVideos()
                if (result.isNotEmpty()) {
                    // 更新推荐值
                    val index: Int = (result.size * Math.random()).toInt()
                    heuristicVideoId = result[index].id
                }
                LoadResult.Page(result, prevPage, nextPage)
            }
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}
