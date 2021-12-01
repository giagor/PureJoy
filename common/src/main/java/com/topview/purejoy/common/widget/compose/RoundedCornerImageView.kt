package com.topview.purejoy.common.widget.compose

import android.content.Context
import android.util.AttributeSet
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
        RoundImageViewCompose(
            painter = imagePainter,
            modifier = modifier, percent, contentDescription
        )
    }
}

@Composable
fun RoundImageViewCompose(painter: Painter?, modifier: Modifier,
                          percent: Int = 0, contentDescription: String? = null) {
    Surface(shape = RoundedCornerShape(percent)) {
        if (painter != null) {
            Image(painter = painter,
                contentDescription = contentDescription,
                modifier = modifier,
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
                BitmapPainter(this)
            }
            is Int -> {
                if (this != 0) {
                    painterResource(id = this)
                } else {
                    null
                }
            }
            is String -> {
                remoteLoader.getRemotePainter(request = this)
            }
            is Painter -> {
                this
            }
            else -> {
                null
            }
        }
    }


@Preview(showBackground = true)
@Composable
private fun RoundImageViewComposePreview() {
    Surface(modifier = Modifier) {
        // 如果希望预览效果，换一个能够看得见效果的图而不是无意义的null
        RoundImageViewCompose(null,
            modifier = Modifier.size(150.dp),
            100)
    }
}