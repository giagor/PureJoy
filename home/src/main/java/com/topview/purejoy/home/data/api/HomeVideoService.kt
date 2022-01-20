package com.topview.purejoy.home.data.api

import com.topview.purejoy.home.data.bean.VideoCategoryJson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeVideoService {

    /**
     * 此接口需要登录
     */
    @GET("/video/category/list")
    fun getCategoryList(): Call<VideoCategoryJson>

    /**
     * 此接口需要登录
     */
    @GET("/video/timeline/recommend")
    fun getRecommendVideo(
        @Query("offset") offset: Int
    ): Call<ResponseBody>

    /**
     * 此接口需要登录
     */
    @GET("/video/group")
    fun getVideoByCategory(
        @Query("id") id: Long,
        @Query("offset") offset: Int
    ): Call<ResponseBody>
}