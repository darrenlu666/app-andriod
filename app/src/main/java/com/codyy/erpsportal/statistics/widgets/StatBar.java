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


public class StatBar extends View {
    private String mText = "";
    private int mBarColor = Color.GREEN;
    private float mTextSize = 60;

    private TextPaint mTextPaint;
    private Paint mRectPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float mMiddlePadding = 0f;

    private float mBarPadding = 0f;

    private int max = 1000;
    private int current = 600;

    public StatBar(Context context) {
        super(context);
        init(null, 0);
    }

    public StatBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StatBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.StatBar, defStyle, 0);

        mText = a.getString(
                R.styleable.StatBar_android_text);
        mBarColor = a.getColor(
                R.styleable.StatBar_barColor,
                mBarColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mTextSize = a.getDimension(
                R.styleable.StatBar_android_textSize,
                mTextSize);

        mMiddlePadding = a.getDimension(R.styleable.StatBar_middlePadding, mMiddlePadding);
        mBarPadding = a.getDimension(R.styleable.StatBar_barPadding, mBarPadding);

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
        mTextPaint.setColor(mBarColor);
        mTextWidth = mTextPaint.measureText("888888");

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        float textX = getWidth() - paddingRight - mTextWidth;

        float barWidth = (contentWidth - mMiddlePadding - mTextWidth) * current/max;
        float barRight = paddingLeft + barWidth;

        canvas.drawRect(paddingLeft, paddingTop + mBarPadding, barRight, getHeight() - getPaddingBottom() - mBarPadding, mRectPaint);

        // Draw the text.
        canvas.drawText(mText,
                barRight + mMiddlePadding,
                paddingTop + (contentHeight + mTextHeight)/2,
                mTextPaint);

    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        invalidateTextPaintAndMeasurements();
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

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
