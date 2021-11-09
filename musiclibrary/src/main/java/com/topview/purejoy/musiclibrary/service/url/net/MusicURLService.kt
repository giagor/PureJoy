package com.topview.purejoy.musiclibrary.service.url.net

import com.topview.purejoy.musiclibrary.service.url.entity.URLItemWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface MusicURLService {
    @GET("song/url")
    fun requestMusicURL(@Query("id") id: String) : Call<URLItemWrapper>
}