package com.topview.purejoy.video.ui.horizontal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.R
import com.topview.purejoy.video.ui.components.CreatorView
import com.topview.purejoy.video.ui.components.MVSign

/**
 * 横屏视频的顶部横幅，包括Title、作者、点赞等按钮
 */
@Composable
internal fun HorizontalTopBanner(
    modifier: Modifier = Modifier,
    video: Video?,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalVideoTitle(
            video = video,
            onBackClick = onBackClick,
            modifier = Modifier.padding(start = 15.dp).weight(1F)
        )
        Spacer(modifier = Modifier.weight(0.7F))
        CreatorView(
            video = video,
            modifier = Modifier.padding(end = 15.dp)
        )
        HorizontalFunctionBar(
            modifier = Modifier.padding(end = 15.dp)
        )
    }
}

/**
 * 横屏时右上角的按钮
 */
@Composable
internal fun HorizontalFunctionBar(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    iconSize: Dp = 24.dp
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.video_ic_likes),
                modifier = Modifier.size(iconSize),
                contentDescription = null
            )
            Icon(
                painter = painterResource(id = R.drawable.video_ic_video_share),
                modifier = Modifier.size(iconSize),
                contentDescription = null
            )
            Icon(
                painter = painterResource(id = R.drawable.video_ic_collect),
                modifier = Modifier.size(iconSize),
                contentDescription = null
            )
        }
    }
}

/**
 * 横屏状态下左上角的标题和返回按钮
 */
@Composable
internal fun HorizontalVideoTitle(
    modifier: Modifier = Modifier,
    video: Video?,
    onBackClick: () -> Unit = {},
    iconSize: Dp = 28.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.video_ic_arrow_back_24),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .size(iconSize)
                .clickable { onBackClick() },
            tint = Color.White
        )
        if (video != null) {
            if (video.isMv) {
                MVSign(
                    color = Color.White
                )
            }
            Text(
                text = video.title ?: "",
                color = Color.White,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}