package com.topview.purejoy.video.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.video.data.VideoSource
import com.topview.purejoy.video.data.api.VideoService
import com.topview.purejoy.video.entity.Video
import kotlinx.coroutines.flow.Flow
import java.util.*

class VideoRepository(
    private val videoList: ArrayList<Video>,
    private val maxPage: Int = Int.MAX_VALUE
) {
    private val videoService = ServiceCreator.create(VideoService::class.java)

    fun getPagingVideoFlow(): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(
                pageSize = maxPage,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { VideoSource(videoService, videoList, maxPage) }
        ).flow
    }
}