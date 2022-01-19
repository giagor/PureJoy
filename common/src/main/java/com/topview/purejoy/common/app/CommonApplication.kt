package com.topview.purejoy.common.app

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Process
import androidx.room.Room
import com.alibaba.android.arouter.launcher.ARouter
import com.topview.purejoy.common.business.db.AppDatabase
import com.topview.purejoy.common.business.db.AppDatabaseManager
import com.topview.purejoy.common.component.download.DownloadManager
import java.io.File

class CommonApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext() = context

        val musicPath: File by lazy {
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        if (isAppMainProcess()) {
            initDownloadLibrary()
            initAppDatabase()
        }
        initARouter()
    }

    /**
     * 对下载库进行初始化
     * */
    private fun initDownloadLibrary() {
        DownloadManager.init(this)
    }

    /**
     * 对ARouter进行初始化
     * */
    private fun initARouter() {
        if (isDebugVersion(this)) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    /**
     * 初始化应用程序的数据库
     * */
    private fun initAppDatabase() {
        AppDatabaseManager.appDatabase = Room.databaseBuilder(
            this, AppDatabase::class.java, "AppDataBase"
        ).build()
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

    /**
     * 判断是否为debug版本
     * */
    private fun isDebugVersion(context: Context): Boolean {
        try {
            val info: ApplicationInfo = context.applicationInfo
            return (info.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return false;
    }
}