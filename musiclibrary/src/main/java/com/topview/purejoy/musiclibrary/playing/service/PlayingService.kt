package com.topview.purejoy.musiclibrary.playing.service

import com.topview.purejoy.musiclibrary.playing.entity.LrcWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayingService {
    @GET("lyric")
    fun requestLrc(@Query("id") id: Long): Call<LrcWrapper>
}