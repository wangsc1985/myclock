package com.wang17.myclock.plugin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.wang17.myclock.R

class ColoursClockCircleView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val paint = Paint()
    private val color1 = resources.getColor(R.color.second_color1)
    private val color2 = resources.getColor(R.color.second_color2)
    private var viewRadius = 0f
    private val circleSize = dip2px(10f)
    private val strokeWidth = dip2px(5.0f)
    private var progress = 0
    private var preProgress = -1
    private var isInversion = true
    private var rectF: RectF? = null
    private fun init() {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewRadius = Math.min(measuredWidth, measuredHeight) * 1.0f / 2
        rectF = RectF(
                circleSize / 2,
                circleSize / 2,
                2 * viewRadius - circleSize / 2 - strokeWidth,
                2 * viewRadius - circleSize / 2 - strokeWidth
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = strokeWidth
        var startAngle = 0f
        var sweepAngle = 0f
        if (isInversion) {
            startAngle = -90f
            sweepAngle = 6f * progress
            val num = sweepAngle.toInt() / 30
            for (i in 0 until num) {
                if (i % 2 == 0) {
                    paint.color = color1
                } else {
                    paint.color = color2
                }
                canvas.drawArc(rectF!!, startAngle + 30f * i, 30f, false, paint)
            }
            if (num % 2 == 0) {
                paint.color = color1
            } else {
                paint.color = color2
            }
            canvas.drawArc(rectF!!, startAngle + 30f * num, sweepAngle % 30f, false, paint)
        } else {
            startAngle = 6f * progress - 90f
            var startI = 6f.toInt() * progress / 30 //  12
            if (progress % 5 != 0) {
                if (startI % 2 == 0) {
                    paint.color = color1
                } else {
                    paint.color = color2
                }
                canvas.drawArc(rectF!!, startAngle, 30 * (startI + 1) - 6f * progress, false, paint)
            } else {
                startI--
            }
            paint.strokeWidth = strokeWidth
            for (i in startI + 1..11) {
                if (i % 2 == 0) {
                    paint.color = color1
                } else {
                    paint.color = color2
                }
                canvas.drawArc(rectF!!, -90f + 30f * i, 30f, false, paint)
            }
        }
    }

    private fun dip2px(dipValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dipValue * scale
    }

    fun setProgress(progress: Int) {
        var progress = progress
        if (progress < 0) progress = 0
        if (progress > 60) progress = 60
        this.progress = progress
        if (progress < preProgress) {
            isInversion = !isInversion
        }
        preProgress = progress
        invalidate()
    }

    companion object {
        private const val _TAG = "wangsc"
    }

    init {
        init()
    }
}