package com.topview.purejoy.musiclibrary.playlist.square.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import coil.ImageLoader
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView
import com.topview.purejoy.musiclibrary.BR
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.CommonBindingAdapter
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener
import com.topview.purejoy.musiclibrary.playlist.entity.Playlist

class PlaylistSquareAdapter : CommonBindingAdapter<Playlist,
        PlaylistSquareAdapter.PlaylistSquareHolder>(layoutId = R.layout.playlist_square_item) {

    var itemClickListener: DataClickListener<Playlist>? = null

    class PlaylistSquareHolder(binding: ViewDataBinding) : CommonBindingAdapter.BindingHolder(binding) {
        override fun variableId(): Int {
           return BR.squarePlaylist
        }

    }


    override fun convert(holder: PlaylistSquareHolder, item: Playlist) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(item, data.indexOf(item))
        }
        val iv = holder.getView<ImageView>(R.id.playlist_square_item_iv)
        iv.load(item.coverImgUrl) {
            transformations(RoundedCornersTransformation(radius = 16f))
        }
        super.convert(holder, item)
    }

    override fun createBindingHolder(
        parent: ViewGroup,
        layoutResId: Int,
        binding: ViewDataBinding
    ): PlaylistSquareHolder {
        return PlaylistSquareHolder(binding)
    }

}