package com.codyy.erpsportal.statistics.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;

/**
 * 顺序排列布局
 * Created by gujiajia on 2016/6/20.
 */
@UiThread
public class OrderLayout extends ViewGroup {

    private int mHorizontalPadding = 0;

    private int mVerticalPadding = 0;

    public OrderLayout(Context context) {
        this(context, null);
    }

    public OrderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public OrderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OrderLayout,defStyleAttr, defStyleRes);
        mHorizontalPadding = a.getDimensionPixelSize(R.styleable.OrderLayout_horizontalGap, 0);
        mVerticalPadding = a.getDimensionPixelSize(R.styleable.OrderLayout_verticalGap, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resultWidthMode = 0;
        int resultWidthSize = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int resultHeightMode = 0;
        int resultHeightSize = 0;

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int horizontalPadding = getPaddingLeft() + getPaddingRight();

        int widthCache = horizontalPadding;
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if ((child.getVisibility()) != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                widthCache += child.getMeasuredWidth();
            }
        }

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                resultWidthMode = MeasureSpec.EXACTLY;
                resultWidthSize = widthSize;
                break;
            case MeasureSpec.AT_MOST: {
                if (getChildCount() > 1)
                    widthCache =+ (getChildCount() - 1) * mHorizontalPadding;
                resultWidthMode = MeasureSpec.EXACTLY;
                resultWidthSize = Math.min(widthCache, widthSize);
                break;
            }
            case MeasureSpec.UNSPECIFIED:
                resultWidthMode = MeasureSpec.EXACTLY;
                if (getChildCount() > 1)
                    widthCache =+ (getChildCount() - 1) * mHorizontalPadding;
                resultWidthSize = widthCache;
                break;
        }

        //先得知道宽度，然后才能知道何时换行
        if (widthMode == MeasureSpec.UNSPECIFIED) {//宽度未指定么，只有一行
            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                    resultHeightMode = MeasureSpec.EXACTLY;
                    resultHeightSize = heightSize;
                    computeChildrenPosition(resultWidthSize, resultHeightSize);
                    break;
                case MeasureSpec.AT_MOST: {
                    resultHeightSize = Math.min(computeHeightWhenWidthUnspecified(), heightSize);
                    resultHeightMode = MeasureSpec.EXACTLY;
                    break;
                }
                case MeasureSpec.UNSPECIFIED: {
                    resultHeightSize = computeHeightWhenWidthUnspecified();
                    resultHeightMode = MeasureSpec.EXACTLY;
                }
            }

        } else {
            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                    resultHeightMode = MeasureSpec.EXACTLY;
                    resultHeightSize = heightSize;
                    computeChildrenPosition(resultWidthSize, resultHeightSize);
                    break;
                case MeasureSpec.AT_MOST: {
                    resultHeightSize = Math.min(computeHeight(resultWidthSize), heightSize);
                    resultHeightMode = MeasureSpec.EXACTLY;
                    break;
                }
                case MeasureSpec.UNSPECIFIED: {
                    resultHeightSize = computeHeight(resultWidthSize);
                    resultHeightMode = MeasureSpec.EXACTLY;
                }
            }
        }

        int resultWidthSpec = MeasureSpec.makeMeasureSpec(resultWidthSize, resultWidthMode);

        setMeasuredDimension(resultWidthSpec,
                MeasureSpec.makeMeasureSpec(resultHeightSize, resultHeightMode));
    }

    private void computeChildrenPosition(int widthSize, int heightSize) {
        int paddingLeft = getPaddingLeft();
        int currentLineHeight = 0;
        int currentChildTop = getPaddingTop();
        int maxX = widthSize - getPaddingRight();

        int currentChildLeft = getPaddingLeft();
        int currentChildRight;
        for (int i=0; i< getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                currentChildRight = currentChildLeft + child.getMeasuredWidth();
                if (currentChildRight >= maxX) {//右边超过了范围就换行
                    //换行了，当前子View要在下一行，top要加一下,主要此时currentLineHeight是上一行的
                    currentChildTop += currentLineHeight + mVerticalPadding;
                    positionChild(child, paddingLeft, currentChildTop);
                    //换行了，下一个首位置得从leftPadding重新累计
                    currentChildLeft = paddingLeft + child.getMeasuredWidth() + mVerticalPadding;

                    //重新算这行的高
                    currentLineHeight = child.getMeasuredHeight();
                } else {//不换行获取本行高度最大组件的高度
                    currentLineHeight = Math.max(child.getMeasuredHeight(), currentLineHeight);
                    positionChild(child, currentChildLeft, currentChildTop);
                    //计算好下一个首位置
                    currentChildLeft = currentChildRight + mVerticalPadding;
                }
            }
        }
    }

    /**
     * 计算高度
     * @param resultWidthSize 已计算出来的宽度
     * @return
     */
    private int computeHeight(int resultWidthSize) {
        int paddingLeft = getPaddingLeft();
        int heightCache = getPaddingTop() + getPaddingBottom();
        int currentLineHeight = 0;
        boolean firstLine = true;//当前是否在遍历首行
        int currentChildLeft = getPaddingLeft();
        int currentChildTop = getPaddingTop();
        int currentChildRight;
        int maxX = resultWidthSize - getPaddingRight();

        /**
         * 遍历可见的子View，累加高度
         */
        for (int i=0; i< getChildCount(); i++) {
            final View child = getChildAt(i);
            if ((child.getVisibility()) != GONE) {
                currentChildRight = currentChildLeft + child.getMeasuredWidth();
                if (currentChildRight >= maxX) {//右边超过了范围就换行
                    //如果不是第一行，累加高度要加垂直padding
                    heightCache += firstLine? currentLineHeight: currentLineHeight + mVerticalPadding;
                    firstLine = false;

                    //换行了，当前子View要在下一行，top要加一下,主要此时currentLineHeight是上一行的
                    currentChildTop = currentChildTop + currentLineHeight + mVerticalPadding;
                    positionChild(child, paddingLeft, currentChildTop);
                    //换行了，下一个首位置得从leftPadding重新累计
                    currentChildLeft = paddingLeft + child.getMeasuredWidth() + mVerticalPadding;

                    //换行了，重记这行的高
                    currentLineHeight = child.getMeasuredHeight();
                } else {//不换行获取本行高度最大组件的高度
                    currentLineHeight = Math.max(child.getMeasuredHeight(), currentLineHeight);
                    positionChild(child, currentChildLeft, currentChildTop);
                    //计算好下一个首位置
                    currentChildLeft = currentChildRight + mVerticalPadding;
                }
            }
        }
        return currentChildTop + currentLineHeight + getPaddingBottom();
    }

    private void positionChild(View child, int left, int top) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        layoutParams.mLeft = left;
        layoutParams.mTop = top;
        layoutParams.mRight = left + child.getMeasuredWidth();
        layoutParams.mBottom = top + child.getMeasuredHeight();
    }

    /**
     * 计算宽度未指定时的高度
     * @return 计算好的宽度
     */
    private int computeHeightWhenWidthUnspecified() {
        int resultHeightSize;
        int currentLineHeight = 0;
        for (int i=0; i< getChildCount(); i++) {
            final View child = getChildAt(i);
            if ((child.getVisibility()) != GONE) {
                currentLineHeight = Math.max(child.getMeasuredHeight(), currentLineHeight);
            }
        }
        resultHeightSize = currentLineHeight;
        return resultHeightSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i=0; i<getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams st = (LayoutParams) child.getLayoutParams();
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     * 自定义LayoutParams以缓存位置信息
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {

        private int mLeft, mTop, mRight, mBottom;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
