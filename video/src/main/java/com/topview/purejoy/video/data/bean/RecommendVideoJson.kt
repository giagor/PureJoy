package com.topview.purejoy.video.data.bean

import com.google.gson.annotations.SerializedName
import com.topview.purejoy.common.entity.Video

data class RecommendVideoJson(
    val code: Int,
    @SerializedName("hasmore") val hasMore: Boolean?,
    @SerializedName("datas") val outerList: List<OuterData>?
)
class OuterData(
    val data: RecommendData
)

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
    @SerializedName("durationms") val duration: Long,
    @SerializedName("relateSong") val songs: List<SongJson>?
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

internal fun RecommendVideoJson.toVideos(): List<Video> {
    val list: MutableList<Video> = mutableListOf()
    outerList?.let { videoData ->
        if (videoData.isNotEmpty()) {
            videoData.forEach { outerData ->
                list.add(
                    outerData.data.toVideo()
                )
            }
        }
    }
    return list
}
internal fun RecommendData.toVideo(): Video =
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
        duration = duration,
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