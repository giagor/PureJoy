package com.topview.purejoy.common.music.player.abs.transformation

import com.topview.purejoy.common.music.data.Item
import com.topview.purejoy.common.music.data.Wrapper

interface ItemTransformation<T : Item> : Transformation<Wrapper, T> {
}