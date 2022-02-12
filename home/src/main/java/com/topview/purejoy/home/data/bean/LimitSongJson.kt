package com.topview.purejoy.home.data.bean

import com.google.gson.annotations.SerializedName
import com.topview.purejoy.common.music.service.entity.AL
import com.topview.purejoy.common.music.service.entity.AR
import com.topview.purejoy.common.music.service.entity.MusicItem

class LimitSongJson {
    var code: Int? = null
    var songs: MutableList<SongJson>? = null

    class SongJson {
        var id: Long? = null
        var name: String? = null
        var al: AlbumJson? = null
        var ar: MutableList<ArtistJson>? = null
        @SerializedName("mv") var mvId: Long? = null

        class AlbumJson {
            var name: String? = null
            var id: Long? = null
            var picUrl: String? = null
        }

        class ArtistJson {
            var name: String? = null
            var id: Long? = null
        }
    }
}

internal fun LimitSongJson.SongJson.ArtistJson.toAR() = AR(
    id = id!!, name = name ?: ""
)

internal fun LimitSongJson.toMusicItems(): List<MusicItem>? =
    songs?.map { songJson ->
        MusicItem(
            name = songJson.name!!,
            id = songJson.id!!,
            al = songJson.al!!.run {
                AL(id = id!!, name = name!!, picUrl = picUrl!!)
            },
            ar = songJson.ar?.map {
                it.toAR()
            } ?: listOf(),
            mv = songJson.mvId ?: 0L
        )
    }
