package com.topview.purejoy.home.entity

import com.topview.purejoy.common.music.service.entity.AR

/**
 * id：歌手id
 * name：歌手名字
 * */
data class Artist(
    var id: Long? = null,
    var name: String? = null
)

/**
 * 转化为音乐服务的歌手实体类AR
 * */
fun Artist.toAR(): AR = AR(
    id = this.id ?: 0,
    name = this.name ?: ""
)