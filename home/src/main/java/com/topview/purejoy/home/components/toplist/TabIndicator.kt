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
import androidx.compose.material.*
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
    indicatorWidth: Dp = 32.dp,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        TabDefaults.FixedIndicator(
            modifier = Modifier.simpleTabIndicatorOffset(
                tabPositions[selectedTabIndex],
                indicatorWidth,
                (-2).dp
            ).zIndex(-1F),
            color = Color.Red
        )
    },
    onTabClick: (Int) -> Unit = {},
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        edgePadding = 0.dp,
        divider = {},
        modifier = modifier,
        indicator = indicator
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


object TabDefaults {
    val DefaultHeight = 5.dp

    @Composable
    fun FixedIndicator(
        modifier: Modifier,
        height: Dp = DefaultHeight,
        color: Color = LocalContentColor.current
    ) {
        Box(
            modifier = modifier
                .height(height)
                .background(
                    color = color,
                    shape = RoundedCornerShape(50)
                )
        )
    }
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