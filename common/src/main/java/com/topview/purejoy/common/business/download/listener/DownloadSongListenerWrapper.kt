package com.topview.purejoy.common.business.download.listener

import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.manager.DownloadingSongManager
import com.topview.purejoy.common.component.download.listener.user.SimpleUserDownloadListener
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.util.ThreadUtil

/**
 * DownloadUtil中使用这个监听器，完成业务层的下载数据的同步
 * */
internal class DownloadSongListenerWrapper : SimpleUserDownloadListener() {
    override fun onFailure(downloadTask: DownloadTask, msg: String) {
        super.onFailure(downloadTask, msg)
        deleteDownloadSongInfoRecord(downloadTask.tag)
    }

    override fun onCancelled(downloadTask: DownloadTask) {
        super.onCancelled(downloadTask)
        deleteDownloadSongInfoRecord(downloadTask.tag)
    }

    override fun onSuccess(downloadTask: DownloadTask) {
        super.onSuccess(downloadTask)
        deleteDownloadSongInfoRecord(downloadTask.tag)
    }

    override fun alreadyDownloaded(downloadTask: DownloadTask) {
        super.alreadyDownloaded(downloadTask)
        deleteDownloadSongInfoRecord(downloadTask.tag)
    }

    private fun deleteDownloadSongInfoRecord(tag: String) {
        DownloadingSongManager.remove(tag)
        AppDatabaseManager.appDatabase?.let {
            ThreadUtil.runOnIO { it.downloadSongInfoDao().deleteDownloadSongInfo(tag) }
        }
    }
}