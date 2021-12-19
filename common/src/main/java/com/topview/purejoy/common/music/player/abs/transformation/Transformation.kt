package com.topview.purejoy.common.music.player.abs.transformation

interface Transformation<S, T> {

    fun transform(source: S): T?

}