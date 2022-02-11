package com.topview.purejoy.common.component.download.task.handler

import com.topview.purejoy.common.component.download.DownloadManager
import com.topview.purejoy.common.component.download.listener.net.ResourcePreviewCallback
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.component.download.task.SubDownloadTask
import java.io.File

object TaskHandler {
    fun handleTask(
        downloadTask: DownloadTask,
    ) {
        DownloadManager.downHttpHelper.getContentLength(
            downloadTask.url,
            object : ResourcePreviewCallback {
                override fun onFailure(e: Exception) {
                    downloadTask.callObserversOnFailure("下载出错")
                }

                override fun resourceErr() {
                    downloadTask.callObserversOnFailure("找不到资源")
                }

                override fun supportRange(contentLength: Long) {
                    // 如果已经下载成功过且下载的文件仍然有效，就调用监听方法，并且return 
                    if (isAlreadyDownload(downloadTask.path, contentLength, downloadTask.tag)) {
                        downloadTask.setStatus(DownloadStatus.SUCCESS)
                        downloadTask.callObserverAlreadyDownloaded()
                        return
                    }

                    // 配置下载的信息
                    configureDownloadInfo(downloadTask, contentLength, true)

                    val cacheTask =
                        DownloadManager.downDbHelper.getDownloadTaskByTag(downloadTask.tag)
                    // 若数据库中找到父任务的记录
                    if (cacheTask != null) {
//                        val parentId: Long = cacheTask.id!!
                        val parentTotalSize: Long = cacheTask.totalSize
                        // 若下载任务的本地文件被删除了，或者，数据库中记录的父任务记录和服务器返回的不同（说明
                        // 资源的长度已经改变了），则将其当做新任务处理
                        if (!File(downloadTask.path).exists() || parentTotalSize != contentLength) {
                            // 数据库中删除父任务和子任务，本地删除path相关的文件
                            clearTaskInfo(downloadTask.path, cacheTask)
                            // 当做新任务处理，数据库中插入父任务和子任务的记录
                            handleNewTask(downloadTask)
                        } else {
                            // 复用之前的下载任务
                            handleExistingTask(downloadTask, cacheTask)
                        }
                    } else {
                        // 若数据库中找不到父任务的记录，则本地删除path相关的文件，然后当作新任务处理
                        clearTaskInfo(downloadTask.path, null)
                        handleNewTask(downloadTask)
                    }

                    DownloadManager.downloadDispatcher.enqueue(downloadTask)
                }

                override fun unSupportRange(contentLength: Long) {
                    configureDownloadInfo(downloadTask, contentLength, false)
                    handleNewTask(downloadTask)
                    DownloadManager.downloadDispatcher.enqueue(downloadTask)
                }
            })
    }

    /**
     * 配置下载的信息，其中包括设置下载任务大小、是否断点续传，以及下载的线程数
     * */
    private fun configureDownloadInfo(
        downloadTask: DownloadTask,
        totalSize: Long,
        breakPointDownload: Boolean
    ) {
        // 设置下载的大小
        downloadTask.totalSize = totalSize
        // 是否断点续传
        downloadTask.breakPointDownload = breakPointDownload
        // 根据下载任务的大小，配置下载的线程数
        downloadTask.threadNum =
            DownloadManager.downloadConfiguration.getDownloadThreadNum(downloadTask.totalSize)
    }

    /**
     * 判断文件是否已经下载过，且下载的文件是否仍然有效
     * */
    private fun isAlreadyDownload(path: String, contentLength: Long, tag: String): Boolean {
        val file = File(path)
        // 文件不存在，直接返回false
        if (!file.exists()) {
            return false
        }

        // 文件的长度与服务器返回的长度不相等，返回false
        if (file.length() != contentLength) {
            return false
        }

        // 判断数据库中是否有父任务的记录，有则返回false
        val downloadTask: DownloadTask? = DownloadManager.downDbHelper.getDownloadTaskByTag(tag)
        if (downloadTask != null) {
            return false
        }

        return true
    }

    /**
     * 处理新任务
     *
     * @return 新的下载任务
     * */
    private fun handleNewTask(
        downloadTask: DownloadTask
    ): DownloadTask {
        if (downloadTask.breakPointDownload) {
            // 将父任务插入数据库，并为程序中父任务的Id属性赋值
            DownloadManager.downDbHelper.insertDownloadTask(downloadTask)
        }
        // 初始化子任务
        initSubTasks(downloadTask)

        if (downloadTask.breakPointDownload) {
            // 将子任务插入到数据库中
            DownloadManager.downDbHelper.insertSubDownloadTasks(downloadTask.subTasks)
        }
        downloadTask.callObserversInsertTaskToDb()
        return downloadTask
    }

    private fun handleExistingTask(downloadTask: DownloadTask, cacheTask: DownloadTask) {
        // 给id赋值
        downloadTask.id = cacheTask.id
        // 查询子任务
        val subTasks = DownloadManager.downDbHelper.getSubDownloadTaskByParentId(downloadTask.id!!)
        // 对子任务进行配置
        for (subTask in subTasks) {
            subTask.configureDownloadInfo(
                downloadTask.url,
                downloadTask.path,
                downloadTask.tag,
                downloadTask.breakPointDownload,
                downloadTask
            )
        }
        // 添加子任务
        downloadTask.addTasks(subTasks)
    }

    private fun initSubTasks(downloadTask: DownloadTask) {
        // 获取平均每个子任务要下载的大小
        val averageSize = downloadTask.totalSize / downloadTask.threadNum
        for (i in 0 until downloadTask.threadNum) {
            var subTaskSize = averageSize
            if (i == downloadTask.threadNum - 1) {
                subTaskSize += downloadTask.totalSize % downloadTask.threadNum
            }
            val subTask = SubDownloadTask(
                parentId = downloadTask.id,
                url = downloadTask.url,
                path = downloadTask.path,
                startPos = i * averageSize,
                downloadedSize = 0,
                subTaskSize = subTaskSize,
                tag = downloadTask.tag,
                breakPointDownload = downloadTask.breakPointDownload,
                subDownloadListener = downloadTask
            )
            downloadTask.addTask(subTask)
        }
    }

    /**
     * 删除任务的相关信息，包括本地文件和数据库中的记录
     *
     * @param path 本地文件的路径
     * @param downloadTask 如果不为null，则在数据库中删除对应的任务
     * */
    private fun clearTaskInfo(path: String, downloadTask: DownloadTask?) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }

        downloadTask?.let {
            // 数据库中删除对应的子任务
            DownloadManager.downDbHelper.deleteDownloadTask(it)
        }
    }
}