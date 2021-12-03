package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.home.data.api.TopListService
import com.topview.purejoy.home.data.bean.LimitSongJson
import com.topview.purejoy.home.data.bean.TopListJson
import retrofit2.await

class TopListRemoteStore {
    private val topListService = ServiceCreator.create(TopListService::class.java)

    suspend fun getTopListDetail(): TopListJson = topListService.getTopListDetail().await()

    suspend fun getLimitTopListSong(id: Long, limit: Int): LimitSongJson =
        topListService.getLittleTopListSong(id, limit).await()
}