package com.topview.purejoy.common.util

import android.Manifest
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener

object DownloadUtil {
    private const val MUSIC_SAVE_DIR = "/storage/emulated/0/Music"

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
                    DownloadManager.download(url, MUSIC_SAVE_DIR, name, downloadListener)
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
                    DownloadManager.download(url, MUSIC_SAVE_DIR, name, downloadListener)
                } else {
                    permissionDenied?.invoke()
                }
            }
    }

    private fun showLog(msg: String) {
        Log.d("abcde", msg)
    }
}