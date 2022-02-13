package com.topview.purejoy.home.data.repo

import com.topview.purejoy.common.entity.User
import com.topview.purejoy.home.data.bean.UserJson
import com.topview.purejoy.home.data.source.LoginRemoteStore
import com.topview.purejoy.home.util.MD5Util
import java.util.*

internal object LoginRepository {
    private val loginRemoteStore = LoginRemoteStore()

    suspend fun requestCaptcha(phone: String) {
        loginRemoteStore.requestCaptcha(phone)
    }

    suspend fun loginWithCaptcha(
        phone: String,
        captcha: String
    ): User? {
        val json = loginRemoteStore.loginWithCaptcha(phone, captcha)
        return json?.code?.run {
            if (this != 200) {
                // 502是密码错误，503是验证码错误
                null
            } else {
                disposeProfile(json.profile)
            }
        }
    }

    suspend fun checkExist(phone: String): Boolean {
        val json = loginRemoteStore.checkExist(phone)
        return if (json != null) {
            if (json.code == null || json.code != 200) {
                // 不应当出现的情况，抛出异常
                throw CodeInvalidException("Response Code ${json.code} is invalid")
            }
            json.exist != null && json.exist == 1
        } else {
            false
        }
    }

    suspend fun register(
        phone: String,
        captcha: String,
    ): User? {
        val json = loginRemoteStore.registerOrChangePass(
            phone = phone,
            captcha = captcha,
            password = randomPassword()
        )
        return json?.code?.run {
            if (this != 200) {
                // 503是验证码错误
                null
            } else {
                disposeProfile(json.profile)
            }
        }
    }

    suspend fun loginWithPassword(
        phone: String,
        password: String
    ): User? {
        val json = loginRemoteStore.loginWithPassword(
            phone = phone,
            md5_password = MD5Util.simpleEncode(password)
        )
        return json?.code?.run {
            if (this != 200) {
                // 502是密码错误，400是手机号不存在
                null
            } else {
                disposeProfile(json.profile)
            }
        }
    }

    suspend fun checkLoginStatus() : User? {
        val json = loginRemoteStore.checkLoginStatus()
        return disposeProfile(json?.data?.profile)
    }

    suspend fun logout() {
        loginRemoteStore.logout()
    }

    private fun disposeProfile(profile: UserJson.Profile?): User? {
        return profile?.run {
            val nickname = nickname
            val userId = userId
            if (nickname != null && userId != null) {
                User(this.avatarUrl, nickname, userId, this.backgroundUrl)
            } else {
                null
            }
        }
    }

    class CodeInvalidException(message: String?): Throwable(message)

    private fun randomPassword() = UUID.randomUUID().toString().substring(0, 14)
}