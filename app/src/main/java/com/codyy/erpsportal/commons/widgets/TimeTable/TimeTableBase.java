package com.codyy.erpsportal.commons.widgets.TimeTable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.TimeTableContent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * create by kmdai 17-2-8
 */
public class TimeTableBase extends View {
    /**
     * 绿色
     */
    public int mGreenColor = Color.parseColor("#19AB20");
    /**
     * 线的颜色
     */
    private int mLineColor = Color.parseColor("#e9e9e9");
    /**
     * 点击效果背景
     */
    private int mSelectColor = Color.parseColor("#FF19AA20");
    /**
     * “今天”标示背景
     */
    private int mTodayBGColor = Color.parseColor("#f4faf4");
    /**
     * 上午下午显示的颜色
     */
    private int mAmPmColor = Color.parseColor("#999999");
    /**
     * 学科颜色
     */
    private int mSubjectColor = Color.parseColor("#444444");
    /**
     * 上午几节课
     */
    private int mAMRow;
    /**
     * 下午几节课
     */
    private int mPMRow;
    /**
     * 每星期宽度
     */
    private int mColumnSize;
    /**
     * 每节课视图高度
     */
    private int mRowSize;

    private int mStartWith;
    private Paint mLinePT;
    private TextPaint mSubjectPT;
    private TextPaint mAmPmPT;
    /**
     * 高宽
     */
    private int mWidth, mHeight;

    private int mPressX;
    private int mPressY;
    private int mToday;
    private int mSubjectTestSize;
    private int mAmPmTextSize;
    /**
     * 水平平均值
     */
    private int mHorizontalAV;
    /**
     * 课表竖向平均值
     */
    private int mVerticalAV;
    /**
     * 星期几开始这一周
     */
    private int mWeekStart;
    private boolean mIsShowToday;
    /**
     *
     */
    private List<TimeTableView2.TimeTable> mTimeTables;
    private List<TimeTableContent> mTimeTableContents = null;

    public TimeTableBase(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TimeTableBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TimeTableBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeTableBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private TimeTableView2.TimeTableListener mTimeTableListener;

    /**
     * 返回列表宽度
     *
     * @return
     */
    public int getColumnSize() {
        return mColumnSize;
    }

    public int getRowSize() {
        return mRowSize;
    }

    public int getWeekStart() {
        return mWeekStart;
    }

    public int getToday() {
        return mToday;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeTableView2, defStyleAttr, defStyleRes);
            mAMRow = a.getInt(R.styleable.TimeTableView2_AMRow, 4);
            mPMRow = a.getInt(R.styleable.TimeTableView2_PMRow, 4);
            mColumnSize = a.getDimensionPixelSize(R.styleable.TimeTableView2_columnSize, 0);
            mStartWith = a.getInt(R.styleable.TimeTableView2_startWith, 0);
            mRowSize = a.getDimensionPixelSize(R.styleable.TimeTableView2_rowSize, 0);
            mSubjectTestSize = a.getDimensionPixelSize(R.styleable.TimeTableView2_subjectTextSize, 15);
            mAmPmTextSize = a.getDimensionPixelSize(R.styleable.TimeTableView2_noon, 10);
            if (mRowSize > 0) {
                setMinimumHeight(mRowSize * (mAMRow + mPMRow));
            }
            if (mColumnSize > 0) {
                setMinimumWidth(mColumnSize * 7);
            }
            mWeekStart = a.getInt(R.styleable.TimeTableView2_startWith, 0);
            a.recycle();
        }
        mPressX = mPressY = -1;//初始化点击位置
        mLinePT = new Paint();
        mLinePT.setStyle(Paint.Style.FILL);
        mLinePT.setStrokeWidth(1);
        mLinePT.setColor(mLineColor);

