package com.topview.purejoy.musiclibrary.service.recover.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(primaryKeys = ["id"])
data class RecoverMusicData(val id: Long, val name: String, val url: String? = null)

@Entity
data class RecoverARData(@PrimaryKey(autoGenerate = true)var rid: Int? = null,
                         val id: Long, val name: String, val mid: Long)

@Entity
data class RecoverALData(@PrimaryKey(autoGenerate = true)var lid: Int? = null,
                         val id: Long, val name: String, val picUrl: String, val mid: Long)

data class RecoverData(
    @Embedded val musicData: RecoverMusicData,
    @Relation(parentColumn = "id", entityColumn = "mid") val alData: RecoverALData,
    @Relation(parentColumn = "id", entityColumn = "mid") val arDataList: List<RecoverARData>
)