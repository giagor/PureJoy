package com.topview.purejoy.home.tasks.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.components.PasswordLoginScreenState
import com.topview.purejoy.home.components.SnackBarState
import com.topview.purejoy.home.data.repo.LoginRepository

class PasswordViewModel: MVVMViewModel() {

    private var phone: String? = null

    private val loginRepository = LoginRepository
    /**
     * 界面状态
     */
    private var _passwordLoginScreenState: MutableLiveData<PasswordLoginScreenState> =
        MutableLiveData(PasswordLoginScreenState())
    val passwordLoginScreenState: LiveData<PasswordLoginScreenState> = _passwordLoginScreenState

    fun setPhone(phone: String?) {
       this.phone = phone
    }

    fun login() {
        _passwordLoginScreenState.value?.let { state ->
            state.loading = true
            viewModelScope.rxLaunch<User> {
                onRequest = {
                    phone?.let {
                        loginRepository.loginWithPassword(it, state.password)
                    } ?: throw IllegalStateException("手机号码不应当为空，请检查Fragment跳转时的传参")
                }
                onSuccess = {
                    state.loading = false
                    UserManager.login(it)
                    state.loginSuccess = true
                }
                onEmpty = {
                    state.loading = false
                    state.snackBarState = SnackBarState.Show("登录失败，请重试")
                }
                onError = {
                    onEmpty?.invoke(Unit)
                }
            }
        }
    }
}