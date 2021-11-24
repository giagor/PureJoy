package com.topview.purejoy.home.entity

/**
 * id：歌曲的id
 * name：歌曲的名字
 * picUrl：歌曲的封面图
 * artistName：歌手名字
 * */
data class Song(
    var id: Long?,
    var name: String? = null,
    var picUrl: String? = null,
    var artistName: String? = null
)