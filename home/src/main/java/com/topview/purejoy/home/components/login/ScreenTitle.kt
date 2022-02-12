package com.topview.purejoy.home.components.login

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 通用的界面标题
 * @param textStyle 标题文字样式
 * @param leadingContent 标题文字前的内容
 * @param trailingContent 标题文字后的内容
 */
@Composable
internal fun ScreenTitle(
    modifier: Modifier = Modifier,
    title: String,
    textStyle: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    leadingContent: @Composable RowScope.() -> Unit = {},
    trailingContent: @Composable RowScope.() -> Unit = {}
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        leadingContent()
        Text(
            text = title,
            modifier = Modifier.padding(start = 10.dp),
            style = textStyle
        )
        trailingContent()
    }
}
