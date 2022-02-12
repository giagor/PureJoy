package com.topview.purejoy.home.components.status

/**
 * 简单表示页面状态的State类
 */
sealed class PageState {
    object Empty : PageState()
    object Success : PageState()
    object Error: PageState()
    object Loading: PageState()
}