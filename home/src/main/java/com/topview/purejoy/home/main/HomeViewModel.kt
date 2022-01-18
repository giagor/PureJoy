package com.topview.purejoy.home.main

import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.data.repo.LoginRepository

class HomeViewModel : MVVMViewModel() {
    /**
     * 自动登录
     * */
    fun keepLogin() {
        if (UserManager.userLiveData.value != null) {
            return
        }

        viewModelScope.rxLaunch<User?> {
            onRequest = {
                LoginRepository.checkLoginStatus()
            }

            onSuccess = { user: User? ->
                user?.let {
                    UserManager.login(it)
                }
            }
        }
    }
}