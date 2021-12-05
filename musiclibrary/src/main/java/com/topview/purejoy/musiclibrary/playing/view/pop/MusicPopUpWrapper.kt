package com.topview.purejoy.musiclibrary.playing.view.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting

class MusicPopUpWrapper(context: Context, width: Int, height: Int) {

    val rootView: View = LayoutInflater.from(context)
        .inflate(R.layout.activity_playing_pop_layout, null)

    val viewHolder: BaseViewHolder = BaseViewHolder(rootView)

    val adapter: PopAdapter = PopAdapter()

    init {
        val rv = viewHolder.getView<RecyclerView>(R.id.music_playing_pop_list)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
    }



    val popWindow: PopupWindow by lazy {
        val w = PopupWindow(context)
        w.width = width
        w.height = height
        w.contentView = rootView
        w.isFocusable = true
        w.isOutsideTouchable = true
        w
    }

    fun updatePlayingCount(count: Int) {
        viewHolder.setText(R.id.music_playing_pop_playing_count_tx, "($count)")
    }

    fun updateMode(mode: Int) {
        val iv = when(mode) {
            MediaModeSetting.ORDER -> R.drawable.music_playing_order_16
            MediaModeSetting.RANDOM -> R.drawable.music_playing_random_16
            MediaModeSetting.LOOP -> R.drawable.music_playing_loop_16
            else -> null
        }
        val tx = when(mode) {
            MediaModeSetting.ORDER -> R.string.order_mode
            MediaModeSetting.RANDOM -> R.string.random_mode
            MediaModeSetting.LOOP -> R.string.loop_mode
            else -> null
        }
        iv?.let {
            viewHolder.setImageResource(R.id.music_playing_pop_mode_iv, iv)
        }
        tx?.let {
            viewHolder.setText(R.id.music_playing_pop_mode_tx, tx)
        }
    }


}