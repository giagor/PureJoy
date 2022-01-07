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
import com.topview.purejoy.video.R

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
            drawableId = R.drawable.ic_video_likes,
            text = getFormatString(video?.likedCount)
        )
        VideoFunctionButton(
            drawableId = R.drawable.ic_video_comment,
            text = getFormatString(video?.commentCount)
        )
        VideoFunctionButton(
            drawableId = R.drawable.ic_video_share,
            text = getFormatString(video?.shareCount)
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

/**
 * 获取点赞/评论/分享次数的格式化文本。函数将自动把超过4位数的Int转换为万/亿来表示，只保留小数点后一位
 * 例如，36500转换为3.6万，99999999转换为9999万
 */
private fun getFormatString(count: Int?): String {
    if (count == Video.UNSPECIFIED) {
        return "--"
    }
    if (count == null || count < 0) {
        return "0"
    }
    if (count < 10000) {
        return count.toString()
    } else {
        val unit: String
        var f: Float = count / 10000F
        if (f >= 10000) {
            unit = "亿"
            f /= 10000
        } else {
            unit = "万"
        }
        val i = f.toInt()
        // 计算小数点后一位的值
        val bit = (f * 10).toInt() - i * 10
        return if (bit > 0) {
            "${f.toInt()}.${bit}$unit"
        } else {
            "${f.toInt()}$unit"
        }
    }
}