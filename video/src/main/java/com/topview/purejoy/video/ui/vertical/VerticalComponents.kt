package com.topview.purejoy.video.ui.vertical

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.widget.compose.RoundImageViewCompose
import com.topview.purejoy.video.R
import com.topview.purejoy.video.ui.theme.FollowRed
import com.topview.purejoy.video.ui.theme.HalfAlphaWhite

/**
 * MV特有的Sign
 */
@Composable
internal fun MVSign(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = HalfAlphaWhite,
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
 * 顶部标题
 */
@Composable
internal fun VerticalPagerTitle(
    modifier: Modifier = Modifier,
    isMv: Boolean = false,
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Icon(
                painter = painterResource(id = R.drawable.ic_video_arrow_back_24),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.weight(1F))
            if (isMv) {
                MVSign()
            }
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = R.drawable.ic_video_more_vert_24),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onMoreClick() }
            )
        }
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
            modifier = Modifier.size(36.dp),
            percent = 50
        )
        Text(
            text = video?.creatorName ?: "",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_video_follow),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = FollowRed
        )
    }
}

@Composable
internal fun DescriptionView(
    modifier: Modifier = Modifier,
    video: Video?
) {
    var maxLine by remember {
        mutableStateOf(2)
    }
    var expanded by remember {
        mutableStateOf(false)
    }

    Text(
        text = getDescriptionText(video = video, expanded = expanded),
        maxLines = maxLine,
        overflow = TextOverflow.Ellipsis,
        color = Color.White,
        letterSpacing = 0.1.sp,
        modifier = modifier
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = {
                    expanded = !expanded
                    maxLine = if (expanded) Int.MAX_VALUE else 2
                }
            )
            .animateContentSize()
    )
}

@Composable
internal fun VerticalErrorComponents(
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

@Composable
internal fun VerticalLoadingComponents(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = HalfAlphaWhite
    )
}

/**
 * 暂停时显示的图标
 */
@Composable
internal fun VerticalPlayIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_video_horizontal_play),
        contentDescription = null,
        modifier = modifier.size(100.dp),
        tint = HalfAlphaWhite
    )
}

@Composable
private fun getDescriptionText(video: Video?, expanded: Boolean): AnnotatedString {
    val downSign = "\u25BC"
    val upSign = "\u25B2"
    if (video?.title == null) {
        return AnnotatedString(stringResource(id = R.string.video_not_description))
    }

    return AnnotatedString.Builder().run {
        pushStyle(
            SpanStyle(
                fontSize = 16.sp,
                baselineShift = BaselineShift.Superscript
            )
        )
        append("${video.title} ${if (expanded) upSign else downSign}\n")
        pop()

        pushStyle(
            SpanStyle(
                fontSize = 13.sp,
                baselineShift = BaselineShift(0.4F)
            )
        )
        append(video.description ?: "")
        toAnnotatedString()
    }
}