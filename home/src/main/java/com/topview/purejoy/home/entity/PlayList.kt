package com.topview.purejoy.home.entity

/**
 * 每日推荐歌单对应的实体类
 *
 * picUrl：歌单的封面
 * playCount：播放数量
 * songCounts：包含的歌曲数量
 * */
data class PlayList(
    var id: Long? = null,
    var name: String? = null,
    var picUrl: String? = null,
    var playCount: Long? = null,
    var songCounts: Int? = null
)

/**
 * 该类是对PlayList类的封装，主要用于分页加载的初次请求中，用于记录"歌单的总数量"
 * */
data class PlayListPagerWrapper(
    val playlists: List<PlayList>? = null,
    val playlistCount: Int? = null
)