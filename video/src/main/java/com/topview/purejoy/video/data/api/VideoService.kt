package com.topview.purejoy.video.data.api

import com.topview.purejoy.video.data.bean.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {

    /**
     * 根据视频id获取推荐视频，暂时来说mv和mlog的id都可以使用这个接口
     */
    @GET("/related/allvideo")
    fun getRelevantVideo(@Query("id") vid: String): Call<RelevantVideoJson>

    /**
     * 获取mv详情
     */
    @GET("/mv/detail")
    fun getMVDetail(@Query("mvid") mvid: String): Call<MVDetailJson>

    /**
     * 获取mv播放地址
     */
    @GET("/mv/url")
    fun getMVUrl(@Query("id") mvid: String): Call<MVUrlJson>

    /**
     * 获取mlog视频播放地址
     */
    @GET("/video/url")
    fun getVideoUrl(@Query("id") vid: String): Call<VideoUrlJson>
    /**
     * 获取mlog视频详情
     */
    @GET("/video/detail")
    fun getVideoDetail(@Query("id") vid: String): Call<VideoDetailJson>

    /**
     * 获取MV点赞详情
     */
    @GET("/mv/detail/info")
    fun getMVLikeInfo(@Query("mvid") mvid: String): Call<MVLikeInfoJson>
}