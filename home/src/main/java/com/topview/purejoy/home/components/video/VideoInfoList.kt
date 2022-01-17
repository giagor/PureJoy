package com.topview.purejoy.home.components.video

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.video.VideoPlayerLauncher

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun VideoInfoList(
    modifier: Modifier = Modifier,
    videoItems: LazyPagingItems<ExternVideo>,
    state: SwipeRefreshState,
    onRefresh: () -> Unit,
) {
    // TODO 音乐的暂停和恢复
    SwipeRefresh(
        state = state,
        onRefresh = onRefresh,
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
                                    VideoPlayerLauncher.launch(
                                        listOf(item.video)
                                    )
                                }
                            )
                    )
                }
            }
        }
    }
}