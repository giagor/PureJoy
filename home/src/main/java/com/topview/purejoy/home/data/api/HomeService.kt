package com.topview.purejoy.home.data.api

import com.topview.purejoy.home.data.bean.BannerJson
import com.topview.purejoy.home.data.bean.DailyRecommendPlayListJson
import com.topview.purejoy.home.data.bean.RecommendNewSongJson
import com.topview.purejoy.home.data.bean.SearchSongJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {
    @GET("/banner")
    fun getBanners(@Query("type") type: Int): Call<BannerJson>

    @GET("/personalized")
    fun getDailyRecommendPlayList(@Query("limit") limit: Int): Call<DailyRecommendPlayListJson>

    @GET("/personalized/newsong")
    fun getRecommendNewSong(@Query("limit") limit: Int): Call<RecommendNewSongJson>

    @GET("/cloudsearch/search")
    fun getSearchSongs(
        @Query("keywords") keyword: String,
        @Query("type") type: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<SearchSongJson>
}