package com.topview.purejoy.video.data.bean

import com.google.gson.annotations.SerializedName

class VideoUrlJson(
    val code: Int,
    @SerializedName("urls") val url: List<UrlInfo>?
)