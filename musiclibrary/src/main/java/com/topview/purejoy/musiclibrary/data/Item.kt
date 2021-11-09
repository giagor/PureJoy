package com.topview.purejoy.musiclibrary.data

import java.io.Serializable

// 音乐实体必须实现的接口
interface Item : Serializable {
    fun url(): String?
    fun isSame(other: Item?): Boolean = this == other
}