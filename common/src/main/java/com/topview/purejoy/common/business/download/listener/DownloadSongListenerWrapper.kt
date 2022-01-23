package com.topview.purejoy.common.business.download.listener

import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo
import com.topview.purejoy.common.business.download.manager.DownloadingSongManager
import com.topview.purejoy.common.component.download.listener.user.SimpleUserDownloadListener
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.util.NotificationHelper
import com.topview.purejoy.common.util.ThreadUtil

/**
 * DownloadUtil中使用这个监听器，完成业务层的下载数据的同步
 * */
internal class DownloadSongListenerWrapper : SimpleUserDownloadListener() {
    companion object {
        private const val DOWNLOAD_NOTIFICATION_TITLE = "下载通知"
        private const val DOWNLOAD_SUCCESS = "下载成功"
        private const val DOWNLOAD_FAILURE = "下载失败"
        private const val DOWNLOAD_CANCEL = "下载取消"
        private const val ALREADY_DOWNLOADED = "已经下载过了"
        private var notificationId: Int = 1
            get() {
                return field++
            }
    }

    override fun insertTaskToDb(downloadTask: DownloadTask) {
        super.insertTaskToDb(downloadTask)

        // 业务层同步将数据插入到数据库中
        AppDatabaseManager.appDatabase?.let {
            ThreadUtil.runOnIO {
                val downloadSongInfo = DownloadSongInfo.copyFromTask(downloadTask)
                it.downloadSongInfoDao().insertDownloadSongInfo(downloadSongInfo)
            }
        }
    }

    override fun onFailure(downloadTask: DownloadTask, msg: String) {
        super.onFailure(downloadTask, msg)
        deleteDownloadSongInfoRecord(downloadTask.tag)
        showNotification(downloadTask, DOWNLOAD_FAILURE, notificationId)
    }

    override fun onCancelled(downloadTask: DownloadTask) {
        super.onCancelled(downloadTask)
        deleteDownloadSongInfoRecord(downloadTask.tag)
        showNotification(downloadTask, DOWNLOAD_CANCEL, notificationId)
    }

    override fun onSuccess(downloadTask: DownloadTask) {
        super.onSuccess(downloadTask)
        deleteDownloadSongInfoRecord(downloadTask.tag)
        showNotification(downloadTask, DOWNLOAD_SUCCESS, notificationId)
    }

    override fun alreadyDownloaded(downloadTask: DownloadTask) {
        super.alreadyDownloaded(downloadTask)
        deleteDownloadSongInfoRecord(downloadTask.tag)
        showNotification(downloadTask, ALREADY_DOWNLOADED, notificationId)
    }

    private fun deleteDownloadSongInfoRecord(tag: String) {
        DownloadingSongManager.remove(tag)
        AppDatabaseManager.appDatabase?.let {
            ThreadUtil.runOnIO { it.downloadSongInfoDao().deleteDownloadSongInfo(tag) }
        }
    }

    private fun showNotification(
        downloadTask: DownloadTask,
        contentText: String,
        notificationId: Int
    ) {
        val notification =
            NotificationHelper.getCommonNotifyBuilder(NotificationHelper.DOWNLOAD_CHANNEL_ID)
                .setContentTitle(DOWNLOAD_NOTIFICATION_TITLE)
                .setContentText("${downloadTask.name} $contentText")
                .build()
        NotificationHelper.showNotification(notificationId, notification)
    }
}