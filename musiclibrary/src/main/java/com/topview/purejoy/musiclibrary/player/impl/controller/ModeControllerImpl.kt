package com.topview.purejoy.musiclibrary.player.impl.controller

import com.topview.purejoy.musiclibrary.player.abs.controller.ModeController
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting

class ModeControllerImpl(override var current: Int) : ModeController {

    override fun nextMode(): Int {
        current = MediaModeSetting.getInstance().getNextMode(current)
        // should notify listener player's mode has changed
        return current
    }
}