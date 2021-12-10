package com.topview.purejoy.musiclibrary.playlist.square.service

import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistSquareResponse
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistTagResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaylistSquareService {

    @GET("/playlist/catlist")
    fun requireTags(): Call<PlaylistTagResponse>

    @GET("/top/playlist")
    fun requirePlaylists(@Query("limit") limit: Int,
                         @Query("order") order: String,
                         @Query("cat") cat: String,
                         @Query("offset") offset: Int): Call<PlaylistSquareResponse>
}