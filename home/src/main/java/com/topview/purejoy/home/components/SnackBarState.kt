package com.topview.purejoy.home.components

sealed class SnackBarState(val show: Boolean) {
    object None : SnackBarState(false)
    class Show(val message: String): SnackBarState(true)
}