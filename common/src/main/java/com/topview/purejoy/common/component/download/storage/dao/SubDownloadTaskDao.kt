package com.topview.purejoy.common.component.download.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
@Dao
interface SubDownloadTaskDao {
    /**
     * 插入子任务列表
     *
     * @param subDownloadTasks 子任务列表
     * @return 子任务的Id
     * */
    @Insert
    fun insertSubDownloadTasks(subDownloadTasks: List<SubDownloadTask>): List<Long>

    /**
     * 更新子任务
     *
     * @param subDownloadTask 更新的子任务
     * */
    @Update
    fun updateSubDownloadTask(subDownloadTask: SubDownloadTask)

    /**
     * 通过parentId查找子任务
     *
     * @param parentId 父任务的Id
     * @return 子任务列表
     * */
    @Query("SELECT * FROM SubDownloadTask WHERE parentId = :parentId")
    fun getSubDownloadTaskByParentId(parentId: Long): List<SubDownloadTask>
}