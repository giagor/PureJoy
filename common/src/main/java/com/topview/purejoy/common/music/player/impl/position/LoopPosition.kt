package com.topview.purejoy.common.music.player.impl.position

import com.topview.purejoy.common.music.player.abs.core.Position

open class LoopPosition(override var max: Int, private var current: Int) : Position {
    override fun current(): Int {
        synchronized(this) {
            return current
        }
    }

    override fun next(): Int {
        return current()
    }

    override fun last(): Int {
        return current()
    }

    override fun with(position: Position) {
        synchronized(this) {
            current = position.current()
            max = position.max
        }
    }

    override fun toString(): String {
        return "[LoopPosition current = $current, max = $max]"
    }
}