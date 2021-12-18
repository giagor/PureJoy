package com.topview.purejoy.common.music.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.topview.purejoy.common.IPCPlayerController

class MusicNotificationReceiver(
    private val controller: IPCPlayerController,
    var listener: ClearListener? = null) : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            PREVIOUS_ACTION -> controller.last()
            NEXT_ACTION -> controller.next()
            STATE_ACTION -> controller.playOrPause()
            CLEAR_ACTION -> listener?.onClear()
            else -> {

            }
        }
    }

    interface ClearListener {
        fun onClear()
    }

    companion object {
        const val PREVIOUS_ACTION = "com.topview.purejoy.musiclibrary.previous"
        const val NEXT_ACTION = "com.topview.purejoy.musiclibrary.next"
        const val STATE_ACTION = "com.topview.purejoy.musiclibrary.state"
        const val CLEAR_ACTION = "com.topview.purejoy.musiclibrary.clear"
        const val TAG = "NotificationReceiver"
    }
}