package com.topview.purejoy.home.data.api

import com.topview.purejoy.home.data.bean.BannerJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {
    @GET("/banner")
    fun getBanners(@Query("type") type: Int): Call<BannerJson>
}