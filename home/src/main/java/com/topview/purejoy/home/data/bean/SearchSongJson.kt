package com.topview.purejoy.home.data.bean

import com.google.gson.annotations.SerializedName

/**
 * 搜索的歌曲数据 对应的实体类
 * 接口：baseUrl/cloudsearch/search?keywords=...&type=1
 * */
class SearchSongJson {
    var result: Result? = null

    class Result {
        var songs: MutableList<Song>? = null
        var songCount: Int? = null

        class Song {
            var id: Long? = null
            var name: String? = null

            @SerializedName("ar")
            var artists: MutableList<Artist>? = null

            class Artist {
                var id: Long? = null
                var name: String? = null
            }
        }
    }
}