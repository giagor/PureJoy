package com.topview.purejoy.musiclibrary.recommendation.music.entity

import com.topview.purejoy.musiclibrary.entity.MusicItem

data class RecommendReason(val songId: Long, val reason: String)

data class DailySongs(val dailySongs: List<MusicItem>, val recommendReasons: List<RecommendReason>)

data class DailySongsWrapper(val code: Int, val data: DailySongs)

data class SongWithReason(val item: MusicItem, val reason: String? = null)