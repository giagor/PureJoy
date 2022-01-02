package com.topview.purejoy.common.music.service.url.viewmodel

import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.player.abs.Loader
import com.topview.purejoy.common.music.service.entity.MusicItem

interface MusicURLViewModel {
    fun requestMusicURL(item: MusicItem, index: Int, callback: Loader.Callback<Item>)
}