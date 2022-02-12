package com.topview.purejoy.common.music.service.recover.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(primaryKeys = ["id"])
class RecoverMusicData(val id: Long, val name: String, val url: String? = null, val mv: Long) {
    override fun equals(other: Any?): Boolean {
        if (other !is RecoverMusicData) {
            return false
        }
        return other.id == id
    }

    override fun hashCode(): Int {
        return id.toInt() * 37
    }
}

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