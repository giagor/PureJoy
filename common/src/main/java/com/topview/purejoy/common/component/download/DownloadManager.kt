package com.topview.purejoy.common.component.download

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import com.topview.purejoy.common.component.download.configure.DefaultDownloadConfiguration
import com.topview.purejoy.common.component.download.configure.DownloadConfiguration
import com.topview.purejoy.common.component.download.dispatcher.DownloadDispatcher
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
import com.topview.purejoy.common.component.download.network.DownloadHttpHelper
import com.topview.purejoy.common.component.download.network.DownloadHttpHelperImpl
import com.topview.purejoy.common.component.download.storage.DbManager
import com.topview.purejoy.common.component.download.storage.db.DownloadDataBase
import com.topview.purejoy.common.component.download.storage.helper.DownloadDbHelper
import com.topview.purejoy.common.component.download.storage.helper.DownloadDbHelperImpl
import com.topview.purejoy.common.component.download.task.BatchDownloadTask
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.component.download.util.getDownloadPath

/**
 * Created by giagor on 2021/12/18
 *
 * 下载的管理类
 * */
// todo 使用方式
object DownloadManager {

    /**
     * 表示是否已经初始化
     * */
    private var init = false

    internal val handler: Handler = Handler(Looper.getMainLooper())
    internal var downloadConfiguration: DownloadConfiguration = DefaultDownloadConfiguration()
    internal val downHttpHelper: DownloadHttpHelper =
        DownloadHttpHelperImpl(downloadConfiguration.getDownloadOkClient())
    internal val downDbHelper: DownloadDbHelper = DownloadDbHelperImpl()
    internal val downloadDispatcher: DownloadDispatcher = DownloadDispatcher()

    /**
     * 初始化方法
     *
     * @param applicationContext 通常是Application context
     * @param configuration 配置(可选项)
     * */
    fun init(applicationContext: Context, configuration: DownloadConfiguration? = null) {
        // 防止重复初始化
        if (init) {
            return
        }

        init = true
        configuration?.let {
            downloadConfiguration = configuration
        }
        // 初始化数据库
        DbManager.downloadDatabase = Room.databaseBuilder(
            applicationContext,
            DownloadDataBase::class.java, "DownloadDataBase"
        ).build()
    }

    /**
     * 用户调用该方法，进行任务的下载
     *
     * @param url 要下载的文件的url
     * @param saveDir 要保存到的目录
     * @param name 文件名
     * */
    fun download(
        url: String,
        saveDir: String,
        name: String,
        listener: UserDownloadListener?
    ): DownloadTask {
        val path = getDownloadPath(saveDir, name)
        // 创建任务
        val fullDownloadTask = DownloadTask(
            path = path,
            url = url,
            downloadListener = listener
        )
        fullDownloadTask.downloadTask()
        return fullDownloadTask
    }

    fun batchDownload() = BatchDownloadTask()
}