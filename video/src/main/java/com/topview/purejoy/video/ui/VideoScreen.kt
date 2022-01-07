package com.topview.purejoy.video.ui

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.paging.compose.LazyPagingItems
import com.google.android.exoplayer2.ExoPlayer
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.ui.vertical.VerticalVideoScreen
import kotlinx.coroutines.flow.collect

@Composable
internal fun VideoScreen(
    items: LazyPagingItems<Video>,
    onPageChange: (Video?) -> Unit,
    onVideoSurfaceClick: () -> Unit,
    onRetryClick: (Video?) -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit = {}
) {
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }.collect {
            orientation = it
        }
    }
    when(orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            VerticalVideoScreen(
                items = items,
                onPageChange = onPageChange,
                onVideoSurfaceClick = onVideoSurfaceClick,
                onRetryClick = onRetryClick,
                onBackClick = onBackClick,
                onMoreClick = onMoreClick
            )
        }
        Configuration.ORIENTATION_LANDSCAPE -> {
        }
    }

}


internal val LocalExoPlayer = compositionLocalOf<ExoPlayer> {
    error("CompositionLocal LocalExoPlayer not present")
}