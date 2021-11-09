package com.topview.purejoy.musiclibrary.service.url.viewmodel

import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.player.abs.Loader

interface MusicURLViewModel {
    fun requestMusicURL(item: MusicItem, index: Int, callback: Loader.Callback<Item>)
}