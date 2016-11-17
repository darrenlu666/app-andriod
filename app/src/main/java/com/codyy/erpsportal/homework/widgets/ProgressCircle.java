package com.codyy.erpsportal.homework.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.erpsportal.R;

/**
 * 视频上传进度view
 * Created by ldh on 2016/5/24.
 */
public class ProgressCircle extends View {
    private Paint mPaint;
    /**
     * 进度圆圈的类型（上传中/上传失败）
     */
    public int mCircleType;
    public static final int CIRCLE_TYPE_UPLOADING = 0X001;
    public static final int CIRCLE_TYPE_UPLOAD_FAILURE = 0X002;

    /**
     * 圆圈半径
     */
    private int mRadius;

    /**
     * 圆环宽度
     */
    private float mRingWidth;

    /**
     * 圆环颜色
     */
    private int mRingColor;

    /**
     * 显示进度区域的颜色
     */
    private int mProgressAreaColor;

    /**
     * 当前进度
     */
    private int mProgress;

    /**
     * 最大进度
     */
    private int mMax;

    public ProgressCircle(Context context) {
        this(context, null);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressCircle);
        mRingWidth = ta.getDimension(R.styleable.ProgressCircle_ringWidth, 5);
        mRingColor = ta.getColor(R.styleable.ProgressCircle_ringColor, Color.WHITE);
        mProgressAreaColor = ta.getColor(R.styleable.ProgressCircle_progressAreaColor, Color.WHITE);
        mCircleType = ta.getBoolean(R.styleable.ProgressCircle_isFailure, false) ? CIRCLE_TYPE_UPLOAD_FAILURE : CIRCLE_TYPE_UPLOADING;
        mMax = ta.getInteger(R.styleable.ProgressCircle_circleMax,100);
        ta.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;//圆心X坐标
        mRadius = (int) (center - mRingWidth / 2);
        mPaint.setColor(mRingColor);
        mPaint.setStrokeWidth(mRingWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(center, center, mRadius, mPaint);

        switch (mCircleType) {
            case CIRCLE_TYPE_UPLOADING:
                mPaint.setColor(mProgressAreaColor);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                RectF oval = new RectF(center - mRadius + 10, center - mRadius + 10, center + mRadius - 10, center + mRadius - 10);  //用于定义的圆弧的形状和大小的界限
                canvas.drawArc(oval, -90, 360 * mProgress / mMax, true, mPaint);
                break;
            case CIRCLE_TYPE_UPLOAD_FAILURE:
                mPaint.setColor(Color.RED);
                mPaint.setTextSize(80);
                canvas.drawText("!", center, center + mRadius - 30, mPaint);
                break;
        }
    }


    public int getCircleType() {
        return mCircleType;
    }

    public void setCircleType(int mCircleType) {
        this.mCircleType = mCircleType;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public float getRingWidth() {
        return mRingWidth;
    }

    public void setRingWidth(float mRingWidth) {
        this.mRingWidth = mRingWidth;
    }

    public int getRingColor() {
        return mRingColor;
    }

    public void setRingColor(int mRingColor) {
        this.mRingColor = mRingColor;
    }

    public int getProgressAreaColor() {
        return mProgressAreaColor;
    }

    public void setProgressAreaColor(int mProgressAreaColor) {
        this.mProgressAreaColor = mProgressAreaColor;
    }


    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    public int getProgress() {
        return mProgress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress <= mMax) {
            this.mProgress = progress;
            postInvalidate();
        }
    }

}
