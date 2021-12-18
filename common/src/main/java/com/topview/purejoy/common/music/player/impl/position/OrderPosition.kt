package com.topview.purejoy.common.music.player.impl.position

import com.topview.purejoy.common.music.player.abs.core.Position

open class OrderPosition(override var max: Int, private var current: Int) : Position {
    override fun current(): Int {
        return current
    }

    override fun next(): Int {
        current = (current + 1) % max
        return current
    }

    override fun last(): Int {
        current = (current - 1 + max) % max
        return current
    }

    override fun with(position: Position) {
        current = position.current()
        max = position.max
    }

}