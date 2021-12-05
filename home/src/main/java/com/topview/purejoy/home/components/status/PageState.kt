package com.topview.purejoy.home.components.status

sealed class PageState {
    object Empty : PageState()
    object Success : PageState()
    object Error: PageState()
    object Loading: PageState()
}