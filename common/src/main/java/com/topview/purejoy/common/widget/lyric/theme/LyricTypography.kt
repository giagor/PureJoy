package com.topview.purejoy.common.widget.lyric.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


class LyricTypography(
    highlightTextStyle: TextStyle,
    normalTextStyle: TextStyle,
    indicatorTextStyle: TextStyle
) {
    /**
     * 高亮文字设置
     */
    var highlightTextStyle by mutableStateOf(highlightTextStyle)

    /**
     * 普通文字设置
     */
    var normalTextStyle by mutableStateOf(normalTextStyle)

    /**
     * 指示器文字设置
     */
    var indicatorTextStyle by mutableStateOf(indicatorTextStyle)
}

internal val DefaultLyricTypography = LyricTypography(
    highlightTextStyle = TextStyle(
        color = DefaultHighlightColor,
        fontSize = 16.sp
    ),
    normalTextStyle = TextStyle(
        color = Color.Black,
        fontSize = 16.sp
    ),
    indicatorTextStyle = TextStyle(
        fontSize = 12.sp
    )
)

internal val LocalLyricTypography = compositionLocalOf {
    DefaultLyricTypography
}