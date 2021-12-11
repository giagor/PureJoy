package com.topview.purejoy.musiclibrary.player.abs.transformation

interface Transformation<S, T> {

    fun transform(source: S): T?

}