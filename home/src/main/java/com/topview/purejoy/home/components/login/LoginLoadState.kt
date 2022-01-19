package com.topview.purejoy.home.components.login

sealed class LoginLoadState {
    object None : LoginLoadState()
    object Loading : LoginLoadState()
    object Success : LoginLoadState()
    class Error(val message: String) : LoginLoadState()
}
