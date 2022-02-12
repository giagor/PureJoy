package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.awaitSync
import com.topview.purejoy.home.data.api.TopListService
import com.topview.purejoy.home.data.bean.LimitSongJson
import com.topview.purejoy.home.data.bean.TopListJson

class TopListRemoteStore {
    private val topListService = ServiceCreator.create(TopListService::class.java)

    fun getTopListDetail(): TopListJson? = topListService.getTopListDetail().awaitSync()

    fun getLimitTopListSong(id: Long, limit: Int?): LimitSongJson? =
        topListService.getLimitTopListSong(id, limit).awaitSync()
}