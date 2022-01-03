package com.topview.purejoy.musiclibrary.router

import com.topview.purejoy.common.router.CommonRouter

object MusicLibraryRouter {
    const val ACTIVITY_MUSIC_LIBRARY_DAILY_RECOMMEND =
        "/musiclibrary/recommendation/music/view/daily/recommend"

    fun routeToDailyRecommendActivity() {
        routeWithoutParams(ACTIVITY_MUSIC_LIBRARY_DAILY_RECOMMEND)
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