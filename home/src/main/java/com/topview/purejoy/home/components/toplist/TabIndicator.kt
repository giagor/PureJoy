package com.topview.purejoy.home.components.toplist

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.topview.purejoy.home.entity.TabData
import com.topview.purejoy.home.entity.TopListTab

/**
 * 仿网易云的TabLayout
 * @param backgroundColor 整个TabRow的背景颜色
 * @param contentColor 提供给LocalContentColor的值
 * @param tabArray 包含每一项Tab信息的数组
 */
@Composable
internal fun TabIndicator(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    tabArray: Array<out TabData>,
    onTabClick: (Int) -> Unit = {},
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
    indicatorWidth: Dp = 32.dp
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        edgePadding = 0.dp,
        divider = {},
        modifier = modifier,
        indicator = {
            Box(
                modifier = Modifier
                    .simpleTabIndicatorOffset(
                        it[selectedTabIndex],
                        indicatorWidth,
                        (-2).dp
                    )
                    .height(5.dp)
                    .background(
                        color = Color.Red,
                        shape = RoundedCornerShape(50)
                    )
                    .zIndex(-1F)
            )
        }
    ) {
        tabArray.forEachIndexed { index, tabData ->
            val selected = index == selectedTabIndex
            IndicationTab(
                selected = selected,
                onClick = { onTabClick(index) },
            ) {
                Text(
                    tabData.content,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Light,
                    letterSpacing = 0.sp,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Composable
internal fun IndicationTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = indication
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}


internal fun Modifier.simpleTabIndicatorOffset(
    currentTabPosition: TabPosition,
    indicatorWidth: Dp,
    indicatorOffsetY: Dp = 0.dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentIndicatorWidth by animateDpAsState(
        targetValue = indicatorWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left +
                (currentTabPosition.width - currentIndicatorWidth) / 2,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    this.fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset, y = indicatorOffsetY)
        .width(currentIndicatorWidth)
}



@Preview(showBackground = true)
@Composable
private fun TabIndicatorPreview() {
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    Surface {
        TabIndicator(
            selectedTabIndex = selectedTabIndex,
            onTabClick = {
                selectedTabIndex = it
            },
            tabArray = TopListTab.values()
        )
    }
}