package com.topview.purejoy.musiclibrary.service.recover.db.dao

import androidx.room.*
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverALData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverARData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverMusicData

@Dao
interface RecoverDao {
    @Transaction
    @Query("select * from RecoverMusicData")
    fun obtainRecoverData(): List<RecoverData>

    @Transaction
    @Insert
    fun insertRecoverData(rm: List<RecoverMusicData>,
                          ra: List<RecoverALData>,
                          rl: List<RecoverARData>)

    @Transaction
    @Delete
    fun deleteRecoverData(rm: List<RecoverMusicData>,
                          ra: List<RecoverALData>,
                          rl: List<RecoverARData>)


}