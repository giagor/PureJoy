package com.topview.purejoy.home.components.toplist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.topview.purejoy.home.R
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.util.ImageUtil

/**
 * 官方榜内的卡片
 */
@Composable
internal fun OfficialTopListCard(
    modifier: Modifier = Modifier,
    topList: TopList,
    tracks: List<TopList.Track>,
    urlList: List<String>?,
    backgroundColor: Color = Color.White,
    titleColor: Color = Color.Black,
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.padding(10.dp)) {
            // 创建每个控件的锚点
            val (bigTitle, image, music, frequency) = createRefs()
            TopListCoverByAlbum(
                headerUrl = urlList?.get(0),
                bodyUrl = urlList?.get(1),
                tailUrl = urlList?.get(2),
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 5.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                headerContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home_toplist_music_play),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center).size(18.dp)
                    )
                }
            )
            Text(
                text = topList.name,
                color = titleColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 5.dp, top = 5.dp)
                    .constrainAs(bigTitle) {
                        start.linkTo(parent.start)
                        bottom.linkTo(image.top, margin = 5.dp)
                    }
            )
            Text(
                text = topList.updateFrequency,
                fontSize = 10.sp,
                color = Color.Black.copy(alpha = 0.2F),
                modifier = Modifier.constrainAs(frequency) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
            )
            TracksColumn(
                tracks = tracks,
                modifier = Modifier.constrainAs(music) {
                    start.linkTo(image.end, margin = 15.dp)
                    centerVerticallyTo(parent)
                }
            )
        }
    }
}

@Composable
internal fun TracksColumn(
    tracks: List<TopList.Track>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tracks.forEachIndexed{ index, track ->
            Text(
                text = getAnnotatedString(index + 1, track),
                maxLines = 1,
                fontSize = 13.sp,
                modifier = Modifier.width(170.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 重叠式专辑图片展示控件
 * @param headerUrl 显示在顶层的图片的地址
 * @param bodyUrl 显示在中间的图片的地址
 * @param tailUrl 显示在最后的图片的地址
 * @param modifier 整个Box的修饰
 * @param headerContent header内的Compose内容，考虑到需要放置的Icon控件存在状态转换，故独立出来成为一个参数
 */
@Composable
internal fun TopListCoverByAlbum(
    headerUrl: String?,
    bodyUrl: String?,
    tailUrl: String?,
    modifier: Modifier = Modifier,
    headerContent: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart
    ) {
        PlaceholderImage(
            url = tailUrl,
            imageAlpha = 0.2F,
            modifier = Modifier
                .padding(start = 40.dp)
                .size(40.dp),
            color = Color.Transparent
        )
        PlaceholderImage(
            url = bodyUrl,
            imageAlpha = 0.5F,
            modifier = Modifier
                .padding(start = 20.dp)
                .size(50.dp)
                .zIndex(1F),
            color = Color.Transparent
        )
        PlaceholderImage(
            url = headerUrl,
            modifier = Modifier
                .size(60.dp)
                .zIndex(2F)
        ) {
            headerContent?.invoke(this)
        }
    }
}

@Composable
fun PlaceholderImage(
    modifier: Modifier = Modifier,
    url: String?,
    imageAlpha: Float = DefaultAlpha,
    shape: Shape = RoundedCornerShape(8.dp),
    color: Color = Color.White,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    Surface(
        shape = shape,
        color = color,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = rememberImagePainter(ImageUtil.limitImageSize(url, 180)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                alpha = imageAlpha
            )
            content?.invoke(this)
        }
    }
}

private fun getAnnotatedString(index: Int, track: TopList.Track) =
    AnnotatedString.Builder(capacity = track.first.length + track.second.length + 3).run {
        val text = "$index.${track.first} - ${track.second}"
        append(text)
        addStyle(
            SpanStyle(
                color = Color.Gray,
                fontWeight = FontWeight.Light
            ),
            track.first.length + index.toString().length + 1,
            text.length
        )
        toAnnotatedString()
    }
