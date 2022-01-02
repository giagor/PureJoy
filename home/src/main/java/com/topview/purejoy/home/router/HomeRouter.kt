package com.topview.purejoy.home.router

import com.alibaba.android.arouter.launcher.ARouter

object HomeRouter {
    const val ACTIVITY_HOME_SEARCH = "/home/search"

    fun routeToSearchActivity() {
        routeWithoutParams(ACTIVITY_HOME_SEARCH)
    }

    /**
     * 不需要传递参数的路由，可以直接调用该方法
     *
     * @param path 路由地址
     * */
    private fun routeWithoutParams(path: String): Any? {
        return ARouter.getInstance()
            .build(path)
            .navigation()
    }
}