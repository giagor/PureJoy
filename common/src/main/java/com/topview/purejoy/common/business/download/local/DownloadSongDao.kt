package com.topview.purejoy.common.business.download.local

import androidx.room.Dao
import androidx.room.Insert
import com.topview.purejoy.common.business.download.bean.DownloadSong

@Dao
interface DownloadSongDao {
    /**
     * 插入DownloadSong
     * */
    @Insert
    fun insertDownloadSong(downloadSong: DownloadSong): Long
}