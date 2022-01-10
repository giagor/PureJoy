package com.topview.purejoy.video.ui.horizontal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.R
import com.topview.purejoy.video.ui.VideoViewModel
import com.topview.purejoy.video.ui.components.sliderHeight
import com.topview.purejoy.video.ui.state.BottomSliderState
import com.topview.purejoy.video.ui.state.HorizontalVideoScreenState
import com.topview.purejoy.video.ui.theme.Gray92
import com.topview.purejoy.video.util.ProgressUtil

@Composable
internal fun HorizontalBottomBanner(
    modifier: Modifier = Modifier,
    video: Video?,
    screenState: HorizontalVideoScreenState,
    sliderState: BottomSliderState = remember { BottomSliderState() },
    videoViewModel: VideoViewModel = viewModel(),
) {
    val sliderRange = if (video == null || video.duration == Video.UNSPECIFIED_LONG) 0F..0F else
        0F..video.duration.toFloat()
    val textColor = Color.White

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 当前进度
        Text(
            text = ProgressUtil.toTimeText(sliderState.progress),
            color = textColor
        )
        Spacer(modifier = Modifier.width(10.dp))
        Slider(
            value = sliderState.progress,
            onValueChange = {
                sliderState.dragging = true
                sliderState.progress = it
            },
            onValueChangeFinished = {
                videoViewModel.seekTo(sliderState.progress.toLong())
                sliderState.dragging = false
            },
            modifier = Modifier
                .weight(1F, false)
                .height(sliderHeight),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Gray92
            ),
            valueRange = sliderRange
        )
        Spacer(modifier = Modifier.width(10.dp))
        // 总时长
        Text(
            text = ProgressUtil.toTimeText(video?.duration ?: 0L),
            color = textColor
        )
        Spacer(modifier = Modifier.width(20.dp))
        // 倍速控件
        Text(
            text = stringResource(id = R.string.video_speed_change),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 20.dp)
                .clickable {
                    // 隐藏控制控件，显示倍速控件
                    screenState.isControlShowing = false
                    screenState.isSpeedListShowing = true
                }
        )
    }
}