package com.topview.purejoy.musiclibrary.common.adapter

interface DataClickListener<T> {
    fun onClick(value: T, position: Int)
}