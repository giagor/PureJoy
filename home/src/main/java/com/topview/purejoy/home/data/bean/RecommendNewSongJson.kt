package com.topview.purejoy.home.data.bean

/**
 * 推荐新音乐数据 对应的实体类
 *
 * {
 *      ...
 *      result[
 *          {
 *              "id": 1894606139,
 *              "type": 4,
 *              "name": "一起向未来",
 *              "copywriter": null,
 *              "picUrl": "http://p1.music.126.net/CNn4NS8_qH_OQnQXd236IQ==/109951166616303692.jpg",
 *              "canDislike": true,
 *              "trackNumberUpdateTime": null,
 *              "song": {
 *                  ...
 *                  "artists": [
 *                       {
 *                           "name": "易烊千玺",
 *                           "id": 939088,
 *                           "picId": 0,
 *                           "img1v1Id": 0,
 *                           "briefDesc": "",
 *                           "picUrl": "http://p4.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg",
 *                           "img1v1Url": "http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg",
 *                           "albumSize": 0,
 *                           "alias": [],
 *                           "trans": "",
 *                           "musicSize": 0,
 *                           "topicPerson": 0
 *                       }
 *                   ],
 *                  ...
 *              },
 *              "alg": "hot_server"
 *          },...
 *      ]
 * }
 * */
class RecommendNewSongJson {
    var result: MutableList<Result>? = null

    class Result {
        var id: Long? = null
        var name: String? = null
        var picUrl: String? = null
        var song: Song? = null

        class Song {

            var artists: MutableList<Artist>? = null

            class Artist {
                var name: String? = null
                var id: Long? = null
            }
        }
    }
}