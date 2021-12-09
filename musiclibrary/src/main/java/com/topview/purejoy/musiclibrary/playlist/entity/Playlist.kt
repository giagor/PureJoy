package com.topview.purejoy.musiclibrary.playlist.entity

data class TrackID(val id: Long)

data class Playlist(val id: Long, val name: String, val coverImgUrl: String,
                    val createTime: Long, val updateTime: Long, val trackCount: Long,
                    val playCount: Long, val description: String, val tags: List<String>,
                    val trackIds: List<TrackID>)

data class PlaylistResponse(val code: Int, val playlist: Playlist)