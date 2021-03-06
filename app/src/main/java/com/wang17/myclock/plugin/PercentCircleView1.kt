package com.wang17.myclock.plugin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PercentCircleView1 @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val paint = Paint()
    private val color = Color.YELLOW
    private var viewRadius = 0f
    private val circleSize = dip2px(10f)
    private val strokeWidth = dip2px(3.0f)
    private var progress = 0
    private var preProgress = -1
    private var isInversion = true
    private var max = 60
    private var rectF: RectF? = null
    private fun init() {
        paint.color = color
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
        paint.color = color
        paint.strokeWidth = strokeWidth
        if (isInversion) {
            canvas.drawArc(rectF!!, -90f, 360 * (progress * 1.0f / max), false, paint)
        } else {
            canvas.drawArc(rectF!!, 360 * (progress * 1.0f / max) - 90f, 360f - 360 * (progress * 1.0f / max), false, paint)
        }
    }

    private fun dip2px(dipValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dipValue * scale
    }

    fun setProgress(progress: Int) {
        var progress = progress
        if (progress < 0) progress = 0
        if (progress > max) progress = max
        this.progress = progress
        if (progress < preProgress) {
            isInversion = !isInversion
        }
        preProgress = progress
        invalidate()
    }

    fun setMax(max: Int) {
        this.max = max
    }

    companion object {
        private const val _TAG = "wangsc"
    }

    init {
        init()
    }
}