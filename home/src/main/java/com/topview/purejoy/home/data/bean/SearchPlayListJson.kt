package com.topview.purejoy.home.data.bean

import com.google.gson.annotations.SerializedName

/**
 * 搜索的歌单数据 对应的实体类
 * 接口：baseUrl/cloudsearch/search?keywords=...&type=1000
 * */
class SearchPlayListJson {
    var result: Result? = null

    class Result {
        var playlists: MutableList<PlayList>? = null
        var playlistCount: Int? = null

        class PlayList {
            var id: Long? = null
            var name: String? = null

            @SerializedName("coverImgUrl")
            var picUrl: String? = null

            @SerializedName("trackCount")
            var songCounts: Int? = null
            var playCount: Long? = null
        }
    }
}