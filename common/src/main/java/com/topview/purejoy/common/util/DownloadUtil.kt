package com.topview.purejoy.common.util

import android.Manifest
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo
import com.topview.purejoy.common.business.download.listener.DownloadSongListenerWrapper
import com.topview.purejoy.common.business.download.manager.DownloadingSongManager
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
     * @param activity 申请权限需要使用到
     * @param task 下载的任务
     * @param downloadListener 下载监听器
     * @param permissionAllowed 用户同意权限时的回调（包括原来已授权权限）
     * @param permissionDenied 用户拒绝权限时的回调
     * */
    fun downloadMusic(
        activity: FragmentActivity,
        task: DownloadTask,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(activity)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                handleUserPermission(
                    allGranted,
                    task,
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
     * @param fragment 申请权限需要使用到
     * @param task 下载的任务
     * @param downloadListener 下载监听器
     * @param permissionAllowed 用户同意权限时的回调（包括原来已授权权限）
     * @param permissionDenied 用户拒绝权限时的回调
     * */
    fun downloadMusic(
        fragment: Fragment,
        task: DownloadTask,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(fragment)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                handleUserPermission(
                    allGranted,
                    task,
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
            val task = DownloadManager.download(
                url,
                musicSaveDir,
                name,
                DownloadSongListenerWrapper()
            )
            downloadListener?.let {
                task.registerObserver(it)
            }
            permissionAllowed?.invoke(task)
            DownloadingSongManager.put(task.tag, task)
            AppDatabaseManager.appDatabase?.let {
                val downloadSongInfo = DownloadSongInfo(
                    id = null,
                    name = name,
                    url = task.url,
                    path = task.path,
                    downloadedSize = 0,
                    totalSize = 0,
                    tag = task.tag
                )
                val downloadSongInfoDao = it.downloadSongInfoDao()
                ThreadUtil.runOnIO {
                    downloadSongInfoDao.insertDownloadSongInfo(
                        downloadSongInfo
                    )
                }
            }
        } else {
            permissionDenied?.invoke()
        }
    }

    private fun handleUserPermission(
        allGranted: Boolean,
        task: DownloadTask,
        downloadListener: UserDownloadListener? = null,
        permissionAllowed: ((DownloadTask) -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        if (allGranted) {
            task.download()
            downloadListener?.let {
                task.registerObserver(it)
            }
            permissionAllowed?.invoke(task)
            DownloadingSongManager.put(task.tag, task)
            AppDatabaseManager.appDatabase?.let {
                val downloadSongInfo = DownloadSongInfo(
                    id = null,
                    name = task.name,
                    url = task.url,
                    path = task.path,
                    downloadedSize = 0,
                    totalSize = 0,
                    tag = task.tag
                )
                val downloadSongInfoDao = it.downloadSongInfoDao()
                ThreadUtil.runOnIO {
                    downloadSongInfoDao.insertDownloadSongInfo(
                        downloadSongInfo
                    )
                }
            }
        } else {
            permissionDenied?.invoke()
        }
    }
}