package com.topview.purejoy.common.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topview.purejoy.common.entity.User


object UserManager {
    private val _userLiveData: MutableLiveData<User?> = MutableLiveData()
    val userLiveData: LiveData<User?> = _userLiveData

    fun login(user: User) {
        _userLiveData.value = user
    }

    fun logout() {
        _userLiveData.value = null
    }
}