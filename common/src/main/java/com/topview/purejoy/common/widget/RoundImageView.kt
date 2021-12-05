package com.topview.purejoy.common.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import com.topview.purejoy.common.R
import kotlin.math.abs

/**
 * create by usagisang
 * 兼具圆形、圆角功能，可以旋转的ImageView
 * 属性：
 * draw_type 指示绘制的样式类型，circle是圆形，round_rect是圆角矩形
 * round_rect_radius 仅对圆角矩形生效，指示圆角矩形的圆角的弧度
 * thumb 指示是否对图片进行压缩（压缩基于ImageView的宽高）
 */
class RoundImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
    AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * 是否对图片进行缩放
     */
    private val thumb: Boolean

    /**
     *  绘制图片的画笔
     */
    private val bitmapPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     *  旋转动画
     */
    private var rotateAnimator: RotateAnimator? = null

    /**
     * 绘制模式，默认为圆形绘制模式
     */
    private var drawType: Int = CIRCLE

    /**
     * 绘制圆角矩形时边角的弧度
     */
    private var roundRectRadius: Float = 50F


    init {
        // 获取属性集合
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        // 获取设置的属性值
        drawType = typedArray.getInt(R.styleable.RoundImageView_draw_type, CIRCLE)
        roundRectRadius = typedArray.getFloat(
            R.styleable.RoundImageView_round_rect_radius, 50F)
        thumb = typedArray.getBoolean(R.styleable.RoundImageView_thumb, false)
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas?) {
        if (drawable is BitmapDrawable) {
            // 按照自定义方式绘制Bitmap
            drawBitmap((drawable as BitmapDrawable).bitmap, canvas)
        } else {
            drawable?.let {
                // 如果不为空，转换为BitmapDrawable
                setImageDrawable(it.toBitmapDrawable())
            }  ?: let {
                // 按照默认的形式绘制
                super.onDraw(canvas)
            }
        }
    }

    /**
     *  辅助方法，尝试在View上绘制圆形或正方形的Bitmap
     */
    private fun drawBitmap(bitmap: Bitmap, canvas: Canvas?) {
        var newBitmap = bitmap
        // 如果需要压缩，尝试进行压缩
        if (thumb) {
            newBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height)
        }
        bitmapPaint.shader = BitmapShader(newBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas?.let {
            val halfWidth = (width / 2).toFloat()
            val halfHeight = (height / 2).toFloat()
            if (drawType == CIRCLE) {
                it.drawCircle(
                    halfWidth,
                    halfHeight,
                    halfWidth.coerceAtMost(halfHeight),
                    bitmapPaint
                )
            } else {
                it.drawRoundRect(0F, 0F, (width - paddingRight - paddingLeft).toFloat(),
                    (height - paddingTop - paddingBottom).toFloat(),
                    roundRectRadius, roundRectRadius, bitmapPaint)
            }
        }
    }

    /**
     * 尝试将Drawable转换为BitmapDrawable。相比起将Drawable转换为Bitmap然后绘制，
     * 转换为BitmapDrawable起到了缓存的作用，避免多次调用onDraw后创建了多余的Bitmap。
     */
    private fun Drawable.toBitmapDrawable(): BitmapDrawable {
        val bitmapWidth = if (this.intrinsicWidth > 0) this.intrinsicWidth else width
        val bitmapHeight = if (this.intrinsicHeight > 0) this.intrinsicHeight else height
        return BitmapDrawable(resources, this.toBitmap(bitmapWidth, bitmapHeight))
    }

    /**
     *  开启或继续进行这个View的旋转动画
     * @param duration 旋转一周的时间，默认25s
     */
    fun startAnimator(duration: Long = 25000L) {
        if (rotateAnimator == null) {
            rotateAnimator = RotateAnimator(this, duration)
        }
        rotateAnimator?.start()
    }

    /**
     *  暂停旋转动画
     */
    fun pauseAnimator() {
        rotateAnimator?.pause()
    }

    /**
     *  停止旋转动画
     */
    fun cancelAnimator() {
        rotateAnimator?.cancel()
        rotateAnimator = null
    }

    private class RotateAnimator(val view: View, duration: Long): ValueAnimator(),
        ValueAnimator.AnimatorUpdateListener {

        var lastRotate = 0F

        init {
            setFloatValues(0F, 360F)
            addUpdateListener(this)
            this.duration = duration
            // 使用线性插值器
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        override fun pause() {
            super.pause()
            // 重置旋转信息
            lastRotate = 0F
        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            val nowRotate = animatedValue as Float
            var viewRotate = view.rotation
            viewRotate += abs(nowRotate - lastRotate)
            if (viewRotate > 360F) {
                viewRotate -= 360F
            }
            lastRotate = nowRotate
            view.rotation = viewRotate
        }
    }

    companion object {
        const val CIRCLE = 1
        const val ROUND_RECT = 2
    }
}