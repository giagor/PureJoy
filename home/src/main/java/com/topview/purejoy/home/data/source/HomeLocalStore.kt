package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo

class HomeLocalStore {
    fun getDownloadSongInfoList(): List<DownloadSongInfo> {
        val appDatabase =
            checkNotNull(AppDatabaseManager.appDatabase, { "Didn't initialize AppDatabase yet" })
        val dao = appDatabase.downloadSongInfoDao()
        return dao.queryDownloadSongInfo()
    }
}