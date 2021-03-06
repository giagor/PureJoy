package com.topview.purejoy.common.business.data.repo

import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.data.source.CommonLocalStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CommonRepository {
    private val commonLocalStore: CommonLocalStore = CommonLocalStore()
    
    suspend fun getDownloadSongInfoList(): List<DownloadSongInfo>? =
        withContext(Dispatchers.IO) {
            commonLocalStore.getDownloadSongInfoList()
        }
}