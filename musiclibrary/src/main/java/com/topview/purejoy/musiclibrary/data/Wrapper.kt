package com.topview.purejoy.musiclibrary.data

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

// 音乐实体的封装类，便于在AIDL中使用
// 为了方便在集合中的使用，请传入一个唯一的标识identity,
class Wrapper(var value: SerializableItem? = null, var bundle: Bundle? = null,
              var identity: Long) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as? SerializableItem,
        parcel.readBundle(Wrapper::class.java.classLoader),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(value)
        parcel.writeBundle(bundle)
        parcel.writeLong(identity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Wrapper> {
        override fun createFromParcel(parcel: Parcel): Wrapper {
            return Wrapper(parcel)
        }

        override fun newArray(size: Int): Array<Wrapper?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Wrapper) {
            return false
        }
        return identity == other.identity
    }

    override fun hashCode(): Int {
        return (31 * identity).toInt()
    }

    override fun toString(): String {
        return "[Wrapper value = $value, bundle = $bundle]"
    }
}