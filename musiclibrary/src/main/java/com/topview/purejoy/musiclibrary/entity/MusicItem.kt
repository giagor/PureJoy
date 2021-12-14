package com.topview.purejoy.musiclibrary.entity

import android.os.Parcel
import android.os.Parcelable
import com.topview.purejoy.musiclibrary.common.transformation.MusicItemTransformation
import com.topview.purejoy.musiclibrary.common.transformation.WrapperTransformation
import com.topview.purejoy.musiclibrary.data.ParcelableItem
import com.topview.purejoy.musiclibrary.data.Wrapper
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverALData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverARData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverData
import com.topview.purejoy.musiclibrary.service.recover.db.entity.RecoverMusicData

data class AR(val id: Long, val name: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AR> {
        override fun createFromParcel(parcel: Parcel): AR {
            return AR(parcel)
        }

        override fun newArray(size: Int): Array<AR?> {
            return arrayOfNulls(size)
        }
    }


}

data class AL(
    val id: Long,
    val name: String = "",
    val picUrl: String = "", ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(picUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AL> {
        override fun createFromParcel(parcel: Parcel): AL {
            return AL(parcel)
        }

        override fun newArray(size: Int): Array<AL?> {
            return arrayOfNulls(size)
        }
    }
}

data class BR(val br: Int = 0, val fid: Long = 0,
              val size: Long = 0, val vd: Long = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(br)
        parcel.writeLong(fid)
        parcel.writeLong(size)
        parcel.writeLong(vd)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BR> {
        override fun createFromParcel(parcel: Parcel): BR {
            return BR(parcel)
        }

        override fun newArray(size: Int): Array<BR?> {
            return arrayOfNulls(size)
        }
    }
}

data class MusicResponse(val code: Int, val songs: List<MusicItem>)

class MusicItem(
    val name: String = "",
    val id: Long,
    var url: String? = null,
    val ar: List<AR> = listOf(),
    val al: AL, ) : ParcelableItem {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString(),
        parcel.createTypedArrayList(AR) ?: listOf(),
        parcel.readParcelable(MusicItem::class.java.classLoader)!!
    ) {
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeTypedList(ar)
        parcel.writeParcelable(al, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicItem> {
        override fun createFromParcel(parcel: Parcel): MusicItem {
            return MusicItem(parcel)
        }

        override fun newArray(size: Int): Array<MusicItem?> {
            return arrayOfNulls(size)
        }
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

fun Wrapper.getMusicItem(): MusicItem? {
    return MusicItemTransformation.transform(this)
}

fun MusicItem.wrap(): Wrapper {
    return WrapperTransformation.transform(this)
}


fun List<MusicItem>.wrap(): List<Wrapper> {
    val ans = mutableListOf<Wrapper>()
    forEach {
        ans.add(it.wrap())
    }
    return ans
}