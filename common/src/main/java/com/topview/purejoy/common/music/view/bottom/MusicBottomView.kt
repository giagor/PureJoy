package com.topview.purejoy.common.music.view.bottom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.topview.purejoy.common.R
import com.topview.purejoy.common.base.CommonActivity
import com.topview.purejoy.common.music.util.addViewToContent
import com.topview.purejoy.common.music.util.getDisplaySize
import com.topview.purejoy.common.router.CommonRouter
import com.topview.purejoy.common.widget.compose.RoundedCornerImageView

class MusicBottomView(
    val activity: CommonActivity,
    val controller: MusicController = MusicController(),
) : LifecycleObserver {
    private val popHelper: MusicPopHelper
    init {
        activity.lifecycle.addObserver(this)
        val size = activity.getDisplaySize()
        popHelper = MusicPopHelper(activity, size.width(),
            size.height() / 3 * 2, controller)
    }

    var bar: View? = null



    fun addMusicBottomBar(marginBottom: Int = 0, duration: Long = 0): View {
        if (this.bar != null) throw IllegalStateException("Bottom view has been added once")
        val bar = LayoutInflater.from(activity).inflate(R.layout.music_bottom_bar, null)
        val iv = bar.findViewById<RoundedCornerImageView>(R.id.music_bottom_bar_iv)
        val nameTx = bar.findViewById<TextView>(R.id.music_bottom_bar_name_tx)
        val stateIv = bar.findViewById<ImageView>(R.id.music_bottom_bar_status_iv)
        val listIv = bar.findViewById<ImageView>(R.id.music_bottom_bar_playlist_iv)
        val contentView = ((activity.window.decorView as ViewGroup).getChildAt(0)
                as ViewGroup).getChildAt(1) as FrameLayout
        controller.currentItem.observe(activity) {
            if (it == null) {
                bar.visibility = View.GONE
            } else {
                bar.visibility = View.VISIBLE
                iv.loadImageRequest = it.al.picUrl
                nameTx.text = it.name
            }
        }
        controller.playState.observe(activity) {
            if (it) {
                stateIv.setImageResource(R.drawable.music_bottom_bar_pause_32)
            } else {
                stateIv.setImageResource(R.drawable.music_bottom_bar_play_32)
            }
        }
        stateIv.setOnClickListener {
            controller.playerController?.playOrPause()
        }
        controller.currentItem.observe(activity) {
            if (it == null) {
                bar.visibility = View.GONE
            } else {
                if (bar.visibility != View.VISIBLE) {
                    bar.visibility = View.VISIBLE
                }
            }
        }
        listIv.setOnClickListener {
            if (!popHelper.musicPopWindow.popWindow.isShowing) {
                popHelper.musicPopWindow.popWindow.showAsDropDown(contentView)
            }
        }
        bar.setOnClickListener {
            CommonRouter.routeToPlayingActivity()
        }
        activity.addViewToContent(bar, marginBottom, duration)
        this.bar = bar
        return bar
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        controller.unregisterServiceListener()
        if (popHelper.musicPopWindow.popWindow.isShowing) {
            popHelper.musicPopWindow.popWindow.dismiss()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        controller.registerServiceListener()
    }
}