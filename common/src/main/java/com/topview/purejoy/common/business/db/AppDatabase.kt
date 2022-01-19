package com.topview.purejoy.common.business.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo
import com.topview.purejoy.common.business.download.local.DownloadSongInfoDao

@Database(entities = [DownloadSongInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadSongInfoDao(): DownloadSongInfoDao
}