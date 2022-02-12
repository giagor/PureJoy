package com.topview.purejoy.common.business.data.manager

import com.topview.purejoy.common.business.data.bean.DownloadSongInfo

/**
 * 正在下载的歌曲的管理者
 * */
object DownloadingSongManager {
    /**
     * Key：下载任务的标识符tag
     * Value：下载的歌曲信息
     * */
    private val downloadSongMap: MutableMap<String, DownloadSongInfo> = HashMap()

    fun put(tag: String, songInfo: DownloadSongInfo) {
        downloadSongMap[tag] = songInfo
    }

    fun get(tag: String): DownloadSongInfo? = downloadSongMap[tag]

    fun remove(tag: String) {
        downloadSongMap.remove(tag)
    }
}