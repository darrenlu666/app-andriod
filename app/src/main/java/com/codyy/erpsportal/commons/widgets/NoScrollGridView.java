package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * Created by kmdai on 2015/7/27.
 */

public class NoScrollGridView extends GridView {
    private Paint mPaint;
    private int mColumnWidth;
    private boolean isDrawLine = false;

    public NoScrollGridView(Context context) {
        super(context);
        init(context);
    }

    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NoScrollGridView);
            isDrawLine = array.getBoolean(R.styleable.NoScrollGridView_isdrawline, false);
        }
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setStrokeWidth(UIUtils.dip2px(context, 1));
        mPaint.setColor(getResources().getColor(R.color.divide_color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawLine) {
            int columns = getNumColumns();
            if (columns == 2 && getChildCount() >= 2) {
                View view = getChildAt(0);
                View view1 = getChildAt(1);
                float vw = (view1.getX() - (view.getX() + view.getWidth())) / 2;
                canvas.drawLine(view.getX() + view.getWidth() + vw, view.getY(), view.getX() + view.getWidth() + vw, view.getY() + view.getHeight(), mPaint);

                if (getChildCount() >= 3) {
                    View view2 = getChildAt(2);
                    canvas.drawLine(view2.getX() + view2.getWidth() + vw, view2.getY(), view2.getX() + view2.getWidth() + vw, view2.getY() + view2.getHeight(), mPaint);
                    float vh = (view2.getY() - (view.getY() + view.getHeight())) / 2;
                    for (int i = 0; i < 2; i++) {
                        View view3 = getChildAt(i);
                        canvas.drawLine(view3.getX(), view3.getY() + view3.getHeight() + vh, view3.getX() + view.getWidth(), view3.getY() + view3.getHeight() + vh, mPaint);
                    }
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}