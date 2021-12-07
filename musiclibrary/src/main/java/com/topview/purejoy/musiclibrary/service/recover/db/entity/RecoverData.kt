package com.topview.purejoy.musiclibrary.service.recover.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(primaryKeys = ["id"])
data class RecoverMusicData(val id: Long, val name: String, val url: String? = null)

@Entity(primaryKeys = ["id"])
data class RecoverARData(val id: Long, val name: String, val mid: Long)

@Entity(primaryKeys = ["id"])
data class RecoverALData(val id: Long, val name: String, val picUrl: String, val mid: Long)

data class RecoverData(
    @Embedded val musicData: RecoverMusicData,
    @Relation(parentColumn = "id", entityColumn = "mid") val alData: RecoverALData,
    @Relation(parentColumn = "id", entityColumn = "mid") val arDataList: List<RecoverARData>
)