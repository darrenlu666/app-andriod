package com.codyy.erpsportal.exam.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.codyy.erpsportal.R;

/**
 * 进度条
 * Created by eachann on 2016/1/12.
 */
public class AnalysisProgress extends View {
    private static final String TAG = AnalysisProgress.class.getSimpleName();
    /**
     * 矩形进度条
     */
    public static final int TYPE_RECTANGLE = 0;
    /**
     * 甜甜圈进度条
     */
    public static final int TYPE_CIRCLE = 1;
    /**
     * 默认为矩形进度条
     */
    private int mType = TYPE_RECTANGLE;

    public AnalysisProgress(Context context) {
        super(context);
        init(context, null);
    }

    private Paint mPaint;
    TextPaint mTextPaint;


    public AnalysisProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnalysisProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public AnalysisProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        /*
      甜甜圈进度条中间文字大小
     */
        int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12, context.getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.parseColor("#FFEFEFF3"));//灰色
        switch (mType) {
            case TYPE_RECTANGLE://矩形
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(LEFT_SIDE, TOP_SIDE, getMeasuredWidth(), getMeasuredHeight(), mPaint);// 长方形
                canvas.save();
                mPaint.setColor(Color.parseColor("#FF19AB20"));//ff69be40
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(LEFT_SIDE, TOP_SIDE, (int) (getMeasuredWidth() * mPer), getMeasuredHeight(), mPaint);
                canvas.restore();
                break;
            case TYPE_CIRCLE://甜甜圈
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(STROKE_WIDTH);
                canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2 - STROKE_WIDTH, mPaint);
                canvas.save();
                Rect targetRect = new Rect(LEFT_SIDE, TOP_SIDE, getMeasuredWidth(), getMeasuredHeight());// TODO: 2016/3/28 Avoid object allocations during draw/layout operations (preallocate and reuse instead) ,now do not fix
                Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
                if (mProgress < PASSING_SCORE && mProgress != 0) {
                    this.mTextPaint.setColor(Color.RED);
                } else if (mProgress == 0) {
                    this.mTextPaint.setColor(Color.BLACK);
                } else {
                    this.mTextPaint.setColor(Color.parseColor("#ff69be40"));
                }
                int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                canvas.drawText(mText, targetRect.centerX(), baseline, mTextPaint);
                canvas.save();
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(STROKE_WIDTH);
                if (mProgress < PASSING_SCORE) {
                    this.mPaint.setColor(Color.RED);
                } else {
                    this.mPaint.setColor(Color.parseColor("#ff69be40"));
                }
                RectF arcRectF = new RectF(STROKE_WIDTH, STROKE_WIDTH, getMeasuredWidth() - STROKE_WIDTH, getMeasuredHeight() - STROKE_WIDTH);// TODO: 2016/3/28 Avoid object allocations during draw/layout operations (preallocate and reuse instead) ,now do not fix
                canvas.drawArc(arcRectF, START_ANGLE, (float) mProgress / MAX_PROGRESS * MAX_ANGLE, false, mPaint);
                canvas.restore();
                break;
        }

    }

    /**
     * 画笔宽度
     */
    private static final float STROKE_WIDTH = 15f;
    /**
     * 正确率
     */
    private static final int PASSING_SCORE = 60;
    /**
     * 甜甜圈圆弧起始角度
     */
    private static final int START_ANGLE = -180;
    /**
     * 甜甜圈最大角度
     */
    private static final int MAX_ANGLE = 360;
    /**
     * 最大正确率
     */
    private static final int MAX_PROGRESS = 100;
    /**
     * 最小正确率
     */
    private static final int MIN_PROGRESS = 0;
    /**
     * 进度条左边起始位置
     */
    private static final int LEFT_SIDE = 0;
    /**
     * 进度条上边起始位置
     */
    private static final int TOP_SIDE = 0;
    /**
     * 矩形比例
     */
    private float mPer;
    /**
     * 甜甜圈中间文字
     */
    private String mText;
    /**
     * 甜甜圈进度
     */
    private int mProgress;

    /**
     * @param type TYPE_RECTANGLE 矩形;TYPE_CIRCLE 甜甜圈
     */
    public void setType(int type) {
        this.mType = type;
    }

    /**
     * @param progress 甜甜圈进度
     */
    public void setProgress(int progress) {
        if (progress > MAX_PROGRESS)
            progress = MAX_PROGRESS;
        else if (progress < MIN_PROGRESS) {
            progress = MIN_PROGRESS;
        }
        this.mText = progress + "%";
        this.mProgress = progress;
        this.invalidate();
    }

    /**
     * @param real  实际值
     * @param total 总值
     */
    public void setProgress(float real, int total) {
        if (total > 0) {
            mPer = real / total;
        } else {
            mPer = 0;
        }
        this.invalidate();
    }
}
