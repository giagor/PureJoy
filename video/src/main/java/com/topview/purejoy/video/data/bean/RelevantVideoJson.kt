package com.topview.purejoy.video.data.bean

import androidx.core.text.isDigitsOnly
import com.google.gson.annotations.SerializedName
import com.topview.purejoy.common.entity.Video

class RelevantVideoJson(
    val code: Int,
    val message: String?,
    val data: List<RelevantData> = emptyList()
) {
    class RelevantData(
        val coverUrl: String,
        val vid: String,
        val title: String?,
        @SerializedName("durationms") val duration: Long,
    )
}

internal fun RelevantVideoJson.toVideos(): List<Video> {
    val list: MutableList<Video> = mutableListOf()
    data.forEach {
        list.add(
            Video(
                id = it.vid,
                isMv = it.vid.isDigitsOnly(),
                duration = it.duration,
                title = it.title,
                coverUrl = it.coverUrl
            )
        )
    }
    return list
}