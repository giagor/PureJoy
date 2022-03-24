package com.topview.purejoy.common.music.service.url.viewmodel

import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.service.entity.MusicItem

interface MusicURLViewModel {
    fun requestMusicURLSync(item: MusicItem, index: Int): MusicItem
}