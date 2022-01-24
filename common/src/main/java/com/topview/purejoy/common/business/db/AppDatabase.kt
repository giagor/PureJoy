package com.topview.purejoy.common.business.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topview.purejoy.common.business.data.bean.DownloadSongInfo
import com.topview.purejoy.common.business.data.dao.DownloadSongInfoDao

@Database(entities = [DownloadSongInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadSongInfoDao(): DownloadSongInfoDao
}