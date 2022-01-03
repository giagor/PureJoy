package com.topview.purejoy.musiclibrary.router

import android.os.Bundle
import com.topview.purejoy.common.router.CommonRouter

object MusicLibraryRouter {
    const val ACTIVITY_MUSIC_LIBRARY_DAILY_RECOMMEND =
        "/musiclibrary/recommendation/music/view/daily/recommend"
    const val ACTIVITY_PLAYLIST_SQUARE = "/musiclibrary/playlist/square/view/square"
    const val ACTIVITY_PLAYLIST_DETAIL = "/musiclibrary/playlist/detail/view/detail"

    const val PLAYLIST_EXTRA = "playlist"

    fun routeToDailyRecommendActivity() {
        routeWithoutParams(ACTIVITY_MUSIC_LIBRARY_DAILY_RECOMMEND)
    }



    fun routeToPlaylistSquareActivity() {
        routeWithoutParams(ACTIVITY_PLAYLIST_SQUARE)
    }

    fun routeToPlaylistDetailActivity(pid: Long) {
        val bundle = Bundle()
        bundle.putLong(PLAYLIST_EXTRA, pid)
        CommonRouter.routeWithBundle(ACTIVITY_PLAYLIST_DETAIL, bundle)
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