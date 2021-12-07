package com.topview.purejoy.musiclibrary.service.recover.db

import androidx.room.Room
import androidx.room.RoomDatabase
import com.topview.purejoy.common.app.CommonApplication

fun <T : RoomDatabase> initDB(clazz: Class<T>, name: String): T {
    return Room.databaseBuilder(CommonApplication.getContext(), clazz, name).enableMultiInstanceInvalidation().build()
}