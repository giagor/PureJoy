package com.topview.purejoy.home.components.status

import com.topview.purejoy.common.music.service.entity.MusicItem

data class MusicItemLoadState(
    val pageState: PageState,
    val data: List<MusicItem>? = null
)