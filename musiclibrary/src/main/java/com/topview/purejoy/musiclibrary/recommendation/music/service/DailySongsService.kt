package com.topview.purejoy.musiclibrary.recommendation.music.service

import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongsWrapper
import retrofit2.Call
import retrofit2.http.GET

interface DailySongsService {
    @GET("recommend/songs")
    fun requestDailySongsServices(): Call<DailySongsWrapper>
}