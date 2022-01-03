package com.topview.purejoy.common.music.service.transformation

import android.os.Bundle
import com.topview.purejoy.common.music.data.Wrapper
import com.topview.purejoy.common.music.player.abs.transformation.IWrapperTransformation
import com.topview.purejoy.common.music.service.entity.MusicItem

object WrapperTransformation : IWrapperTransformation<MusicItem> {
    override fun transform(source: MusicItem): Wrapper {
        val bundle = Bundle()
        bundle.putParcelable(MusicItemTransformation.ITEM_EXTRA, source)
        return Wrapper(bundle = bundle, identity = source.id)
    }
}