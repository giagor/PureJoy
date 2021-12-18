package com.topview.purejoy.common.music.player.impl.position

import com.topview.purejoy.common.music.player.abs.core.Position
import com.topview.purejoy.common.music.player.setting.MediaModeSetting

object InitPosition : Position {
    override fun current(): Int {
        return MediaModeSetting.INIT_POSITION
    }

    override fun next(): Int {
        return MediaModeSetting.INIT_POSITION
    }

    override fun last(): Int {
        return MediaModeSetting.INIT_POSITION
    }

    override fun with(position: Position) {

    }

    override var max: Int
        get() = 0
        set(value) {}
}