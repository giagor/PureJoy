package com.topview.purejoy.video.entity

import android.os.Parcel
import android.os.Parcelable


data class Video(
    val vid: String,
    val duration: Long,
    val videoUrl: String,
    val title: String?,
    val description: String?,
    val isMv: Boolean = false,
    val coverUrl: String? = null,
    var praisedCount: Int = 0,
    var commentCount: Int = 0,
    var shareCount: Int = 0,

    val creatorName: String? = null,
    val creatorAvatarUrl: String? = null,
    val songName: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(vid)
        parcel.writeLong(duration)
        parcel.writeString(videoUrl)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeByte(if (isMv) 1 else 0)
        parcel.writeString(coverUrl)
        parcel.writeInt(praisedCount)
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
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }

}
