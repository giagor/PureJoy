package com.topview.purejoy.musiclibrary.recommendation.music.repository

import com.topview.purejoy.musiclibrary.recommendation.music.entity.DailySongsWrapper
import retrofit2.Call

interface DailySongsRepository {
    fun requestDailySongsRepository(): Call<DailySongsWrapper>
}