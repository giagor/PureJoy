package com.topview.purejoy.musiclibrary.player.util

import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.player.abs.core.Position

inline fun <reified T> Any.castAs(block: (T) -> Unit) {
    castAs<T>()?.let {
        block(it)
    }
}

// 便于类型转换
inline fun <reified T> Any.castAs(): T? {
    return if (this is T) {
        this
    } else {
        null
    }
}

// 仅仅是为了在语法上通过检查
fun <T : Item> Item.cast() : T? {
    return (this as? T)
}

fun <T : Item> Item.cast(action: (T) -> Unit) {
    this.cast<T>()?.let {
        action.invoke(it)
    }
}

fun ensureSecurity(source: MutableList<*>, position: Position, action: () -> Unit) {
    if (position.current() < source.size) {
        action.invoke()
    }
}

fun ensureSecurity(source: MutableList<*>, index: Int, action: () -> Unit) {
    if (index < source.size) {
        action.invoke()
    }
}

fun checkWrapper(wrapper: Wrapper?, action: (Wrapper) -> Unit) {
    if (wrapper == null || (wrapper.value == null && wrapper.bundle == null)) {
        throw IllegalArgumentException()
    } else {
        action.invoke(wrapper)
    }
}