package com.topview.purejoy.common.component.download.listener.user

/**
 * Created by giagor on 2021/12/17
 *
 * 一个简单的适配器模式，用户想监听下载的状态、进度等，可以让自己的监听器继承该类，并且重写不需要的方法造成冗余
 * */
open class SimpleUserDownloadListener : UserDownloadListener {
    override fun onStarted() {

    }

    override fun onProgress(progress: Int) {

    }

    override fun onPaused() {

    }

    override fun onResumed() {

    }

    override fun onFailure(msg: String) {

    }

    override fun onCancelled() {

    }

    override fun onSuccess() {

    }

    override fun alreadyDownloaded() {
        
    }
}