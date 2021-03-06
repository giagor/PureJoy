package com.topview.purejoy.home.components.toplist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.topview.purejoy.common.util.createImageRequestForCoil
import com.topview.purejoy.home.entity.TopList
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
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
 * ?????????????????????????????????
 * ???????????????????????????????????????????????????????????????EmptyGridChild
 */
@Composable
internal fun TopListGridChild(
    modifier: Modifier = Modifier,
    topList: TopList,
) {
    ConstraintLayout(modifier = modifier) {
        val (image, icon, update, name) = createRefs()
        // Image??????????????????parent????????????????????????Image??????????????????????????????Text??????????????????
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
                painter = rememberAsyncImagePainter(
                    model = createImageRequestForCoil(
                        data = ImageUtil.limitImageSize(topList.coverUrl, 450)
                    )
                ),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
        }
        ClickablePlayIcon(
            modifier = Modifier
                .size(20.dp)
                .constrainAs(icon) {
                    end.linkTo(parent.end, 5.dp)
                    bottom.linkTo(image.bottom, 7.dp)
                },
            topList = topList
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
        // ????????????Text??????????????????????????????????????????
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
 * ???????????????????????????????????????Grid??????????????????????????????????????????????????????????????????????????????
 */
@Composable
internal fun EmptyGridChild() {
    Spacer(modifier = Modifier.size(imageSize))
}

/**
 * ????????????
 */
private val imageSize = 105.dp