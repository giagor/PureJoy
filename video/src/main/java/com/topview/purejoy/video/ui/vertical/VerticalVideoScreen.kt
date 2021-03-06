package com.topview.purejoy.video.ui.vertical

import android.content.pm.ActivityInfo
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
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ui.PlayerView
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.ui.LocalExoPlayer
import com.topview.purejoy.video.ui.VideoViewModel
import com.topview.purejoy.video.ui.components.CreatorView
import com.topview.purejoy.video.ui.components.ErrorComponents
import com.topview.purejoy.video.ui.components.OrientationChangeIcon
import com.topview.purejoy.video.ui.components.PlayIcon
import com.topview.purejoy.video.ui.state.BottomSliderState
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.util.ProgressUtil
import com.topview.purejoy.video.util.setOrientation
import kotlin.math.roundToLong

/**
 * ??????????????????????????????????????????
 * @param items Paging???????????????????????????Item
 * @param sliderState ???????????????????????????????????????????????????????????????????????????????????????????????????
 * @param onVideoSurfaceClick ???????????????????????????????????????
 * @param onRetryClick ???????????????????????????????????????
 * @param onBackClick ????????????????????????????????????????????????
 * @param onMoreClick ????????????????????????????????????????????????
 */
@ExperimentalPagerApi
@Composable
internal fun VerticalVideoScreen(
    items: LazyPagingItems<Video>,
    sliderState: BottomSliderState,
    onVideoSurfaceClick: () -> Unit,
    onRetryClick: (Video?) -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit = {},
    pagerState: PagerState = rememberPagerState(),
) {
    val currentPage by remember {
        snapshotFlow {
            pagerState.currentPage
        }
    }.collectAsState(initial = 0)

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
                    videoState = remember(page) {
                        mutableStateOf(items[page])
                    },
                    sliderState = sliderState,
                    onVideoSurfaceClick = onVideoSurfaceClick,
                    onMoreClick = onMoreClick,
                    onBackClick = onBackClick,
                    onRetryClick = onRetryClick
                )
            } else {
                VerticalPagerChild(
                    isCurrentPage = false,
                    videoState = remember(page) {
                        mutableStateOf(items[page])
                    },
                    onMoreClick = onMoreClick,
                    onBackClick = onBackClick
                )
            }
        }
    }

}

/**
 * Pager????????????Page??????
 */
@Composable
internal fun VerticalPagerChild(
    modifier: Modifier = Modifier,
    videoState: State<Video?>,
    isCurrentPage: Boolean,
    onBackClick: () -> Unit,
    sliderState: BottomSliderState? = null,
    onRetryClick: (Video?) -> Unit = {},
    onMoreClick: () -> Unit = {},
    onVideoSurfaceClick: () -> Unit = {},
    viewModel: VideoViewModel = viewModel()
) {
    val context = LocalContext.current

    // ????????????????????????????????????State???????????????????????????????????????State
    val realSliderState = sliderState ?: remember(isCurrentPage) { BottomSliderState() }

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (content, functionBar, title, creator, description, sliderRef, screen) = createRefs()

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

            // ???????????????????????????Video????????????????????????????????????
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
                    ErrorComponents(
                        modifier = Modifier.constrainAs(errorTip) {
                            centerTo(parent)
                        },
                        onRetryClick = onRetryClick,
                        video = videoState.value
                    )
                }
                is VideoLoadState.Pause -> {
                    PlayIcon(
                        modifier = Modifier.constrainAs(playIcon) {
                            centerTo(parent)
                        }
                    )
                }
                else -> {}
            }
        } else {
            // ?????????????????????????????????????????????????????????????????????????????????
            val imageRef = createRef()
            Image(
                painter = rememberAsyncImagePainter(videoState.value?.coverUrl),
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
        // ?????????????????????????????????????????????????????????????????????
        // ??????????????????
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
            sliderState = realSliderState
        )
        // ????????????
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
        // ???????????????????????????????????????
        if (!realSliderState.dragging) {
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
            // ???????????????
            OrientationChangeIcon(
                modifier = Modifier.constrainAs(screen) {
                    end.linkTo(parent.end, 10.dp)
                    bottom.linkTo(functionBar.top, 30.dp)
                },
                onRotateScreenClick = {
                    context.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                }
            )
        } else {
            // ??????????????????????????????????????????
            val progressText = createRef()
            ProgressText(
                progress = ProgressUtil.toTimeText(realSliderState.progress.roundToLong()),
                duration = ProgressUtil.toTimeText(videoState.value?.duration ?: 0L),
                modifier = Modifier.constrainAs(progressText) {
                    bottom.linkTo(sliderRef.top, 36.dp)
                    centerHorizontallyTo(parent)
                }
            )
        }
    }
}

