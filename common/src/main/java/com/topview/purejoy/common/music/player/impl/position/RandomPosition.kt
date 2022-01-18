package com.topview.purejoy.common.music.player.impl.position

import com.topview.purejoy.common.music.player.abs.core.Position
import java.util.*

class RandomPosition(override var max: Int,
                     private var current: Int,
                     var random: Random = Random(),
                     private val nextMap: MutableMap<Int, Int> = mutableMapOf(),
                     private val lastMap: MutableMap<Int, Int> = mutableMapOf()
) : Position {

    override fun current(): Int {
        synchronized(this) {
            return current
        }
    }

    override fun next(): Int {
        synchronized(this) {
            if (nextMap.containsKey(current)) {
                current = nextMap[current]!!
            } else {
                val index = random.nextInt(max)
                if (!nextMap.keys.contains(index)) {
                    nextMap[current] = index
                    lastMap[index] = current
                }
                current = index
            }
            return current
        }
    }

    override fun last(): Int {
        synchronized(this) {
            if(lastMap.containsKey(current)) {
                current = lastMap[current]!!
            } else {
                val index = random.nextInt(max)
                if (!lastMap.keys.contains(index)) {
                    nextMap[index] = current
                    lastMap[current] = index
                }
                current = index
            }
            return current
        }
    }

    override fun with(position: Position) {
        synchronized(this) {
            lastMap.clear()
            nextMap.clear()
            current = position.current()
            max = position.max
        }
    }

    override fun toString(): String {
        return "[RandomPosition current = $current, max = $max]"
    }
}