package com.topview.purejoy.musiclibrary.recommendation.music.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.common.util.loadBitmap
import com.topview.purejoy.musiclibrary.entity.MusicItem

class RecommendPop(context: Context, width: Int, height: Int) {

    val window: PopupWindow by lazy {
        val w = PopupWindow(width, height)
        w.contentView = holder.itemView
        w.isFocusable = true
        w.isOutsideTouchable = true
        w
    }


    var data: MusicItem? = null
    set(value) {
        field = value
        updateTitle()
    }

    val holder: BaseViewHolder = BaseViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.music_daily_recommend_pop_layout, null))

    val content: LinearLayout = holder.getView(R.id.music_daily_recommend_pop_item_layout)



    fun updateTitle() {

        data?.let {
            holder.setText(R.id.music_recommend_pop_name_tx, it.name)
                .setText(R.id.music_recommend_pop_author_tx, it.getAuthors())

            loadBitmap(view = holder.getView(R.id.music_daily_recommend_pop_iv),
                it.al.picUrl)
        }

    }

    fun addItemView(image: Int, text: Int, click: ((MusicItem?) -> Unit)?) {
        val view = LayoutInflater.from(content.context)
            .inflate(R.layout.music_recommend_pop_item, null)
        view.findViewById<ImageView>(R.id.music_recommend_pop_item_iv)
            .setImageResource(image)
        view.findViewById<TextView>(R.id.music_recommend_pop_item_tx).setText(text)
        view.setOnClickListener {
            click?.invoke(data)
        }
        content.addView(view)
    }



}