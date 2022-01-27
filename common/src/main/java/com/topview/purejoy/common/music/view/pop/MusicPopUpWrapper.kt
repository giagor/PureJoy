package com.topview.purejoy.common.music.view.pop

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.topview.purejoy.common.R
import com.topview.purejoy.common.music.player.setting.MediaModeSetting
import com.topview.purejoy.common.music.service.entity.MusicItem

class MusicPopUpWrapper(context: Context, width: Int, height: Int, val window: Window) {

    val rootView: View = LayoutInflater.from(context)
        .inflate(R.layout.playing_pop_layout, null)

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
        w.setOnDismissListener {
            setBackground(1f)
        }
        w
    }

    private fun setBackground(value: Float) {
        val lp = window.attributes
        lp.alpha = value
        window.attributes = lp
    }

    fun showDownAt(view: View, alpha: Float = 0.5f) {
        popWindow.showAsDropDown(view, Gravity.BOTTOM, 0, 0)
        setBackground(alpha)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateWindow(data: List<MusicItem>) {
        adapter.data.clear()
        adapter.data.addAll(data)
        adapter.notifyDataSetChanged()
        updatePlayingCount(adapter.data.size)
    }

    fun updatePlayingCount(count: Int) {
        viewHolder.setText(R.id.music_playing_pop_playing_count_tx, "($count)")
    }

    fun updateCurrentItem(item: MusicItem?) {
        adapter.currentItem = item
    }

    fun updateMode(mode: Int) {
        val iv = when(mode) {
            MediaModeSetting.ORDER -> R.drawable.music_playing_order_16
            MediaModeSetting.RANDOM -> R.drawable.music_playing_random_16
            MediaModeSetting.LOOP -> R.drawable.music_playing_loop_16
            else -> null
        }
        val tx = when(mode) {
            MediaModeSetting.ORDER -> R.string.pop_order_mode
            MediaModeSetting.RANDOM -> R.string.pop_random_mode
            MediaModeSetting.LOOP -> R.string.pop_loop_mode
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