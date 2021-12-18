package com.topview.purejoy.common.music.activity.pop

import android.graphics.Color
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.topview.purejoy.common.R
import com.topview.purejoy.common.music.service.entity.MusicItem

class PopAdapter()
    : BaseQuickAdapter<MusicItem, BaseViewHolder>(R.layout.music_pop_list_item) {

    var currentItem: MusicItem? = null
    set(value) {
        field = value
        notifyItemRangeChanged(0, data.size)
    }

    var itemClickListener: PopItemClickListener? = null


    var deleteClickListener: PopItemClickListener? = null


    override fun convert(holder: BaseViewHolder, item: MusicItem) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(item)
        }
        holder.getView<ImageView>(R.id.music_pop_list_item_cancel_iv).setOnClickListener {
            deleteClickListener?.onClick(item)
        }
        holder.setText(R.id.music_pop_list_item_name_tx, item.name)
            .setText(R.id.music_pop_list_item_author_tx, " - ${item.getAuthors()}")
        if (item == currentItem) {
            holder.setTextColor(R.id.music_pop_list_item_name_tx, Color.RED)
                .setTextColor(R.id.music_pop_list_item_author_tx, Color.RED)
        } else {
            holder.setTextColor(R.id.music_pop_list_item_name_tx, Color.BLACK)
                .setTextColor(R.id.music_pop_list_item_author_tx, Color.BLACK)
        }
    }





    interface PopItemClickListener {
        fun onClick(item: MusicItem)
    }

}