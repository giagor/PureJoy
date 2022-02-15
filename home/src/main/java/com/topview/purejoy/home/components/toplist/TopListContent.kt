package com.topview.purejoy.home.components.toplist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.common.util.dpToPx
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.entity.TopListTab
import com.topview.purejoy.home.theme.Gray235
import com.topview.purejoy.home.theme.Gray245
import com.topview.purejoy.home.theme.defaultTopListCardColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun TopListContent(
    topListMap: Map<TopListTab, List<TopList>>,
    onCardClick: (TopList) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    // 是否应该观察滑动距离
    var observeScroll by remember { mutableStateOf(true) }

    // 垂直空间中分隔符的高度,单位为dp
    val dividerHeightDp = 6
    // 垂直空间中分隔符的高度,单位为px
    val dividerHeightPx = dpToPx(dividerHeightDp)
    // Indicator当前选中的tab的位置
    var selectedIndex by remember { mutableStateOf(0) }
    // 缓存每个Item的高度的集合
    val heightMap: MutableMap<Int, Int> = remember {
        mutableMapOf(-1 to 0)
    }
    // 使用SnapShotFlow解决滚动高度更新过快造成重组过多带来的卡顿
    val flow = remember {
        snapshotFlow { scrollState.value }
    }
    // 监听滑动值并做出响应
    LaunchedEffect(true) {
        flow.collectLatest {
            if (observeScroll) {
                val maxHeight = scrollState.maxValue
                val tabHeight = heightMap.getNotNullValue(selectedIndex, maxHeight)
                // 临界状态，不需要跳变
                if (tabHeight == it) {
                    return@collectLatest
                }
                if (tabHeight < it) {
                    // 往下滑导致超出了当前Tab指示的内容的边界，处理这种情况很简单
                    for (i in selectedIndex until heightMap.size) {
                        if (heightMap.getNotNullValue(i, maxHeight) >= it) {
                            selectedIndex = i
                            break
                        }
                    }
                } else {
                    // 往上滑，我们需要找到一个索引, 它满足: 边界大于等于当前scroll的值并且它是最小的
                    // 当总的高度小于第一个Tab的边界，毫无疑问当前的selectedIndex应当为0,
                    // 为了能够直接在for循环内给出统一的逻辑而不是for循环后进行额外的操作,
                    // 因此把迭代结束值和默认值都设为-1
                    for (i in selectedIndex - 1 downTo -1) {
                        if (heightMap.getNotNullValue(i, -1) <= it) {
                            selectedIndex = i + 1
                            // 直接结束循环体
                            break
                        }
                    }
                }
            }
        }
    }

    val tabArray = TopListTab.values()
    // 避免selectedIndex改变的同时该lambda重新创建
    val onTabClick: (Int) -> Unit = {
        selectedIndex = it
        scope.launch {
            // 动画执行过程中不去监听滚动事件
            observeScroll = false
            scrollState.animateScrollTo(
                heightMap.getNotNullValue(it - 1, 0)
            )
            observeScroll = true
        }
    }

    // 整个背景是更暗的235，而Indicator、ActionBar和下面的每个CardItem的背景都应当更亮，背景的暗只应在交界处显现出来
    Surface(color = Gray235) {
        Column {
            Surface(
                color = Gray245
            ) {
                TabIndicator(
                    selectedTabIndex = selectedIndex,
                    onTabClick = onTabClick,
                    modifier = Modifier.padding(vertical = 15.dp),
                    backgroundColor = Gray245,
                    tabArray = tabArray
                )
            }
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                tabArray.forEachIndexed { index, tab ->
                    TopListTabColumnItem(
                        modifier = Modifier
                            .padding(bottom = dividerHeightDp.dp)
                            .onGloballyPositioned {
                                heightMap.apply {
                                    get(index) ?: get(index - 1)?.apply {
                                        heightMap[index] = it.size.height + dividerHeightPx + this
                                    }
                                }
                            },
                        tab = tab,
                        onCardClick = onCardClick,
                        topListMap = topListMap
                    )
                }
            }
        }
    }
}

@Composable
private fun TopListTabColumnItem(
    modifier: Modifier,
    tab: TopListTab,
    onCardClick: (TopList) -> Unit,
    topListMap: Map<TopListTab, List<TopList>>
) {
    val title = "${tab.content}榜"
    val topLists = topListMap[tab]!!
    if (tab == TopListTab.Official) {
        OfficialTopListContent(
            title = title,
            topLists = topLists,
            onCardClick = onCardClick,
            modifier = modifier
        )
    } else {
       GridTopListContent(
           title = title,
           topLists = topLists,
           onCardClick = onCardClick,
           modifier = modifier
       )
    }
}


@Composable
internal inline fun TitleItem(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 15.dp, bottom = 10.dp)
        )
        content()
    }
}

/**
 * 官方榜单内容
 */
@Composable
internal inline fun OfficialTopListContent(
    title: String,
    topLists: List<TopList>,
    modifier: Modifier = Modifier,
    crossinline onCardClick: (TopList) -> Unit = {}
) {
    TabItem(
        modifier = modifier,
        boxModifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
    ) {
        TitleItem(
            title = title,
        ) {
            topLists.forEachIndexed { index, topList ->
                val tracks = topList.tracks
                val urlList = topList.trackCoverUrl
                val colors = defaultTopListCardColor[index]
                if (tracks != null) {
                    OfficialTopListCard(
                        topList = topList,
                        tracks = tracks,
                        urlList = urlList,
                        backgroundColor = colors.backgroundColor,
                        titleColor = colors.titleColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onClick = { onCardClick.invoke(topList) },
                            )
                    )
                }
            }
        }
    }
}

/**
 * 以网格式排列的榜单
 */
@Composable
internal inline fun GridTopListContent(
    title: String,
    topLists: List<TopList>,
    modifier: Modifier = Modifier,
    crossinline onCardClick: (TopList) -> Unit = {}
) {
    TabItem(
        modifier = modifier,
        boxModifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 10.dp)
    ) {
        TitleItem(
            title = title,
        ) {
            TopListGrid(
                topLists = topLists,
                onChildClick = onCardClick,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }
    }

}

/**
 * 卡片式的居中布局
 */
@Composable
internal inline fun TabItem(
    modifier: Modifier = Modifier,
    boxModifier: Modifier = Modifier,
    crossinline content: @Composable BoxScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Gray245,
        contentColor = LocalContentColor.current,
        modifier = modifier,
        elevation = 0.dp
    ) {
        Box(
            modifier = boxModifier,
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}


private fun <T, V> MutableMap<T, V>.getNotNullValue(key: T, defaultValue: V): V =
    this[key] ?: defaultValue