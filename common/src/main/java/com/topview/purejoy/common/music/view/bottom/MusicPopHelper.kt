package com.topview.purejoy.common.music.view.bottom

import android.annotation.SuppressLint
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.topview.purejoy.common.R
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.music.view.pop.MusicPopUpWrapper
import com.topview.purejoy.common.music.view.pop.PopAdapter
import com.topview.purejoy.common.music.service.entity.MusicItem
import com.topview.purejoy.common.music.service.entity.wrap

@SuppressLint("NotifyDataSetChanged")
class MusicPopHelper(val activity: CommonActivity, width: Int, height: Int, val controller: MusicController) {

    // 播放列表的弹窗
    val musicPopWindow: MusicPopUpWrapper by lazy {
        val p = MusicPopUpWrapper(activity, width = width, height, activity.window)
        p.adapter.itemClickListener = object : PopAdapter.PopItemClickListener {
            override fun onClick(item: MusicItem) {
                controller.playerController?.jumpTo(p.adapter.data.indexOf(item))
            }
        }
        p.adapter.deleteClickListener = object : PopAdapter.PopItemClickListener {
            override fun onClick(item: MusicItem) {
                controller.dataController?.remove(item.wrap())
            }

        }
        p.viewHolder.getView<LinearLayout>(R.id.music_playing_pop_mode_layout)
            .setOnClickListener {
                controller.modeController?.nextMode()
            }

        p.viewHolder.getView<ImageButton>(R.id.music_playing_pop_clear_bt)
            .setOnClickListener {
                showClearDialog()
            }
        controller.currentItem.observe(activity) {
            p.adapter.apply {
                this.currentItem = it
                notifyDataSetChanged()
            }
            if (it == null) {
                if(p.popWindow.isShowing) {
                    p.popWindow.dismiss()
                }
            }
        }
        controller.playItems.observe(activity) {
            if (it == null) {
                if (p.popWindow.isShowing) {
                    p.popWindow.dismiss()
                }
                return@observe
            }
            if (it.isEmpty()) {
                p.updateWindow(it)
                if (p.popWindow.isShowing) {
                    p.popWindow.dismiss()
                }
            } else {
                p.updateWindow(it)
            }
        }
        controller.currentMode.observe(activity) {
            p.updateMode(it)
        }
        p
    }

    fun showClearDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.clear_playing_msg)
            .setPositiveButton(
                R.string.ensure
            ) { dialog, _ ->
                controller.dataController?.clear()
                dialog?.dismiss()
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog?.dismiss()
            }
        builder.create().show()
    }

}