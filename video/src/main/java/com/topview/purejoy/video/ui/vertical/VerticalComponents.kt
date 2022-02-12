package com.topview.purejoy.video.ui.vertical

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.video.R
import com.topview.purejoy.video.ui.components.MVSign
import com.topview.purejoy.video.ui.theme.HalfAlphaWhite

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
                painter = painterResource(id = R.drawable.video_ic_arrow_back_24),
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
                painter = painterResource(id = R.drawable.video_ic_more_vert_24),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onMoreClick() }
            )
        }
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
internal fun VerticalLoadingComponents(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = HalfAlphaWhite
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