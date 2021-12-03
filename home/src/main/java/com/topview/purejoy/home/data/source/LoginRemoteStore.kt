package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.await
import com.topview.purejoy.home.data.api.LoginService
import com.topview.purejoy.home.data.bean.PhoneExistJson
import com.topview.purejoy.home.data.bean.UserJson

class LoginRemoteStore {
    private val loginService = ServiceCreator.create(LoginService::class.java)

    suspend fun requestCaptcha(phone: String) =
        loginService.sendCaptcha(phone, getTimestamp()).await()

    suspend fun loginWithCaptcha(
        phone: String,
        captcha: String
    ): UserJson? = loginService.loginWithCaptcha(
        phone,
        captcha,
        getTimestamp()
    ).await()

    suspend fun checkExist(phone: String): PhoneExistJson? =
        loginService.checkExist(phone, getTimestamp()).await()


    suspend fun loginWithPassword(
        phone: String,
        md5_password: String,
    ): UserJson? = loginService.loginWithPassword(phone, md5_password, getTimestamp()).await()

    suspend fun registerOrChangePass(
        phone: String,
        captcha: String,
        password: String
    ): UserJson? = loginService.registerOrChangePass(
        captcha = captcha,
        phone = phone,
        password = password,
        timestamp = getTimestamp()
    ).await()


    private fun getTimestamp() = System.currentTimeMillis().toString()
}