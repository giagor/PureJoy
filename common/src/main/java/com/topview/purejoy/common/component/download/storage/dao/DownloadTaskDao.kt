package com.topview.purejoy.common.component.download.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.topview.purejoy.common.component.download.task.DownloadTask

/**
 * Created by giagor on 2021/12/31
 * */
@Dao
interface DownloadTaskDao {
    /**
     * 插入下载任务
     *
     * @param downloadTask 要插入的任务
     * @return 任务的Id
     * */
    @Insert
    fun insertDownloadTask(downloadTask: DownloadTask): Long

    /**
     * 删除下载任务
     *
     * @param tag 要删除任务的tag
     * */
    @Query("DELETE FROM DownloadTask WHERE tag = :tag")
    fun deleteDownloadTask(tag: String)

    /**
     * 查找下载任务
     *
     * @param tag 下载任务的标识
     * @return 查找到的下载任务
     * */
    @Query("SELECT * FROM DownloadTask WHERE tag = :tag")
    fun getDownloadTaskByTag(tag: String): DownloadTask?
}