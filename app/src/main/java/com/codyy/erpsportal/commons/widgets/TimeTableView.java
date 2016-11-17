package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.codyy.erpsportal.commons.models.entities.TimeTableContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 课程表的类
 * Created by kmdai on 2015/4/8.
 */
public class TimeTableView extends View {
    private String TAG = "TimeTableView";
    private String[] week = {"星期一", "星期二", "星期三", "星期四", "星期五"};
    private List<TimeTableContent> contents = null;
    private TimeTableListener mTimeTableListener = null;
    /**
     * 高宽
     */
    private int mWidth, mHeight;
    /**
     * 左边边距
     */
    private int marginLeft = 60;
    /**
     * 日期字体的高度
     */
    private int titleHeight = 100;

    /**
     * 课表竖向平均值
     */
    private float verticalAV = 0;
    /**
     * 水平平均值
     */
    private float horizontalAV = 0;
    /**
     * 高的一半
     */
    private float heightHalf = 0;
    /**
     * 画笔
     */
    private int dayOfWeek;
    private Paint mPaint;
    private Paint linepaint;
    private TextPaint textPaint;
    private TextPaint classTextPaint;

    /**
     * 点击时的行和列
     */
    private int downX, downY, upX, upY;
    private int clickX = 0, clickY = 0;
    private int textToTop = 0;
    /**
     * 标题（星期几）字体大小
     */
    private int titleTextSize = 40;

    private List<TimeTable> timeTables;

    public TimeTableView(Context context) {
        super(context);
        init(context);
    }

