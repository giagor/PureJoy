package com.topview.purejoy.video.ui.horizontal

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.topview.purejoy.video.R
import com.topview.purejoy.video.VideoViewModel
import com.topview.purejoy.video.entity.Video
import com.topview.purejoy.video.ui.MarqueeText
import com.topview.purejoy.video.ui.state.BottomSliderState
import com.topview.purejoy.video.ui.state.VideoLoadState
import com.topview.purejoy.video.ui.theme.Gray155
import com.topview.purejoy.video.ui.theme.Gray77
import com.topview.purejoy.video.ui.theme.Gray92
import com.topview.purejoy.video.ui.theme.HalfAlphaWhite
import kotlinx.coroutines.flow.collectLatest

/**
 * 横向视频的底部控制栏
 */
@Composable
internal fun HorizontalBottomSlider(
    modifier: Modifier = Modifier,
    video: Video?,
    isCurrentPage: Boolean,
    sliderState: BottomSliderState = remember(isCurrentPage) { BottomSliderState() },
    videoViewModel: VideoViewModel = viewModel(),
    alphaAnimateDuration: Int = 1500,
    onProgressChange: (Float) -> Unit = {},
    onProgressChangeFinished: () -> Unit = {}
) {
    val videoLoadState by videoViewModel.videoLoadState.collectAsState()
    val sliderRange = if (video == null) 0F..0F else 0F..video.duration.toFloat()
    var dragging by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            videoViewModel.progressFlow.collectLatest {
                if (videoLoadState is VideoLoadState.Playing && !dragging) {
                    sliderState.progress = it.toFloat()
                }
            }
        }
    }

    val alpha = remember {
        Animatable(1F)
    }
    val showAnimate = isCurrentPage && isLoading(videoLoadState)
    LaunchedEffect(showAnimate) {
        if (showAnimate) {
            alpha.animateTo(
                targetValue = 0.4F,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = alphaAnimateDuration,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else if(alpha.value < 1F) {
            alpha.snapTo(1F)
        }
    }

    Column(
        modifier = modifier.background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = sliderState.progress,
            onValueChange = {
                dragging = true
                onProgressChange(it)
                sliderState.progress = it
            },
            onValueChangeFinished = {
                onProgressChangeFinished()
                videoViewModel.seekTo(sliderState.progress.toLong())
                dragging = false
            },
            modifier = Modifier
                .height(20.dp)
                .wrapContentWidth()
                .alpha(alpha.value),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Gray92
            ),
            valueRange = sliderRange
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 25.dp, vertical = 7.dp
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_video_music_note_24),
                contentDescription = null,
                tint = Gray77,
                modifier = Modifier.size(20.dp)
            )
            MarqueeText(
                text = video?.songName ?: stringResource(id = R.string.video_not_song_tip),
                color = Gray155,
                fontSize = 16.sp,
                gradientEdgeColor = Color.Black,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
internal fun ProgressText(
    modifier: Modifier = Modifier,
    progress: String,
    duration: String
) {
    Text(
        text = getProgressText(progress, duration),
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

private fun getProgressText(progress: String, duration: String): AnnotatedString {
    return AnnotatedString.Builder().run {
        pushStyle(
            SpanStyle(
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        append(progress)
        pop()
        pushStyle(
            SpanStyle(
                fontSize = 24.sp,
                color = HalfAlphaWhite,
                fontWeight = FontWeight.Bold
            )
        )
        append("  /  $duration")
        toAnnotatedString()
    }
}

private fun isLoading(state: VideoLoadState) = state is VideoLoadState.Loading
