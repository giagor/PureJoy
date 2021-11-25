package com.topview.purejoy.home.entity

/**
 * id：歌曲的id
 * name：歌曲的名字
 * picUrl：歌曲的封面图
 * artistName：歌手名字
 * */
data class Song(
    var id: Long? = null,
    var name: String? = null,
    var picUrl: String? = null,
    var artistName: String? = null
)

/**
 * 该类是对Song类的封装，主要用于分页加载的初次请求中，用于记录"歌曲的总数量"
 * */
data class SongPagerWrapper(val songs: List<Song>? = null, val songCount: Int? = null)