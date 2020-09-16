package com.wang17.myclock.plugin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PercentCircleView extends View {

    private static final String _TAG = "wangsc";
    private Paint circlePaint = new Paint();
    private int progressColor = Color.YELLOW;
    private float viewRadius = 0f;
    private float circleSize = dip2px(10f);
    private float strokeWidth = dip2px(3.0f);
    private int progress = 0;

    private int max = 60;
    private RectF rectF = null;

    public PercentCircleView(Context context) {
        this(context, null);
    }

    public PercentCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint.setColor(progressColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewRadius = Math.min(getMeasuredWidth(), getMeasuredHeight()) * 1.0f / 2;
        rectF = new RectF(
                circleSize / 2,
                circleSize / 2,
                2 * viewRadius - circleSize / 2 - strokeWidth,
                2 * viewRadius - circleSize / 2 - strokeWidth
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        circlePaint.setColor(progressColor);
        circlePaint.setStrokeWidth(strokeWidth);
        canvas.drawArc(rectF, -90f, 360 * (progress * 1.0f / this.max), false, circlePaint);
    }

    private float dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dipValue * scale;
    }

    public void setProgress(int progress) {
        if (progress < 0) progress = 0;
        if (progress > this.max) progress = this.max;
        this.progress=progress;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
    }

}
