package com.topview.purejoy.musiclibrary.playing.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.topview.purejoy.musiclibrary.R
import kotlin.math.roundToInt

class MusicProgressBar : View {

    var color: Int
        set(value) {
            field = value
            listener?.onProgressChange(value)
            invalidate()
        }
    var value: Int
        set(value) {
            field = value
            invalidate()
        }
    var max: Int
        set(value) {
            field = value
            invalidate()
        }
    var thumbColor: Int
    set(value) {
        field = value
        invalidate()
    }
    var secondColor: Int
    set(value) {
        field = value
        invalidate()
    }
    private var progressWidth: Float
    private val paint: Paint
    private val radius: Float
    private val touchRadius: Float
    private var touched: Boolean = false
    var listener: MusicProgressBarListener? = null
    private val shadowColor: Int

    constructor(context: Context): this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleInt: Int)
            : super(context, attributeSet, defStyleInt) {
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.MusicProgressBar)
        color = array.getColor(R.styleable.MusicProgressBar_progress_color, Color.WHITE)
        shadowColor = array.getColor(R.styleable.MusicProgressBar_shadow_color, Color.DKGRAY)
        value = array.getInt(R.styleable.MusicProgressBar_progress_value, 0)
        max = array.getInt(R.styleable.MusicProgressBar_progress_max, 100)
        progressWidth = array.getFloat(R.styleable.MusicProgressBar_progress_width, 4f)
        secondColor = array.getColor(R.styleable.MusicProgressBar_progress_second_color, Color.GRAY)
        thumbColor = array.getColor(R.styleable.MusicProgressBar_thumb_color, Color.WHITE)
        paint = Paint()
        paint.strokeWidth = progressWidth
        radius = (progressWidth / 2 + 4.0f)
        touchRadius = 2 * radius
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        array.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val mh = height / 2.0f
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                return if (y >= mh - radius && y <= mh + radius) {
                    touched = true
                    value = ((x * 1.0 / width) * max).roundToInt()
                    listener?.onStartTracking(this)
                    true
                } else {
                    false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                value = ((x * 1.0 / width) * max).roundToInt()
                return true
            }
            MotionEvent.ACTION_UP -> {
                touched = false
                listener?.onStopTracking(this)
                invalidate()
                return true
            }
            else -> {
                return super.onTouchEvent(event)
            }
        }
    }




    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val hm = MeasureSpec.getMode(heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)

        if (hm == MeasureSpec.AT_MOST) {
            h = Math.min((progressWidth + 2 * touchRadius).roundToInt(), h)
        }
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        val x = (1.0f * value / max) * width
        val y = height / 2.0f
        paint.color = color
        canvas?.drawLine(0f, y, x, y, paint)
        paint.color = shadowColor
        if (!touched) {
            canvas?.drawCircle(x + radius, y, radius, paint)
        } else {
            canvas?.drawCircle(x + touchRadius, y, touchRadius, paint)
        }
        paint.color = thumbColor
        paint.style = Paint.Style.FILL
        if (!touched) {
            canvas?.drawCircle(x + radius, y, radius, paint)
        } else {
            canvas?.drawCircle(x + touchRadius, y, touchRadius, paint)
        }
        paint.style = Paint.Style.STROKE
        paint.color = secondColor
        if (!touched) {
            canvas?.drawLine(x + radius * 2, y, width.toFloat(), y, paint)
        } else {
            canvas?.drawLine(x + touchRadius * 2 , y, width.toFloat(), y, paint)
        }

    }

    interface MusicProgressBarListener {
        fun onProgressChange(value: Int)
        fun onStartTracking(musicProgressBar: MusicProgressBar)
        fun onStopTracking(musicProgressBar: MusicProgressBar)
    }
}