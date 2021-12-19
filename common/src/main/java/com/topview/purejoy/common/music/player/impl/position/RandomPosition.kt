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
        return current
    }

    override fun next(): Int {
        if (nextMap.containsKey(current)) {
            current = nextMap[current]!!
        } else {
            val index = random.nextInt(max)
            nextMap[current] = index
            lastMap[index] = current
            current = index
        }
        return current
    }

    override fun last(): Int {
        if(lastMap.containsKey(current)) {
            current = lastMap[current]!!
        } else {
            val index = random.nextInt(max)
            nextMap[index] = current
            lastMap[current] = index
            current = index
        }
        return current
    }

    override fun with(position: Position) {
        lastMap.clear()
        nextMap.clear()
        current = position.current()
        max = position.max
    }
}