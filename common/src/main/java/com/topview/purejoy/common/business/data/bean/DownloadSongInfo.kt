package com.topview.purejoy.common.business.data.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.topview.purejoy.common.component.download.status.DownloadStatus
import com.topview.purejoy.common.component.download.task.DownloadTask

/**
 * 下载的歌曲（下载完成后，就不在数据库中记录）
 * */
@Entity(
    indices = [Index(
        value = ["tag"],
        unique = true
    )]
)
data class DownloadSongInfo(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    /** 文件名 */
    var name: String,
    /** 下载的url */
    var url: String,
    /** 下载的路径 */
    var path: String,
    /** 下载状态 */
    @Ignore var status: Int = DownloadStatus.INITIAL,
    /** 下载进度 [0~100] */
    @Ignore var progress: Int = 0,
    /** 任务的标识符（与下载库的tag相对应） */
    var tag: String
) {
    constructor() : this(null, "", "", "", DownloadStatus.INITIAL, 0, "")

    companion object {
        fun copyFromTask(downloadTask: DownloadTask): DownloadSongInfo {
            return DownloadSongInfo(
                id = null,
                name = downloadTask.name,
                url = downloadTask.url,
                path = downloadTask.path,
                tag = downloadTask.tag
            )
        }
    }
}