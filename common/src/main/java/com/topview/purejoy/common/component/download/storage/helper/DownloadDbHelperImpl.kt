package com.topview.purejoy.common.component.download.storage.helper

import com.topview.purejoy.common.component.download.storage.DbManager
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
class DownloadDbHelperImpl : DownloadDbHelper {

    override fun insertSubDownloadTasks(subDownloadTasks: List<SubDownloadTask>) {
        checkInitializer()

        val ids = DbManager.downloadDatabase!!.subDownloadTaskDao()
            .insertSubDownloadTasks(subDownloadTasks)
        // 给子任务赋值Id
        for ((index, subTask) in subDownloadTasks.withIndex()) {
            subTask.id = ids[index]
        }
    }

    override fun updateSubDownloadTask(subDownloadTask: SubDownloadTask) {
        checkInitializer()
        DbManager.downloadDatabase!!.subDownloadTaskDao().updateSubDownloadTask(subDownloadTask)
    }

    override fun deleteDownloadTask(downloadTask: DownloadTask) {
        checkInitializer()
        DbManager.downloadDatabase!!.downloadTaskDao().deleteDownloadTask(downloadTask)
    }

    override fun getSubDownloadTaskByParentId(parentId: Long): List<SubDownloadTask> {
        checkInitializer()
        return DbManager.downloadDatabase!!.subDownloadTaskDao()
            .getSubDownloadTaskByParentId(parentId)
    }

    override fun getDownloadTaskByTag(tag: String): DownloadTask? {
        checkInitializer()
        return DbManager.downloadDatabase!!.downloadTaskDao().getDownloadTaskByTag(tag)
    }

    override fun insertDownloadTask(
        downloadTask: DownloadTask
    ) {
        checkInitializer()
        val id = DbManager.downloadDatabase!!.downloadTaskDao().insertDownloadTask(downloadTask)
        downloadTask.id = id
    }

    /**
     * 检查数据库是否已经初始化
     * */
    private fun checkInitializer() {
        check(DbManager.downloadDatabase != null) {
            "Didn't initialize download database!"
        }
    }
}