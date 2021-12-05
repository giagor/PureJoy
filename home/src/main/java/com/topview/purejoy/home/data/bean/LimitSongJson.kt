package com.topview.purejoy.home.data.bean

class LimitSongJson {
    var code: Int? = null
    var songs: MutableList<SongJson>? = null

    class SongJson {
        var id: Long? = null
        var name: String? = null
        var al: AlbumJson? = null
        var ar: MutableList<ArtistJson>? = null

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