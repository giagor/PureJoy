package com.topview.purejoy.musiclibrary.playlist.square.entity

import com.topview.purejoy.musiclibrary.playlist.entity.Playlist

data class PlaylistTag(val name: String, val resourceCount: Long, val type: Int,
                       val category: Int, val resourceType: Int, val hot: Boolean,
                       val activity: Boolean)

data class PlaylistTagResponse(val code: Int, val sub: List<PlaylistTag>,
                               val categories: Map<String, String>)

data class PlaylistSquareResponse(val code: Int, val total: Int,
                                  val more: Boolean, val cat: String,
                                  val playlists: List<Playlist>)