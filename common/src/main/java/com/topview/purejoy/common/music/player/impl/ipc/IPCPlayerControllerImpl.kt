package com.topview.purejoy.common.music.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.common.IPCPlayerController
import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.player.abs.controller.MediaController

class IPCPlayerControllerImpl<T : Item>(
    val realController: MediaController<T>,
    val handler: Handler = Handler(Looper.getMainLooper())
) : IPCPlayerController.Stub() {

    private val TAG = "PlayerController"

    override fun last() {
        handler.post {
            realController.last()
        }
    }

    override fun next() {
        handler.post {
            realController.next()
        }
    }

    override fun playOrPause() {
        handler.post {
            realController.playOrPause()
        }
    }

    override fun duration(): Int {
        return realController.duration()
    }

    override fun seekTo(progress: Int) {
        handler.post {
            realController.seekTo(progress)
        }
    }

    override fun isPlaying(): Boolean {
        return realController.isPlaying()
    }

    override fun progress(): Int {
        return realController.progress()
    }

    override fun jumpTo(index: Int) {
        handler.post {
            realController.jumpTo(index)
        }
    }


}