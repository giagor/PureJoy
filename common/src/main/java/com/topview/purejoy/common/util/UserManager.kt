package com.topview.purejoy.common.util

import com.topview.purejoy.common.entity.User


object UserManager {
    private var _user: User? = null
    val user: User? = _user

    fun login(user: User) {
        _user = user
    }

    fun logout() {
        _user = null
    }
}