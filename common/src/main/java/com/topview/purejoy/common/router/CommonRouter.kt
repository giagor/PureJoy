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

    /**
     * 不需要传递参数的路由，可以直接调用该方法
     *
     * @param path 路由地址
     * */
    fun routeWithoutParams(path: String): Any? {
        return ARouter.getInstance()
            .build(path)
            .navigation()
    }
}