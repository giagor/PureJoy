package com.topview.purejoy.video.data.bean

import com.google.gson.annotations.SerializedName
import com.topview.purejoy.common.entity.Video

class RecommendVideoJson{
    var code: Int? = null
    @SerializedName("msg") var message: String? = null
    @SerializedName("hasmore") var hasMore: Boolean? = null
    @SerializedName("datas") var outerList: List<OuterData<*>>? = null
    class OuterData<T>(
        /**
         * type=2为MV，type为1的为mlog视频，type为7是直播
         */
        val type: Int?,
        val data: T
    )
}

class RecommendData(
    val coverUrl: String,
    val vid: String,
    val title: String?,
    val description: String?,
    val praisedCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val creator: RecommendCreator?,
    val urlInfo: UrlInfo?,
    @SerializedName("durationms") val duration: Long?,
    @SerializedName("relateSong") val songs: List<SongJson>?,
    @SerializedName("playTime") val playCount: Long,
    val previewUrl: String?,
    val videoGroup: List<VideoGroupJson>?
)

class MVData(
    val coverUrl: String?,
    val id: Long,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val duration: Long?,
    val playCount: Long,
    val urlInfo: UrlInfo?,
    val name: String,
    val artists: List<MVDetailJson.MVDetailArtist>?,
    @SerializedName("relateSong") val songs: List<SongJson>?,
    val videoGroup: List<VideoGroupJson>?
)

class UrlInfo(
    val url: String?
)
class RecommendCreator(
    @SerializedName("nickname") val creatorName: String,
    @SerializedName("avatarUrl") val creatorAvatarUrl: String?,
)

class SongJson(val name: String, @SerializedName("ar") val artists: List<RecommendArtist>?)

class RecommendArtist(val name: String)

class VideoGroupJson(val name: String?)

fun RecommendData.toVideo(): Video =
    Video(
        id = vid,
        isMv = false,
        coverUrl = coverUrl,
        videoUrl = urlInfo?.url,
        title = title,
        description = description,
        likedCount = praisedCount,
        commentCount = commentCount,
        shareCount = shareCount,
        duration = duration ?: Video.UNSPECIFIED_LONG,
        creatorName = creator?.creatorName,
        creatorAvatarUrl = creator?.creatorAvatarUrl,
        songName = if (songs == null || songs.isEmpty()) {
            null
        } else {
            songs[0].run {
                if (artists == null || artists.isEmpty()) {
                    this.name
                } else {
                    "$name - ${artists[0].name}"
                }
            }
            null
        }
    )

fun MVData.toVideo(): Video =
    Video(
        id = id.toString(),
        isMv = true,
        coverUrl = coverUrl,
        videoUrl = urlInfo?.url,
        title = name,
        description = null,
        likedCount = likeCount,
        commentCount = commentCount,
        shareCount = shareCount,
        duration = duration ?: Video.UNSPECIFIED_LONG,
        creatorName = artists?.get(0)?.name,
        creatorAvatarUrl = artists?.get(0)?.avatar,
        songName = if (songs == null || songs.isEmpty()) {
            null
        } else {
            songs[0].run {
                if (this@toVideo.artists == null || this@toVideo.artists.isEmpty()) {
                    this.name
                } else {
                    "$name - ${getArtistName(this@toVideo.artists)}"
                }
            }
            null
        }
    )