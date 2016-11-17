package com.codyy.erpsportal.statistics.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.text.NumberFormat;


public class ProportionBar extends View {

    private final static int DEFAULT_BAR_HEIGHT_DP = 8;

    private String mText = "";
    private int mBarColor = Color.GREEN;
    private int mBarColorLow = Color.RED;
    private float mTextSize = 60;
    private int mTextColor = Color.GREEN;
    private int mTextColorLow = Color.RED;

    private TextPaint mTextPaint;
    private Paint mRectPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float mMiddlePadding = 0f;

    private int mBarHeight = 0;

    private int mMax = 1000;
    private float mCurrent = 600;

    private float mBarTop;

    private float mTextY;

    public ProportionBar(Context context) {
        super(context);
        init(null, 0);
    }

    public ProportionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProportionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mBarHeight = UIUtils.dip2px(getContext(), DEFAULT_BAR_HEIGHT_DP);
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ProportionBar, defStyle, 0);

        mText = a.getString(
                R.styleable.ProportionBar_android_text);
        mBarColor = a.getColor(
                R.styleable.ProportionBar_barColor,
                mBarColor);
        mBarColorLow = a.getColor(R.styleable.ProportionBar_barColorLow, mBarColorLow);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mTextSize = a.getDimension(
                R.styleable.ProportionBar_android_textSize,
                mTextSize);
        mTextColor = a.getColor(R.styleable.ProportionBar_android_textColor, mTextColor);
        mTextColorLow = a.getColor(R.styleable.ProportionBar_textColorLow, mTextColorLow);
        mMiddlePadding = a.getDimension(R.styleable.ProportionBar_middlePadding, mMiddlePadding);
        mBarHeight = (int)a.getDimension(R.styleable.ProportionBar_barHeight, mBarHeight);

        a.recycle();

        // Set up Rect Paint
        mRectPaint = new Paint();
        mRectPaint.setColor(mBarColor);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextWidth = mTextPaint.measureText("000.00%");

        Rect rect = new Rect();
        mTextPaint.getTextBounds("8",0,1,rect);
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = rect.height();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = Math.max(mBarHeight, (int) mTextHeight) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
        if (mTextHeight > mBarHeight) {
            mBarTop = (mTextHeight - mBarHeight)/2 + getPaddingTop();
            mTextY = getMeasuredHeight() - getPaddingBottom() - 1;
        } else {
            mBarTop = getPaddingTop();
            mTextY = getMeasuredHeight() - getPaddingBottom() - (mTextHeight - mBarHeight)/2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;

        float barWidth = (contentWidth - mMiddlePadding - mTextWidth) * mCurrent / mMax;
        float barRight = paddingLeft + barWidth;

        canvas.drawRect(paddingLeft, mBarTop, barRight, paddingTop + mBarHeight, mRectPaint);
        canvas.drawText(mText, barRight + mMiddlePadding, mTextY, mTextPaint);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getBarColor() {
        return mBarColor;
    }

    public void setBarColor(int exampleColor) {
        mBarColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        invalidateTextPaintAndMeasurements();
    }

    public void setPercent(float percent) {
        if (percent < 0) percent = 0;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(0);
        setText(nf.format(percent) + "%");//字超过一百不调整，所以可能显示200%

        if (percent > 100) percent = 100;//条条超过一百算一百
        mCurrent = percent * 10;
        if (percent <= 50) {
            mTextPaint.setColor(mTextColorLow);
            mRectPaint.setColor(mBarColorLow);
        } else {
            mTextPaint.setColor(mTextColor);
            mRectPaint.setColor(mBarColor);
        }
        invalidate();
    }

    public float getCurrent() {
        return mCurrent;
    }

    public void setCurrent(float current) {
        this.mCurrent = current;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        this.mMax = max;
    }
}
