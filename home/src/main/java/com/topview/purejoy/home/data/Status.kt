package com.topview.purejoy.home.data

/**
 * 表示数据的获取状态
 * */
internal object Status {
    /**
     * Discover相关
     * */
    const val DISCOVER_BANNER_NET_ERROR = "discover_banner_net_error"
    const val DISCOVER_DAILY_RECOMMEND_PLAYLIST_NET_ERROR =
        "discover_daily_recommend_playlist_net_error"
    const val DISCOVER_RECOMMEND_NEW_SONG_NET_ERROR =
        "discover_recommend_new_song_net_error"

    /**
     * Search相关
     * */
    const val SEARCH_SONG_LOAD_MORE_NET_ERROR = "search_song_load_more_net_error"
    const val SEARCH_PLAYLIST_LOAD_MORE_NET_ERROR = "search_playlist_load_more_net_error"
    const val SEARCH_SONG_REQUEST_URL_ERROR = "search_song_request_url_error"
    const val SEARCH_SONG_REQUEST_URL_ID_EMPTY = "search_song_request_url_ID_EMPTY"
    const val SEARCH_SONG_FIRST_ERROR = "search_song_first_error"
    const val SEARCH_PLAYLIST_FIRST_ERROR = "search_playlist_first_error"
}