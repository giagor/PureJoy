package com.topview.purejoy.musiclibrary.playing.view.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import com.topview.purejoy.musiclibrary.R
import com.topview.purejoy.musiclibrary.playing.entity.LrcItem
import java.util.*
import kotlin.math.*

class LrcView : View {
    var rowInterval: Float
        set(value) {
            field = value
            invalidateIfInit()
        }
    var commonColor: Int
        set(value) {
            field = value
            invalidateIfInit()
        }
    private val source: MutableList<LrcItem> by lazy {
        mutableListOf()
    }
    private val sortSet: TreeSet<LrcItem> by lazy {
        TreeSet()
    }
    private var isInited: Boolean = false
    var highlightColor: Int
        set(value) {
            field = value
            invalidateIfInit()
        }
    private var lrcIndex: Int = 0
    private val measureRect: Rect by lazy {
        Rect()
    }
    var thumbColor: Int
        set(value) {
            field = value
            invalidateIfInit()
        }
    private val timeStrSize: Float
    private val timeStrColor: Int
    private val showAfterRelease: Int
    private val linePadding: Float = 10f
    private val emptyText: String
    private var state: State = State.NORMAL
    private val thumbPadding: Float = 20f
    private val lrcPaint: Paint = Paint()
    private val path: Path by lazy {
        Path()
    }
    private val TAG = "LrcView"
    private val scroller: Scroller by lazy {
        Scroller(this.context)
    }



    var listener: ThumbClickListener? = null

    private val thumbPaint: Paint by lazy {
        Paint()
    }

    private var outerIndex: Int = 0

    var strSize: Float = 200f
        set(value) {
            field = value
            lrcPaint.textSize = value
            val fm = lrcPaint.fontMetrics
            textHeight = fm.bottom - fm.top
            invalidateIfInit()
        }

    private val tPoint: PointF by lazy {
        PointF()
    }

    private val bPoint: PointF by lazy {
        PointF()
    }

    private val rPoint: PointF by lazy {
        PointF()
    }
    private var lastY: Float = 0f


    private var textHeight: Float = 0f


    private var animation: ValueAnimator? = null

    private val duration: Long

    var outerIndicatorColor: Int

