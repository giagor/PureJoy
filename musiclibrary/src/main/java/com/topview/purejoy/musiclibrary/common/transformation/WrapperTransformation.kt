package com.topview.purejoy.musiclibrary.common.transformation

import android.os.Bundle
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.entity.MusicItem
import com.topview.purejoy.musiclibrary.player.abs.transformation.IWrapperTransformation
import com.topview.purejoy.musiclibrary.player.abs.transformation.Transformation

object WrapperTransformation : IWrapperTransformation<MusicItem> {
    override fun transform(source: MusicItem): Wrapper {
        val bundle = Bundle()
        bundle.putParcelable(MusicItemTransformation.ITEM_EXTRA, source)
        return Wrapper(bundle = bundle, identity = source.id)
    }
}