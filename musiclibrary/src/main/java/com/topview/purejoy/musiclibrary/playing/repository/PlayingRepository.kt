package com.topview.purejoy.musiclibrary.playing.repository

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.musiclibrary.playing.entity.LrcResponse
import com.topview.purejoy.musiclibrary.playing.service.PlayingService

class PlayingRepository(
    private val remoteSource: PlayingService = ServiceCreator.create(PlayingService::class.java),
) {
    suspend fun requestLrc(id: Long): LrcResponse {
        val w = remoteSource.requestLrc(id).awaitAsync()
        return LrcResponse(wrapper = w, id = id)
    }
}