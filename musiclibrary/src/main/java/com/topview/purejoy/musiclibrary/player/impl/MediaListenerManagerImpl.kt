package com.topview.purejoy.musiclibrary.player.impl

import com.topview.purejoy.musiclibrary.player.abs.MediaListenerManger
import com.topview.purejoy.musiclibrary.player.impl.listener.ChangeListener
import com.topview.purejoy.musiclibrary.player.impl.listener.ListenerFilter
import com.topview.purejoy.musiclibrary.player.util.castAs

class MediaListenerMangerImpl(
    private val listeners: MutableList<ChangeListener<*>> = mutableListOf(),
) : MediaListenerManger {
    override fun <T> registerChangeListener(listener: ChangeListener<T>) {
        listeners.add(listener)
    }

    override fun <T> removeChangeListener(listener: ChangeListener<T>) {
        listeners.remove(listener)
    }

    override fun <T> invokeChangeListener(value: T, filter: ListenerFilter<T>) {
        listeners.forEach { li ->
            li.castAs<ChangeListener<T>> {
                if (filter.filter(it)) {
                    it.onChange(value)
                }
            }
        }
    }
}