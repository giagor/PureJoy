package com.topview.purejoy.home.router

import com.alibaba.android.arouter.launcher.ARouter
import com.topview.purejoy.common.router.CommonRouter

object HomeRouter {
    const val ACTIVITY_HOME_SEARCH = "/home/search"
    const val ACTIVITY_HOME_TOPLIST = "/home/tasks/toplist"

    fun routeToSearchActivity() {
        routeWithoutParams(ACTIVITY_HOME_SEARCH)
    }

    fun routeToTopListActivity() {
        routeWithoutParams(ACTIVITY_HOME_TOPLIST)
    }

    /**
     * 不需要传递参数的路由，可以直接调用该方法
     *
     * @param path 路由地址
     * */
    private fun routeWithoutParams(path: String): Any? {
        return CommonRouter.routeWithoutParams(path)
    }
}