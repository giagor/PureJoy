package com.topview.purejoy.home.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Stable
class TopListCardColor(
    backgroundColor: Color,
    titleColor: Color
) {
    var backgroundColor by mutableStateOf(backgroundColor)
        private set
    var titleColor by mutableStateOf(titleColor)
        private set
}

internal val defaultTopListCardColor = listOf(
    TopListCardColor(
        Color(0xFFF4E1E7),
        Color(0xFFE62F77)
    ),
    TopListCardColor(
        Color(0xFFE1EBEA),
        Color(0xFF25988B)
    ),
    TopListCardColor(
        Color(0xFFF3E1E1),
        Color(0xFFE22424)
    ),
    TopListCardColor(
        Color(227,231,240),
        Color(55,111,193)
    ),
)