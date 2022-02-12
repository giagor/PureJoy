package com.topview.purejoy.home.data.bean

/**
 * 获取每日推荐歌单数据对应的实体类
 *
 * {
 *      ...
 *      "result": [
 *          {
 *              "id": 2859214503,
 *              "type": 0,
 *              "name": "[一周欧美上新] Silk Sonic全专丝滑上线，Avril携回忆重磅回归！",
 *              "copywriter": null,
 *              "picUrl": "https://p1.music.126.net/lMhwnXGHHjEE1WP58h_Kww==/109951166614765565.jpg",
 *              "canDislike": false,
 *              "trackNumberUpdateTime": 1636754400000,
 *              "playCount": 23755644,
 *              "trackCount": 50,
 *              "highQuality": false,
 *              "alg": "featured"
 *          }...
 *      ]
 * }
 * */
class DailyRecommendPlayListJson {
    var result: MutableList<Result>? = null

    class Result {
        var id: Long? = null
        var name: String? = null
        var playCount: Long? = null
        var picUrl: String? = null
    }
}