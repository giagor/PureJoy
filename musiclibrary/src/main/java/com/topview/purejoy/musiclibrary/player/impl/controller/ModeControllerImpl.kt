package com.topview.purejoy.musiclibrary.player.impl.controller

import com.topview.purejoy.musiclibrary.player.abs.MediaListenerManger
import com.topview.purejoy.musiclibrary.player.abs.controller.ModeController
import com.topview.purejoy.musiclibrary.player.impl.MediaListenerMangerImpl
import com.topview.purejoy.musiclibrary.player.impl.listener.ModeFilter
import com.topview.purejoy.musiclibrary.player.setting.MediaModeSetting

class ModeControllerImpl(val listenerManager: MediaListenerManger = MediaListenerMangerImpl(),
                         override var current: Int
) : ModeController {

    override fun nextMode(): Int {
        current = MediaModeSetting.getInstance().getNextMode(current)
        listenerManager.invokeChangeListener(current, ModeFilter)
        return current
    }
}