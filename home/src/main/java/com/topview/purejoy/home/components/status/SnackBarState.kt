package com.topview.purejoy.home.components.status

sealed class SnackBarState(val show: Boolean) {
    object None : SnackBarState(false)
    class Show(val message: String): SnackBarState(true)
}