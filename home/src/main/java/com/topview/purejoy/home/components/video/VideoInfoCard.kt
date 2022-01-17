package com.topview.purejoy.home.components.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.topview.purejoy.common.widget.compose.RoundImageViewCompose
import com.topview.purejoy.home.R
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.theme.Gray245
import com.topview.purejoy.home.util.ImageUtil
import com.topview.purejoy.video.ui.components.MVSign
import com.topview.purejoy.video.util.ProgressUtil

@Composable
internal fun VideoInfoCard(
    modifier: Modifier = Modifier,
    externVideo: ExternVideo
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier,
    ) {
        Column {
            CoverImage(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .weight(1F),
                externVideo = externVideo
            )
            Text(
                text = externVideo.video.title ?: stringResource(id = R.string.home_video_title_is_null),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                modifier = Modifier.padding(8.dp)
            )
            VideoCardBottom(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 10.dp),
                externVideo = externVideo
            )
        }
    }
}

@Composable
internal fun CoverImage(
    modifier: Modifier = Modifier,
    externVideo: ExternVideo
) {
    Box(
        modifier = modifier
    ) {
        // TODO 根据网络情况，自动加载previewUrl
        Image(
            painter = rememberImagePainter(
                data = ImageUtil.limitImageSize(externVideo.video.coverUrl, 600),
                builder = {
                    placeholder( remember { ImageUtil.getRandomColorDrawable() })
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )
        RoundImageViewCompose(
            painter = rememberImagePainter(
                ImageUtil.limitImageSize(externVideo.video.creatorAvatarUrl, 90)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 10.dp),
            imageModifier = Modifier.size(30.dp),
            backgroundColor = Gray245,
            border = BorderStroke(
                width = 1.dp,
                color = Color.White
            ),
            percent = 50
        )
        Text(
            text = ProgressUtil.toTimeText(externVideo.video.duration),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 8.dp),
            color = Color.White.copy(alpha = 0.8F),
            maxLines = 1,
            fontSize = 10.sp
        )
        if (externVideo.video.isMv) {
            MVSign(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
            )
        }
    }
}

@Composable
internal fun VideoCardBottom(
    modifier: Modifier = Modifier,
    externVideo: ExternVideo
) {
    val fontSize = 11.sp
    Row(
        modifier = modifier
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.Gray
        ) {
            SmallIconWithText(
                painter = painterResource(id = R.drawable.home_ic_play_line),
                fontSize = fontSize,
                modifier = Modifier.padding(end = 10.dp),
                text = ProgressUtil.getFormatString(externVideo.playCount)
            )
            SmallIconWithText(
                painter = painterResource(id = R.drawable.home_ic_like_line),
                fontSize = fontSize,
                text = ProgressUtil.getFormatString(externVideo.video.likedCount)
            )
            Spacer(modifier = Modifier.weight(1F))
            val tag = externVideo.tag
            if (tag != null) {
                Text(
                    text = tag,
                    fontSize = fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SmallIconWithText(
    modifier: Modifier = Modifier,
    painter: Painter,
    fontSize: TextUnit = 10.sp,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(10.dp),
        )
        Text(
            text = text,
            color = Color.Gray,
            fontSize = fontSize
        )
    }
}

