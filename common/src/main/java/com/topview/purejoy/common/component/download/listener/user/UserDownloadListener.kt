package com.topview.purejoy.common.component.download.listener.user

import com.topview.purejoy.common.component.download.task.DownloadTask

/**
 * Created by giagor on 2021/12/17
 *
 * 下载过程中，将下载进度等信息回调给用户的接口
 * */
interface UserDownloadListener {
    /**
     * 通知下载已经开始，任务刚开始下载时会回调该方法
     * */
    fun onStarted(downloadTask: DownloadTask)

    /**
     * 下载进度
     *
     * @param progress 取值为[0,100]间的整数
     * */
    fun onProgress(downloadTask : DownloadTask,progress: Int)

    /**
     * 通知下载已暂停，一般是用户手动暂停该下载任务，会回调该方法
     * */
    fun onPaused(downloadTask: DownloadTask)

    /**
     * 通知下载已恢复，一般是用户暂停任务后，又继续下载，会回调该方法
     * */
    fun onResumed(downloadTask: DownloadTask)

    /**
     * 下载失败，一般是内部下载出现错误会回调该方法
     *
     * @param msg 一些提示信息
     * */
    fun onFailure(downloadTask: DownloadTask, msg: String = "")

    /**
     * 通知下载已取消，一般是用户手动取消下载任务，会回调该方法
     * */
    fun onCancelled(downloadTask: DownloadTask)

    /**
     * 通知下载成功
     * */
    fun onSuccess(downloadTask: DownloadTask)

    /**
     * 通知用户该文件之前已经下载成功过了
     * */
    fun alreadyDownloaded(downloadTask: DownloadTask)
}