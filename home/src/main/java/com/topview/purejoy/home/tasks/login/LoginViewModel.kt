package com.topview.purejoy.home.tasks.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.home.components.PhoneUiState
import com.topview.purejoy.home.components.SnackBarState
import com.topview.purejoy.home.data.repo.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel: MVVMViewModel() {

    private val loginRepository: LoginRepository = LoginRepository

    /**
     * 手机号码界面状态
     */
    private val _phoneUiState: MutableLiveData<PhoneUiState> = MutableLiveData(PhoneUiState())
    val phoneUiState: LiveData<PhoneUiState> = _phoneUiState

    /**
     * 验证码请求状态
     */
    private val _captchaState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val captchaState: StateFlow<Boolean> = _captchaState

    /**
     * 从UI状态中读取号码并发起请求
     */
    fun requestCode() {
        _phoneUiState.value?.let { state ->
            // 仅接受一次loading
            if (! state.loading) {
                if (state.text.length < 11) {
                    _phoneUiState.value?.snackBarState = SnackBarState.Show("请输入合法的手机号码")
                    return@let
                }
                state.loading = true
                viewModelScope.rxLaunch<Unit> {
                    onRequest = {
                        loginRepository.requestCaptcha(state.text)
                    }
                    onSuccess = {
                        resetPhoneUiState()
                        _captchaState.value = true
                    }
                    onError = {
                        state.snackBarState = SnackBarState.Show("请求验证码失败")
                        resetPhoneUiState()
                    }
                }
            }
        }
    }

    /**
     * 单纯请求发送验证码，不进行任何UI状态更新
     */
    fun requestCode(phone: String?) {
        if (phone != null) {
            viewModelScope.rxLaunch<Unit> {
                onRequest = {
                    loginRepository.requestCaptcha(phone)
                }
            }
        }
    }

    fun changeText(newValue: String) {
        _phoneUiState.value?.let { uiState ->
            if (uiState.loading) {
                return@let
            }
            if (newValue.isEmpty()) {
                uiState.text = newValue
                // 禁用button
                uiState.buttonEnable = false
            } else {
                newValue.toLongOrNull()?.let {
                    uiState.buttonEnable = true
                    // 实现字符过滤
                    uiState.text = newValue
                }
            }
        }
    }

    fun resetCaptchaStatus() {
        _captchaState.value = false
    }

    private fun resetPhoneUiState() {
        phoneUiState.value?.apply {
            loading = false
        }
    }
}