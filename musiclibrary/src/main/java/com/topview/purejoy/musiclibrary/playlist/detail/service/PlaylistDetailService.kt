package com.topview.purejoy.musiclibrary.playlist.detail.service

import com.topview.purejoy.common.music.service.entity.MusicResponse
import com.topview.purejoy.musiclibrary.playlist.entity.PlaylistResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaylistDetailService {
    @GET("/playlist/detail")
    fun getDetails(@Query("id") id: Long): Call<PlaylistResponse>

    @GET("/song/detail")
    fun requestSongsDetails(@Query("ids") ids: String): Call<MusicResponse>

}