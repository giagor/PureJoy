package com.topview.purejoy.home.tasks.login

import androidx.lifecycle.viewModelScope
import com.topview.purejoy.common.entity.User
import com.topview.purejoy.common.mvvm.viewmodel.MVVMViewModel
import com.topview.purejoy.common.util.UserManager
import com.topview.purejoy.home.components.login.PasswordLoginScreenState
import com.topview.purejoy.home.components.status.PageState
import com.topview.purejoy.home.components.status.SnackBarEvent
import com.topview.purejoy.home.data.repo.LoginRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class PasswordViewModel: MVVMViewModel() {

    private var phone: String? = null

    private val loginRepository = LoginRepository
    /**
     * 界面状态
     */
    private var _screenState: MutableStateFlow<PasswordLoginScreenState> =
        MutableStateFlow(PasswordLoginScreenState())

    val screenState: StateFlow<PasswordLoginScreenState> = _screenState

    /**
     * SnackBar的弹出事件
     */
    private val _snackBarEvent: MutableSharedFlow<SnackBarEvent> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1
    )
    val snackBarEvent: SharedFlow<SnackBarEvent> = _snackBarEvent

    fun setPhone(phone: String?) {
       this.phone = phone
    }

    fun login() {
        _screenState.value.let { state ->
            state.pageState = PageState.Loading
            viewModelScope.rxLaunch<User> {
                onRequest = {
                    phone?.let {
                        loginRepository.loginWithPassword(it, state.password)
                    } ?: throw IllegalStateException("手机号码不应当为空，请检查Fragment跳转时的传参")
                }
                onSuccess = {
                    UserManager.login(it)
                    state.pageState = PageState.Success
                }
                onEmpty = {
                    state.pageState = PageState.Error
                    _snackBarEvent.tryEmit(SnackBarEvent("登录失败，请重试"))
                }
                onError = {
                    onEmpty?.invoke(Unit)
                }
            }
        }
    }
}