package com.topview.purejoy.video.ui.vertical

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.entity.Video.CREATOR.UNSPECIFIED
import com.topview.purejoy.video.R
import com.topview.purejoy.video.util.ProgressUtil.getFormatString

/**
 * 功能按钮列表
 */
@Composable
internal fun VerticalFunctionBar(
    modifier: Modifier = Modifier,
    video: Video?
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        VideoFunctionButton(
            drawableId = R.drawable.video_ic_likes,
            text = if (video != null && video.likedCount != UNSPECIFIED)
                getFormatString(video.likedCount) else DefaultUnspecifiedString
        )
        VideoFunctionButton(
            drawableId = R.drawable.video_ic_comment,
            text = if (video != null && video.likedCount != UNSPECIFIED)
                getFormatString(video.commentCount) else DefaultUnspecifiedString
        )
        VideoFunctionButton(
            drawableId = R.drawable.video_ic_video_share,
            text = if (video != null && video.likedCount != UNSPECIFIED)
                getFormatString(video.shareCount) else DefaultUnspecifiedString
        )
    }
}

/**
 * 单个功能按钮
 */
@Composable
internal fun VideoFunctionButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableId: Int,
    text: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = drawableId),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .padding(bottom = 5.dp)
        )
        Text(
            text = text,
            style = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 15.sp,
            )
        )
    }
}

private const val DefaultUnspecifiedString = "--"