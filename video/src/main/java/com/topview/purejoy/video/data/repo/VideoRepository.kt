package com.topview.purejoy.video.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.video.data.VideoSource
import com.topview.purejoy.video.data.api.VideoService
import com.topview.purejoy.video.data.bean.toVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VideoRepository(
    private val videoList: List<Video>,
    private val maxPage: Int = Int.MAX_VALUE
) {
    private val videoService = ServiceCreator.create(VideoService::class.java)
    private val pager: Pager<Int, Video> by lazy {
        Pager(
            config = PagingConfig(
                pageSize = maxPage,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { VideoSource(videoService, videoList, maxPage) }
        )
    }

    fun getPagingVideoFlow(): Flow<PagingData<Video>> {
        return pager.flow
    }

    suspend fun getVideoPlayUrl(vid: String): String? =
        withContext(Dispatchers.IO) {
            val json = videoService.getVideoUrl(vid = vid).awaitAsync()
            json?.url?.get(0)?.url
        }


    suspend fun getMVPlayUrl(id: String): String? =
        withContext(Dispatchers.IO) {
            val json = videoService.getMVUrl(id).awaitAsync()
            json?.data?.url
        }


    suspend fun loadDetailOfVideo(video: Video) {
        withContext(Dispatchers.IO) {
            if (video.isMv) {
                val detailJson = videoService.getMVDetail(video.id).awaitAsync()
                detailJson?.mappingToVideo(video)
                val likeJson = videoService.getMVLikeInfo(video.id).awaitAsync()
                video.likedCount = likeJson?.likedCount ?: 0
            } else {
                val json = videoService.getVideoDetail(video.id).awaitAsync()
                json?.data?.toVideo()?.let {
                    video.merge(it)
                }
            }
        }
    }
}