package com.topview.purejoy.musiclibrary.playlist.detail.repo

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.musiclibrary.entity.MusicResponse
import com.topview.purejoy.musiclibrary.playlist.detail.service.PlaylistDetailService
import com.topview.purejoy.musiclibrary.playlist.entity.PlaylistResponse
import retrofit2.Call

interface IPlaylistDetailRepository {

    fun getDetails(id: Long): Call<PlaylistResponse>

    fun requestSongDetails(ids: String): Call<MusicResponse>

}

class PlaylistDetailRepository(private val service: PlaylistDetailService =
    ServiceCreator.create(PlaylistDetailService::class.java)) : IPlaylistDetailRepository {
    override fun getDetails(id: Long): Call<PlaylistResponse> {
        return service.getDetails(id)
    }

    override fun requestSongDetails(ids: String): Call<MusicResponse> {
        return service.requestSongsDetails(ids)
    }

}