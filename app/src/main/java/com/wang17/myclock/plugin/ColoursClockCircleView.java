package com.wang17.myclock.plugin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.wang17.myclock.R;

public class ColoursClockCircleView extends View {

    private static final String _TAG = "wangsc";
    private Paint paint = new Paint();
    private int color1 = getResources().getColor(R.color.second_color1),color2 = getResources().getColor(R.color.second_color2);
    private float viewRadius = 0f;
    private float circleSize = dip2px(10f);
    private float strokeWidth = dip2px(5.0f);
    private int progress = 0, preProgress = -1;
    private boolean isInversion = true;

    private RectF rectF;

    public ColoursClockCircleView(Context context) {
        this(context, null);
    }

    public ColoursClockCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColoursClockCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
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
        paint.setStrokeWidth(strokeWidth);

        float startAngle = 0f, sweepAngle = 0f;
        if (isInversion) {
            startAngle = -90f;
            sweepAngle = 6f * progress;

            int num = (int) sweepAngle / 30;
            for (int i = 0; i < num; i++) {
                if (i % 2 == 0) {
                    paint.setColor(color1);
                } else {
                    paint.setColor(color2);
                }
                canvas.drawArc(rectF, startAngle + 30f * i, 30f, false, paint);
            }
            if (num % 2 == 0) {
                paint.setColor(color1);
            } else {
                paint.setColor(color2);
            }
            canvas.drawArc(rectF, startAngle + 30f * num, sweepAngle % 30f, false, paint);

        } else {
            startAngle = 6f * progress - 90f;

            int startI = (int) 6f * progress / 30;  //  12
            if (progress % 5 != 0) {
                if (startI % 2 == 0) {
                    paint.setColor(color1);
                } else {
                    paint.setColor(color2);
                }
                canvas.drawArc(rectF, startAngle, 30 * (startI + 1) - 6f * progress, false, paint);
            }
            else{
                startI--;
            }

            paint.setStrokeWidth(strokeWidth);
            for (int i = startI+1; i < 12; i++) {
                if (i % 2 == 0) {
                    paint.setColor(color1);
                } else {
                    paint.setColor(color2);
                }
                canvas.drawArc(rectF, -90f + 30f * i, 30f, false, paint);
            }
        }
    }

    private float dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dipValue * scale;
    }

    public void setProgress(int progress) {
        if (progress < 0)
            progress = 0;
        if (progress > 60)
            progress = 60;

        this.progress = progress;
        if (progress < preProgress) {
            isInversion = !isInversion;
        }
        preProgress = progress;
        invalidate();
    }

}
