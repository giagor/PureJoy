package com.topview.purejoy.video.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.topview.purejoy.video.data.api.VideoService
import com.topview.purejoy.video.data.bean.toVideos
import com.topview.purejoy.video.entity.Video
import retrofit2.await
import java.util.*

class VideoSource(
    private val videoService: VideoService,
    private val initialList: ArrayList<Video>,
    private val maxPage: Int = Int.MAX_VALUE
): PagingSource<Int, Video>() {

    /**
     * 作为推荐参考的Video的vid
     */
    private var heuristicVideoId: String = initialList[initialList.size - 1].vid

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return runCatching {
            val page = params.key ?: 1
            val prevPage = if (page == 1) null else page - 1
            var nextPage = if (page < maxPage) page + 1 else null
            if (page == 1) {
                LoadResult.Page(initialList, prevPage, nextPage)
            } else {
                val response = videoService.getRelevantVideo(heuristicVideoId).await()
                if (!response.hasMore || response.outerList.isEmpty()) {
                    nextPage = null
                }
                LoadResult.Page(response.toVideos(), prevPage, nextPage)
            }
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}