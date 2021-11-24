package com.topview.purejoy.musiclibrary.entity

import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper

data class AR(val id: Long, val name: String = "")

data class AL(val id: Long,
              val name: String = "",
              val picUrl: String = "", )

data class BR(val br: Int = 0, val fid: Long = 0, val size: Long = 0, val vd: Long = 0)

class MusicItem(
    val name: String = "",
    val id: Long,
    var url: String? = null,
    val ar: List<AR> = listOf(),
    val al: AL,
    val h: BR,
    val m: BR,
    val l: BR) : Item {

    override fun url(): String? {
        return url
    }

    override fun isSame(other: Item?): Boolean {
        if (other !is MusicItem) {
            return false
        }
        return other.id == id
    }

    fun getAuthors(): String {
        val builder = StringBuilder()
        for (a in ar) {
            if (builder.isNotEmpty()) {
                builder.append('/')
            }
            builder.append(a.name)
        }
        return builder.toString()
    }

    override fun toString(): String {
        return "[MusicItem name = $name, id = $id, url = $url, al = $al, ar = $ar]"
    }
}

/**
 * 从List中移除collection中包含的Item
 */
fun MutableList<MusicItem>.removeAll(collection: Collection<Wrapper>) {
    collection.forEach {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.isSame(it.value)) {
                iterator.remove()
                break
            }
        }
    }
}