package com.topview.purejoy.common.music.service.url.repository

import com.topview.purejoy.common.music.service.url.entity.URLItemWrapper
import retrofit2.Call

interface MusicURLRepository {
    fun requestMusicURL(id: String): Call<URLItemWrapper>
}