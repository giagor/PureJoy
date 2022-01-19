package com.topview.purejoy.common.business.download.local

import androidx.room.Dao
import androidx.room.Insert
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo

@Dao
interface DownloadSongInfoDao {
    /**
     * 插入DownloadSong
     * */
    @Insert
    fun insertDownloadSong(downloadSongInfo: DownloadSongInfo): Long
}