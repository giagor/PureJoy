package com.topview.purejoy.home.data.api

import com.topview.purejoy.home.data.bean.LoginStatusJson
import com.topview.purejoy.home.data.bean.PhoneExistJson
import com.topview.purejoy.home.data.bean.UserJson
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {
    @POST("/captcha/sent")
    @FormUrlEncoded
    fun sendCaptcha(
        @Field("phone")
        phone: String,
        @Field("timestamp")
        timestamp: String
    ): Call<Unit>

    @POST("/login/cellphone")
    @FormUrlEncoded
    fun loginWithCaptcha(
        @Field("phone")
        phone: String,
        @Field("captcha")
        captcha: String,
        @Field("timestamp")
        timestamp: String
    ): Call<UserJson>

    @POST("/login/cellphone")
    @FormUrlEncoded
    fun loginWithPassword(
        @Field("phone")
        phone: String,
        @Field("md5_password")
        password: String,
        @Field("timestamp")
        timestamp: String
    ): Call<UserJson>

    @POST("/cellphone/existence/check")
    @FormUrlEncoded
    fun checkExist(
        @Field("phone")
        phone: String,
        @Field("timestamp")
        timestamp: String
    ): Call<PhoneExistJson>

    /**
     * 这个接口除了可以拿来注册，也可以拿来修改密码
     * 疑似不需要nickname参数也能运行。
     * 目前没发现对nickname重名查询的接口，暂时移除nickname
     */
    @POST("/register/cellphone")
    fun registerOrChangePass(
        @Field("captcha")
        captcha: String,
        @Field("phone")
        phone: String,
        @Field("password")
        password: String,
        @Field("timestamp")
        timestamp: String
    ): Call<UserJson>

    @GET("/login/status")
    fun checkLoginStatus(): Call<LoginStatusJson>
}