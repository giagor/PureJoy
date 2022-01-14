package com.topview.purejoy.video.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.widget.compose.RoundImageViewCompose
import com.topview.purejoy.video.R
import com.topview.purejoy.video.ui.theme.FollowRed
import com.topview.purejoy.video.ui.theme.HalfAlphaBlack
import com.topview.purejoy.video.ui.theme.HalfAlphaWhite

/**
 * 暂停时显示的图标
 */
@Composable
internal fun PlayIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 100.dp
) {
    Icon(
        painter = painterResource(id = R.drawable.video_ic_horizontal_play),
        contentDescription = null,
        modifier = modifier.size(iconSize),
        tint = HalfAlphaWhite
    )
}

@Composable
internal fun PauseIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 100.dp
) {
    Icon(
        painter = painterResource(id = R.drawable.video_ic_pause),
        contentDescription = null,
        modifier = modifier.size(iconSize),
        tint = HalfAlphaWhite
    )
}

/**
 * MV特有的Sign
 */
@Composable
internal fun MVSign(
    modifier: Modifier = Modifier,
    color: Color = HalfAlphaWhite
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color,
        modifier = modifier,
    ) {
        Text(
            text = "MV",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 7.dp),
            color = Color.Black
        )
    }
}

/**
 * 展示视频作者
 */
@Composable
internal fun CreatorView(
    modifier: Modifier = Modifier,
    video: Video?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RoundImageViewCompose(
            painter = rememberImagePainter(video?.creatorAvatarUrl),
            imageModifier = Modifier.size(36.dp),
            percent = 50
        )
        Text(
            text = video?.creatorName ?: "",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Icon(
            painter = painterResource(id = R.drawable.video_ic_follow),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = FollowRed
        )
    }
}

/**
 * 因为某些原因导致视频加载/播放失败时显示重试链接文字
 */
@Composable
internal fun ErrorComponents(
    modifier: Modifier = Modifier,
    onRetryClick: (Video?) -> Unit,
    video: Video?
) {
    val message = stringResource(id = R.string.video_video_reload_tip)
    val underlineStartIndex = 8
    val retryTag = "retry"
    val annotatedString = AnnotatedString.Builder().run {
        append(message)
        addStyle(
            style = SpanStyle(
                color = HalfAlphaWhite
            ),
            start = 0,
            end = underlineStartIndex
        )
        addStyle(
            style = SpanStyle(
                color = Color.White,
                textDecoration = TextDecoration.Underline,
            ),
            start = underlineStartIndex,
            end = message.length
        )
        addStringAnnotation(
            tag = retryTag,
            annotation = "",
            start = underlineStartIndex,
            end = message.length
        )
        toAnnotatedString()
    }
    ClickableText(
        text = annotatedString,
        onClick = {
            if (it >= underlineStartIndex) {
                onRetryClick(video)
            }
        },
        modifier = modifier
    )
}

/**
 * 屏幕转向按钮
 */
@Composable
internal fun OrientationChangeIcon(
    modifier: Modifier = Modifier,
    onRotateScreenClick: () -> Unit = {}
) {
    Surface(
        color = HalfAlphaBlack,
        shape = CircleShape,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.video_ic_rotate_screen),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .size(16.dp)
                .clickable {
                   onRotateScreenClick()
                },
            tint = Color.White
        )
    }
}

/**
 * 进度条高度
 */
internal val sliderHeight = 20.dp