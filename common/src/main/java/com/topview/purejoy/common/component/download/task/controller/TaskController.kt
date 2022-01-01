package com.topview.purejoy.common.component.download.task.controller

/**
 * 对下载任务进行控制的接口
 * */
interface TaskController {
    /**
     * 暂停任务下载
     * */
    fun pauseDownload()

    /**
     * 恢复任务下载（注意先暂停后才能恢复）
     * */
    fun resumeDownload()

    /**
     * 取消任务下载
     * */
    fun cancelDownload()
}