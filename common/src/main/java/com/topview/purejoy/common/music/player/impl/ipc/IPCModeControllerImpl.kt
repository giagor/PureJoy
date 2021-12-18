package com.topview.purejoy.common.music.player.impl.ipc

import android.os.Handler
import android.os.Looper
import com.topview.purejoy.common.IPCModeController
import com.topview.purejoy.common.music.player.abs.controller.ModeController

class IPCModeControllerImpl(
    val handler: Handler = Handler(Looper.getMainLooper()),
    val realController: ModeController
) : IPCModeController.Stub() {
    override fun nextMode() {
        handler.post {
            realController.nextMode()
        }
    }

    private val TAG = "ModeController"

    override fun currentMode(): Int {
        return realController.current
    }
}