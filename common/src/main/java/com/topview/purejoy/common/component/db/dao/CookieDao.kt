package com.topview.purejoy.common.component.db.dao

import androidx.room.*
import com.topview.purejoy.common.component.db.entity.CookieData

@Dao
interface CookieDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cookieList: List<CookieData>)

    @Transaction
    @Query("SELECT * FROM cookie_data")
    fun selectAll(): List<CookieData>

    @Transaction
    @Query("SELECT * FROM cookie_data WHERE expiresAt > :time")
    fun selectByTime(time: Long): List<CookieData>

    @Transaction
    @Delete
    fun delete(cookieList: List<CookieData>)

    @Transaction
    @Query("DELETE FROM cookie_data WHERE expiresAt <= :time")
    fun deleteByTime(time: Long)
}