package com.topview.purejoy.musiclibrary.data

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

// 音乐实体的封装类，便于在AIDL中使用
class Wrapper(var value: Item?, var bundle: Bundle? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as? Item,
        parcel.readBundle(Bundle::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(value)
        parcel.writeBundle(bundle)
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

    override fun toString(): String {
        return "[Wrapper value = $value, bundle = $bundle]"
    }
}