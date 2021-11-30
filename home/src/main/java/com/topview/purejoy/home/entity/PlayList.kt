package com.topview.purejoy.home.entity

/**
 * 每日推荐歌单对应的实体类
 * */
data class PlayList(
    var id: Long? = null,
    var name: String? = null,
    var picUrl: String? = null,
    var playCount: Long? = null
) 