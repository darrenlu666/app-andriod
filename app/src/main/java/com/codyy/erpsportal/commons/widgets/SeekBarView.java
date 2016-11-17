package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.codyy.erpsportal.R;


/**
 * Created by kmdai on 2015/4/11.
 */
public class SeekBarView extends View implements Handler.Callback {
    private int mWidth, mHeight;
    private int barWidth = 0;
    private int barHeight = 10;
    private float den = 0;
    private int startX, startY, endX, endY;
    /**
     * 画笔
     */
    private Paint mPaint;
    private Paint mPaintBar;
    private RectF rectF;
    private int radius = 40;
    private int marginLeft = 10;

    private float max = 5;
    private int score;
    private float horizontalAV;
    private boolean isMove = false;
    private int moveStartX, moveStartY, moveEndX, moveEndY;

    private TextPaint textPaint, textPaint1;
    private int strokeWidth = 20;
    private int textPadding = 30;
    private int textTop = 60;

    private Handler handler;

    private boolean isTouch = true;
    private OnSeekListener onSeekListener;

    public SeekBarView(Context context) {
        super(context);
        init(context, null);
    }

    public SeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int textw = 0;

    /**
     * view初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        den = getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SeekBarView);
            marginLeft = array.getInteger(R.styleable.SeekBarView_margin_left, 50);
            textPadding = array.getInteger(R.styleable.SeekBarView_textPadding, 30);
            radius = array.getInteger(R.styleable.SeekBarView_circle_radius, 40);
            strokeWidth = array.getInteger(R.styleable.SeekBarView_stroke_width, 20);
            barHeight = array.getInteger(R.styleable.SeekBarView_bg_height, 20);
            textTop = array.getInteger(R.styleable.SeekBarView_text_top, 40);
        }
        handler = new Handler(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);

        mPaintBar = new Paint();
        mPaintBar.setAntiAlias(true);
        mPaintBar.setStrokeWidth(strokeWidth);
        mPaintBar.setColor(Color.parseColor("#19ab20"));
        mPaintBar.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);
        textPaint.setColor(Color.parseColor("#19ab20"));
        textPaint.setTextAlign(Paint.Align.CENTER);

        textPaint1 = new TextPaint();
        textPaint1.setStyle(Paint.Style.FILL);
        textPaint1.setAntiAlias(true);
        textPaint1.setTextSize(50);
        textPaint1.setColor(Color.parseColor("#707070"));
        textPaint1.setTextAlign(Paint.Align.CENTER);
        textw = (int) (marginLeft * den + 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawSeekBar(canvas);
    }

    /**
     * 画滑动条
     *
     * @param canvas
     */
    private void drawSeekBar(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#ffe8e8e8"));
        Rect rect = new Rect(startX, startY - barHeight / 2, endX, endY + barHeight / 2);
        canvas.drawRect(rect, mPaint);


        RectF rectF2 = new RectF(endX - 10, startY - barHeight / 2, endX + 10, endY + barHeight / 2);
        canvas.drawArc(rectF2, 270, 180, false, mPaint);


        mPaint.setColor(Color.parseColor("#19ab20"));
        Rect rect1 = new Rect(startX, startY - barHeight / 2, (int) rectF.left, endY + barHeight / 2);
        canvas.drawRect(rect1, mPaint);

        RectF rectF1 = new RectF(startX - 10, startY - barHeight / 2, startX + 10, endY + barHeight / 2);
        canvas.drawArc(rectF1, 90, 180, false, mPaint);

        canvas.drawArc(rectF, 0, 360, false, mPaintBar);


        canvas.drawText(String.valueOf(score), rectF.left + radius, rectF.bottom + textTop, textPaint);
        StaticLayout staticLayout = new StaticLayout("0", textPaint1, textw, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
        int h = staticLayout.getHeight();
        canvas.translate(startX - textPadding, startY - h / 2);
        staticLayout.draw(canvas);
        canvas.translate(-(startX - textPadding), -(startY - h / 2));
        StaticLayout staticLayout1 = new StaticLayout(String.valueOf((int) max), textPaint1, textw, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
        int h1 = staticLayout.getHeight();
        canvas.translate(endX + textPadding, endY - h1 / 2);
        staticLayout1.draw(canvas);
        canvas.translate(-(endX + textPadding), -(endY - h1 / 2));
    }

    private boolean isTouchIn = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouch) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouchIn = rectF.contains(event.getX(), event.getY());
                    if (isTouchIn) {
                        isMove = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    moveStartX = (int) event.getX();
                    moveStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    isMove = false;
//                    if (event.getX() + radius <= endX && event.getX() - radius >= startX) {
//                        rectF.set(event.getX() - radius, rectF.top, event.getX() + radius, rectF.bottom);
//
//                    } else if (event.getX() + radius > endX) {
//                        rectF.set(endX - radius * 2, rectF.top, endX, rectF.bottom);
//                    } else if (event.getX() + radius < startX) {
//                        rectF.set(startX, rectF.top, startX + radius * 2, rectF.bottom);
//                    }
                    if (isTouchIn) {
                        calculateScore();
                        invalidate();
                        if (onSeekListener != null) {
                            onSeekListener.getScore(score);
                        }
                        isTouchIn = false;
                    }
//                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isTouchIn) {
                        calculateScore();
                        moveEndX = (int) event.getX();
                        moveEndY = (int) event.getY();
                        moveBar(moveEndX - moveStartX);
                        moveStartX = moveEndX;
                        moveStartY = moveEndY;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
//                    if (onSeekListener != null) {
//                        onSeekListener.getScore(score);
//                    }
//                    moveEndX = (int) event.getX();
//                    moveEndY = (int) event.getY();
//                    moveBar(moveEndX - moveStartX);
//                    moveStartX = moveEndX;
//                    moveStartY = moveEndY;
                    break;
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        barWidth = mWidth - (int) (den * marginLeft + 0.5) * 2;
        startX = (int) (den * marginLeft + 0.5);
        endY = startY = mHeight / 2;
        endX = mWidth - (int) (den * marginLeft + 0.5);
        float a = barWidth - 2 * radius;
        horizontalAV = a / (max + 1);
        float f = score * horizontalAV + startX;
        rectF = new RectF(f, startY - radius, f + 2 * radius, startY + radius);
        StaticLayout staticLayout1 = new StaticLayout(String.valueOf((int) max), textPaint1, textw, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
        int s = (int) (5 * den);
        int minih = staticLayout1.getHeight() + radius * 2 + textTop + s;
        this.setMinimumHeight(minih);
        invalidate();
    }

    /**
     * 设置最大值
     *
     * @param max
     */

    public void setMax(float max) {
        if (max != 0) {
            this.max = max;
            float a = barWidth - 2 * radius;
            horizontalAV = a / (max + 1);
            invalidate();
        }
    }

    public void setScore(int score) {
        if (score <= max && score >= 0) {
            this.score = score;
            float a = (score + 1) * horizontalAV + startX;
            if (rectF == null) {
                rectF = new RectF(a, startY - radius, a + 2 * radius, startY + radius);
            } else {
                rectF.set(a, startY - radius, a + 2 * radius, startY + radius);
            }
            invalidate();
        }
    }

    /**
     * 计算分数
     */
    private void calculateScore() {
        float middle = rectF.right - startX - radius * 2;
        if (startX != 0) {
            score = (int) Math.floor(middle / horizontalAV);
        } else {
            score = 0;
        }
        if (score >= max) {
            score = (int) max;
        }
        if (score <= 0) {
            score = 0;
        }
    }

    /**
     * 获得评分
     *
     * @return
     */
    public int getValus() {
        return score;
    }

    private void moveAuto() {
        isTouch = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 5;
                if (score > 0) {
                    float from = rectF.right;
                    float to = score * horizontalAV + marginLeft;
                    if (from > to) {
                        boolean flag = true;
                        while (flag) {
                            if (rectF.right + s < to) {
                                rectF.set(rectF.left + s, rectF.top, rectF.right + s, rectF.bottom);
                            } else {
                                rectF.set(to - radius, rectF.top, to + radius, rectF.bottom);
                                flag = false;
                            }
                            try {
                                Thread.sleep(100);
                                handler.sendEmptyMessage(0x001);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (from < to) {
                        boolean flag = true;
                        while (flag) {
                            if (rectF.right - s > to) {
                                rectF.set(rectF.left - s, rectF.top, rectF.right - s, rectF.bottom);
                            } else {
                                rectF.set(to - radius, rectF.top, to + radius, rectF.bottom);
                                flag = false;
                            }
                            try {
                                Thread.sleep(100);
                                handler.sendEmptyMessage(0x001);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                isTouch = true;
            }
        }).start();
    }

    private void moveBar(int dx) {
        if (isMove) {
            rectF.set(rectF.left + dx, rectF.top, rectF.right + dx, rectF.bottom);
            if (rectF.left >= startX && rectF.right <= endX) {
                invalidate();
            } else {
                rectF.set(rectF.left - dx, rectF.top, rectF.right - dx, rectF.bottom);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        invalidate();
        return false;
    }

    public void setOnSeekListener(OnSeekListener onSeekListener) {
        this.onSeekListener = onSeekListener;
    }

    public interface OnSeekListener {
        abstract void getScore(int score);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        float a = score * horizontalAV + startX;
        if (rectF == null) {
            rectF = new RectF(a, startY - radius, a + 2 * radius, startY + radius);
        } else {
            rectF.set(a, startY - radius, a + 2 * radius, startY + radius);
        }
        invalidate();
    }
}
