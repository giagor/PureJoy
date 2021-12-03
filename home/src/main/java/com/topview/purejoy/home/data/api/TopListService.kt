package com.topview.purejoy.home.data.api

import com.topview.purejoy.home.data.bean.LimitSongJson
import com.topview.purejoy.home.data.bean.TopListJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TopListService {
    @GET("/toplist/detail")
    fun getTopListDetail(): Call<TopListJson>

    @GET("/playlist/track/all")
    fun getLittleTopListSong(
        @Query("id") id: Long,
        @Query("limit") limit: Int
    ): Call<LimitSongJson>
}