package com.topview.purejoy.musiclibrary.service.recover.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverALData

@Dao
interface RecoverALDataDao {
    @Delete
    fun deleteRecoverALData(data: List<RecoverALData>): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecoverALData(data: List<RecoverALData>)
}