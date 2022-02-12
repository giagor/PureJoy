package com.topview.purejoy.home.entity

import com.topview.purejoy.common.music.service.entity.AL

/**
 * id：专辑id
 * name：专辑名字
 * picUrl：专辑封面图片
 * */
data class Album(
    var id: Long? = null,
    var name: String? = null,
    var picUrl: String? = null,
)

/**
 * 转化为音乐服务的专辑实体类AL
 * */
fun Album.toAL(): AL = AL(
    id = this.id ?: 0,
    name = this.name ?: "",
    picUrl = this.picUrl ?: ""
)