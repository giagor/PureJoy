package com.topview.purejoy.musiclibrary.player.abs.transformation

import com.topview.purejoy.musiclibrary.data.Item
import com.topview.purejoy.musiclibrary.data.Wrapper

interface IWrapperTransformation<S : Item> : Transformation<S, Wrapper> {
}