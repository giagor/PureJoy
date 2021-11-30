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