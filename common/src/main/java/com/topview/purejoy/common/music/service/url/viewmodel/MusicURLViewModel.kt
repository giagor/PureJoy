package com.topview.purejoy.common.music.service.url.viewmodel

import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.player.abs.Loader

interface MusicURLViewModel {
    fun requestMusicURL(item: MusicItem, index: Int, callback: Loader.Callback<Item>)
}