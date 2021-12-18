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
interface DownloadDao {
    @Insert
    fun insertSubDownloadTasks(subDownloadTasks: List<SubDownloadTask>)

    @Query("DELETE FROM SubDownloadTask WHERE tag = :tag")
    fun deleteSubDownloadTasksByTag(tag: String): Int

    @Query("SELECT * FROM SubDownloadTask WHERE tag = :tag")
    fun getSubDownloadTasksByTag(tag: String): List<SubDownloadTask>

    @Update
    fun updateSubDownloadTask(subDownloadTask: SubDownloadTask)
}