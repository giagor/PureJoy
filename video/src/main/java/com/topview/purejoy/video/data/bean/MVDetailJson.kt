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
            video.title = name
            video.coverUrl = cover
            video.creatorName = artistName
            video.creatorAvatarUrl = artists?.get(0)?.avatar
            video.duration = duration
            video.description = description
            video.shareCount = shareCount
            video.commentCount = commentCount
        }
    }
}