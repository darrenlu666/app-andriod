package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.codyy.erpsportal.R;


/**
 * Created by kmdai on 2015/4/8.
 */
public class ProgressBarView extends View implements View.OnClickListener {
    private int mWidth, mHeight;
    private boolean mIsPause;
    /**
     * 进度开始角度
     */
    private final static float START_ANGLE = 270;
    /**
     * 绿色
     */
    private final static int GREEN_COLOR = Color.parseColor("#19ab20");
    /**
     * 红色
     */
    private final static int RED_COLOR = Color.parseColor("#e86153");
    /**
     * 画笔的宽度
     */
    private int mPaintW;
    private Paint mPaint;
    private int mRadius = 0;
    private int bgColor = 0xfff0f3f1;
    private int textColor = 0xff19ab20;
    private float mRateAngle = 0;
    private TextPaint mTextPaint;
    private int textSize = 5;
    private float mMax;
    private int mProgress = 0;
    private Paint.FontMetrics mFontMetrics;
    private float mTextHight;
    private float mTextBaseLine;
    private RectF mArcBorder;
    private float mRatio;
    private Paint mAshenPT;
    private Paint mWhitePT;
    private RectF mPauseRectf;
    private float mDens;
    private RectF mPauseLeft;
    private RectF mPauseRight;
    private OnProgressClickListener mOnClickListener;
    private boolean mCanClick = true;

    public ProgressBarView(Context context) {
        super(context);
        init(context, null);
    }

    public void setPause(boolean pause) {
        mIsPause = pause;
        invalidate();
    }

    public boolean isPause() {
        return mIsPause;
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        mDens = getResources().getDisplayMetrics().density;
        int s = 5;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarView);
            s = a.getInt(R.styleable.ProgressBarView_paintw, 5);
            bgColor = a.getColor(R.styleable.ProgressBarView_progressbg, 0xfff0f3f1);
            textColor = a.getColor(R.styleable.ProgressBarView_text_color, 0xff19ab20);
            textSize = (int) (a.getInt(R.styleable.ProgressBarView_textsize, 5) * mDens + 0.5);
        }
        mAshenPT = new Paint();
        mAshenPT.setColor(Color.parseColor("#59000000"));
        mAshenPT.setAntiAlias(true);
        mAshenPT.setStyle(Paint.Style.FILL);

        mPaintW = (int) (mDens * s + 0.5);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mPaintW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.parseColor("#19ab20"));

        mWhitePT = new Paint();
        mWhitePT.setAntiAlias(true);
        mWhitePT.setStyle(Paint.Style.FILL);
        mWhitePT.setColor(Color.WHITE);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
        mFontMetrics = mPaint.getFontMetrics();
        mTextHight = mFontMetrics.bottom - mFontMetrics.top;
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mArcBorder != null) {
            mPaint.setColor(bgColor);
            canvas.drawArc(mArcBorder, 0, 360, false, mPaint);
            int color = blendColors(GREEN_COLOR, RED_COLOR, mRatio);
            mTextPaint.setColor(color);
            mPaint.setColor(color);
            canvas.drawArc(mArcBorder, START_ANGLE, mRateAngle, false, mPaint);
            float mProgressf = ((float) mProgress / mMax) * 100;
            canvas.drawText((int) mProgressf + "%", mWidth / 2, mTextBaseLine, mTextPaint);
            if (mIsPause) {
                canvas.save();
                canvas.drawArc(mPauseRectf, 0, 360, false, mAshenPT);
                canvas.drawRect(mPauseLeft, mWhitePT);
                canvas.drawRect(mPauseRight, mWhitePT);
                canvas.restore();
            }
        }
    }

    /**
     * @param canClick
     */
    public void setCanClick(boolean canClick) {
        if (canClick) {
            super.setOnClickListener(this);
        } else {
            super.setOnClickListener(null);
            mIsPause = false;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        int childWidth = mWidth < mHeight ? mWidth : mHeight;
        int childHeight = mWidth > mHeight ? mWidth : mHeight;
        mRadius = (childWidth - mPaintW * 2) / 2;
        mArcBorder = new RectF(mWidth / 2 - mRadius, mHeight / 2 - mRadius, mWidth / 2 + mRadius, mHeight / 2 + mRadius);
        float a = (int) (mDens * 3 + 0.5);
        mPauseRectf = new RectF(mArcBorder.left + a, mArcBorder.top + a, mArcBorder.right - a, mArcBorder.bottom - a);
        mTextBaseLine = mHeight - (mHeight - mTextHight) / 2 - mFontMetrics.bottom;
        float pauseh = mHeight / 4;
        float pausew = mWidth / 10;
        mPauseLeft = new RectF(mWidth / 2 - pausew - a, mHeight / 2 - pauseh / 2, mWidth / 2 - a, mHeight / 2 + pauseh / 2);
        mPauseRight = new RectF(mWidth / 2 + a, mHeight / 2 - pauseh / 2, mWidth / 2 + pausew + a, mHeight / 2 + pauseh / 2);
        invalidate();
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        if (mProgress >= mMax) {
            mRateAngle = 360;
            mRatio = 1f;
        } else {
            mRatio = mProgress / mMax;
            mRateAngle = 360 * mRatio;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
        }
        return super.onTouchEvent(event);
    }

    /**
     * 无效的设置
     *
     * @param l
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(this);
    }

    /**
     * 设置暂停监听
     *
     * @param listener
     */
    public void setOnClickListener(OnProgressClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void onClick(View v) {
        if (mIsPause) {
            mIsPause = false;
            if (mOnClickListener != null) {
                mOnClickListener.onStart();
            }
        } else {
            mIsPause = true;
            if (mOnClickListener != null) {
                mOnClickListener.onPause();
            }
        }
        invalidate();
    }

    public interface OnProgressClickListener {
        /**
         * 暂停
         */
        void onPause();

        /**
         * 开始
         */
        void onStart();
    }
}
