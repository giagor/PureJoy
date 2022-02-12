package com.topview.purejoy.common.component.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topview.purejoy.common.component.db.dao.CookieDao
import com.topview.purejoy.common.component.db.entity.CookieData

@Database(entities = [CookieData::class], version = 1)
abstract class CookieDatabase: RoomDatabase() {
    abstract fun cookieDao(): CookieDao

    companion object {
        const val COOKIE_DATABASE_NAME = "cookies"
    }
}

