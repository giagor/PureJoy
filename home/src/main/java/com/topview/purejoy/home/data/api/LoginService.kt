package com.topview.purejoy.home.data.api

import com.topview.purejoy.home.data.bean.PhoneExistJson
import com.topview.purejoy.home.data.bean.UserJson
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @POST("/captcha/sent")
    fun sendCaptcha(
        @Query("phone")
        phone: String,
        @Query("timestamp")
        timestamp: String
    ): Call<Unit>

    @POST("/login/cellphone")
    fun loginWithCaptcha(
        @Query("phone")
        phone: String,
        @Query("captcha")
        captcha: String,
        @Query("timestamp")
        timestamp: String
    ): Call<UserJson>

    @POST("/login/cellphone")
    fun loginWithPassword(
        @Query("phone")
        phone: String,
        @Query("md5_password")
        password: String,
        @Query("timestamp")
        timestamp: String
    ): Call<UserJson>

    @POST("/cellphone/existence/check")
    fun checkExist(
        @Query("phone")
        phone: String,
        @Query("timestamp")
        timestamp: String
    ): Call<PhoneExistJson>

    /**
     * 这个接口除了可以拿来注册，也可以拿来修改密码
     * 疑似不需要nickname参数也能运行。
     * 目前没发现对nickname重名查询的接口，暂时移除nickname
     */
    @POST("/register/cellphone")
    fun registerOrChangePass(
        @Query("captcha")
        captcha: String,
        @Query("phone")
        phone: String,
        @Query("password")
        password: String,
        @Query("timestamp")
        timestamp: String
    ): Call<UserJson>
}