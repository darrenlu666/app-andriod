package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class EmojiconSpan extends DynamicDrawableSpan {
    private final Context mContext;
    private final int mResourceId;
    private final int mSize;
    private Drawable mDrawable;

    public EmojiconSpan(Context context, int resourceId, int size) {
        super(ALIGN_BASELINE);
        mContext = context;
        mResourceId = resourceId;
        mSize = size;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
        //return super.getSize(paint, text, start, end, fm);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        //super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

    public Drawable getDrawable() {
        if (mDrawable == null) {
            try {
                mDrawable = mContext.getResources().getDrawable(mResourceId);
                int size = mSize;
                mDrawable.setBounds(0, 0, size, size);
            } catch (Exception e) {
                // swallow
            }
        }
        return mDrawable;
    }
}