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
 *
 * 使用方式：
 * 1.初始化(推荐在Application中)：DownloadManager.init(...)
 *
 * 2.下载单个任务
 * 调用方式：
 * val task : DownloadTask = DownloadManager.download(url,saveDir,name,listener)
 * 暂停下载：
 * task.pauseDownload()
 * 从暂停中恢复下载：
 * task.resumeDownload()
 * 取消下载：
 * task.cancelDownload()
 *
 * 3.批量下载(下载多个任务)
 * 调用方式：
 *  val batchDownloadTask : BatchDownloadTask = DownloadManager.batchDownload()
 *      .addTask(1, url1, saveDir1, name1, listener1)
 *      .addTask(2, url2, saveDir2, name2, listener2)
 *      .addTask(3, url3, saveDir3, name3, listener3)
 *      .addTask(4, url4, saveDir4, name4, listener4)
 *      .addTask(5, url5, saveDir5, name5, listener5)
 *      .addTask(6, url6, saveDir6, name6, listener6)
 *      .addTask(7, url7, saveDir7, name7, listener7)
 *      .downloadAll()
 * 获取某个具体下载任务的控制类(通过标识符获取)：
 * val task : DownloadTask? = batchDownloadTask.getTask(1)
 * 暂停下载某个具体任务：
 * task?.pauseDownload()
 * 从暂停中恢复下载某个具体任务：
 * task?.resumeDownload()
 * 取消下载某个具体任务：
 * task?.cancelDownload()
 * */
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
     * 创建一个任务（不加入下载队列）
     * */
    fun createTask(
        url: String,
        saveDir: String,
        name: String,
        listener: UserDownloadListener?
    ): DownloadTask {
        val path = getDownloadPath(saveDir, name)
        return DownloadTask(
            id = null,
            path = path,
            url = url,
            totalSize = 0,
            threadNum = 0,
            breakPointDownload = false,
            downloadListener = listener
        )
    }

    /**
     * 用户调用该方法，进行任务的下载
     *
     * @param url 要下载的文件的url
     * @param saveDir 要保存到的目录，例如/storage/emulated/0/Android/data/packagename/files/Music
     * @param name 文件名，需要自己带上后缀名
     * @param listener 监听器，可以继承SimpleUserDownloadListener实现监听
     * */
    fun download(
        url: String,
        saveDir: String,
        name: String,
        listener: UserDownloadListener?
    ): DownloadTask {
        val path = getDownloadPath(saveDir, name)
        // 创建任务
        val downloadTask = DownloadTask(
            id = null,
            path = path,
            url = url,
            totalSize = 0,
            threadNum = 0,
            breakPointDownload = false,
            downloadListener = listener
        )
        // 处理任务
        downloadTask.download()
        return downloadTask
    }

    fun batchDownload() = BatchDownloadTask()
}