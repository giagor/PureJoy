package com.topview.purejoy.common.component.download.storage.helper

import com.topview.purejoy.common.component.download.storage.DbManager
import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
class DownloadDbHelperImpl : DownloadDbHelper {
    override fun insertSubDownloadTasks(subDownloadTasks: List<SubDownloadTask>) {
        DbManager.downloadDatabase?.downloadDao()?.insertSubDownloadTasks(subDownloadTasks)
    }

    override fun deleteSubDownloadTasksByTag(tag: String): Int {
        return DbManager.downloadDatabase?.downloadDao()?.deleteSubDownloadTasksByTag(tag) ?: 0
    }

    override fun getSubDownloadTasksByTag(tag: String): List<SubDownloadTask> {
        return DbManager.downloadDatabase?.downloadDao()?.getSubDownloadTasksByTag(tag) ?: listOf()
    }

    override fun updateSubDownloadTask(subDownloadTask: SubDownloadTask) {
        DbManager.downloadDatabase?.downloadDao()?.updateSubDownloadTask(subDownloadTask)
    }
}