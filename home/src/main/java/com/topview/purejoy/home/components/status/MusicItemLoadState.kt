package com.topview.purejoy.home.components.status

import com.topview.purejoy.common.music.service.entity.MusicItem

class MusicItemLoadState(
    val value: PageState,
    val data: List<MusicItem>? = null
)