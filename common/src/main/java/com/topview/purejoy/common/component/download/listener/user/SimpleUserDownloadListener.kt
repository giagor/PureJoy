package com.topview.purejoy.common.component.download.listener.user

import com.topview.purejoy.common.component.download.task.DownloadTask

/**
 * Created by giagor on 2021/12/17
 *
 * 一个简单的适配器模式，用户想监听下载的状态、进度等，可以让自己的监听器继承该类，并且重写不需要的方法造成冗余
 * */
open class SimpleUserDownloadListener : UserDownloadListener {
    override fun onStarted(downloadTask: DownloadTask) {

    }

    override fun onProgress(downloadTask: DownloadTask, progress: Int) {

    }

    override fun onPaused(downloadTask: DownloadTask) {

    }

    override fun onResumed(downloadTask: DownloadTask) {

    }

    override fun onFailure(downloadTask: DownloadTask, msg: String) {

    }

    override fun onCancelled(downloadTask: DownloadTask) {

    }

    override fun onSuccess(downloadTask: DownloadTask) {

    }

    override fun alreadyDownloaded(downloadTask: DownloadTask) {

    }
}