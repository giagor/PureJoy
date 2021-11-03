package com.topview.purejoy.musiclibrary.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.musiclibrary.IPCModeController
import com.topview.purejoy.musiclibrary.player.abs.controller.ModeController

class IPCModeControllerImpl(
    val handler: Handler = Handler(Looper.getMainLooper()),
    val realController: ModeController
) : IPCModeController.Stub() {
    override fun nextMode() {
        handler.post {
            realController.nextMode()
        }
    }

    override fun currentMode(): Int {
        return realController.current
    }
}