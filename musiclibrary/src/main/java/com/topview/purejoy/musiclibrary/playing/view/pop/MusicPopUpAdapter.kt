package com.topview.purejoy.musiclibrary.playing.view.pop

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonBindingAdapter
import com.topview.purejoy.musiclibrary.entity.MusicItem

class MusicPopUpAdapter() : CommonBindingAdapter<MusicItem,
        MusicPopUpAdapter.PopUpHolder>(R.layout.music_pop_list_item) {

    class PopUpHolder(viewDataBinding: ViewDataBinding)
        : CommonBindingAdapter.BindingHolder(viewDataBinding) {
        override fun variableId(): Int {
            return BR.playingItem
        }
    }


    override fun createBindingHolder(
        parent: ViewGroup,
        layoutResId: Int,
        binding: ViewDataBinding
    ): PopUpHolder {
        return PopUpHolder(binding)
    }
}