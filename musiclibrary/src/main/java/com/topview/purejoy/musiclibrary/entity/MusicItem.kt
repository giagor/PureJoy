package com.topview.purejoy.musiclibrary.entity

import com.topview.purejoy.musiclibrary.data.Item

data class AR(val id: Long, val name: String = "")

data class AL(val id: Long,
              val name: String = "",
              val picUrl: String = "", )

data class BR(val br: Int, val fid: Long, val size: Long, val vd: Long)

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
}