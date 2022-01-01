package com.topview.purejoy.common.component.download.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topview.purejoy.common.component.download.storage.dao.DownloadTaskDao
import com.topview.purejoy.common.component.download.storage.dao.SubDownloadTaskDao
import com.topview.purejoy.common.component.download.task.DownloadTask
import com.topview.purejoy.common.component.download.task.SubDownloadTask

/**
 * Created by giagor on 2021/12/18
 * */
@Database(entities = [SubDownloadTask::class, DownloadTask::class], version = 1)
abstract class DownloadDataBase : RoomDatabase() {
    abstract fun subDownloadTaskDao(): SubDownloadTaskDao
    abstract fun downloadTaskDao(): DownloadTaskDao
}