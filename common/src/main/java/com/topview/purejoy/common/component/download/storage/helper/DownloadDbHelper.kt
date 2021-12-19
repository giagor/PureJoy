package com.topview.purejoy.common.component.download.storage.helper

import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
interface DownloadDbHelper {
    fun insertSubDownloadTasks(subDownloadTasks: List<SubDownloadTask>)

    fun deleteSubDownloadTasksByTag(tag: String): Int

    fun getSubDownloadTasksByTag(tag: String): List<SubDownloadTask>

    fun updateSubDownloadTask(subDownloadTask: SubDownloadTask)
}