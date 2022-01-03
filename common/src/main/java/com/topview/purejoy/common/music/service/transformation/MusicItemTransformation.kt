package com.topview.purejoy.common.music.service.transformation

import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.transformation.ItemTransformation
import com.topview.purejoy.common.music.service.entity.MusicItem

object MusicItemTransformation : ItemTransformation<MusicItem> {
    const val ITEM_EXTRA = "MusicItem"
    override fun transform(source: Wrapper): MusicItem? {
        return source.bundle?.getParcelable(ITEM_EXTRA)
    }
}