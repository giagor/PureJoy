package com.topview.purejoy.common.widget.compose

import android.content.Context
import android.util.AttributeSet
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.topview.purejoy.common.R
import com.topview.purejoy.common.util.pxToDp

/**
 * 兼具圆形、圆角功能，可以旋转的ImageView。
 * 该ImageView需要引入Compose才能使用。
 * 属性：
 * android:src 指定本地图片资源的位置
 * app:percent 图片圆角百分比，百分比大于50时呈现圆形状态
 * android:contentDescription 图片描述
 * app:borderWidth 边框宽度
 * app:borderColor 边框颜色
 * app:backgroundColor 背景颜色
 */
class RoundedCornerImageView
@JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attr, defStyle) {

    /**
     * 圆角百分比
     */
    var percent by mutableStateOf(0)

    /**
     * 图片加载请求。
     * 注意，这是个Any属性，意味着你可以设置Bitmap、本地Drawable的id，甚至是一个String(URL)。
     * 如果设置了一个String，那么将会尝试使用Coil加载远程图片
     * 虽然本项目主要使用Glide作为图片加载框架，但是它和Compose结合的并不成功，效率很低，因此改用Coil
     */
    var loadImageRequest: Any? by mutableStateOf(null)

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

    /**
     * 边框宽度，单位为dp，可以是null，代表不绘制边框。
     * 注意，width为0不代表不绘制边框，而是指边框宽度为一个像素(Hairline)
     */
    var borderWidth: Float? by mutableStateOf(null)

    /**
     * 边框的颜色，默认是白色
     */
    var borderColor: Int by mutableStateOf(android.graphics.Color.WHITE)

    /**
     * 背景颜色
     */
    var background: Int? by mutableStateOf(null)

    val remoteLoader: RemoteLoader = RemoteLoader()

    private val contentDescription: String?

    private var resetRotate = false

    init {
        // 获取属性集合
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.RoundedCornerImageView)
        // 获取设置的属性值
        percent = typedArray.getInt(
            R.styleable.RoundedCornerImageView_percent, 0
        )
        // 获取边框宽度
        val borderPx =  typedArray.getDimension(
            R.styleable.RoundedCornerImageView_borderWidth, -1F)
        if (borderPx != -1F) {
            borderWidth = pxToDp(borderPx)
        }
        // 获取边框的颜色
        borderColor = typedArray.getColor(
            R.styleable.RoundedCornerImageView_borderColor,
            android.graphics.Color.WHITE
        )
        val color = typedArray.getColor(
            R.styleable.RoundedCornerImageView_backgroundColor,
            0
        )
        if (color != 0) {
            background = color
        }
        loadImageRequest = typedArray.getResourceId(
            R.styleable.RoundedCornerImageView_android_src, 0
        )
        contentDescription = typedArray.getString(
            R.styleable.RoundedCornerImageView_android_contentDescription
        )
        typedArray.recycle()
    }

    @Composable
    override fun Content() {
        val angle = remember {
            Animatable(0F)
        }
        LaunchedEffect(loop) {
            if (loop) {
                resetRotate = false
                angle.animateTo(
                    360F + (angle.value % 360F), animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = duration, easing = LinearEasing)
                    )
                )
            } else {
                // 复位或减小角度，避免角度不断累积变大
                angle.snapTo(if (resetRotate) 0F else angle.value % 360F)
            }
        }
        val modifier = Modifier
            .rotate(angle.value)
            .clickable { onClick?.invoke() }
        val imagePainter: Painter? = getPainter(loadImageRequest, remoteLoader)
        // 创建边框
        val borderStroke = borderWidth?.let {
             remember(borderColor, borderWidth) {
                BorderStroke(
                    width = it.dp,
                    color = Color(borderColor)
                )
            }
        }
        val backgroundColor = background?.let {
            Color(it)
        } ?: MaterialTheme.colors.surface
        CompositionLocalProvider(LocalIndication provides NoIndication) {
            RoundImageViewCompose(
                painter = imagePainter,
                imageModifier = modifier,
                percent = percent,
                contentDescription = contentDescription,
                border = borderStroke,
                backgroundColor = backgroundColor
            )
        }
    }
}

/**
 * 一个简单的圆形Image
 * @param modifier Surface的modifier
 * @param backgroundColor Image底部的背景颜色，在Painter为null或者不设置加载图片时显示的PlaceHolder可见
 * @param painter Image的Painter
 * @param border 边框
 * @param elevation 控件周围表示深度的阴影的厚度
 * @param percent 圆角比例，大于等于50时为圆形
 */
@Composable
fun RoundImageViewCompose(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    painter: Painter?,
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    percent: Int = 0,
    contentDescription: String? = null
) {
    Surface(
        shape = RoundedCornerShape(percent),
        modifier = modifier,
        color = backgroundColor,
        border = border,
        elevation = elevation
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }
    }

}

@Composable
internal fun getPainter(request: Any?, remoteLoader: RemoteLoader) =
    request?.run {
        when(this) {
            is ImageVector -> {
                rememberVectorPainter(image = this)
            }
            is ImageBitmap -> {
                remember(request) {
                    BitmapPainter(this)
                }
            }
            is Painter -> {
                this
            }
            else -> {
                remoteLoader.getRemotePainter(request = this)
            }
        }
    }


@Preview(showBackground = true)
@Composable
private fun RoundImageViewComposePreview() {
    Surface(modifier = Modifier) {
        // 如果希望预览效果，换一个能够看得见效果的图而不是无意义的null
        RoundImageViewCompose(
            painter = null,
            imageModifier = Modifier.size(150.dp),
            percent = 100
        )
    }
}