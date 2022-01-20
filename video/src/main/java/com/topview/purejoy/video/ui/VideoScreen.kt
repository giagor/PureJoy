package com.topview.purejoy.video.ui

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.ui.horizontal.HorizontalVideoScreen
import com.topview.purejoy.video.ui.state.BottomSliderState
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.ui.vertical.VerticalVideoScreen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun VideoScreen(
    items: LazyPagingItems<Video>,
    onPageChange: (Video?) -> Unit,
    onVideoSurfaceClick: () -> Unit,
    onRetryClick: (Video?) -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit = {},
    viewModel: VideoViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val pagerState = rememberPagerState()
    // 底部进度条的状态
    val sliderState = remember { BottomSliderState() }
    val loadState by viewModel.videoLoadState.collectAsState()

    val currentPage by remember {
        snapshotFlow {
            pagerState.currentPage
        }
    }.collectAsState(initial = 0)

    val view = LocalView.current

    // 初始状态下为exoPlayer提供数据的Effect
    LaunchedEffect(items.itemCount > 0) {
        if (items.itemCount > 0) {
            onPageChange(items[0])
        }
    }
    // 上拉/下滑到新的一页的处理逻辑
    LaunchedEffect(currentPage) {
        onPageChange(if (items.itemCount <= currentPage) null else items[currentPage])
    }

    // 监听Video的加载状态
    LaunchedEffect(loadState) {
        loadState.apply {
            when (this) {
                is VideoLoadState.Error -> {
                    // 当StatusCode或者是ContentType出现异常，抛弃掉原先的播放地址，这允许重新加载播放地址
                    if (errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ||
                        errorCode == PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE) {
                        items[currentPage]?.videoUrl = null
                    }
                }
                is VideoLoadState.Playing -> {
                    view.keepScreenOn = true
                }
                is VideoLoadState.Pause -> {
                    view.keepScreenOn = false
                }
                else -> {}
            }
        }
    }
    // 更新SliderState的状态
    LaunchedEffect(true) {
        viewModel.progressFlow.collectLatest {
            if (loadState is VideoLoadState.Playing && !sliderState.dragging) {
                sliderState.progress = it.toFloat()
            }
        }
    }

    when(configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            VerticalVideoScreen(
                items = items,
                sliderState = sliderState,
                onVideoSurfaceClick = onVideoSurfaceClick,
                onRetryClick = onRetryClick,
                onBackClick = onBackClick,
                onMoreClick = onMoreClick,
                pagerState = pagerState
            )
        }
        Configuration.ORIENTATION_LANDSCAPE -> {
            HorizontalVideoScreen(
                video = items[currentPage],
                onBackClick = onBackClick,
                onRetryClick = onRetryClick,
                sliderState = sliderState
            )
        }
        else -> {}
    }

}


internal val LocalExoPlayer = compositionLocalOf<ExoPlayer> {
    error("CompositionLocal LocalExoPlayer not present")
}