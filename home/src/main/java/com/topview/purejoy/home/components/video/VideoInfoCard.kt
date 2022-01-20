package com.topview.purejoy.home.components.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import coil.compose.rememberAsyncImagePainter
import com.topview.purejoy.common.entity.Video
import com.topview.purejoy.common.util.createImageRequestForCoil
import com.topview.purejoy.common.widget.compose.RoundImageViewCompose
import com.topview.purejoy.home.R
import com.topview.purejoy.home.entity.ExternVideo
import com.topview.purejoy.home.theme.Blue219
import com.topview.purejoy.home.theme.Gray245
import com.topview.purejoy.home.util.ImageUtil
import com.topview.purejoy.video.util.ProgressUtil

@Composable
internal fun VideoInfoCard(
    modifier: Modifier = Modifier,
    externVideo: ExternVideo
) {
    Surface(
        shape = CardShape,
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
            painter = rememberAsyncImagePainter(
                model = createImageRequestForCoil(
                    data = ImageUtil.limitImageSize(externVideo.video.coverUrl, 600),
                    placeholder = remember { ImageUtil.getRandomColorDrawable() },
                    preferExactIntrinsicSize = true
                )
            ),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )
        RoundImageViewCompose(
            painter = rememberAsyncImagePainter(
                model = createImageRequestForCoil(
                    data = ImageUtil.limitImageSize(externVideo.video.creatorAvatarUrl, 90)
                )
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
        if (externVideo.video.duration != Video.DURATION_UNSPECIFIED) {
            Text(
                text = ProgressUtil.toTimeText(externVideo.video.duration),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 8.dp),
                color = Color.White.copy(alpha = 0.8F),
                maxLines = 1,
                fontSize = 10.sp
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

/**
 * 加载下一页失败后会显示的卡片
 */
@Composable
internal fun AppendErrorItemCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = CardShape
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Blue219
            )
        ) {
            Text(
                text = stringResource(id = R.string.home_video_append_reload),
                color = Color.White
            )
        }
    }
}

/**
 * 底部的显示出正在加载的进度条卡片
 */
@Composable
internal fun AppendLoadingItemCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CardShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = Blue219
            )
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

private val CardShape = RoundedCornerShape(10.dp)

