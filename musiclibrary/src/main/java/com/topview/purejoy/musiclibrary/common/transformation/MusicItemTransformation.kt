package com.topview.purejoy.musiclibrary.common.transformation

import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.player.abs.transformation.ItemTransformation

object MusicItemTransformation : ItemTransformation<MusicItem> {
    const val ITEM_EXTRA = "MusicItem"
    override fun transform(source: Wrapper): MusicItem? {
        return source.bundle?.getParcelable(ITEM_EXTRA)
    }
}