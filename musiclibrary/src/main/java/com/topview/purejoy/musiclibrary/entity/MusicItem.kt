package com.topview.purejoy.musiclibrary.entity

import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverALData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverARData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverMusicData
import java.io.Serializable

data class AR(val id: Long, val name: String = "") : Serializable

data class AL(val id: Long,
                       val name: String = "",
                       val picUrl: String = "", ) : Serializable

data class BR(val br: Int = 0, val fid: Long = 0,
              val size: Long = 0, val vd: Long = 0) : Serializable

class MusicItem(
    val name: String = "",
    val id: Long,
    var url: String? = null,
    val ar: List<AR> = listOf(),
    val al: AL, ) : Item {

    override fun url(): String? {
        return url
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

    override fun equals(other: Any?): Boolean {
        if (other !is MusicItem) {
            return false
        }
        return id == other.id && name == other.name && getAuthors() == other.getAuthors() && al == other.al
    }

    override fun hashCode(): Int {
        return (id * 37 + 51 * name.hashCode() + 23 * ar.hashCode() + 11 * al.hashCode()).toInt()
    }

    override fun toString(): String {
        return "[MusicItem name = $name, id = $id, url = $url, al = $al, ar = $ar]"
    }
}

fun MusicItem.toRecoverMusic(): RecoverMusicData {
    return RecoverMusicData(id = id, name = name, url = url)
}

fun AR.toRecoverAR(mid: Long): RecoverARData {
    return RecoverARData(id = id, name = name, mid = mid)
}

fun AL.toRecoverAL(mid: Long): RecoverALData {
    return RecoverALData(id = id, name = name, picUrl = picUrl, mid = mid)
}

fun RecoverData.toMusicItem(): MusicItem {
    val al = alData.toAL()
    val ar = mutableListOf<AR>()
    for (d in arDataList) {
        ar.add(d.toAR())
    }
    return MusicItem(name = musicData.name, id = musicData.id, url = musicData.url, ar = ar, al = al)
}

fun RecoverARData.toAR(): AR {
    return AR(id, name)
}

fun RecoverALData.toAL(): AL {
    return AL(id, name, picUrl)
}

/**
 * 从List中移除collection中包含的Item
 */
fun MutableList<MusicItem>.removeAll(collection: Collection<Wrapper>) {
    collection.forEach {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item == it.value) {
                iterator.remove()
                break
            }
        }
    }
}

fun List<MusicItem>.wrap(): List<Wrapper> {
    val ans = mutableListOf<Wrapper>()
    forEach {
        ans.add(Wrapper(value = it))
    }
    return ans
}