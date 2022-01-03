package com.topview.purejoy.common.router

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.topview.purejoy.common.component.webview.WebViewConstant

object CommonRouter {
    const val ACTIVITY_COMMON_WEBVIEW = "/common/component/webview"
    const val BUNDLE_EXTRA = "bundle"
    const val ACTIVITY_PLAYING = "/musiclibary/playing/view/playing"

    fun routeToWebViewActivity(url: String) {
        ARouter.getInstance()
            .build(ACTIVITY_COMMON_WEBVIEW)
            .withString(WebViewConstant.URL_EXTRA, url)
            .navigation()
    }

    fun routeToPlayingActivity() {
        routeWithoutParams(ACTIVITY_PLAYING)
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

    fun routeWithBundle(path: String, bundle: Bundle): Any? {
        return ARouter.getInstance().build(path).withBundle(BUNDLE_EXTRA, bundle).navigation()
    }
}