package com.topview.purejoy.video.entity

import com.topview.purejoy.common.entity.Video

internal data class VideoPlayerConfiguration(
    val initialList: List<Video>,
    val onCloseListener: (() -> Unit)?
)