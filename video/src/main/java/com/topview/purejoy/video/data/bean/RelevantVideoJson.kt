package com.topview.purejoy.video.data.bean

import com.google.gson.annotations.SerializedName
import com.topview.purejoy.video.entity.Video

data class RelevantVideoJson(
    val code: Int,
    @SerializedName("hasmore") val hasMore: Boolean,
    @SerializedName("datas") val outerList: List<OuterData> = emptyList()
)
class OuterData(
    val data: Data
)
class Data(
    val coverUrl: String,
    val vid: String,
    val title: String?,
    val description: String?,
    val praisedCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val creator: Creator,
    val urlInfo: UrlInfo,
    @SerializedName("durationms") val duration: Long,
    @SerializedName("relateSong") val songs: List<SongJson> = emptyList()
)

class UrlInfo(
    val url: String
)
class Creator(
    @SerializedName("nickname") val creatorName: String,
    @SerializedName("avatarUrl") val creatorAvatarUrl: String?,
)

class SongJson(val name: String, @SerializedName("ar") val artists: List<Artist> = emptyList())

class Artist(val name: String)

internal fun RelevantVideoJson.toVideos(): List<Video> {
    if (outerList.isEmpty()) {
        return emptyList()
    } else {
        val list: MutableList<Video> = mutableListOf()
        outerList.forEach { outerData ->
            list.add(
                outerData.data.run {
                    Video(
                        vid = vid,
                        coverUrl = coverUrl,
                        videoUrl = urlInfo.url,
                        title = title,
                        description = description,
                        praisedCount = praisedCount,
                        commentCount = commentCount,
                        shareCount = shareCount,
                        duration = duration,
                        creatorName = creator.creatorName,
                        creatorAvatarUrl = creator.creatorAvatarUrl,
                        songName = if (songs.isEmpty()) {
                            null
                        } else {
                            songs[0].run {
                                if (artists.isEmpty()) {
                                    this.name
                                } else {
                                    "$name - ${artists[0].name}"
                                }
                            }
                        }
                    )
                }
            )
        }
        return list
    }
}