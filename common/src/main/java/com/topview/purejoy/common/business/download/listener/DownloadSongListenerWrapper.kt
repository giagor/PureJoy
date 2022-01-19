package com.topview.purejoy.common.business.download.listener

import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.manager.DownloadingSongManager
import com.topview.purejoy.common.component.download.listener.user.SimpleUserDownloadListener
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.util.ThreadUtil

/**
 * !!：业务层下载歌曲应该使用这个监听器或这个监听器的子类
 * */
open class DownloadSongListenerWrapper : SimpleUserDownloadListener() {
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