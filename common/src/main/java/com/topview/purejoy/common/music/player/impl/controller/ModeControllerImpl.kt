package com.topview.purejoy.common.music.player.impl.controller

import com.topview.purejoy.common.music.player.abs.MediaListenerManger
import com.topview.purejoy.common.music.player.abs.controller.ModeController
import com.topview.purejoy.common.music.player.impl.MediaListenerMangerImpl
import com.topview.purejoy.common.music.player.impl.listener.ModeFilter
import com.topview.purejoy.common.music.player.setting.MediaModeSetting

class ModeControllerImpl(override val listenerManager: MediaListenerManger = MediaListenerMangerImpl(),
                         override var current: Int
) : ModeController {

    override fun nextMode(): Int {
        current = MediaModeSetting.getInstance().getNextMode(current)
        listenerManager.invokeChangeListener(current, ModeFilter)
        return current
    }

    override fun setMode(mode: Int) {
        if (MediaModeSetting.getInstance().getPosition(mode) == null)
            throw IllegalArgumentException("Mode $mode is not existed!")
        current = mode
        listenerManager.invokeChangeListener(current, ModeFilter)
    }
}