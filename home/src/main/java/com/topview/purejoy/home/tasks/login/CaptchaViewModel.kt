package com.topview.purejoy.home.tasks.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.components.login.LoginLoadState
import com.topview.purejoy.home.data.repo.LoginRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CaptchaViewModel: MVVMViewModel() {

    private val loginRepository = LoginRepository

    private val _captchaLoginState: MutableStateFlow<LoginLoadState> = MutableStateFlow(
        LoginLoadState.None
    )
    var captchaLoginState: StateFlow<LoginLoadState> = _captchaLoginState

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

    fun restartCountDown() {
        if (flowJob.isActive) {
            flowJob.cancel()
        }
        restartFlow()
    }

    /**
     * 发起登录或创建用户请求
     */
    fun loginOrRegister(phone: String, captcha: String) {
        if (_captchaLoginState.value !is LoginLoadState.Loading) {
            viewModelScope.rxLaunch<User> {
                onRequest = {
                    _captchaLoginState.value = LoginLoadState.Loading
                    if (loginRepository.checkExist(phone)) {
                        loginRepository.loginWithCaptcha(phone, captcha)
                    } else {
                        loginRepository.register(phone, captcha)
                    }
                }
                onSuccess = {
                    UserManager.login(it)
                    _captchaLoginState.value = LoginLoadState.Success
                }
                onEmpty = {
                    _captchaLoginState.value = LoginLoadState.Error("登录失败，请重试")
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