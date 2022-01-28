package com.topview.purejoy.home.router

import androidx.fragment.app.Fragment
import com.topview.purejoy.common.router.CommonRouter

object HomeRouter {
    const val ACTIVITY_HOME_SEARCH = "/home/search"
    const val ACTIVITY_HOME_TOPLIST = "/home/tasks/toplist"
    const val ACTIVITY_HOME_LOGIN = "/home/tasks/login"
    const val FRAGMENT_HOME_DISCOVER = "/home/discover"
    const val FRAGMENT_HOME_MINE = "/home/mine"
    const val FRAGMENT_HOME_VIDEO = "/home/video"
    const val FRAGMENT_HOME_SEARCH_RECOMMEND = "/home/search/content/recommend"
    const val FRAGMENT_HOME_SEARCH_SONG = "/home/search/content/song"
    const val FRAGMENT_HOME_SEARCH_PLAYLIST = "/home/search/content/playlist"
    const val FRAGMENT_HOME_SEARCH_TAB = "/home/search/tab"

    fun routeToSearchActivity() {
        routeWithoutParams(ACTIVITY_HOME_SEARCH)
    }

    fun routeToTopListActivity() {
        routeWithoutParams(ACTIVITY_HOME_TOPLIST)
    }

    fun routeToLoginActivity() {
        routeWithoutParams(ACTIVITY_HOME_LOGIN)
    }

    fun routeToDiscoverFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_DISCOVER) as? Fragment
    }

    fun routeToMineFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_MINE) as? Fragment
    }

    fun routeToVideoFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_VIDEO) as? Fragment
    }

    fun routeToSearchRecommendFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_SEARCH_RECOMMEND) as? Fragment
    }

    fun routeToSearchSongFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_SEARCH_SONG) as? Fragment
    }

    fun routeToSearchPlayListFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_SEARCH_PLAYLIST) as? Fragment
    }

    fun routeToSearchTabFragment(): Fragment? {
        return routeWithoutParams(FRAGMENT_HOME_SEARCH_TAB) as? Fragment
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