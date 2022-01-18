package com.topview.purejoy.video.ui.horizontal

import android.net.TrafficStats
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.topview.purejoy.common.app.CommonApplication
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.R
import com.topview.purejoy.video.ui.components.ErrorComponents
import com.topview.purejoy.video.ui.components.PauseIcon
import com.topview.purejoy.video.ui.components.PlayIcon
import com.topview.purejoy.video.ui.state.HorizontalVideoScreenState
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.ui.theme.HalfAlphaBlack
import com.topview.purejoy.video.util.ProgressUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * 横屏状态下显示当前Video状态的控件
 */
@Composable
internal fun HorizontalVideoStateComponents(
    modifier: Modifier = Modifier,
    video: Video?,
    loadState: VideoLoadState,
    onRetryClick: (Video?) -> Unit = {},
    onPlayIconClick: ()-> Unit = {},
    onPauseIconClick: ()-> Unit = {}
) {
    when(loadState) {
        is VideoLoadState.Error -> {
            ErrorComponents(
                modifier = modifier,
                onRetryClick = onRetryClick,
                video = video
            )
        }
        is VideoLoadState.Pause-> {
            PlayIcon(
                modifier = modifier.clickable(
                    interactionSource = remember{ MutableInteractionSource() },
                    indication = null,
                    onClick = onPlayIconClick

                ),
                iconSize = 80.dp
            )
        }
        is VideoLoadState.Playing-> {
            PauseIcon(
                modifier = modifier.clickable(
                    interactionSource = remember{ MutableInteractionSource() },
                    indication = null,
                    onClick = onPauseIconClick
                ),
                iconSize = 65.dp
            )
        }
        else -> {}
    }
}

/**
 * 横屏状态下Loading时显示网速的控件
 */
@Composable
internal fun HorizontalLoadingComponents(
    modifier: Modifier = Modifier
) {
    var speedByBytes by remember {
        mutableStateOf(0L)
    }
    LaunchedEffect(true) {
        var rBytes = TrafficStats.getUidRxBytes(CommonApplication.getContext().applicationInfo.uid)
        flow {
            while (true) {
                delay(750L)
                val nBytes = TrafficStats.getUidRxBytes(
                    CommonApplication.getContext().applicationInfo.uid
                )
                emit(nBytes - rBytes)
                rBytes = nBytes
            }
        }.collect {
            speedByBytes = it
        }
    }
    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "正在缓冲...${ProgressUtil.toTrafficBytes(speedByBytes)}/s",
            color = Color.White
        )
    }
}

/**
 * 横屏状态下的锁屏按钮
 */
@Composable
internal fun HorizontalLockIcon(
    modifier: Modifier = Modifier,
    state: HorizontalVideoScreenState,
) {
    val painter = if (state.isLocked) {
        painterResource(id = R.drawable.video_ic_locked)
    } else {
        painterResource(id = R.drawable.video_ic_unlock)
    }
    Surface(
        modifier = modifier.padding(1.dp),
        color = HalfAlphaBlack,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 840, heightDp = 392)
@Composable
private fun HorizontalComponentsPreview() {
    Surface(color = Color.Blue) {
        ConstraintLayout {
            val (le, ri) = createRefs()
            val state = remember {
                HorizontalVideoScreenState()
            }
            HorizontalLockIcon(
                state = state,
                modifier = Modifier
                    .constrainAs(le) {
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start)
                    }
                    .clickable {
                        state.isLocked = !state.isLocked
                    }
            )
            HorizontalLockIcon(
                state = state,
                modifier = Modifier
                    .constrainAs(ri) {
                        centerVerticallyTo(parent)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        state.isLocked = !state.isLocked
                    }
            )
        }
    }
}