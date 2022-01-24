package com.topview.purejoy.common.util

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.data.manager.DownloadingSongManager
import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.manage.DownloadManageActivity
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
import com.topview.purejoy.common.component.download.task.DownloadTask
import java.io.File

object DownloadUtil {
    /**
     * 路径为 /storage/emulated/0/Music
     * */
    private val musicSaveDir =
        Environment.getExternalStorageDirectory().absolutePath.plus(File.separator)
            .plus(Environment.DIRECTORY_MUSIC)

    /**
     * 下载音乐
     *
     * @param activity 申请权限需要使用到
     * @param name 歌曲的名字
     * @param url 下载的url
     * @param downloadListener 下载监听器
     * @param permissionAllowed 用户同意权限时的回调（包括原来已授权权限）
     * @param permissionDenied 用户拒绝权限时的回调
     * */
    fun downloadMusic(
        activity: FragmentActivity,
        name: String,
        url: String,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(activity)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                handleUserPermission(
                    allGranted,
                    name,
                    url,
                    downloadListener,
                    permissionAllowed,
                    permissionDenied
                )
            }
    }

    /**
     * 下载音乐
     *
     * @param fragment 申请权限需要使用到
     * @param name 歌曲的名字
     * @param url 下载的url
     * @param downloadListener 下载监听器
     * @param permissionAllowed 用户同意权限时的回调（包括原来已授权权限）
     * @param permissionDenied 用户拒绝权限时的回调
     * */
    fun downloadMusic(
        fragment: Fragment,
        name: String,
        url: String,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(fragment)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                handleUserPermission(
                    allGranted,
                    name,
                    url,
                    downloadListener,
                    permissionAllowed,
                    permissionDenied
                )
            }
    }

    /**
     * 下载音乐
     *
     * @param activity 申请权限需要使用到
     * @param songInfo 已有的歌曲下载记录
     * @param downloadListener 下载监听器
     * @param permissionAllowed 用户同意权限时的回调（包括原来已授权权限）
     * @param permissionDenied 用户拒绝权限时的回调
     * */
    fun downloadMusic(
        activity: FragmentActivity,
        songInfo: DownloadSongInfo,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(activity)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                handleUserPermission(
                    allGranted,
                    songInfo,
                    downloadListener,
                    permissionAllowed,
                    permissionDenied
                )
            }
    }

    /**
     * 下载音乐
     *
     * @param fragment 申请权限需要使用到
     * @param songInfo 已有的歌曲下载记录
     * @param downloadListener 下载监听器
     * @param permissionAllowed 用户同意权限时的回调（包括原来已授权权限）
     * @param permissionDenied 用户拒绝权限时的回调
     * */
    fun downloadMusic(
        fragment: Fragment,
        songInfo: DownloadSongInfo,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(fragment)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                handleUserPermission(
                    allGranted,
                    songInfo,
                    downloadListener,
                    permissionAllowed,
                    permissionDenied
                )
            }
    }

    private fun handleUserPermission(
        allGranted: Boolean,
        name: String,
        url: String,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        if (allGranted) {
            // 创建任务
            val task = DownloadManager.createTask(
                url,
                musicSaveDir,
                name,
                DownloadSongListenerWrapper()
            )
            // 注册监听器
            downloadListener?.let {
                task.registerObserver(it)
            }
            // 下载任务
            task.download()

            permissionAllowed?.invoke(task)
            if (DownloadingSongManager.get(task.tag) == null) {
                val songInfo = DownloadSongInfo.copyFromTask(task)
                DownloadingSongManager.put(songInfo.tag, songInfo)
            }
        } else {
            permissionDenied?.invoke()
        }
    }

    private fun handleUserPermission(
        allGranted: Boolean,
        songInfo: DownloadSongInfo,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        if (allGranted) {
            // 创建任务
            val task = DownloadManager.createTaskWithPath(
                songInfo.url,
                songInfo.path,
                songInfo.name,
                DownloadSongListenerWrapper()
            )
            // 注册监听器
            downloadListener?.let {
                task.registerObserver(it)
            }
            // 下载任务
            task.download()

            permissionAllowed?.invoke(task)

            if (DownloadingSongManager.get(songInfo.tag) == null) {
                DownloadingSongManager.put(songInfo.tag, songInfo)
            }
        } else {
            permissionDenied?.invoke()
        }
    }

    /**
     * DownloadUtil中使用这个监听器，完成业务层的下载数据的同步
     * */
    internal class DownloadSongListenerWrapper() :
        UserDownloadListener {

        private var startNotificationId: Int? = null

        companion object {
            private const val DOWNLOAD_NOTIFICATION_TITLE = "下载通知"
            private const val DOWNLOAD_SUCCESS = "下载成功"
            private const val DOWNLOADING = "正在下载"
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

        override fun onStarted(downloadTask: DownloadTask) {
            super.onStarted(downloadTask)

            // 启动"正在下载"的通知
            startNotificationId = notificationId
            val intent = Intent(CommonApplication.getContext(), DownloadManageActivity::class.java)
            // 跳转到下载管理界面的意图
            val pendingIntent = PendingIntent.getActivity(
                CommonApplication.getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            showNotification(downloadTask, DOWNLOADING, startNotificationId!!, pendingIntent)
        }

        override fun onFailure(downloadTask: DownloadTask, msg: String) {
            super.onFailure(downloadTask, msg)
            deleteDownloadSongInfoRecord(downloadTask.tag)
            startNotificationId?.let {
                NotificationHelper.cancelNotification(it)
            }
            showNotification(downloadTask, DOWNLOAD_FAILURE, notificationId)
        }

        override fun onCancelled(downloadTask: DownloadTask) {
            super.onCancelled(downloadTask)
            deleteDownloadSongInfoRecord(downloadTask.tag)
            startNotificationId?.let {
                NotificationHelper.cancelNotification(it)
            }
            showNotification(downloadTask, DOWNLOAD_CANCEL, notificationId)
        }

        override fun onSuccess(downloadTask: DownloadTask) {
            super.onSuccess(downloadTask)
            deleteDownloadSongInfoRecord(downloadTask.tag)
            startNotificationId?.let {
                NotificationHelper.cancelNotification(it)
            }
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
            notificationId: Int,
            pendingIntent: PendingIntent? = null
        ) {
            val builder: NotificationCompat.Builder =
                NotificationHelper.getCommonNotifyBuilder(NotificationHelper.DOWNLOAD_CHANNEL_ID)
                    .setContentTitle(DOWNLOAD_NOTIFICATION_TITLE)
                    .setContentText("${downloadTask.name} $contentText")
            pendingIntent?.let {
                builder.setContentIntent(it)
            }
            NotificationHelper.showNotification(notificationId, builder.build())
        }
    }
}