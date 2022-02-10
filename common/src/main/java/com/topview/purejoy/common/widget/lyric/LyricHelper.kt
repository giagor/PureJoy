package com.topview.purejoy.common.widget.lyric

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

/**
 * 观察[LazyListState]的布局、滚动状态，计算位于中间的Item的索引
 */
@Composable
internal fun LazyListState.observeMidLine(
    midOffset: Int
) = produceState(
    initialValue = -1,
    key1 = layoutInfo
) {
    if (isScrollInProgress) {
        value = withContext(Dispatchers.IO) {
            layoutInfo.run {
                visibleItemsInfo.sortedWith { i1, i2 ->
                    i1.offset - i2.offset
                }.findLast {
                    // 这里需要对offset进行一次修正以获得相对于顶端的绝对偏移量
                    it.offset - viewportStartOffset < midOffset
                }?.index ?: -1
            }
        }
    }
    awaitDispose {}
}

/**
 * 滚动到索引为[index]上的Item，并令offset为该Item大小的一半
 */
internal suspend fun LazyListState.animateScrollToCenter(
    index: Int?
) {
    if (index == null || index !in 0 until layoutInfo.totalItemsCount) {
        return
    }
    val findLambda: () -> LazyListItemInfo? = {
        layoutInfo.visibleItemsInfo.find {
            it.index == index
        }
    }
    findLambda()?.let {
        // 直接滚动到指定位置即可
        animateScrollToItem(index, it.size / 2)
    } ?: let {
        // 先滚动到指定位置上边缘
        animateScrollToItem(index)
        findLambda()?.let {
            // 再滚动到中央
            animateScrollToItem(index, it.size / 2)
        }
    }
}

internal fun toTimeText(milliSecond: Long): String {
    val totalSecond = (milliSecond / 1000).toInt()
    val totalMinute = totalSecond / 60
    val second = totalSecond % 60
    val builder = StringBuilder()
    val decimalFormat = DecimalFormat("00")
    builder.append(decimalFormat.format(totalMinute))
    builder.append(':')
    builder.append(decimalFormat.format(second))
    return builder.toString()
}