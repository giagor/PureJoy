package com.topview.purejoy.common.app

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import com.topview.purejoy.common.component.download.DownloadManager

class CommonApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext() = context
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        if (isAppMainProcess()) {
            initDownloadLibrary()
        }
    }

    /**
     * 对下载库进行初始化
     * */
    private fun initDownloadLibrary() {
        DownloadManager.init(this)
    }

    /**
     * 判断是不是主进程
     * */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun isAppMainProcess(): Boolean {
        val pid = Process.myPid()
        val processName: String = getAppNameByPID(this, pid) ?: "NOT_FOUND"
        return processName == getMainProcessName(this)
    }

    /**
     * 根据pid得到进程名
     */
    private fun getAppNameByPID(context: Context, pid: Int): String? {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return null
    }

    /**
     * 获取主进程名
     *
     * @param context 上下文
     * @return 主进程名
     */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getMainProcessName(context: Context): String? {
        return context.packageManager.getApplicationInfo(context.packageName, 0).processName
    }
}