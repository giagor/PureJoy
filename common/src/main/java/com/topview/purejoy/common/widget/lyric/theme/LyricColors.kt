package com.topview.purejoy.common.widget.lyric.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Stable
class LyricColors(
    indicatorColor: Color,
    backgroundColor: Color,
) {
    /**
     * 指示器颜色
     */
    var indicatorColor by mutableStateOf(indicatorColor)

    /**
     * 背景颜色
     */
    var backgroundColor by mutableStateOf(backgroundColor)
}

internal val DefaultHighlightColor = Color(0xFF2880EB)
internal val DefaultIndicatorColor = Color(0xFF5a5a5a)
internal val DefaultBackgroundColor = Color.Transparent

internal val LocalLyricColors = compositionLocalOf {
    LyricColors(
        indicatorColor = DefaultIndicatorColor,
        backgroundColor = DefaultBackgroundColor
    )
}