    public TimeTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void init(Context context) {
        timeTables = new ArrayList<>();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        float dens = getResources().getDisplayMetrics().density;
        titleHeight = (int) (40 * dens + 0.5);
        textToTop = (int) (dens * 3 + 0.5);
        if (dm.widthPixels >= 720 && dm.widthPixels < 1080) {
            titleTextSize = 30;
            marginLeft = 50;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize(40);
        mPaint.setTextAlign(Paint.Align.CENTER);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        linepaint = new Paint();
        linepaint.setStyle(Paint.Style.FILL);
        linepaint.setStrokeWidth(1);
        linepaint.setColor(Color.parseColor("#e9e9e9"));

        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(titleTextSize);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#707070"));

        classTextPaint = new TextPaint();
        classTextPaint.setStyle(Paint.Style.FILL);
        classTextPaint.setTextSize(titleTextSize);
        classTextPaint.setAntiAlias(true);
        classTextPaint.setTextAlign(Paint.Align.CENTER);
        classTextPaint.setColor(Color.parseColor("#333333"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTable(canvas);
    }

    /**
     * 画表格
     *
     * @param canvas
     */
    private void drawTable(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        if (mWidth != 0 && mHeight != 0) {
            horizontalAV = (mWidth - marginLeft) / 5;
            verticalAV = (mHeight - titleHeight) / 8;
            heightHalf = (mHeight - titleHeight) / 2;
            Rect rect = new Rect(0, 0, mWidth, titleHeight);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#FFD6D6D6"));
            canvas.drawRect(rect, mPaint);
            if (dayOfWeek > 1 && dayOfWeek <= 6) {
                RectF rectf = new RectF(marginLeft + (dayOfWeek - 2) * horizontalAV, titleHeight, marginLeft + (dayOfWeek - 1) * horizontalAV, mHeight);
                mPaint.setColor(Color.parseColor("#f4faf4"));
                canvas.drawRect(rectf, mPaint);
            }

            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(titleTextSize);
            for (int i = 0; i < 5; i++) {
                StaticLayout mStaticLayout = new StaticLayout(week[i], textPaint, (int) horizontalAV, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
                int h = mStaticLayout.getHeight();
                int s = (titleHeight - h) / 2;
                canvas.translate(marginLeft + i * horizontalAV + horizontalAV / 2, s);
                mStaticLayout.draw(canvas);
                canvas.translate(-(marginLeft + i * horizontalAV + horizontalAV / 2), -s);
            }

            mPaint.setColor(Color.parseColor("#fb4747"));
            canvas.drawLine(0, (heightHalf + titleHeight), mWidth, (heightHalf + titleHeight), mPaint);
            textPaint.setTextSize(marginLeft / 2);
            canvas.drawText("上午", marginLeft / 2, heightHalf + titleHeight - textToTop, textPaint);
            canvas.drawText("下午", marginLeft / 2, heightHalf + titleHeight + marginLeft / 2, textPaint);
            canvas.drawLine(marginLeft, titleHeight, marginLeft, mHeight, linepaint);
            for (int i = 1; i <= 6; i++) {
                canvas.drawLine(marginLeft + horizontalAV * i, titleHeight, marginLeft + horizontalAV * i, mHeight, linepaint);
            }
            for (int i = 1; i <= 6; i++) {
                if (i <= 3) {
                    canvas.drawLine(marginLeft, titleHeight + verticalAV * i, mWidth, titleHeight + verticalAV * i, linepaint);
                } else {
                    canvas.drawLine(marginLeft, heightHalf + titleHeight + verticalAV * (i - 3), mWidth, heightHalf + titleHeight + verticalAV * (i - 3), linepaint);
                }
            }
            drawClick(canvas);
            if (contents != null) {
                if (contents.size() > 0) {
                    drawTimeTable(canvas);
                }
            }
        }
    }

    /**
     * 画课程表
     *
     * @param canvas
     */
    private void drawTimeTable(Canvas canvas) {
        for (TimeTable timeTable : timeTables) {
            String a = timeTable.getClassName().toString();
            if (a.length() > 7) {
                a = a.substring(0, 7) + "...";
            }
            StaticLayout mStaticLayout = new StaticLayout(a, classTextPaint, (int) horizontalAV, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
            int h = mStaticLayout.getHeight();
            int s = 0;
            if (h < verticalAV) {
                s = (int) (verticalAV - h) / 2;
            }
            canvas.translate(marginLeft + (timeTable.getDay() - 1) * horizontalAV + horizontalAV / 2, titleHeight + (timeTable.getNumber() - 1) * verticalAV + s);
            mStaticLayout.draw(canvas);
            canvas.translate(-(marginLeft + (timeTable.getDay() - 1) * horizontalAV + horizontalAV / 2), -(titleHeight + (timeTable.getNumber() - 1) * verticalAV + s));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        invalidate();
    }

    /**
     * @param contents
     */
    public void setClass(List<TimeTableContent> contents) {
        this.contents = contents;
        timeTables.clear();
        for (int i = 0; i < contents.size(); i++) {
            boolean flag = false;
            if (timeTables.size() == 0) {
                TimeTable timeTable = new TimeTable();
                timeTable.setDay(contents.get(i).getDaySeq());
                timeTable.setNumber(contents.get(i).getClassSeq());
                timeTable.setClassName(contents.get(i).getSubjectName());
                timeTables.add(timeTable);
            } else {
                for (TimeTable timeTable : timeTables) {
                    if (timeTable.getDay() == contents.get(i).getDaySeq() && timeTable.getNumber() == contents.get(i).getClassSeq()) {
                        timeTable.setClassName(timeTable.getClassName() + "\n" + contents.get(i).getSubjectName());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TimeTable timeTable = new TimeTable();
                    timeTable.setDay(contents.get(i).getDaySeq());
                    timeTable.setNumber(contents.get(i).getClassSeq());
                    timeTable.setClassName(contents.get(i).getSubjectName());
                    timeTables.add(timeTable);
                }
            }
        }
        invalidate();
    }

    /**
     * 返回课表信息
     *
     * @return
     */
    public List<TimeTableContent> getContents() {
        return this.contents;
    }

    /**
     * 设置点击监听
     *
     * @param timeTableListener
     */
    public void setTimeTableListener(TimeTableListener timeTableListener) {
        mTimeTableListener = timeTableListener;
    }


    /**
     * 点击事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (x > marginLeft && y > titleHeight) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float down_x = event.getX();
                    float down_y = event.getY();
                    downX = (int) Math.floor((down_x - marginLeft) / horizontalAV) + 1;
                    downY = (int) Math.floor((down_y - titleHeight) / verticalAV) + 1;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    float up_x = event.getX();
                    float up_y = event.getY();
                    upX = (int) Math.floor((up_x - marginLeft) / horizontalAV) + 1;
                    upY = (int) Math.floor((up_y - titleHeight) / verticalAV) + 1;
                    if (downX == upX && downY == upY) {
                        if (mTimeTableListener != null) {
                            float[] a = {marginLeft + horizontalAV * (downX - 1), titleHeight + verticalAV * (downY), horizontalAV, verticalAV};
                            mTimeTableListener.onTimeTableClick(upX, upY, a);
                        }
                    }
                    downX = downY = upY = upX = 0;
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    downX = downY = upY = upX = 0;
                    invalidate();
                    break;
            }
        } else {
            downX = downY = upY = upX = 0;
            invalidate();
        }
        return true;
    }

    /**
     * 点击效果
     *
     * @param canvas
     */
    private void drawClick(Canvas canvas) {
        if (downX > 0 && downY > 0) {
            RectF rectF = new RectF(marginLeft + horizontalAV * (downX - 1), titleHeight + verticalAV * (downY - 1), marginLeft + horizontalAV * downX, titleHeight + verticalAV * downY);
            mPaint.setColor(Color.parseColor("#FF19AA20"));
            canvas.drawRect(rectF, mPaint);
        }
    }

    /**
     * 点击课表监听
     */
    public interface TimeTableListener {
        void onTimeTableClick(int day, int number, float[] size);
    }

    public class TimeTable {
        int day, number;
        String className;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}
