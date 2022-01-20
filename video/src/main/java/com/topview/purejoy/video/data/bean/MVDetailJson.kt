package com.topview.purejoy.video.data.bean

import com.google.gson.annotations.SerializedName
import com.topview.purejoy.common.entity.Video

class MVDetailJson(
    val code: Int,
    val data: MVDetailData?
) {
    class MVDetailData(
        val id: Long,
        val name: String,
        @SerializedName("desc") val description: String?,
        val artistName: String?,
        val cover: String?,
        val shareCount: Int,
        val commentCount: Int,
        val duration: Long,
        val artists: List<MVDetailArtist>?
    )

    class MVDetailArtist(
        val name: String,
        @SerializedName("img1v1Url") val avatar: String?
    )

    /**
     * 将值映射到[video]上
     */
    fun mappingToVideo(video: Video) {
        data?.run {
            if (cover != null) {
                video.coverUrl = cover
            }
            if (artistName != null) {
                video.creatorName = artistName
            }
            val avatar = artists?.get(0)?.avatar
            if (avatar != null) {
                video.creatorAvatarUrl = avatar
            }
            if (description != null) {
                video.description = description
            }
            if (video.duration > 0) {
                video.duration = duration
            }
            video.title = name
            video.shareCount = shareCount
            video.commentCount = commentCount
        }
    }
}

internal fun getArtistName(artists: List<MVDetailJson.MVDetailArtist>?): String {
    val builder = StringBuilder()
    artists?.forEachIndexed { index, artist ->
        builder.append(artist.name)
        if (index < artists.size - 1) {
            builder.append("、")
        }
    }
    return builder.toString()
}