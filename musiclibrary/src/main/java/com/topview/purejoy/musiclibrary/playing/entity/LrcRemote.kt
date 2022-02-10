package com.topview.purejoy.musiclibrary.playing.entity

data class Lrc(val version: Int, val lyric: String)

data class LrcWrapper(val lrc: Lrc?, val klyric: Lrc?, val tlyric: Lrc?, val needDesc: Boolean = false)

data class LrcResponse(val wrapper: LrcWrapper?, val id: Long)