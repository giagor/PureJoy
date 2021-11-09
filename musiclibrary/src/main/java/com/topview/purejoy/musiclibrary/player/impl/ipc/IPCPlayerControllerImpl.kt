package com.topview.purejoy.musiclibrary.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.musiclibrary.IPCPlayerController
import com.topview.purejoy.musiclibrary.player.abs.controller.MediaController

class IPCPlayerControllerImpl(
    val realController: MediaController,
    val handler: Handler = Handler(Looper.getMainLooper())
) : IPCPlayerController.Stub() {
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


}