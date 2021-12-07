package com.topview.purejoy.musiclibrary.service.recover.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverARData

@Dao
interface RecoverARDataDao {
    @Delete
    fun deleteRecoverARData(data: List<RecoverARData>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecoverARData(data: List<RecoverARData>)
}