package com.topview.purejoy.common.router

import com.alibaba.android.arouter.launcher.ARouter
import com.topview.purejoy.common.component.webview.WebViewConstant

object CommonRouter {
    const val ACTIVITY_COMMON_WEBVIEW = "/common/component/webview"

    fun routeToWebViewActivity(url: String) {
        ARouter.getInstance()
            .build(ACTIVITY_COMMON_WEBVIEW)
            .withString(WebViewConstant.URL_EXTRA, url)
            .navigation()
    }
}