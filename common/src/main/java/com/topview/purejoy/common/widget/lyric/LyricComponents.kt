package com.topview.purejoy.common.widget.lyric

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.topview.purejoy.common.R
import com.topview.purejoy.common.util.dpToPx
import com.topview.purejoy.common.widget.lyric.theme.LocalLyricColors
import com.topview.purejoy.common.widget.lyric.theme.LocalLyricTypography
import kotlinx.coroutines.delay

/**
 * 显示关于控件状态的简短提示
 */
@Composable
internal fun LyricStateTip(
    modifier: Modifier,
    text: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = LocalLyricTypography.current.highlightTextStyle,
        )
    }
}

/**
 * 显示单句歌词的组件
 */
@Composable
internal fun LyricTextItem(
    modifier: Modifier = Modifier,
    showTrans: Boolean,
    originLyric: Sentence,
    transLyric: Sentence?,
    textStyle: TextStyle
) {
    val text = if (showTrans && transLyric != null) {
        "${originLyric.content}\n${transLyric.content}"
    } else {
        originLyric.content
    }
    Box(
        modifier = modifier
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = textStyle,
        )
    }
}

/**
 * 歌词指示器
 */
@Composable
internal fun LyricIndicator(
    modifier: Modifier = Modifier,
    text: String,
    onIconClick: () -> Unit = {}
) {
    CompositionLocalProvider(
        LocalContentColor provides LocalLyricColors.current.indicatorColor
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = text,
                style = LocalLyricTypography.current.indicatorTextStyle,
            )
            Divider(
                modifier = Modifier.weight(1F, false)
            )
            Icon(
                painter = painterResource(id = R.drawable.common_lyric_play_circle_outline_24),
                contentDescription = null,
                modifier = Modifier.clickable(
                    onClick = onIconClick,
                )
            )
        }
    }
}

/**
 * Compose歌词控件
 * @param onPlayIconClick 点击播放Icon时回调
 * @param onContentClick 点击内容时回调
 */
@Composable
fun LyricContent(
    modifier: Modifier = Modifier,
    lyricState: LyricState,
    loadState: LyricLoadState,
    onPlayIconClick: (Sentence?) -> Unit = {},
    onContentClick: () -> Unit = {}
) {
    Surface(
        color = LocalLyricColors.current.backgroundColor,
        modifier = modifier,
        contentColor = LocalContentColor.current
    ) {
        BoxWithConstraints {
            val simpleModifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .clickable(onClick = onContentClick)
            when (loadState) {
                is LyricLoadState.Error -> {
                    LyricStateTip(
                        modifier = simpleModifier,
                        text = stringResource(id = R.string.common_lyric_error)
                    )
                }
                is LyricLoadState.Empty -> {
                    LyricStateTip(
                        modifier = simpleModifier,
                        text = stringResource(id = R.string.common_lyric_empty)
                    )
                }
                is LyricLoadState.Loading -> {
                    LyricStateTip(
                        modifier = simpleModifier,
                        text = stringResource(id = R.string.common_lyric_loading)
                    )
                }
                is LyricLoadState.Success -> {
                    if (loadState.lyricList.size > 1) {
                        val listState = rememberLazyListState()

                        // 滚动状态
                        val dragState by listState.interactionSource.collectIsDraggedAsState()
                        // 高亮歌词的索引
                        val highlight by lyricState.highlightLineIndex.observeAsState()
                        // 是否显示翻译歌词
                        val showTrans by lyricState.showTransLyric.observeAsState()

                        // 是否显示指示器
                        var showIndicator by remember { mutableStateOf(false) }
                        val delayTime = rememberUpdatedState(
                            newValue = lyricState.indicatorDismissTime
                        )

                        val midHeight = maxHeight / 2
                        val midHeightPx = remember(maxHeight) {
                            dpToPx(midHeight.value).toInt()
                        }

                        // 指示器显示/隐藏，同时复位歌词
                        LaunchedEffect(dragState) {
                            if (dragState) {
                                showIndicator = true
                            } else {
                                delay(delayTime.value)
                                if (showIndicator) {
                                    listState.animateScrollToCenter(highlight)
                                    showIndicator = false
                                }

                            }
                        }

                        // 根据高亮歌词变化自动滚动
                        LaunchedEffect(highlight) {
                            if (!showIndicator) {
                                listState.animateScrollToCenter(highlight)
                            }
                        }

                        val middleIndex by listState.observeMidLine(
                            midOffset = midHeightPx
                        )

                        with(loadState.lyricList.getOrNull(middleIndex)) {
                            // 展示指示线的额外条件是歌词多于一行
                            if (showIndicator) {
                                LyricIndicator(
                                    text = this?.fromTime?.let {
                                        toTimeText(it)
                                    } ?: "",
                                    modifier = Modifier
                                        .padding(horizontal = lyricState.indicatorPadding.dp)
                                        .align(Alignment.Center),
                                    onIconClick = {
                                        onPlayIconClick(this)
                                        showIndicator = false
                                    },
                                )
                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(
                                vertical = midHeight,
                            ),
                            state = listState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = lyricState.contentPadding.dp)
                                .align(Alignment.TopCenter)
                                .clickable(onClick = onContentClick)
                        ) {
                            itemsIndexed(loadState.lyricList) { index, sentence ->
                                LyricTextItem(
                                    showTrans = showTrans == true,
                                    originLyric = sentence,
                                    transLyric = loadState.transLyricMap[sentence],
                                    textStyle = LocalLyricTypography.current.let {
                                        if (index == highlight) {
                                            it.highlightTextStyle
                                        } else {
                                            it.normalTextStyle
                                        }
                                    },
                                    modifier = Modifier.padding(
                                        vertical = lyricState.textMargin.dp,
                                    )
                                )
                            }
                        }
                    } else {
                        LyricStateTip(
                            modifier = simpleModifier,
                            text = loadState.lyricList.getOrNull(0)?.content ?: "")
                    }
                }
            }
        }
    }
}
