package com.topview.purejoy.common.business.download.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 下载的歌曲（下载完成后，就不在数据库中记录）
 * */
@Entity
data class DownloadSongInfo(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    /** 文件名 */
    var name: String,
    /** 下载的url */
    var url: String,
    /** 下载的路径 */
    var path: String,
    /** 已下载的大小 */
    @Ignore var downloadedSize: Long,
    /** 任务总大小 */
    @Ignore var totalSize: Long,
    /** 任务的标识符（与下载库的tag相对应） */
    var tag: String
) {
    constructor() : this(null, "", "", "", 0, 0, "")
}