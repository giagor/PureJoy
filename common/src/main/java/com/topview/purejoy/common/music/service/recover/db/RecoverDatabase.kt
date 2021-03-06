package com.topview.purejoy.common.music.service.recover.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topview.purejoy.common.music.service.recover.dao.RecoverDao
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverALData
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverARData
import com.topview.purejoy.common.music.service.recover.db.entity.RecoverMusicData

@Database(entities = [RecoverMusicData::class, RecoverALData::class, RecoverARData::class], version = 1)
abstract class RecoverDatabase : RoomDatabase() {
    abstract fun recoverDao(): RecoverDao

}