        mAmPmPT = new TextPaint();
        mAmPmPT.setAntiAlias(true);
        mAmPmPT.setStyle(Paint.Style.FILL);
        mAmPmPT.setStrokeWidth(1);
        mAmPmPT.setTextSize(mAmPmTextSize);
        mAmPmPT.setTextAlign(Paint.Align.CENTER);
        mAmPmPT.setColor(mAmPmColor);

        mSubjectPT = new TextPaint();
        mSubjectPT.setStyle(Paint.Style.FILL);
        mSubjectPT.setTextSize(mSubjectTestSize);
        mSubjectPT.setAntiAlias(true);
        mSubjectPT.setTextAlign(Paint.Align.CENTER);
        mSubjectPT.setColor(mSubjectColor);
        mIsShowToday = true;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int dayOfWeekTure;
        if (c.getFirstDayOfWeek() == Calendar.SUNDAY) {
            dayOfWeekTure = c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : c.get(Calendar.DAY_OF_WEEK) - 1;
        } else {
            dayOfWeekTure = c.get(Calendar.DAY_OF_WEEK);
        }
        if (mWeekStart == 0) {
            mToday = dayOfWeekTure % 7;
        } else {
            mToday = dayOfWeekTure - 1;
        }
        mTimeTables = new ArrayList<>();
        if (isInEditMode()) {
            List<TimeTableContent> timeTableContents = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                TimeTableContent timeTableContent = new TimeTableContent();
                timeTableContent.setClassSeq(i);
                timeTableContent.setDaySeq(i);
                timeTableContent.setSubjectName("测试测试测试测试");
                timeTableContents.add(timeTableContent);
                setClass(timeTableContents);
            }
        }
    }

    /**
     * 设置上午、下午节次
     *
     * @param amRow
     * @param pmRow
     */
    public void setClassCount(int amRow, int pmRow) {
        if (amRow < 4 || mPMRow < 4) {
            return;
        }
        mAMRow = amRow;
        mPMRow = pmRow;
        setMinimumHeight(mRowSize * (mAMRow + mPMRow));
        invalidate();
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mToday >= 0 && mIsShowToday) {
            mLinePT.setColor(mTodayBGColor);
            canvas.drawRect(mColumnSize * mToday, 0, mColumnSize * mToday + mColumnSize, mHeight, mLinePT);
        }
        if (mPressX >= 0 && mPressY >= 0) {
            mLinePT.setColor(mSelectColor);
            canvas.drawRect(mColumnSize * mPressX, mRowSize * mPressY, mColumnSize * mPressX + mColumnSize, mRowSize * mPressY + mRowSize, mLinePT);
        }
        //画网格
        mLinePT.setColor(mLineColor);
        for (int i = 0; i < 7; i++) {
            canvas.drawLine(mColumnSize * i, 0, mColumnSize * i, mHeight, mLinePT);
        }
        for (int i = 0; i < mAMRow; i++) {
            canvas.drawLine(0, mRowSize * i, mWidth, mRowSize * i, mLinePT);
        }
        canvas.drawLine(0, mRowSize * mAMRow, mWidth, mRowSize * mAMRow, mAmPmPT);
        for (int i = mAMRow; i < (mAMRow + mPMRow); i++) {
            canvas.drawLine(0, mRowSize * i, mWidth, mRowSize * i, mLinePT);
        }
        //上下午分割线
        canvas.drawText("上午", mAmPmTextSize, (mRowSize * mAMRow) - 10, mAmPmPT);
        canvas.drawText("下午", mAmPmTextSize, (mRowSize * mAMRow) + mAmPmTextSize, mAmPmPT);
        canvas.save();
        //画课表学科
        if (mTimeTables != null) {
            if (mTimeTables.size() > 0) {
                drawSubject(canvas);
            }
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressX = (int) (x / mColumnSize);
                mPressY = (int) (y / mRowSize);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int px;
                int py;
                px = (int) (x / mColumnSize);
                py = (int) (y / mRowSize);
                if (mPressX == px && mPressY == py) {//判断是否点击某一节课
                    if (mTimeTableListener != null) {
                        mTimeTableListener.onTimeTableClick(mPressX + 1, mPressY + 1, null);
                    }
                }
            case MotionEvent.ACTION_CANCEL://点击结束或事件取消
                mPressY = -1;
                mPressY = -1;
                invalidate();
                break;
        }
        return true;
    }

    private void drawSubject(Canvas canvas) {
        for (TimeTableView2.TimeTable timeTable : mTimeTables) {
            String a = timeTable.getClassName();
            if (a.length() > 7) {
                a = a.substring(0, 7) + "...";
            }
            int day;
            if (mWeekStart == 0) {
                day = timeTable.daySeq % 7 + 1;
            } else {
                day = timeTable.getDaySeq();
            }
            if (day == (mPressX + 1) && (mPressY + 1) == timeTable.classSeq) {
                mSubjectPT.setColor(Color.WHITE);
            } else if (timeTable.processing) {
                mSubjectPT.setColor(mGreenColor);
            } else {
                mSubjectPT.setColor(mSubjectColor);
            }
            StaticLayout staticLayout = new StaticLayout(a, mSubjectPT, mColumnSize - 20, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
            int h = staticLayout.getHeight();
            int s = 0;
            if (h < mRowSize) {
                s = (mRowSize - h) / 2;
            }
            canvas.translate((day - 1) * mColumnSize + mColumnSize / 2, (timeTable.classSeq - 1) * mRowSize + s);
            staticLayout.draw(canvas);
            canvas.translate(-((day - 1) * mColumnSize + mColumnSize / 2), -((timeTable.classSeq - 1) * mRowSize + s));
        }
    }

    /**
     * @param mTimeTableContents
     */
    public void setClass(List<TimeTableContent> mTimeTableContents) {
        this.mTimeTableContents = mTimeTableContents;
        mTimeTables.clear();
        for (TimeTableContent timeTableContent : mTimeTableContents) {
            boolean flag = false;
            if (mTimeTables.size() == 0) {
                TimeTableView2.TimeTable timeTable = new TimeTableView2.TimeTable();
                timeTable.setDaySeq(timeTableContent.getDaySeq());
                timeTable.setClassSeq(timeTableContent.getClassSeq());
                timeTable.setClassName(timeTableContent.getSubjectName());
                mTimeTables.add(timeTable);
            } else {
                for (TimeTableView2.TimeTable timeTable : mTimeTables) {
                    if (timeTable.getDaySeq() == (timeTableContent.getDaySeq()) && timeTable.getClassSeq() == timeTableContent.getClassSeq()) {
                        timeTable.setClassName(timeTable.getClassName() + "\n" + timeTableContent.getSubjectName());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TimeTableView2.TimeTable timeTable = new TimeTableView2.TimeTable();
                    timeTable.setDaySeq(timeTableContent.getDaySeq());
                    timeTable.setClassSeq(timeTableContent.getClassSeq());
                    timeTable.setClassName(timeTableContent.getSubjectName());
                    mTimeTables.add(timeTable);
                }
            }
        }
        invalidate();
    }

    public void setTimeTableListener(TimeTableView2.TimeTableListener timeTableListener) {
        mTimeTableListener = timeTableListener;
    }

    public void setIsShowToday(boolean isShowToday) {
        mIsShowToday = isShowToday;
        invalidate();
    }

    public void setTimeTable(List<TimeTableView2.TimeTable> timeTables) {
        this.mTimeTables = timeTables;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0 && w >= oldw && h >= oldh) {
            mWidth = w;
            mHeight = h;
            mHorizontalAV = w / 7;
            mVerticalAV = h / (mAMRow + mPMRow);
            if (mVerticalAV > mRowSize) {
                mRowSize = mVerticalAV;
                setMinimumHeight(h);
            }
            if (mHorizontalAV > mColumnSize) {
                mColumnSize = mHorizontalAV;
                setMinimumWidth(w);
            }
            invalidate();
        }
    }
}
