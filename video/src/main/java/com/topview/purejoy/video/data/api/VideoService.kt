package com.topview.purejoy.video.data.api

import com.topview.purejoy.video.data.bean.RelevantVideoJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {

    /**
     * 获取推荐视频
     */
    @GET("/related/allvideo")
    fun getRelevantVideo(@Query("id") vid: String): Call<RelevantVideoJson>
}