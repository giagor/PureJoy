package com.topview.purejoy.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.topview.purejoy.common.util.dpToPx

class StatusCircleButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    companion object {
        const val PAUSE = 1
        const val START = 2
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val statusButtonSize = dpToPx(13F)
    private var statusColor: Int = Color.GREEN
    private var progressCircleRadius = dpToPx(15F)
    private var progressDoneColor: Int = Color.GREEN
    private var progressUndoneColor: Int = Color.GRAY
    private var progressWidth = dpToPx(3F)

    private var status = PAUSE
    private var progress = 0F

    private val statusPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = statusColor
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = progressDoneColor
        style = Paint.Style.STROKE
        strokeWidth = progressWidth
    }

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val halfStatusSize = statusButtonSize / 2
        trianglePath.moveTo(width / 2 - halfStatusSize, height / 2 - halfStatusSize)
        trianglePath.lineTo(width / 2 - halfStatusSize, height / 2 + halfStatusSize)
        trianglePath.lineTo(width / 2 + halfStatusSize, (height / 2).toFloat())
        trianglePath.close()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 重新测量View的高度，使得View在wrap_content下，也有合理的大小
        val size = (progressCircleRadius + progressWidth) * 2
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