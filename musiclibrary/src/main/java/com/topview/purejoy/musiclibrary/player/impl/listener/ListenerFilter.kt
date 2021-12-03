package com.topview.purejoy.musiclibrary.player.impl.listener

import com.topview.purejoy.musiclibrary.data.Item

// 过滤器，用于过滤不符合条件的ChangeListener
interface ListenerFilter<T> {
    fun filter(listener: ChangeListener<T>): Boolean
}

// PlayStateChangeListener的过滤器
object PlayStateFilter : ListenerFilter<Boolean> {
    override fun filter(listener: ChangeListener<Boolean>): Boolean {
        return listener.key == PlayStateKey
    }
}

// ModeStateChangeListener的过滤器
object ModeFilter : ListenerFilter<Int> {
    override fun filter(listener: ChangeListener<Int>): Boolean {
        return listener.key == ModeStateKey
    }
}

// ItemChangeListener的过滤器
object ItemFilter : ListenerFilter<Item> {
    override fun filter(listener: ChangeListener<Item>): Boolean {
        return listener.key == ItemKey
    }
}