package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.erpsportal.R;

/**
 * Created by kmdai on 2015/7/27.
 */
public class DivideView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    public DivideView(Context context) {
        super(context);
        init();
    }

    public DivideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DivideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.user_setting_textcolor));
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mHeight, mWidth / 2 - 20, mHeight, mPaint);
        RectF rectF1 = new RectF(mWidth / 2 - 10, mHeight - 10, mWidth / 2 + 10, mHeight);
        canvas.drawArc(rectF1, 0, 360, false, mPaint);
        canvas.drawLine(mWidth / 2 + 20, mHeight, mWidth, mHeight, mPaint);
    }
}
