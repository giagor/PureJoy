package com.topview.purejoy.common.business.download.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.topview.purejoy.common.business.download.bean.DownloadSongInfo

@Dao
interface DownloadSongInfoDao {
    /**
     * 插入DownloadSongInfo
     * */
    @Insert
    fun insertDownloadSongInfo(downloadSongInfo: DownloadSongInfo): Long

    /**
     * 根据传入的tag在数据库中删除对应的DownloadSongInfo
     * */
    @Query("DELETE FROM DownloadSongInfo WHERE tag = :tag")
    fun deleteDownloadSongInfo(tag: String): Int

    @Query("SELECT * FROM DownloadSongInfo")
    fun queryDownloadSongInfo(): List<DownloadSongInfo>
}