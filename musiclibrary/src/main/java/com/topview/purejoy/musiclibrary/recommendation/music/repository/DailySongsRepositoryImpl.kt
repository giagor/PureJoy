package com.topview.purejoy.musiclibrary.recommendation.music.repository

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongsWrapper
import com.topview.purejoy.musiclibrary.recommendation.music.service.DailySongsService
import retrofit2.Call

class DailySongsRepositoryImpl(
    private val service: DailySongsService = ServiceCreator.create(DailySongsService::class.java),
) : DailySongsRepository {
    override fun requestDailySongsRepository(): Call<DailySongsWrapper> {
        return service.requestDailySongsServices()
    }
}