package com.topview.purejoy.home.components.toplist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import com.topview.purejoy.home.R
import com.topview.purejoy.home.entity.TopList
import com.topview.purejoy.home.theme.Gray235
import com.topview.purejoy.home.theme.Gray245
import com.topview.purejoy.home.util.ImageUtil

@Composable
internal inline fun TopListGrid(
    modifier: Modifier = Modifier,
    topLists: List<TopList>,
    crossinline onChildClick: (TopList) -> Unit = {},
) {
    var surplus = topLists.size
    val column = 3
    while (surplus > 0) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth().wrapContentHeight()
        ) {
            for (i in 0 until column) {
                val topList = topLists.getOrNull(topLists.size - surplus + i)
                if (topList != null) {
                    TopListGridChild(
                        topList = topList,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onChildClick.invoke(topList) },
                                indication = null
                            )
                    )
                } else {
                    EmptyGridChild()
                }
            }
        }
        surplus -= column
    }
}

/**
 * 展示单个榜单信息的控件
 * 如果更新这个控件导致总宽度变化，请同步更新EmptyGridChild
 */
@Composable
internal fun TopListGridChild(
    topList: TopList,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (image, icon, update, name) = createRefs()
        // Image不应当直接在parent居中，否则会导致Image上方出现一个和名称的Text一样大的空白
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.constrainAs(image) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top)
                bottom.linkTo(name.top)
            },
            color = Gray245
        ) {
            Image(
                painter = rememberImagePainter(ImageUtil.limitImageSize(
                    topList.coverUrl, 450)
                ),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_home_toplist_music_play),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
                .constrainAs(icon) {
                    end.linkTo(parent.end, 5.dp)
                    bottom.linkTo(image.bottom, 7.dp)
                }
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5F),
            modifier = Modifier.constrainAs(update) {
                top.linkTo(parent.top, 3.dp)
                end.linkTo(parent.end, 3.dp)
            }
        ) {
            Text(
                text = topList.updateFrequency,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9F),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
        // 必须限制Text的宽度，否则整个布局会被破坏
        Text(
            text = topList.name,
            modifier = Modifier
                .width(100.dp)
                .constrainAs(name) {
                    start.linkTo(parent.start)
                    top.linkTo(image.bottom, 2.dp)
                    bottom.linkTo(parent.bottom, 2.dp)
                }
        )
    }
}

/**
 * 空白占位控件，主要是为了让Grid达到完美平分屏幕宽度以及每个子项完美对齐的要求而引入
 */
@Composable
internal fun EmptyGridChild() {
    Spacer(modifier = Modifier.size(imageSize))
}

/**
 * 封面大小
 */
private val imageSize = 105.dp