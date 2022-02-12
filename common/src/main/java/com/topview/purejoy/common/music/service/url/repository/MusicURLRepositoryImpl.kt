package com.topview.purejoy.common.music.service.url.repository

import com.topview.purejoy.common.music.service.url.entity.URLItemWrapper
import com.topview.purejoy.common.music.service.url.net.MusicURLService
import com.topview.purejoy.common.net.ServiceCreator
import retrofit2.Call

class MusicURLRepositoryImpl(
    private val service: MusicURLService = ServiceCreator.create(MusicURLService::class.java),
) : MusicURLRepository {

    override fun requestMusicURL(id: String): Call<URLItemWrapper> {
        return service.requestMusicURL(id = id)
    }
}