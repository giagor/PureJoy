package com.topview.purejoy.musiclibrary.service.recover.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverMusicData

@Dao
interface RecoverMusicDataDao {
    @Delete
    fun deleteRecoverMusicData(data: List<RecoverMusicData>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecoverMusicData(data: List<RecoverMusicData>)
}