package com.topview.purejoy.common.component.download.status

/**
 * Created by giagor on 2021/12/17
 *
 * 定义 父任务和子任务的状态
 * */
object DownloadStatus {
    /**
     * 初始状态，默认值
     * */
    const val INITIAL: Int = 121

    /**
     * 表示"下载中"
     * */
    const val DOWNLOADING: Int = 122

    /**
     * 表示"下载暂停"
     * */
    const val PAUSED: Int = 123

    /**
     * 表示"下载取消"
     * */
    const val CANCELED: Int = 124

    /**
     * 表示"下载失败"
     * */
    const val FAILURE: Int = 125

    /**
     * 表示"下载成功"
     * */
    const val SUCCESS: Int = 126

    /**
     * 表示"准备下载"，它的意思是当前已经触发下载，但是还在等待「下载信息的获取」或者「等待调度器调度」。
     * 设置该状态的原因是与「初始状态INITIAL」区分开来。
     * */
    const val PREPARE_DOWNLOAD: Int = 127
}