package com.topview.purejoy.home.entity

import com.topview.purejoy.common.music.service.entity.AR
import com.topview.purejoy.common.music.service.entity.MusicItem

/**
 * id：歌曲的id
 * name：歌曲的名字
 * url：歌曲播放的url
 * picUrl：歌曲的封面图
 * artistName：歌手名字
 * mvId：歌曲mv的id
 * album：专辑信息
 * artists：歌手列表
 * */
data class Song(
    var id: Long? = null,
    var name: String? = null,
    var url: String? = null,
    var picUrl: String? = null,
    var artistName: String? = null,
    var mvId: Long? = null,
    var album: Album? = null,
    var artists: List<Artist>? = null
)

/**
 * 该类是对Song类的封装，主要用于分页加载的初次请求中，用于记录"歌曲的总数量"
 * */
data class SongPagerWrapper(val songs: List<Song>? = null, val songCount: Int? = null)

/**
 * 转化为音乐服务对应的实体类MusicItem
 * */
fun Song.toMusicItem(): MusicItem {
    val ar: MutableList<AR> = mutableListOf()
    artists?.forEach {
        ar.add(it.toAR())
    }
    return MusicItem(
        name = this.name ?: "",
        id = this.id ?: 0,
        url = this.url ?: "",
        ar = ar,
        al = this.album?.toAL() ?: Album().toAL(),
        mv = this.mvId ?: 0
    )
}