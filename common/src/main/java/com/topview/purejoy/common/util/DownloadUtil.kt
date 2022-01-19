package com.topview.purejoy.common.util

import android.Manifest
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.topview.purejoy.common.business.download.manager.DownloadingSongManager
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
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
        permissionAllowed: (() -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(activity)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    permissionAllowed?.invoke()
                    val task = DownloadManager.download(url, musicSaveDir, name, downloadListener)
                    DownloadingSongManager.put(task.tag, task)
                } else {
                    permissionDenied?.invoke()
                }
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
        permissionAllowed: (() -> Unit)? = null,
        permissionDenied: (() -> Unit)? = null
    ) {
        PermissionX.init(fragment)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    permissionAllowed?.invoke()
                    val task = DownloadManager.download(url, musicSaveDir, name, downloadListener)
                    DownloadingSongManager.put(task.tag, task)
                } else {
                    permissionDenied?.invoke()
                }
            }
    }
}