    private val checkStateRunnable: Runnable = Runnable {
        if (state == State.RELEASE) {
            state = State.NORMAL
            invalidateIfInit()
        }
    }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleInt: Int)
            : super(context, attributeSet, defStyleInt) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LrcView)
        strSize = typedArray.getFloat(R.styleable.LrcView_str_size, 40f)
        timeStrSize = typedArray.getFloat(R.styleable.LrcView_time_str_size, strSize)
        rowInterval = typedArray.getDimension(R.styleable.LrcView_row_interval, 40f)

        emptyText = typedArray.getString(R.styleable.LrcView_empty_text) ?: ""
        commonColor = typedArray.getColor(R.styleable.LrcView_common_color, Color.DKGRAY)
        highlightColor = typedArray.getColor(R.styleable.LrcView_highlight_color, Color.GRAY)
        thumbColor = typedArray.getColor(R.styleable.LrcView_thumb_color, commonColor)
        timeStrColor = typedArray.getColor(R.styleable.LrcView_time_str_color, highlightColor)
        outerIndicatorColor = typedArray.getColor(R.styleable.LrcView_outer_indicator_color, Color.WHITE)

        showAfterRelease = typedArray.getInt(R.styleable.LrcView_show_after_release, 2000)
        duration = typedArray.getInt(R.styleable.LrcView_animate_duration, 100).toLong()

        typedArray.recycle()
        lrcPaint.isAntiAlias = true
        lrcPaint.style = Paint.Style.FILL
        thumbPaint.isAntiAlias = true
        thumbPaint.style = Paint.Style.FILL_AND_STROKE
        thumbPaint.textSize = timeStrSize
        isInited = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
    }



    fun getState(): State {
        return state
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                animation?.cancel()
                lastY = event.y
                if (state != State.NORMAL && shouldNotifyThumbClick(event.x, event.y)) {
                    state = State.NORMAL
                    listener?.onClick(source[lrcIndex])
                    invalidate()
                    return false
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                removeCallbacks(checkStateRunnable)
                state = State.DRAG
                val y = event.y
                val offset = y - lastY
                val row = (-offset / (rowInterval + textHeight)).roundToInt()
                val endIndex = min(max(0, lrcIndex + row), source.size - 1)
                if (endIndex != lrcIndex) {
                    animation = IndexAnimation(view = this, startIndex = lrcIndex, endIndex = endIndex, duration = duration)
                    animation?.start()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                state = State.RELEASE
                postDelayed(checkStateRunnable, showAfterRelease.toLong())
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    interface ThumbClickListener {
        fun onClick(item: LrcItem)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2f
        val centerX = width / 2f
        if (source.isNotEmpty()) {
            if (state != State.NORMAL) {
                drawLrc(canvas, lrcIndex)
                drawThumb(canvas)
            } else {
                drawLrc(canvas, outerIndex)
            }
        } else {
            lrcPaint.color = highlightColor
            canvas.drawText(emptyText, 0, emptyText.length, centerX, centerY, lrcPaint)
        }
    }

    private fun obtainCenterColor() : Int {
        return if (state == State.NORMAL)  {
            outerIndicatorColor
        } else {
            highlightColor
        }
    }

    private fun obtainColor(index: Int) : Int {
        return if (index == outerIndex) {
            outerIndicatorColor
        } else if (index == lrcIndex) {
            highlightColor
        } else {
            commonColor
        }
    }

    private fun drawLrc(canvas: Canvas, centerIndex: Int) {
        val centerX = width / 2.0f
        val centerY = height / 2.0f
        var content = source[centerIndex].content
        lrcPaint.color = obtainCenterColor()
        lrcPaint.getTextBounds(content, 0, content.length, measureRect)
        canvas.drawText(content,
            centerX - (measureRect.right - measureRect.left) / 2, centerY, lrcPaint)
        var currentY = centerY
        if (centerIndex- 1 >= 0) {
            val fontMetrics = lrcPaint.fontMetrics
            val interval = abs(fontMetrics.ascent) + abs(fontMetrics.leading) + rowInterval
            for (i in centerIndex - 1 downTo 0) {
                lrcPaint.color = obtainColor(i)
                content = source[i].content
                lrcPaint.getTextBounds(content, 0, content.length, measureRect)
                currentY -= interval
                canvas.drawText(content,
                    centerX - (measureRect.right - measureRect.left) / 2,
                    currentY, lrcPaint)
            }
        }
        currentY = centerY
        if (centerIndex + 1 < source.size) {
            val fontMetrics = lrcPaint.fontMetrics
            val interval = abs(fontMetrics.descent) + rowInterval + abs(fontMetrics.leading)
            for (i in centerIndex + 1 until source.size) {
                lrcPaint.color = obtainColor(i)
                content = source[i].content
                lrcPaint.getTextBounds(content, 0, content.length, measureRect)
                currentY += interval
                canvas.drawText(content, centerX - (measureRect.right - measureRect.left) / 2, currentY, lrcPaint)
            }
        }

    }

    private fun drawThumb(canvas: Canvas) {
        val centerY = height / 2f
        val timeStr = source[lrcIndex].strTime
        thumbPaint.color = timeStrColor
        thumbPaint.getTextBounds(timeStr, 0, timeStr.length, measureRect)
        val textWidth = measureRect.right - measureRect.left
        val textHeight = measureRect.bottom - measureRect.top
        val x = width - textWidth - thumbPadding
        canvas.drawText(timeStr, x, centerY, thumbPaint)
        val ry = centerY - textHeight / 2
        thumbPaint.color = commonColor
        tPoint.x = thumbPadding
        bPoint.x = thumbPadding
        rPoint.x = (sqrt(15f) / 2 * textHeight)
        tPoint.y = centerY - textHeight
        bPoint.y = centerY
        rPoint.y = ry
        path.reset()
        path.moveTo(tPoint.x, tPoint.y)
        path.lineTo(bPoint.x, bPoint.y)
        path.lineTo(rPoint.x, rPoint.y)
        path.lineTo(tPoint.x, tPoint.y)
        path.close()
        canvas.drawPath(path, thumbPaint)

        thumbPaint.color = commonColor
        canvas.drawLine(rPoint.x + linePadding,
            ry, x - linePadding, ry, thumbPaint)
    }

    private fun invalidateIfInit() {
        if (isInited) {
            invalidate()
        }
    }

    private fun shouldNotifyThumbClick(x: Float, y: Float): Boolean {
        return y >= tPoint.y && y <= bPoint.y
    }


    fun setTime(time: Long) {
        post {
            if (source.isNotEmpty()) {
                val i = LrcItem(time = time, content = "", strTime = "")
                val item = sortSet.ceiling(i)
                item?.let {
                    val index = source.indexOf(it)
                    if (state == State.NORMAL && index != outerIndex) {
                        animation?.cancel()
                        animation = OuterIndexAnimation(view = this, start = outerIndex, end = index, duration)
                        animation?.start()
                    }
                    if (state != State.NORMAL && index != lrcIndex) {
                        animation?.cancel()
                        animation = IndexAnimation(startIndex = lrcIndex, view = this, endIndex = index, duration = duration)
                        animation?.start()
                    }
                }
            }

        }
    }


    private class OuterIndexAnimation(
        val view: LrcView,
        start: Int,
        val end: Int,
        duration: Long) : ValueAnimator(), ValueAnimator.AnimatorUpdateListener {

        init {
            this.duration = duration
            setIntValues(start, end)
            interpolator = LinearInterpolator()
            addUpdateListener(this)

        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            view.outerIndex = animatedValue as Int
            view.invalidate()
        }

        override fun cancel() {
            view.outerIndex = end
            view.invalidate()
            super.cancel()
        }
    }


    fun setDataSource(source: List<LrcItem>) {
        this.source.clear()
        sortSet.clear()
        sortSet.addAll(source)
        this.source.addAll(sortSet)
        lrcIndex = 0
        invalidateIfInit()
    }

    fun getDataSource(): List<LrcItem> {
        return Collections.unmodifiableList(source)
    }

    private class IndexAnimation(
        val view: LrcView,
        startIndex: Int,
        val endIndex: Int,
        duration: Long) : ValueAnimator(), ValueAnimator.AnimatorUpdateListener {

        init {
            setIntValues(startIndex, endIndex)
            this.duration = duration
            addUpdateListener(this)
            interpolator = LinearInterpolator()
        }


        override fun onAnimationUpdate(animation: ValueAnimator?) {
            view.lrcIndex = animatedValue as Int
            view.postInvalidate()
        }


    }





    enum class State {
        NORMAL,
        DRAG,
        RELEASE
    }
}