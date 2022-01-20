package com.topview.purejoy.common.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * 视频信息通用实体类
 * @param id 视频的唯一标识符id，可以是vid(例如4C304D63074DA7507D74D32922DCEF11)或者是mvid(例如5436712)
 * @param isMv 视频是否为MV(MV与Mlog的接口不同，因此必须指明视频类型)
 * @param duration 视频时长
 * @param title 视频标题
 * @param videoUrl 播放地址
 * @param description 视频的详细描述
 * @param coverUrl 视频封面地址
 * @param likedCount 视频点赞数
 * @param commentCount 评论数
 * @param shareCount 分享数
 * @param creatorName 视频作者昵称（在MV里，视频作者名称为歌手本人）
 * @param creatorAvatarUrl 视频作者头像（在MV里，视频作者是歌手本人）
 * @param songName 表示该视频的背景BGM的名称。此名称尽量保持格式为"歌手 - 歌曲名"
 */
data class Video(
    val id: String,
    val isMv: Boolean,
    var duration: Long = DURATION_UNSPECIFIED,
    var title: String? = null,
    var videoUrl: String? = null,
    var description: String? = null,
    var coverUrl: String? = null,
    var likedCount: Int = UNSPECIFIED,
    var commentCount: Int = UNSPECIFIED,
    var shareCount: Int = UNSPECIFIED,

    var creatorName: String? = null,
    var creatorAvatarUrl: String? = null,
    var songName: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByte(if (isMv) 1 else 0)
        parcel.writeLong(duration)
        parcel.writeString(title)
        parcel.writeString(videoUrl)
        parcel.writeString(description)
        parcel.writeString(coverUrl)
        parcel.writeInt(likedCount)
        parcel.writeInt(commentCount)
        parcel.writeInt(shareCount)
        parcel.writeString(creatorName)
        parcel.writeString(creatorAvatarUrl)
        parcel.writeString(songName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        const val UNSPECIFIED = -1
        const val DURATION_UNSPECIFIED = 0L
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * 将可变部分的数据替换为另一个Video内的数据
     */
    fun merge(otherVideo: Video) {
        if (otherVideo.duration > 0) {
            duration = otherVideo.duration
        }
        if (otherVideo.title != null) {
            title = otherVideo.title
        }
        if (otherVideo.videoUrl != null) {
            videoUrl = otherVideo.videoUrl
        }
        if (otherVideo.description != null) {
            description = otherVideo.description
        }
        if (otherVideo.coverUrl != null) {
            coverUrl = otherVideo.coverUrl
        }
        if (otherVideo.creatorName != null) {
            creatorName = otherVideo.creatorName
        }
        if (otherVideo.creatorAvatarUrl != null) {
            creatorAvatarUrl = otherVideo.creatorAvatarUrl
        }
        if (otherVideo.songName != null) {
            songName = otherVideo.songName
        }
        likedCount = otherVideo.likedCount
        commentCount = otherVideo.commentCount
        shareCount = otherVideo.shareCount
    }
}
