package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.manager.DownloadingSongManager
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.task.DownloadTask
import java.util.*

class HomeLocalStore {
    fun getDownloadTaskList(): List<DownloadTask> {
        val appDatabase =
            checkNotNull(AppDatabaseManager.appDatabase, { "Didn't initialize AppDatabase yet" })
        val dao = appDatabase.downloadSongInfoDao()
        // 数据库中查询下载歌曲的信息
        val downloadSongInfoList = dao.queryDownloadSongInfo()
        // 存放下载任务的列表
        val downloadTasks: LinkedList<DownloadTask> = LinkedList<DownloadTask>()
        // 将下载歌曲信息映射为下载任务
        for (downloadSongInfo in downloadSongInfoList) {
            val downloadingTask = DownloadingSongManager.get(downloadSongInfo.tag)
            // 若该歌曲没有正在下载，则为其创建一个对应的下载任务
            if (downloadingTask == null) {
                downloadTasks.addLast(
                    DownloadManager.createTask(
                        downloadSongInfo.url,
                        downloadSongInfo.path,
                        downloadSongInfo.name,
                        null
                    )
                )
                // 若该歌曲正在下载，则直接获取其对应的下载任务
            } else {
                downloadTasks.addFirst(downloadingTask)
            }
        }
        return downloadTasks
    }
}