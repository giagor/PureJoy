package com.topview.purejoy.musiclibrary.playlist.square.adapter

import android.widget.ImageView
import android.widget.TextView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.adapter.DataClickListener
import com.topview.purejoy.musiclibrary.playlist.entity.Playlist

class PlaylistSquareAdapter : BaseQuickAdapter<Playlist,
        BaseViewHolder>(R.layout.playlist_square_item) {

    var itemClickListener: DataClickListener<Playlist>? = null

    override fun convert(holder: BaseViewHolder, item: Playlist) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(item, data.indexOf(item))
        }
        val iv = holder.getView<ImageView>(R.id.playlist_square_item_iv)
        iv.load(item.coverImgUrl) {
            transformations(RoundedCornersTransformation(radius = 16f))
        }
        holder.getView<TextView>(R.id.playlist_square_item_name_tx).text = item.name
    }


}