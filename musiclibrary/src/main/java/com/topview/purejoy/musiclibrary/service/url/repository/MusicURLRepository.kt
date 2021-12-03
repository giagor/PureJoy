package com.topview.purejoy.musiclibrary.service.url.repository

import com.topview.purejoy.musiclibrary.service.url.entity.URLItemWrapper
import retrofit2.Call

interface MusicURLRepository {
    fun requestMusicURL(id: String): Call<URLItemWrapper>
}