package com.topview.purejoy.home.data.repo

import com.topview.purejoy.common.entity.User
import com.topview.purejoy.home.data.bean.UserJson
import com.topview.purejoy.home.data.source.LoginRemoteStore
import com.topview.purejoy.home.util.MD5Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

internal object LoginRepository {
    private val loginRemoteStore = LoginRemoteStore()

    suspend fun requestCaptcha(phone: String) {
        withContext(Dispatchers.IO) {
            loginRemoteStore.requestCaptcha(phone)
        }
    }

    suspend fun loginWithCaptcha(
        phone: String,
        captcha: String
    ): User? {
        return withContext(Dispatchers.IO) {
            val json = loginRemoteStore.loginWithCaptcha(phone, captcha)
            json?.code?.run {
                if (this != 200) {
                    // 502是密码错误，503是验证码错误
                    null
                } else {
                    // 如果profile不为空但是本应当拥有的属性为空，会抛出异常
                    disposeProfile(json.profile)
                }
            }
        }
    }

    suspend fun checkExist(phone: String): Boolean {
        return withContext(Dispatchers.IO) {
            val json = loginRemoteStore.checkExist(phone)
            if (json != null) {
                if (json.code == null || json.code != 200) {
                    // 不应当出现的情况，抛出异常
                    throw CodeInvalidException("Response Code ${json.code} is invalid")
                }
                json.exist != null && json.exist == 1
            } else {
                false
            }
        }
    }

    suspend fun register(
        phone: String,
        captcha: String,
    ): User? {
        return withContext(Dispatchers.IO) {
            val json = loginRemoteStore.registerOrChangePass(
                phone = phone,
                captcha = captcha,
                password = randomPassword()
            )
            json?.code?.run {
                if (this != 200) {
                    // 503是验证码错误
                    null
                } else {
                    // 如果profile不为空但是本应当拥有的属性为空，会抛出异常
                    disposeProfile(json.profile)
                }
            }
        }
    }

    suspend fun loginWithPassword(
        phone: String,
        password: String
    ): User? {
        return withContext(Dispatchers.IO) {
            val json = loginRemoteStore.loginWithPassword(
                phone = phone,
                md5_password = MD5Util.simpleEncode(password)
            )
            json?.code?.run {
                if (this != 200) {
                    // 502是密码错误，400是手机号不存在
                    null
                } else {
                    // 如果profile不为空但是本应当拥有的属性为空，会抛出异常
                    disposeProfile(json.profile)
                }
            }
        }
    }

    private fun disposeProfile(profile: UserJson.Profile?): User? {
        return profile?.run {
            User(this.avatarUrl, this.nickname!!, this.userId!!, this.backgroundUrl)
        }
    }

    class CodeInvalidException(message: String?): Throwable(message)

    private fun randomPassword() = UUID.randomUUID().toString().substring(0, 14)
}