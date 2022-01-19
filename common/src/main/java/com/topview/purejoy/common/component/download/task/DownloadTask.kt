package com.topview.purejoy.common.component.download.task

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.subtask.SubDownloadListener
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.controller.TaskController
import com.topview.purejoy.common.component.download.util.md5EncryptForStrings
import java.io.File
import java.util.concurrent.ExecutorService
import kotlin.concurrent.thread

/**
 * Created by giagor on 2021/12/18
 *
 * 表示一个完整的下载任务，它可以被分解为多个子任务
 * */
@Entity
class DownloadTask(
    /** 父任务id */
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    /** 文件的保存路径 */
    var path: String,
    /** 下载资源的url */
    var url: String,
    /** 记录资源的总大小 */
    var totalSize: Long,
    /** 下载的线程数 */
    @Ignore var threadNum: Int,
    /** 表示是否要断点续传*/
    @Ignore var breakPointDownload: Boolean,
    /** 用户的监听器 */
    @Ignore var downloadListener: UserDownloadListener? = null,
) : SubDownloadListener, TaskController {
    /**
     * 通过url、path，唯一标识一个下载任务
     * */
    var tag: String = md5EncryptForStrings(url, path)

    @Ignore
    val subTasks: MutableList<SubDownloadTask> = mutableListOf()

    @Volatile
    @Ignore
    private var status: Int = DownloadStatus.INITIAL

    /**
     * 表示已经传输的大小
     * */
    @Ignore
    private var transmissionTotalSize: Long = 0

    /**
     * 成功的子任务数量
     * */
    @Ignore
    private var successTaskCounts = 0

    /**
     * 标记取消的子任务数量
     * */
    @Ignore
    private var cancelTaskCounts = 0

    /**
     * 暂停的子任务数量
     * */
    @Ignore
    private var pauseTaskCounts = 0

    /**
     * 失败的子任务数量
     * */
    @Ignore
    private var failTaskCounts = 0

    /**
     * 表示是否从"暂停"的状态中恢复过来，方便回调用户的接口
     * */
    @Ignore
    private var resumed = false

    @Ignore
    private val observers: MutableList<UserDownloadListener> = ArrayList()

    /**
     * 表示是否已经触发了下载
     * */
    @Volatile
    @Ignore
    private var triggerDownload = false

    init {
        downloadListener?.let {
            observers.add(it)
        }
    }

    constructor() : this(
        null, "", "", 0, 0,
        false, null
    )

    @Synchronized
    override fun pauseDownload() {
        if (!canPause()) {
            return
        }

        status = DownloadStatus.PAUSED
        for (subTask in subTasks) {
            subTask.pauseSubTask()
        }
    }

    @Synchronized
    override fun resumeDownload() {
        if (!canResume()) {
            return
        }

        restoreFromPause()
        resumed = true
        DownloadManager.downloadDispatcher.enqueue(this)
    }

    @Synchronized
    override fun cancelDownload() {
        // 判断是否可以取消下载
        if (!canCancel()) {
            return
        }

        // 获取前一个状态
        val prePaused = checkPaused()
        status = DownloadStatus.CANCELED
        for (subTask in subTasks) {
            subTask.cancelSubTask()
        }

        // 特殊处理下 暂停 -> 取消 的这种状态迁移，方便清理资源，以及处理回调
        if (prePaused) {
            thread {
                notifyCanceled()
            }
        }
    }

    @Synchronized
    override fun onSuccess() {
        successTaskCounts++
        if (successTaskCounts == subTasks.size) {
            status = DownloadStatus.SUCCESS
        }
        notifyListener()
    }

    @Synchronized
    override fun onProgress(transmission: Long) {
        notifyProgress(transmission)
    }

    @Synchronized
    override fun onPaused() {
        pauseTaskCounts++
        notifyListener()
    }

    @Synchronized
    override fun onFailure(msg: String) {
        status = DownloadStatus.FAILURE
        failTaskCounts++
        // 如果有子任务执行失败，则强制取消其他的子任务
        if (failTaskCounts == 1) {
            for (subTask in subTasks) {
                subTask.cancelSubTask()
            }
        }
        notifyListener()
    }

    @Synchronized
    override fun onCancelled() {
        cancelTaskCounts++
        notifyListener()
    }

    internal fun executeSubTasks(executorService: ExecutorService) {
        // 仅执行处于INITIAL状态的任务
        if (!checkInitial()) {
            DownloadManager.downloadDispatcher.finished(this)
            return
        }

        status = DownloadStatus.DOWNLOADING
        if (resumed) {
            resumed = false
//            it.onResumed()
            callObserversOnResume()
        } else {
//            it.onStarted()
            callObserversOnStart()
        }
//        downloadListener?.let {
//            DownloadManager.handler.post {
//                // 根据resumed的值，回调用户不同的接口
//                if (resumed) {
//                    resumed = false
//                    it.onResumed()
//                } else {
//                    it.onStarted()
//                }
//            }
//        }
        // 执行各个子任务
        for (subDownloadTask in subTasks) {
            if (!subDownloadTask.checkCompleted()) {
                executorService.execute(subDownloadTask)
            }
        }
    }

    /**
     * 从"暂停"状态中恢复后，要做的一些处理
     * */
    private fun restoreFromPause() {
        status = DownloadStatus.INITIAL
        pauseTaskCounts = 0
    }

    /**
     * 删除文件 以及 清空数据库中子任务的记录
     * */
    private fun clearTaskInfo() {
        clearLocalFile()

        if (breakPointDownload) {
            // 数据库中删除对应的任务
            DownloadManager.downDbHelper.deleteDownloadTask(this)
        }
    }

    /**
     * 若本地有path关联的文件，则删除
     * */
    private fun clearLocalFile() {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 通知监听者
     * */
    private fun notifyListener(failMsg: String = "") {
        if (!checkFinished()) {
            return
        }

        when (status) {
            DownloadStatus.PAUSED -> notifyPaused()
            DownloadStatus.CANCELED -> notifyCanceled()
            DownloadStatus.SUCCESS -> notifySuccess()
            DownloadStatus.FAILURE -> notifyFailure(failMsg)
            else -> {
            }
        }
    }

    private fun notifySuccess() {
        callObserversOnSuccess()
//        DownloadManager.handler.post {
//            downloadListener?.onSuccess()
//        }

        if (breakPointDownload) {
            // 数据库中删除对应的任务
            DownloadManager.downDbHelper.deleteDownloadTask(this)
        }
        DownloadManager.downloadDispatcher.finished(this)
    }

    private fun notifyProgress(transmission: Long) {
        transmissionTotalSize += transmission
        val progress = (transmissionTotalSize * 100 / totalSize).toInt()

        callObserversOnProgress(progress)
//        DownloadManager.handler.post {
//            downloadListener?.onProgress(progress)
//        }
    }

    private fun notifyCanceled() {
        clearTaskInfo()
        callObserverOnCancelled()
//        DownloadManager.handler.post {
//            downloadListener?.onCancelled()
//        }
        // 通知调度器
        DownloadManager.downloadDispatcher.finished(this)
    }

    private fun notifyPaused() {
        callObserversOnPause()
//        DownloadManager.handler.post {
//            downloadListener?.onPaused()
//        }
        // 通知调度器
        DownloadManager.downloadDispatcher.finished(this)
    }

    private fun notifyFailure(msg: String) {
        clearTaskInfo()
        callObserversOnFailure(msg)
//        DownloadManager.handler.post {
//            downloadListener?.onFailure()
//        }
        // 通知调度器
        DownloadManager.downloadDispatcher.finished(this)
    }

    /**
     * 添加单个子任务
     * */
    fun addTask(subDownloadTask: SubDownloadTask) {
        subTasks.add(subDownloadTask)
    }

    /**
     * 添加多个子任务
     * */
    fun addTasks(subDownloadTasks: List<SubDownloadTask>) {
        subTasks.addAll(subDownloadTasks)
    }

    internal fun callObserversOnStart() {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onStarted()
            }
        }
    }

    internal fun callObserversOnProgress(progress: Int) {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onProgress(progress)
            }
        }
    }

    internal fun callObserversOnPause() {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onPaused()
            }
        }
    }

    internal fun callObserversOnResume() {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onResumed()
            }
        }
    }

    internal fun callObserversOnFailure(msg: String) {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onFailure(msg)
            }
        }
    }

    internal fun callObserverOnCancelled() {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onCancelled()
            }
        }
    }

    internal fun callObserversOnSuccess() {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.onSuccess()
            }
        }
    }

    internal fun callObserverAlreadyDownloaded() {
        for (observer in observers) {
            DownloadManager.handler.post {
                observer.alreadyDownloaded()
            }
        }
    }

    /**
     * 注册观察者
     * */
    fun registerObserver(observer: UserDownloadListener) {
        observers.add(observer)
    }

    /**
     * 取消观察者的注册
     * */
    fun unregisterObserver(observer: UserDownloadListener) {
        observers.remove(observer)
    }

    /**
     * 触发任务的下载
     * */
    fun download() {
        if (!triggerDownload) {
            triggerDownload = true
            DownloadManager.downloadDispatcher.enqueue(this)
        }
    }

    /**
     * 设置状态
     * */
    fun setStatus(status: Int) {
        this.status = status
    }

    private fun getFinishedCount() =
        successTaskCounts + cancelTaskCounts + pauseTaskCounts + failTaskCounts

    private fun checkInitial() = status == DownloadStatus.INITIAL

    private fun checkPaused() = status == DownloadStatus.PAUSED

    private fun checkFinished() = getFinishedCount() == subTasks.size

    /**
     * 判断是否可以取消下载
     * */
    private fun canCancel() = status != DownloadStatus.FAILURE
            && status != DownloadStatus.SUCCESS
            && status != DownloadStatus.CANCELED

    /**
     * 判断是否可以暂停下载
     * */
    private fun canPause() = status == DownloadStatus.DOWNLOADING

    /**
     * 判断是否可以恢复下载
     * */
    private fun canResume() = status == DownloadStatus.PAUSED
}