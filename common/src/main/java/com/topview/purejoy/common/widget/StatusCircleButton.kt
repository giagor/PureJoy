package com.topview.purejoy.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.topview.purejoy.common.R
import com.topview.purejoy.common.util.dpToPx

class StatusCircleButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    companion object {
        const val PAUSE = 1
        const val START = 2
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * 中间的状态的大小（长宽）
     * */
    private var statusButtonSize = 0F

    /**
     * 中间状态的颜色
     * */
    private var statusColor: Int = 0

    /**
     * 进度条与圆心的半径
     * */
    private var progressCircleRadius = 0F

    /**
     * 已完成的进度条的颜色
     * */
    private var progressDoneColor: Int = 0

    /**
     * 未完成的进度条的颜色
     * */
    private var progressUndoneColor: Int = 0

    /**
     * 进度条边的宽度
     * */
    private var progressStrokeWidth = 0F

    /**
     * 显示的状态
     * */
    private var status = 0

    /**
     * 当前进度
     * */
    private var progress = 0F

    private val statusPaint: Paint

    private val progressPaint: Paint

    /**
     * 三角形路径
     * */
    private var trianglePath = Path()

    /**
     * 状态集合
     * */
    private var statusSet: Set<Int> = HashSet<Int>().apply {
        add(PAUSE)
        add(START)
    }

    init {
        initAttrs(attrs)

        statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = statusColor
        }

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = progressDoneColor
            style = Paint.Style.STROKE
            strokeWidth = progressStrokeWidth
        }
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusCircleButton)
        statusButtonSize =
            typedArray.getDimension(R.styleable.StatusCircleButton_status_button_size, dpToPx(13F))
        statusColor =
            typedArray.getColor(R.styleable.StatusCircleButton_status_color, Color.GREEN)
        progressCircleRadius =
            typedArray.getDimension(
                R.styleable.StatusCircleButton_progress_circle_radius,
                dpToPx(15F)
            )
        progressDoneColor =
            typedArray.getColor(R.styleable.StatusCircleButton_progress_done_color, Color.GREEN)
        progressUndoneColor =
            typedArray.getColor(R.styleable.StatusCircleButton_progress_undone_color, Color.GRAY)
        progressStrokeWidth =
            typedArray.getDimension(
                R.styleable.StatusCircleButton_progress_stroke_width,
                dpToPx(3F)
            )
        status =
            typedArray.getInt(R.styleable.StatusCircleButton_status, PAUSE)
        progress =
            typedArray.getFloat(R.styleable.StatusCircleButton_progress, 0F)
        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        setTrianglePath()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 重新测量View的高度，使得View在wrap_content下，也有合理的大小
        val size = (progressCircleRadius + progressStrokeWidth) * 2
        val width = resolveSize(size.toInt(), widthMeasureSpec)
        val height = resolveSize(size.toInt(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        drawStatus(canvas)
        drawProgress(canvas)
    }

    /**
     * 画按钮状态
     * */
    private fun drawStatus(canvas: Canvas) {
        val halfStatusSize = statusButtonSize / 2
        if (status == PAUSE) {
            canvas.drawPath(trianglePath, statusPaint)
        } else if (status == START) {
            canvas.drawRect(
                width / 2 - halfStatusSize,
                height / 2 - halfStatusSize,
                width / 2 + halfStatusSize,
                height / 2 + halfStatusSize,
                statusPaint
            )
        }
    }

    /**
     * 画进度条
     * */
    private fun drawProgress(canvas: Canvas) {
        // 已完成部分，其角度值
        val doneAngle = (progress / 100) * 360
        // 画已完成的进度条
        progressPaint.color = progressDoneColor
        canvas.drawArc(
            width / 2 - progressCircleRadius,
            height / 2 - progressCircleRadius,
            width / 2 + progressCircleRadius,
            height / 2 + progressCircleRadius,
            -90F,
            doneAngle,
            false,
            progressPaint
        )

        // 画未完成的进度条
        progressPaint.color = progressUndoneColor
        canvas.drawArc(
            width / 2 - progressCircleRadius,
            height / 2 - progressCircleRadius,
            width / 2 + progressCircleRadius,
            height / 2 + progressCircleRadius,
            -90F + doneAngle,
            360 - doneAngle,
            false,
            progressPaint
        )
    }

    /**
     * 设置三角形路径
     * */
    private fun setTrianglePath() {
        trianglePath.reset()
        val halfStatusSize = statusButtonSize / 2
        trianglePath.moveTo(width / 2 - halfStatusSize, height / 2 - halfStatusSize)
        trianglePath.lineTo(width / 2 - halfStatusSize, height / 2 + halfStatusSize)
        trianglePath.lineTo(width / 2 + halfStatusSize, (height / 2).toFloat())
        trianglePath.close()
    }

    fun setStatusButtonSize(size: Float) {
        this.statusButtonSize = size
        setTrianglePath()
        invalidate()
    }

    fun setStatusColor(color: Int) {
        statusPaint.color = color
        invalidate()
    }

    fun setProgressCircleRadius(radius: Float) {
        this.progressCircleRadius = radius
        requestLayout()
    }

    fun setProgressDoneColor(color: Int) {
        this.progressDoneColor = color
        invalidate()
    }

    fun setProgressUnDoneColor(color: Int) {
        this.progressUndoneColor = color
        invalidate()
    }

    fun setProgressStrokeWidth(width: Float) {
        this.progressStrokeWidth = width
        progressPaint.strokeWidth = width
        requestLayout()
    }

    /**
     * 更新进度条
     * */
    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    /**
     * 更新按钮的状态值
     *
     * @param status 取值为PAUSE或者START
     * */
    fun setStatus(status: Int) {
        if (this.status != status && (status in statusSet)) {
            this.status = status
            invalidate()
        }
    }
}