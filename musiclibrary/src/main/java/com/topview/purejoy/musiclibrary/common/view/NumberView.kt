package com.topview.purejoy.musiclibrary.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.topview.purejoy.common.util.dpToPx
import com.topview.purejoy.musiclibrary.R

class NumberView : View {

    var number: Int
    set(value) {
        field = value
        str = value.toString()
        invalidate()
    }

    private lateinit var str: String

    var textSize: Float
    set(value) {
        field = value

        paint.textSize = value
        invalidate()
    }

    var color: Int
    set(value) {
        field = value
        paint.color = value
        invalidate()
    }

    private val paint: Paint
    private val rect: Rect

    private val defWidth: Int
    private val defHeight: Int

    constructor(context: Context): this(context, null)

    constructor(context: Context, attr: AttributeSet?): this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, def: Int): super(context, attr, def) {
        paint = Paint()
        rect = Rect()
        defWidth = dpToPx(48)
        defHeight = dpToPx(48)
        val ta = context.obtainStyledAttributes(R.styleable.NumberView)
        number = ta.getInt(R.styleable.NumberView_number, 1)
        textSize = ta.getFloat(R.styleable.NumberView_android_textSize, 48f)
        color = ta.getColor(R.styleable.NumberView_android_color, Color.BLACK)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(widthMeasureSpec)
        } else {
            defWidth.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        }
        val height = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else {
            defHeight.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            paint.getTextBounds(str, 0, str.length, rect)
            val x = 0f.coerceAtLeast((width - rect.width()) / 2f)
            val y = 0f.coerceAtLeast(height / 2f + rect.height() / 2)
            drawText(str, x, y, paint)
        }
    }
}