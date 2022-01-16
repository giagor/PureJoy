package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.home.data.api.LoginService
import com.topview.purejoy.home.data.bean.PhoneExistJson
import com.topview.purejoy.home.data.bean.UserJson

class LoginRemoteStore {
    private val loginService = ServiceCreator.create(LoginService::class.java)

    suspend fun requestCaptcha(phone: String) =
        loginService.sendCaptcha(phone, getTimestamp()).awaitAsync()

    suspend fun loginWithCaptcha(
        phone: String,
        captcha: String
    ): UserJson? = loginService.loginWithCaptcha(
        phone,
        captcha,
        getTimestamp()
    ).awaitAsync()

    suspend fun checkExist(phone: String): PhoneExistJson? =
        loginService.checkExist(phone, getTimestamp()).awaitAsync()


    suspend fun loginWithPassword(
        phone: String,
        md5_password: String,
    ): UserJson? = loginService.loginWithPassword(phone, md5_password, getTimestamp()).awaitAsync()

    suspend fun registerOrChangePass(
        phone: String,
        captcha: String,
        password: String
    ): UserJson? = loginService.registerOrChangePass(
        captcha = captcha,
        phone = phone,
        password = password,
        timestamp = getTimestamp()
    ).awaitAsync()


    private fun getTimestamp() = System.currentTimeMillis().toString()
}