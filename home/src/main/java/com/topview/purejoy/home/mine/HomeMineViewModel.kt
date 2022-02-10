package com.topview.purejoy.home.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.common.util.showToast
import com.topview.purejoy.home.data.repo.LoginRepository

class HomeMineViewModel : MVVMViewModel() {
    val userLiveData: LiveData<User?> = UserManager.userLiveData

    fun logout() {
        if (userLiveData.value == null) {
            return
        }

        viewModelScope.rxLaunch<Unit> {
            onRequest = {
                LoginRepository.logout()
            }

            onSuccess = {
                UserManager.logout()
            }

            onError = {
                showToast(CommonApplication.getContext(), "退出登陆失败")
            }
        }
    }
}