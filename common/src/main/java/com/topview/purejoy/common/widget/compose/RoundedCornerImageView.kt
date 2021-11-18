package com.topview.purejoy.common.widget.compose

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.topview.purejoy.common.R

/**
 * 兼具圆形、圆角功能，可以旋转的ImageView。
 * 该ImageView需要引入Compose才能使用。
 * 属性：
 * android:src 指定本地图片资源的位置
 * percent 图片圆角百分比，百分比大于50时呈现圆形状态
 */
class RoundedCornerImageView
@JvmOverloads constructor(context: Context, attr: AttributeSet? = null,
                          defStyle: Int = 0) :
    AbstractComposeView(context, attr, defStyle) {

    /**
     * 需要展示的Bitmap
     */
    var bitmap by mutableStateOf<Bitmap?>(null)

    /**
     * 圆角百分比
     */
    var percent by mutableStateOf(0)

    /**
     * 需要展示的drawable的id，注意，drawable的优先级低于Bitmap，优先展示Bitmap
     * 这个drawable的初始资源值是无意义的值0，需要从展示Bitmap转为展示drawable时，尽量先设置有意义的drawable值
     */
    var drawableId by mutableStateOf(0)

    /**
     * 是否开始旋转。修改这个变量将启动旋转动画
     */
    var loop by mutableStateOf(false)

    /**
     * View进行一圈旋转的时间，单位为ms
     */
    var duration by mutableStateOf(25000)

    /**
     * 点击事件
     */
    var onClick: (() -> Unit)? = null

    private val contentDescription: String?

    init {
        // 获取属性集合
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.RoundedCornerImageView)
        // 获取设置的属性值
        percent = typedArray.getInt(
            R.styleable.RoundedCornerImageView_percent, 0)
        drawableId = typedArray.getResourceId(
            R.styleable.RoundedCornerImageView_android_src, 0)
        contentDescription = typedArray.getString(
            R.styleable.RoundedCornerImageView_android_contentDescription)
        typedArray.recycle()
    }

    @Composable
    override fun Content() {
        val angle = remember {
            Animatable(0F)
        }
        LaunchedEffect(loop) {
            if (loop) {
                angle.animateTo(360F, animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, easing = LinearEasing)
                ))
            } else if (angle.value != 0F) {
                angle.snapTo(0F)
            }
        }
        val modifier = Modifier.rotate(angle.value).clickable { onClick?.invoke() }
        if (bitmap != null) {
            RoundImageViewCompose(bitmap = bitmap, modifier = modifier, percent)
        } else {
            RoundImageViewCompose(drawableId, modifier, percent)
        }
    }
}

@Composable
fun RoundImageViewCompose(bitmap: Bitmap?, modifier: Modifier,
                          percent: Int = 0, contentDescription: String? = null) {
    Surface(shape = RoundedCornerShape(percent)) {
        if (bitmap != null) {
            Image(painter = remember {
                mutableStateOf(BitmapPainter(bitmap.asImageBitmap()))
            }.value,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }
    }

}

@Composable
fun RoundImageViewCompose(@DrawableRes id: Int, modifier: Modifier,
                          percent: Int = 0, contentDescription: String? = null) {
    Surface(shape = RoundedCornerShape(percent)) {
        Image(painter = painterResource(id = id),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop)
    }
}

@Preview(showBackground = true)
@Composable
private fun RoundImageViewComposePreview() {
    Surface(modifier = Modifier) {
        // 如果希望预览效果，换一个能够看得见效果的图而不是无意义的0值
        RoundImageViewCompose(
            id = 0,
            modifier = Modifier.size(150.dp),
            100)
    }
}