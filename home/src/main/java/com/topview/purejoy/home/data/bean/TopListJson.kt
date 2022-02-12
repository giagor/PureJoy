package com.topview.purejoy.home.data.bean

class TopListJson {
    var list: MutableList<TopListJson>? = null
    var code: Int? = null

    class TopListJson {
        var tracks: MutableList<Track>? = null

        var id: Long? = null
        var name: String? = null
        var coverImgUrl: String? = null
        var playCount: Long? = null

        /**
         * 订阅总数
         */
        var subscribedCount: Long? = null

        var description: String? = null

        /**
         * 歌曲总数
         */
        var trackCount: Int? = null

        var updateFrequency: String? = null

        var updateTime: Long? = null

        class Track {
            var first: String? = null
            var second: String? = null
        }
    }
}