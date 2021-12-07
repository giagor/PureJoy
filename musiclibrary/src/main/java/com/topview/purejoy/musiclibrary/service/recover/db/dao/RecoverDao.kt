package com.topview.purejoy.musiclibrary.service.recover.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverData

@Dao
interface RecoverDao {
    @Transaction
    @Query("select * from RecoverMusicData")
    fun obtainRecoverData(): List<RecoverData>


}