package com.topview.purejoy.home.tasks.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.components.CaptchaFieldState
import com.topview.purejoy.home.components.CaptchaScreenState
import com.topview.purejoy.home.components.SnackBarState
import com.topview.purejoy.home.data.repo.LoginRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CaptchaViewModel: MVVMViewModel() {

    private val loginRepository = LoginRepository

    /**
     * 界面状态
     */
    private val _captchaUiState: MutableLiveData<CaptchaScreenState> = MutableLiveData()
    val captchaUiState: LiveData<CaptchaScreenState> = _captchaUiState

    /**
     * 生成秒数序列的Flow
     */
    private var countDownFlow = flow {
        for (i in 60 downTo 0) {
            emit(i)
            delay(1000L)
        }
    }.conflate()

    private lateinit var flowJob: Job

    private var _timeSecond = mutableStateOf(60)

    /**
     * 对外暴露的时间读数
     */
    val time: Int by _timeSecond

    init {
       restartFlow()
    }

    fun setPhone(phone: String?) {
        _captchaUiState.value = CaptchaScreenState(
            CaptchaFieldState(),
            phone ?: ""
        )
    }

    fun restartCountDown() {
        if (flowJob.isActive) {
            flowJob.cancel()
        }
        restartFlow()
    }

    /**
     * 发起登录或创建用户请求
     */
    fun loginOrRegister() {
        captchaUiState.value?.let { state ->
            viewModelScope.rxLaunch<User> {
                onRequest = {
                    val captcha = state.captchaFieldState.text
                    // 清空验证码
                    state.captchaFieldState.text = ""
                    if (loginRepository.checkExist(state.phone)) {
                        loginRepository.loginWithCaptcha(state.phone, captcha)
                    } else {
                        loginRepository.register(state.phone, captcha)
                    }
                }
                onSuccess = {
                    UserManager.login(it)
                    state.loginSuccess = true
                }
                onEmpty = {
                    state.snackBarState = SnackBarState.Show("登录失败，请重试")
                }
                onError = {
                    onEmpty?.invoke(Unit)
                }
            }
        }
    }

    private fun restartFlow() {
        // 启动协程来更新时间读数
        flowJob = viewModelScope.launch {
            countDownFlow.collect {
                _timeSecond.value = it
            }
        }
    }
}