package com.topview.purejoy.video.ui.vertical

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.ui.PlayerView
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.ui.LocalExoPlayer
import com.topview.purejoy.video.ui.VideoViewModel
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.util.ProgressUtil
import kotlin.math.roundToLong

/**
 * 短视频样式的竖屏视频播放页面
 * @param items Paging组件获取的延时加载Item
 * @param onVideoSurfaceClick 点击事件，点击视频表面触发
 * @param onRetryClick 点击事件，点击重试按钮触发
 * @param onBackClick 点击事件，点击左上角返回按钮触发
 * @param onMoreClick 点击事件，点击右上角的菜单项触发
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun VerticalVideoScreen(
    items: LazyPagingItems<Video>,
    onPageChange: (Video?) -> Unit,
    onVideoSurfaceClick: () -> Unit,
    onRetryClick: (Video?) -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit = {},
    viewModel: VideoViewModel = viewModel()
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
    // 监听Video的加载状态
    LaunchedEffect(viewModel.videoLoadState.value) {
        viewModel.videoLoadState.value.apply {
            if (this is VideoLoadState.Error) {
                // 当StatusCode或者是ContentType出现异常，抛弃掉原先的播放地址，这允许重新加载播放地址
                if (errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ||
                    errorCode == PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE) {
                    items[currentPage]?.videoUrl = null
                }
            }
        }
    }

    Box(
        modifier = Modifier.background(color = Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            count = items.itemCount,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxSize()
        ) { page ->
            val isCurrentPage = currentPage == page

            if (isCurrentPage) {
                VerticalPagerChild(
                    isCurrentPage = true,
                    videoState = mutableStateOf(items[page]) ,
                    onVideoSurfaceClick = onVideoSurfaceClick,
                    onMoreClick = onMoreClick,
                    onBackClick = onBackClick,
                    onRetryClick = onRetryClick
                )

            } else {
                VerticalPagerChild(
                    isCurrentPage = false,
                    videoState = mutableStateOf(items[page]),
                    onMoreClick = onMoreClick,
                    onBackClick = onBackClick
                )
            }
        }
    }

}

/**
 * Pager的单个子Page布局
 */
@Composable
internal fun VerticalPagerChild(
    modifier: Modifier = Modifier,
    videoState: State<Video?>,
    isCurrentPage: Boolean,
    onBackClick: () -> Unit,
    onRetryClick: (Video?) -> Unit = {},
    onMoreClick: () -> Unit = {},
    onVideoSurfaceClick: () -> Unit = {},
    viewModel: VideoViewModel = viewModel()
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

        if (isCurrentPage && videoState.value != null) {
            val exoPlayer = LocalExoPlayer.current
            val loadState = viewModel.videoLoadState.collectAsState()
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
                modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onVideoSurfaceClick
                    ).constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(sliderRef.top)
                        centerHorizontallyTo(parent)
                    }
            )

            val (indicator, errorTip, playIcon) = createRefs()
            when(loadState.value) {
                is VideoLoadState.Loading -> {
                    VerticalLoadingComponents(
                        modifier = Modifier.constrainAs(indicator) {
                            centerTo(parent)
                        }
                    )
                }
                is VideoLoadState.Error -> {
                    VerticalErrorComponents(
                        modifier = Modifier.constrainAs(errorTip) {
                            centerTo(parent)
                        },
                        onRetryClick = onRetryClick,
                        video = videoState.value
                    )
                }
                is VideoLoadState.Pause-> {
                    VerticalPlayIcon(
                        modifier = Modifier.constrainAs(playIcon) {
                            centerTo(parent)
                        }
                    )
                }
                else -> {}
            }
        } else {
            val imageRef = createRef()
            Image(
                painter = rememberImagePainter(videoState.value?.coverUrl),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(imageRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(sliderRef.top)
                        centerHorizontallyTo(parent)
                    }
                    .fillMaxSize()
            )
        }
        VerticalBottomSlider(
            video = videoState.value,
            modifier = Modifier
                .constrainAs(sliderRef) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
                .navigationBarsPadding()
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
        VerticalPagerTitle(
            modifier = Modifier
                .constrainAs(title) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, 10.dp)
                }
                .padding(
                    rememberInsetsPaddingValues(
                        insets = LocalWindowInsets.current.systemGestures,
                        additionalStart = 15.dp,
                        additionalTop = 5.dp,
                        additionalBottom = 10.dp
                    )
                ),
            isMv = videoState.value?.isMv ?: false,
            onBackClick = onBackClick,
            onMoreClick = onMoreClick
        )
        if (!isDragging) {
            VerticalFunctionBar(
                video = videoState.value,
                modifier = Modifier.constrainAs(functionBar) {
                    end.linkTo(parent.end, 10.dp)
                    bottom.linkTo(sliderRef.top, 20.dp)
                }
            )
            CreatorView(
                video = videoState.value,
                modifier = Modifier.constrainAs(creator) {
                    bottom.linkTo(description.top, 10.dp)
                    start.linkTo(parent.start, 15.dp)
                }
            )
            DescriptionView(
                video = videoState.value,
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
                duration = ProgressUtil.toTimeText(videoState.value?.duration ?: 0L),
                modifier = Modifier.constrainAs(progressText) {
                    bottom.linkTo(sliderRef.top, 36.dp)
                    centerHorizontallyTo(parent)
                }
            )
        }
    }
}

