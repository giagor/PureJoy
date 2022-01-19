package com.topview.purejoy.common.business.download.manager

import com.topview.purejoy.common.component.download.task.DownloadTask

/**
 * 正在下载的歌曲的管理者
 * */
object DownloadingSongManager {
    /**
     * Key：下载任务的标识符tag
     * Value：下载任务
     * */
    private val downloadSongMap: MutableMap<String, DownloadTask> = HashMap()

    fun put(tag: String, task: DownloadTask) {
        downloadSongMap.put(tag, task)
    }

    fun get(tag: String): DownloadTask? = downloadSongMap[tag]

    fun remove(tag: String) {
        downloadSongMap.remove(tag)
    }
}