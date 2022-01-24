package com.topview.purejoy.common.business.data.source

import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.data.manager.DownloadingSongManager
import java.util.*

class CommonLocalStore {
    fun getDownloadSongInfoList(): List<DownloadSongInfo> {
        val appDatabase =
            checkNotNull(AppDatabaseManager.appDatabase, { "Didn't initialize AppDatabase yet" })
        val dao = appDatabase.downloadSongInfoDao()
        // 数据库中查询下载歌曲的信息
        val localSongInfoList = dao.queryDownloadSongInfo()
        // 要返回的List
        val songInfoList = LinkedList<DownloadSongInfo>()
        for (localInfo in localSongInfoList) {
            val memoryInfo = DownloadingSongManager.get(localInfo.tag)
            // 若该歌曲没有正在下载，则添加到最后
            if (memoryInfo == null) {
                songInfoList.addLast(localInfo)
                // 若该歌曲正在下载，则添加到列表的最前面
            } else {
                songInfoList.addFirst(memoryInfo)
            }
        }
        return songInfoList
    }
}