package com.topview.purejoy.musiclibrary.playlist.square.repo

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistSquareResponse
import com.topview.purejoy.musiclibrary.playlist.square.entity.PlaylistTagResponse
import com.topview.purejoy.musiclibrary.playlist.square.service.PlaylistSquareService
import retrofit2.Call

interface IPlaylistSquareRepository {
    fun requireTags(): Call<PlaylistTagResponse>
    fun requirePlaylists(limit: Int, order: String,
                         cat: String, offset: Int): Call<PlaylistSquareResponse>
}

class PlaylistSquareRepository(
    private val service: PlaylistSquareService = ServiceCreator.create(
        PlaylistSquareService::class.java),
) : IPlaylistSquareRepository {
    override fun requireTags(): Call<PlaylistTagResponse> {
        return service.requireTags()
    }

    override fun requirePlaylists(
        limit: Int,
        order: String,
        cat: String,
        offset: Int
    ): Call<PlaylistSquareResponse> {
        return service.requirePlaylists(limit, order, cat, offset)
    }

}