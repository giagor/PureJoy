package com.topview.purejoy.common.business.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topview.purejoy.common.business.download.bean.DownloadSong
import com.topview.purejoy.common.business.download.local.DownloadSongDao

@Database(entities = [DownloadSong::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadSongDao(): DownloadSongDao
}