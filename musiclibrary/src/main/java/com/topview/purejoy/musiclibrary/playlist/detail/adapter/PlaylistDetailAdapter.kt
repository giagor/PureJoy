package com.topview.purejoy.musiclibrary.playlist.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonBindingAdapter
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener

class PlaylistDetailAdapter() : CommonBindingAdapter<MusicItem,
        PlaylistDetailAdapter.PlaylistDetailHolder>(layoutId = R.layout.music_item) {

    var itemClickListener: DataClickListener<MusicItem>? = null
    var buttonClickListener: DataClickListener<MusicItem>? = null
    var mvClickListener: DataClickListener<MusicItem>? = null

    class PlaylistDetailHolder(viewDataBinding: ViewDataBinding)
        : CommonBindingAdapter.BindingHolder(viewDataBinding) {
        override fun variableId(): Int {
            return BR.musicItem
        }

    }

    override fun convert(holder: PlaylistDetailHolder, item: MusicItem) {
        val position = data.indexOf(item)
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(item, position)
        }
        holder.getView<ImageView>(R.id.music_item_more_bt).setOnClickListener {
            buttonClickListener?.onClick(item, position)
        }
        val iv = holder.getView<ImageView>(R.id.music_item_mv_bt)
        if (item.mv != -1L) {
            iv.visibility = View.VISIBLE
            iv.setOnClickListener {
                mvClickListener?.onClick(item, position)
            }
        } else {
            iv.visibility = View.GONE
        }
        super.convert(holder, item)
    }

    override fun createBindingHolder(
        parent: ViewGroup,
        layoutResId: Int,
        binding: ViewDataBinding
    ): PlaylistDetailHolder {
        return PlaylistDetailHolder(DataBindingUtil.inflate(LayoutInflater.
        from(context), layoutResId, parent, false))
    }

}