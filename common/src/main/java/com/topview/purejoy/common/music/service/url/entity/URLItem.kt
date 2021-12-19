package com.topview.purejoy.common.music.service.url.entity

data class URLItemWrapper(val code: Int, val data: List<URLItem>)

data class URLItem(val id: Long, val url: String, val size: Long)
