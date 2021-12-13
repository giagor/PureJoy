package com.topview.purejoy.video.ui.horizontal

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.compose.LazyPagingItems
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ui.PlayerView
import com.topview.purejoy.video.entity.Video
import com.topview.purejoy.video.ui.LocalExoPlayer
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.util.ProgressUtil
import kotlin.math.roundToLong


@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun HorizontalVideoScreen(
    items: LazyPagingItems<Video>,
    videoLoadState: VideoLoadState,
    onPageChange: (Video?) -> Unit,
    onVideoSurfaceClick: () -> Unit,
    onRetryClick: (Video?) -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val currentPage by remember {
        snapshotFlow {
            pagerState.currentPage
        }
    }.collectAsState(initial = 0)
    // 上拉/下滑到新的一页的处理逻辑
    LaunchedEffect(currentPage) {
        onPageChange(if (items.itemCount <= currentPage) null else items[currentPage])
    }
    // 初始状态下为exoPlayer提供数据的Effect
    LaunchedEffect(items.itemCount > 0) {
        if (items.itemCount > 0) {
            onPageChange(items[0])
        }
    }

    ConstraintLayout(
        modifier = Modifier.background(color = Color.Black)
    ) {
        val (pager, indicator, errorTip, playIcon) = createRefs()
        VerticalPager(
            state = pagerState,
            count = items.itemCount,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(pager) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom, 10.dp)
                },
        ) { page ->
            val isCurrentPage = currentPage == page

            if (isCurrentPage) {
                HorizontalPagerChild(
                    isCurrentPage = true,
                    video = items[page],
                    onVideoSurfaceClick = onVideoSurfaceClick,
                    onMoreClick = onMoreClick,
                    onBackClick = onBackClick
                )

            } else {
                HorizontalPagerChild(
                    isCurrentPage = false,
                    video = items[page],
                    onMoreClick = onMoreClick,
                    onBackClick = onBackClick
                )
            }
        }
        when(videoLoadState) {
            is VideoLoadState.Loading -> {
                HorizontalLoadingComponents(
                    modifier = Modifier.constrainAs(indicator) {
                        centerTo(pager)
                    }
                )
            }
            is VideoLoadState.Error -> {
                HorizontalErrorComponents(
                    modifier = Modifier.constrainAs(errorTip) {
                        centerTo(pager)
                    },
                    onRetryClick = onRetryClick,
                    video = items[currentPage]
                )
            }
            is VideoLoadState.Pause-> {
                HorizontalPlayIcon(
                    modifier = Modifier.constrainAs(playIcon) {
                        centerTo(pager)
                    }
                )
            }
            else -> {}
        }
    }

}

/**
 * Pager的单个子Page布局
 */
@Composable
internal fun HorizontalPagerChild(
    modifier: Modifier = Modifier,
    video: Video?,
    isCurrentPage: Boolean,
    onVideoSurfaceClick: () -> Unit = {},
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isDragging by remember {
        mutableStateOf(false)
    }
    var progress by remember {
        mutableStateOf(0F)
    }

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (content, functionBar, title, creator, description, sliderRef) = createRefs()

        if (isCurrentPage && video != null) {
            val exoPlayer = LocalExoPlayer.current
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        useController = false
                        player = exoPlayer
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onVideoSurfaceClick
                    )
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(sliderRef.top)
                        centerHorizontallyTo(parent)
                    }
            )
        } else {
            val imageRef = createRef()
            Image(
                painter = rememberImagePainter(video?.coverUrl),
                contentDescription = null,
                modifier = Modifier.constrainAs(imageRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(sliderRef.top)
                    centerHorizontallyTo(parent)
                }
            )
        }
        HorizontalBottomSlider(
            video = video,
            modifier = Modifier
                .constrainAs(sliderRef) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
                .zIndex(1F),
            isCurrentPage = isCurrentPage,
            onProgressChange = {
                if (!isDragging) {
                    isDragging = true
                }
                progress = it
            },
            onProgressChangeFinished = {
                if (isDragging) {
                    isDragging = false
                }
            }
        )
        HorizontalPagerTitle(
            modifier = Modifier.constrainAs(title) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top, 10.dp)
            },
            isMv = video?.isMv ?: false,
            onBackClick = onBackClick,
            onMoreClick = onMoreClick
        )
        if (!isDragging) {
            HorizontalFunctionBar(
                video = video,
                modifier = Modifier.constrainAs(functionBar) {
                    end.linkTo(parent.end, 10.dp)
                    bottom.linkTo(sliderRef.top, 20.dp)
                }
            )
            CreatorView(
                video = video,
                modifier = Modifier.constrainAs(creator) {
                    bottom.linkTo(description.top, 10.dp)
                    start.linkTo(parent.start, 15.dp)
                }
            )
            DescriptionView(
                video = video,
                modifier = Modifier
                    .width(240.dp)
                    .constrainAs(description) {
                        bottom.linkTo(sliderRef.top, 20.dp)
                        start.linkTo(parent.start, 15.dp)
                    }
            )
        } else {
            val progressText = createRef()
            ProgressText(
                progress = ProgressUtil.toTimeText(progress.roundToLong()),
                duration = ProgressUtil.toTimeText(video?.duration ?: 0L),
                modifier = Modifier.constrainAs(progressText) {
                    bottom.linkTo(sliderRef.top, 36.dp)
                    centerHorizontallyTo(parent)
                }
            )
        }
    }
}