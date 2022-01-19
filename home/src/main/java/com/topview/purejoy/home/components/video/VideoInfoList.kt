package com.topview.purejoy.home.components.video

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.topview.purejoy.home.R
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.util.LocalMusicController
import com.topview.purejoy.video.VideoPlayerLauncher

/**
 * 显示视频信息的列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun VideoInfoList(
    modifier: Modifier = Modifier,
    videoItems: LazyPagingItems<ExternVideo>,
    state: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
) {
    val controller = LocalMusicController.current

    // 观察refresh的状态并更新
    LaunchedEffect(videoItems.loadState.refresh) {
        state.isRefreshing = videoItems.loadState.refresh is LoadState.Loading
    }

    SwipeRefresh(
        state = state,
        onRefresh = {
            videoItems.refresh()
        },
        modifier = modifier
    ) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = PaddingValues(3.dp)
        ) {
            items(videoItems.itemCount) {
                val item = videoItems[it]
                if (item != null) {
                    VideoInfoCard(
                        externVideo = item,
                        modifier = Modifier
                            .padding(3.dp)
                            .height(280.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    controller.playerController?.apply {
                                        if (isPlaying) {
                                            playOrPause()
                                        }
                                    }
                                    VideoPlayerLauncher.launch(
                                        listOf(item.video)
                                    )
                                }
                            )
                    )
                }
            }
            videoItems.loadState.append.apply {
                when (this) {
                    is LoadState.Error -> {
                        item {
                            AppendErrorItemCard(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .height(280.dp),
                                onClick = {
                                    videoItems.retry()
                                }
                            )
                        }
                    }
                    is LoadState.Loading -> {
                        val childModifier = Modifier.padding(3.dp)
                            .height(280.dp)
                        item {
                            AppendLoadingItemCard(modifier = childModifier)
                        }
                        // 如果卡片数不是单数
                        if (videoItems.itemCount % 2 == 0) {
                            item {
                                AppendLoadingItemCard(modifier = childModifier)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    if (videoItems.loadState.refresh is LoadState.Error && videoItems.itemCount == 0) {
        RefreshErrorItem(
            modifier = Modifier.fillMaxSize(),
            onClick = {
                videoItems.retry()
            }
        )
    }
}

@Composable
private fun RefreshErrorItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.clickable(
            onClick = onClick,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.home_video_refresh_video_error),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            color = Color.Gray,
        )
    }
}