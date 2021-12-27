package com.topview.purejoy.common.component.download.task

import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.net.ResourcePreviewCallback
import com.topview.purejoy.common.component.download.listener.subtask.SubDownloadListener
import com.topview.purejoy.common.component.download.listener.user.UserDownloadListener
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.util.md5EncryptForStrings
import java.io.File
import java.util.concurrent.ExecutorService
import kotlin.concurrent.thread

/**
 * 如果资源太小，则采取单线程下载。因为在资源太小的情况下，如果采取多线程下载，那么线程创建、切换等开销，将
 * 会拖慢整体的下载速度
 * */
private const val MULTI_THREAD_DOWNLOAD_MINIMUM_SIZE = 20 * 1024 * 1024

/**
 * Created by giagor on 2021/12/18
 *
 * 表示一个完整的下载任务，它可以被分解为多个子任务
 * */
class DownloadTask(
    /** 文件的保存路径 */
    val path: String,
    /** 下载资源的url */
    val url: String,
    /** 用户的监听器 */
    val downloadListener: UserDownloadListener? = null,
) : SubDownloadListener {
    /**
     * 通过url、path，唯一标识一个下载任务
     * */
    val tag: String = md5EncryptForStrings(url, path)

    /**
     * 记录资源的总大小
     * */
    var totalSize: Long = 0

    private val subTasks: MutableList<SubDownloadTask> = mutableListOf()

    @Volatile
    private var status: Int = DownloadStatus.INITIAL

    /**
     * 表示已经传输的大小
     * */
    private var transmissionTotalSize: Long = 0

    /**
     * 成功的子任务数量
     * */
    private var successTaskCounts = 0

    /**
     * 标记取消的子任务数量
     * */
    private var cancelTaskCounts = 0

    /**
     * 暂停的子任务数量
     * */
    private var pauseTaskCounts = 0

    /**
     * 失败的子任务数量
     * */
    private var failTaskCounts = 0

    /**
     * 表示是否要多线程下载，默认为true
     * */
    private var multiThreadDownload = true

    /**
     * 表示是否要断点续传，默认为true
     * */
    private var breakPointDownload = true

    private var threadNum: Int = DownloadManager.downloadConfiguration.getDownloadThreadNum()

    /**
     * 表示是否从"暂停"的状态中恢复过来，方便回调用户的接口
     * */
    private var resumed = false

    internal fun downloadTask() {
        DownloadManager.downHttpHelper.getContentLength(url, object : ResourcePreviewCallback {
            override fun onFailure(e: Exception) {
                DownloadManager.handler.post {
                    downloadListener?.onFailure("下载出错")
                }
            }

            override fun resourceErr() {
                DownloadManager.handler.post {
                    downloadListener?.onFailure("找不到资源")
                }
            }

            override fun supportRange(contentLength: Long) {
                // 设置任务的总大小
                totalSize = contentLength

                // 根据资源的大小，决定是要采用多线程下载，还是单线程下载
                setDownloadThreadStrategy(contentLength)
                // 断点续传下载
                breakPointDownload = true

                // 数据库中查询子任务
                val localSubTasks =
                    DownloadManager.downDbHelper.getSubDownloadTasksByTag(tag)

                // 数据库中的子任务列表是不是空的
                if (localSubTasks.isEmpty()) {
                    // 列表是空的，说明这是一个新任务，处理一个新任务
                    handleNewTask(tag, contentLength)
                } else {
                    // 计算原来的任务总大小    
                    var preTotalSize: Long = 0
                    for (localSubTask in localSubTasks) {
                        preTotalSize += localSubTask.subTaskSize
                    }
                    // 判断数据库记录的总任务大小与服务器获取的任务大小是否相等
                    if (preTotalSize != totalSize) {
                        // 不相等，说明服务器的资源长度发生了改变
                        handleRetryTask(tag, contentLength)
                    } else {
                        // 相等，再判断数据库中的子任务条数是否和下载的线程数相等
                        if (localSubTasks.size != threadNum) {
                            // 线程数不匹配，重新执行该任务
                            handleRetryTask(tag, contentLength)
                        } else {
                            // 服务器的资源长度未发生变化，并且数据库中记录的子任务数和当前的线程数匹配，那么
                            // 在之前已下载的基础上，再继续下载    
                            handleExistingTask(localSubTasks)
                        }
                    }
                }

                DownloadManager.downloadDispatcher.enqueue(this@DownloadTask)
            }

            override fun unSupportRange(contentLength: Long) {
                totalSize = contentLength
                multiThreadDownload = false
                breakPointDownload = false
                threadNum = 1
                handleNewTask(tag, contentLength)
                DownloadManager.downloadDispatcher.enqueue(this@DownloadTask)
            }
        })
    }


    fun pauseDownload() {
        if (!canPause()) {
            return
        }

        status = DownloadStatus.PAUSED
        for (subTask in subTasks) {
            subTask.pauseSubTask()
        }
    }

    fun resumeDownload() {
        if (!canResume()) {
            return
        }

        restoreFromPause()
        resumed = true
        DownloadManager.downloadDispatcher.enqueue(this)
    }

    fun cancelDownload() {
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

    private fun initMultiThreadDownload(tag: String, contentLength: Long) {
        // 获取平均每个子任务要下载的大小
        val averageSize = contentLength / threadNum
        for (i in 0 until threadNum) {
            var subTaskSize = averageSize
            if (i == threadNum - 1) {
                subTaskSize += contentLength % threadNum
            }
            val subTask = SubDownloadTask(
                url = url,
                path = path,
                startPos = i * averageSize,
                downloadedSize = 0,
                subTaskSize = subTaskSize,
                tag = tag,
                breakPointDownload = breakPointDownload
            )
            subTasks.add(subTask)
        }
    }

    private fun initSingleThreadDownload(tag: String, contentLength: Long) {
        val subTask = SubDownloadTask(
            url = url,
            path = path,
            startPos = 0,
            downloadedSize = 0,
            subTaskSize = contentLength,
            tag = tag,
            breakPointDownload = breakPointDownload
        )
        subTasks.add(subTask)
    }

    /**
     * 处理新任务
     * */
    private fun handleNewTask(tag: String, contentLength: Long) {
        // 清除本地的文件
        clearLocalFile()
        
        if (multiThreadDownload) {
            initMultiThreadDownload(tag, contentLength)
        } else {
            initSingleThreadDownload(tag, contentLength)
        }

        if (breakPointDownload) {
            // 插入数据库
            DownloadManager.downDbHelper.insertSubDownloadTasks(subTasks)

            // 再从数据库中查询，这样做的主要目的是让子任务获取Id，方便后面在数据库中更新下载进度
            subTasks.clear()
            subTasks.addAll(DownloadManager.downDbHelper.getSubDownloadTasksByTag(tag))
        }

        // 设置监听器
        for (subTask in subTasks) {
            subTask.subDownloadListener = this
        }
    }

    /**
     * 处理现有的任务，但是线程数不匹配
     * */
    private fun handleRetryTask(tag: String, contentLength: Long) {
        // 删除原来的子任务
        clearTaskInfo()
        // 当做新任务处理
        handleNewTask(tag, contentLength)
    }

    /**
     * 处理现有的任务
     * */
    private fun handleExistingTask(localSubTasks: List<SubDownloadTask>) {
        for (subTask in localSubTasks) {
            subTask.subDownloadListener = this
            subTasks.add(subTask)
        }
    }

    internal fun executeSubTasks(executorService: ExecutorService) {
        // 仅执行处于INITIAL状态的任务
        if (!checkInitial()) {
            DownloadManager.downloadDispatcher.finished(this)
            return
        }

        status = DownloadStatus.DOWNLOADING
        downloadListener?.let {
            DownloadManager.handler.post {
                // 根据resumed的值，回调用户不同的接口
                if (resumed) {
                    resumed = false
                    it.onResumed()
                } else {
                    it.onStarted()
                }
            }
        }
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
        cancelTaskCounts = 0
        pauseTaskCounts = 0
    }

    /**
     * 删除文件 以及 清空数据库中子任务的记录
     * */
    private fun clearTaskInfo() {
        clearLocalFile()

        if (breakPointDownload) {
            // 数据库中删除对应的子任务
            DownloadManager.downDbHelper.deleteSubDownloadTasksByTag(tag)
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
        DownloadManager.handler.post {
            downloadListener?.onSuccess()
        }

        if (breakPointDownload) {
            // 数据库中删除对应的子任务
            DownloadManager.downDbHelper.deleteSubDownloadTasksByTag(tag)
        }
        DownloadManager.downloadDispatcher.finished(this)
    }

    private fun notifyProgress(transmission: Long) {
        transmissionTotalSize += transmission
        val progress = (transmissionTotalSize * 100 / totalSize).toInt()

        DownloadManager.handler.post {
            downloadListener?.onProgress(progress)
        }
    }

    private fun notifyCanceled() {
        clearTaskInfo()
        DownloadManager.handler.post {
            downloadListener?.onCancelled()
        }
        // 通知调度器
        DownloadManager.downloadDispatcher.finished(this)
    }

    private fun notifyPaused() {
        DownloadManager.handler.post {
            downloadListener?.onPaused()
        }
        // 通知调度器
        DownloadManager.downloadDispatcher.finished(this)
    }

    private fun notifyFailure(msg: String) {
        clearTaskInfo()
        DownloadManager.handler.post {
            downloadListener?.onFailure()
        }
        // 通知调度器
        DownloadManager.downloadDispatcher.finished(this)
    }

    /**
     * 设置下载的线程策略，包括「是否采用多线程下载」和「下载的线程数」
     * */
    private fun setDownloadThreadStrategy(contentLength: Long) {
        if (contentLength > MULTI_THREAD_DOWNLOAD_MINIMUM_SIZE) {
            multiThreadDownload = true
            threadNum = DownloadManager.downloadConfiguration.getDownloadThreadNum()
        } else {
            multiThreadDownload = false
            threadNum = 1
        }
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