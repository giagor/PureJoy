package com.topview.purejoy.common.music.player.impl.position

import com.topview.purejoy.common.music.player.abs.core.Position

open class OrderPosition(override var max: Int, private var current: Int) : Position {
    override fun current(): Int {
        synchronized(this) {
            return current
        }
    }

    override fun next(): Int {
        synchronized(this) {
            current = (current + 1) % max
            return current
        }
    }

    override fun last(): Int {
        synchronized(this) {
            current = (current - 1 + max) % max
            return current
        }
    }

    override fun with(position: Position) {
        synchronized(this) {
            current = position.current()
            max = position.max
        }
    }

    override fun toString(): String {
        return "[OrderPosition current = $current, max = $max]"
    }
}