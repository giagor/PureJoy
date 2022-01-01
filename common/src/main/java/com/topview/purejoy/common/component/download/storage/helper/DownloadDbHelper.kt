package com.topview.purejoy.common.component.download.storage.helper

import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
interface DownloadDbHelper {
    /**
     * 插入子任务并为程序中子任务的Id属性赋值
     *
     * @param subDownloadTasks 插入的子任务列表
     * */
    fun insertSubDownloadTasks(subDownloadTasks: List<SubDownloadTask>)

    /**
     * 更新子任务
     *
     * @param subDownloadTask 更新的子任务
     * */
    fun updateSubDownloadTask(subDownloadTask: SubDownloadTask)

    /**
     * 删除任务（父任务和关联的子任务）
     *
     * @param downloadTask 删除的任务
     * */
    fun deleteDownloadTask(downloadTask: DownloadTask)

    /**
     * 通过父任务的Id获取子任务列表
     *
     * @param parentId 父任务Id
     * @return 子任务列表
     * */
    fun getSubDownloadTaskByParentId(parentId: Long): List<SubDownloadTask>

    /**
     * 通过tag获取父任务
     *
     * @param tag 下载任务的tag
     * @return 父任务
     * */
    fun getDownloadTaskByTag(tag: String): DownloadTask?

    /**
     * 插入父任务并为程序中的downloadTask的id属性赋值
     *
     * @param downloadTask 父任务
     * @return 父任务的Id
     * */
    fun insertDownloadTask(downloadTask: DownloadTask)
}