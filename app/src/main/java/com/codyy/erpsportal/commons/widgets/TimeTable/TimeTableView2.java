package com.codyy.erpsportal.commons.widgets.TimeTable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
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
 * Created by kmdai on 2015/9/22.
 */
public class TimeTableView2 extends View {
    private String TAG = "TimeTableView";
    private final static String[] WEEK_STARTWITH_MONDAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private final static String[] WEEK_STARTWITH_SUNDAY = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private List<Holiday> mWeekDate;
    private String[] mWeek;
    private List<TimeTableContent> mTimeTableContents = null;
    private TimeTableListener mTimeTableListener = null;
    /**
     * 上午下午显示的颜色
     */
    private int mAmPmColor = Color.parseColor("#999999");
    /**
     * 标题颜色
     */
    private int mTitleColor = Color.parseColor("#707070");
    /**
     * 绿色
     */
    private int mGreenColor = Color.parseColor("#19AB20");
    /**
     * 学科颜色
     */
    private int mSubjectColor = Color.parseColor("#444444");
    /**
     * 线的颜色
     */
    private int mLineColor = Color.parseColor("#e9e9e9");
    /**
     * “今天”标示背景
     */
    private int mTodayBGColor = Color.parseColor("#f4faf4");
    /**
     * 标题背景颜色
     */
    private int mTitleBGColor = Color.parseColor("#e6e6e6");
    /**
     * 点击效果背景
     */
    private int mSelectColor = Color.parseColor("#FF19AA20");
    /**
     * 高宽
     */
    private int mWidth, mHeight;
    /**
     * 标题高度(px)
     */
    private final static int TITLE_H = 50;
    /**
     * 日期字体的高度
     */
    private int mTitleHeight = 100;

    /**
     * 课表竖向平均值
     */
    private float mVerticalAV = 0;
    /**
     * 水平平均值
     */
    private float mHorizontalAV = 0;
    /**
     * 高的一半
     */
    private float mHeightHalf = 0;
    private int mAmPmTextSize = 10;
    private int mSubjectTestSize;
    private Paint mPaint;
    /**
     * 今天在在view中的位置(0~6)
     */
    private int mDayOfWeek;
    /**
     * 今天是星期几(1~7)
     */
    private int mDayOfWeekTure;
    private Paint mTodayBGPT;
    private TextPaint mGreenTXPT;
    private TextPaint mSubjectPT;
    private TextPaint mAmPmPT;
    private TextPaint mTitlePT;
    private Paint mLinePT;
    private Point mSelectPoint;
    private int mAmPmLengh = 13;
    private int mAmTop = 2;
    private boolean mIsHorizontalLock = false;
    private String mToday;
    /**
     * 是否标记今天
     */
    private boolean mIsShowToday;
    /**
     * 点击时的行和列x:(1~7),y:(1~8)
     */
    private int mDownX, mDownY, mUpX, mUpY;
    /**
     * 标题（星期几）字体大小
     */
    private int mTitleTextSize;
    /**
     * 星期几开始这一周
     */
    private int mWeekStart;
    /**
     *
     */
    private List<TimeTable> mTimeTables;
    private Bitmap mHolidayIcon;

    /**
     * 点击的框
     */
    private RectF mClickRect;
    /**
     * 星期背景框
     */
    private Rect mWeekRect;

    /**
     * 标示今天的框
     */
    private RectF mTodayRect;

    public TimeTableView2(Context context) {
        super(context);
        init(context, null);
    }

    public TimeTableView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TimeTableView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void init(Context context, AttributeSet attrs) {
        boolean isShowDate;
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TimeTableView2);
            mTitleTextSize = attributes.getInteger(R.styleable.TimeTableView2_titleSize, 13);
            mHorizontalAV = attributes.getDimensionPixelSize(R.styleable.TimeTableView2_columnSize, 0);
            mSubjectTestSize = attributes.getDimensionPixelSize(R.styleable.TimeTableView2_subjectTextSize, 50);
            mWeekStart = attributes.getInteger(R.styleable.TimeTableView2_startWith, 0);
            isShowDate = attributes.getBoolean(R.styleable.TimeTableView2_isShowDate, false);
            if (mHorizontalAV > 0) {
                mIsHorizontalLock = true;
                this.setMinimumWidth((int) (mHorizontalAV * 7));
            }
            if (isShowDate) {
                getCurrentWeekDate();
            }
            attributes.recycle();
        }
        mIsShowToday = true;
        mHolidayIcon = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.holiday_icon)).getBitmap();
        mSelectPoint = new Point();
        mTimeTables = new ArrayList<>();
        mTitleHeight = dip2px(context, TITLE_H);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize(mTitleTextSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mGreenTXPT = new TextPaint();
        mGreenTXPT.setAntiAlias(true);
        mGreenTXPT.setStyle(Paint.Style.FILL);
        mGreenTXPT.setStrokeWidth(1);
        mGreenTXPT.setTextSize(mTitleTextSize);
        mGreenTXPT.setTextAlign(Paint.Align.CENTER);
        mGreenTXPT.setColor(mGreenColor);

        mAmPmPT = new TextPaint();
        mAmPmPT.setAntiAlias(true);
        mAmPmPT.setStyle(Paint.Style.FILL);
        mAmPmPT.setStrokeWidth(1);
        mAmPmTextSize = dip2px(context, mAmPmTextSize);
        mAmPmPT.setTextSize(mAmPmTextSize);
        mAmPmPT.setTextAlign(Paint.Align.CENTER);
        mAmPmPT.setColor(mAmPmColor);
        mAmPmLengh = dip2px(context, mAmPmLengh);
        mAmTop = dip2px(context, mAmTop);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat df = new SimpleDateFormat("MM-dd");
        mToday = df.format(c.getTime());
        if (c.getFirstDayOfWeek() == Calendar.SUNDAY) {
            mDayOfWeekTure = c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? 7 : c.get(Calendar.DAY_OF_WEEK) - 1;
        } else {
            mDayOfWeekTure = c.get(Calendar.DAY_OF_WEEK);
        }
        if (mWeekStart == 0) {
            mWeek = WEEK_STARTWITH_SUNDAY;
            mDayOfWeek = mDayOfWeekTure % 7;
        } else {
            mWeek = WEEK_STARTWITH_MONDAY;
            mDayOfWeek = mDayOfWeekTure - 1;
        }

        mLinePT = new Paint();
        mLinePT.setStyle(Paint.Style.FILL);
        mLinePT.setStrokeWidth(1);
        mLinePT.setColor(mLineColor);

        mTitlePT = new TextPaint();
        mTitlePT.setStyle(Paint.Style.FILL);
        mTitlePT.setAntiAlias(true);
        mTitlePT.setTextAlign(Paint.Align.CENTER);
        mTitlePT.setTextSize(dip2px(context, mTitleTextSize));
        mTitlePT.setColor(mTitleColor);

        mSubjectPT = new TextPaint();
        mSubjectPT.setStyle(Paint.Style.FILL);
        mSubjectPT.setTextSize(mSubjectTestSize);
        mSubjectPT.setAntiAlias(true);
        mSubjectPT.setTextAlign(Paint.Align.CENTER);
        mSubjectPT.setColor(mSubjectColor);
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
//        canvas.drawColor(Color.WHITE);
        if (mHorizontalAV > 0 && mVerticalAV > 0) {
            if (mIsShowToday) {
                //标示今天
                mPaint.setColor(mTodayBGColor);
                canvas.drawRect(mTodayRect, mPaint);
            }
            //点击效果
            drawClick(canvas);
            //标题背景

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTitleBGColor);
            canvas.drawRect(mWeekRect, mPaint);

            //上下午分割线
            mPaint.setColor(mTitleColor);
            canvas.drawText("上午", mAmPmLengh, (mHeightHalf + mTitleHeight) - mAmTop, mAmPmPT);
            canvas.drawText("下午", mAmPmLengh, (mHeightHalf + mTitleHeight) + mAmPmTextSize, mAmPmPT);
            //画网格
            for (int i = 0; i <= (mWeek.length + 1); i++) {
                canvas.drawLine(mHorizontalAV * i, 0, mHorizontalAV * i, mHeight, mLinePT);
            }
            for (int i = 1; i < 8; i++) {
                if (i == 4) {
                    canvas.drawLine(0, mTitleHeight + mVerticalAV * i, mWidth, mTitleHeight + mVerticalAV * i, mAmPmPT);
                } else {
                    canvas.drawLine(0, mTitleHeight + mVerticalAV * i, mWidth, mTitleHeight + mVerticalAV * i, mLinePT);
                }
            }
            //标题
            for (int i = 0; i < mWeek.length; i++) {
                if (i == mDayOfWeek && mIsShowToday) {
                    mTitlePT.setColor(mGreenColor);
                } else {
                    mTitlePT.setColor(mTitleColor);
                }
                float dx = i * mHorizontalAV + mHorizontalAV / 2;
                String week;
                if (mWeekDate != null && mWeekDate.size() >= mWeek.length) {
                    week = mWeek[i] + "\n" + mWeekDate.get(i).getmDate();
                    if (mWeekDate.get(i).isHoliday()) {
                        canvas.drawBitmap(mHolidayIcon, (i + 1) * mHorizontalAV - mHolidayIcon.getWidth(), 0, mPaint);
                    }
                } else {
                    week = mWeek[i];
                }
                StaticLayout staticLayout = new StaticLayout(week, mTitlePT, (int) mHorizontalAV, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
                int h = staticLayout.getHeight();
                int dy = (mTitleHeight - h) / 2;
//                int dy = 0;
                canvas.translate(dx, dy);
                staticLayout.draw(canvas);
                canvas.translate(-(dx), -dy);
            }
            canvas.save();
            //画课表学科
            if (mTimeTables != null) {
                if (mTimeTables.size() > 0) {
                    drawTimeTable(canvas);
                }
            }
            canvas.restore();
        }
    }

    /**
     * 画课程表
     *
     * @param canvas
     */
    private void drawTimeTable(Canvas canvas) {
        for (TimeTable timeTable : mTimeTables) {
            String a = timeTable.getClassName().toString();
            if (a.length() > 7) {
                a = a.substring(0, 7) + "...";
            }
            int day;
            if (mWeekStart == 0) {
                day = timeTable.daySeq % 7 + 1;
            } else {
                day = timeTable.getDaySeq();
            }
            if (day == mDownX && mDownY == timeTable.classSeq) {
                mSubjectPT.setColor(Color.WHITE);
            } else if (timeTable.processing) {
                mSubjectPT.setColor(mGreenColor);
            } else {
                mSubjectPT.setColor(mSubjectColor);
            }
            StaticLayout staticLayout = new StaticLayout(a, mSubjectPT, (int) mHorizontalAV, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0, false);
            int h = staticLayout.getHeight();
            int s = 0;
            if (h < mVerticalAV) {
                s = (int) (mVerticalAV - h) / 2;
            }
            canvas.translate((day - 1) * mHorizontalAV + mHorizontalAV / 2, mTitleHeight + (timeTable.classSeq - 1) * mVerticalAV + s);
            staticLayout.draw(canvas);
            canvas.translate(-((day - 1) * mHorizontalAV + mHorizontalAV / 2), -(mTitleHeight + (timeTable.classSeq - 1) * mVerticalAV + s));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mVerticalAV = (mHeight - mTitleHeight) / (float) 8;
        mHeightHalf = mVerticalAV * 4;
        if (!mIsHorizontalLock) {
            mHorizontalAV = mWidth / (float) 7;
        }
        mTodayRect = new RectF(mDayOfWeek * mHorizontalAV, mTitleHeight, (mDayOfWeek + 1) * mHorizontalAV, mHeight);
        mWeekRect = new Rect(0, 0, mWidth, mTitleHeight);
        mClickRect = new RectF();
        invalidate();
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
                TimeTable timeTable = new TimeTable();
                timeTable.setDaySeq(timeTableContent.getDaySeq());
                timeTable.setClassSeq(timeTableContent.getClassSeq());
                timeTable.setClassName(timeTableContent.getSubjectName());
                mTimeTables.add(timeTable);
            } else {
                for (TimeTable timeTable : mTimeTables) {
                    if (timeTable.getDaySeq() == (timeTableContent.getDaySeq()) && timeTable.getClassSeq() == timeTableContent.getClassSeq()) {
                        timeTable.setClassName(timeTable.getClassName() + "\n" + timeTableContent.getSubjectName());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TimeTable timeTable = new TimeTable();
                    timeTable.setDaySeq(timeTableContent.getDaySeq());
                    timeTable.setClassSeq(timeTableContent.getClassSeq());
                    timeTable.setClassName(timeTableContent.getSubjectName());
                    mTimeTables.add(timeTable);
                }
            }
        }
        invalidate();
    }

    public void setTimeTable(List<TimeTable> timeTables) {
        this.mTimeTables = timeTables;
        invalidate();
    }

    public void setWeekDate(List<Holiday> week) {
        mWeekDate = week;
        boolean flag = false;
        for (Holiday holiday : week) {
            if (mToday.equals(holiday.getmDate())) {
                mIsShowToday = true;
                flag = true;
                break;
            }
        }
        if (!flag) {
            mIsShowToday = false;
        }
        invalidate();
    }

    /**
     * 返回课表信息
     *
     * @return
     */
    public List<TimeTableContent> getContents() {
        return this.mTimeTableContents;
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
        if (x > 0 && y > mTitleHeight) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float down_x = event.getX();
                    float down_y = event.getY();
                    mDownX = (int) Math.ceil(down_x / mHorizontalAV);
                    mDownY = (int) Math.ceil((down_y - mTitleHeight) / mVerticalAV);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    float up_x = event.getX();
                    float up_y = event.getY();
                    mUpX = (int) Math.ceil((up_x) / mHorizontalAV);
                    mUpY = (int) Math.ceil((up_y - mTitleHeight) / mVerticalAV);
                    if (mDownX == mUpX && mDownY == mUpY) {
                        if (mTimeTableListener != null) {
                            float[] a = {mHorizontalAV * (mDownX - 1), mTitleHeight + mVerticalAV * (mDownY), mHorizontalAV, mVerticalAV};
                            mTimeTableListener.onTimeTableClick(mUpX, mUpY, a);
                        }
                    }
                    mDownX = mDownY = mUpY = mUpX = -1;
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mDownX = mDownY = mUpY = mUpX = -1;
                    invalidate();
                    break;
            }
        } else {
            mDownX = mDownY = mUpY = mUpX = 0;
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
        if (mDownX > 0 && mDownY > 0) {
            mClickRect.set(mHorizontalAV * (mDownX - 1), mTitleHeight + mVerticalAV * (mDownY - 1), mHorizontalAV * mDownX, mTitleHeight + mVerticalAV * mDownY);
            mPaint.setColor(mSelectColor);
            canvas.drawRect(mClickRect, mPaint);
        }
    }

    /**
     * 点击课表监听
     */
    public interface TimeTableListener {
        void onTimeTableClick(int day, int classSeq, float[] size);
    }

    public static class TimeTable implements Parcelable {
        int daySeq, classSeq;
        String className;
        int count;
        boolean processing;

        public boolean isProcessing() {
            return processing;
        }

        public void setProcessing(boolean processing) {
            this.processing = processing;
        }

        public int getDaySeq() {
            return daySeq;
        }

        public void setDaySeq(int daySeq) {
            this.daySeq = daySeq;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public int getClassSeq() {
            return classSeq;
        }

        public void setClassSeq(int classSeq) {
            this.classSeq = classSeq;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.daySeq);
            dest.writeInt(this.classSeq);
            dest.writeString(this.className);
            dest.writeInt(this.count);
            dest.writeByte(this.processing ? (byte) 1 : (byte) 0);
        }

        public TimeTable() {
        }

        protected TimeTable(Parcel in) {
            this.daySeq = in.readInt();
            this.classSeq = in.readInt();
            this.className = in.readString();
            this.count = in.readInt();
            this.processing = in.readByte() != 0;
        }

        public static final Parcelable.Creator<TimeTable> CREATOR = new Parcelable.Creator<TimeTable>() {
            @Override
            public TimeTable createFromParcel(Parcel source) {
                return new TimeTable(source);
            }

            @Override
            public TimeTable[] newArray(int size) {
                return new TimeTable[size];
            }
        };
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    /**
     * dp转化为 px
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static class Holiday implements Parcelable {
        String mDate;
        boolean mIsholiday;

        public boolean isHoliday() {
            return mIsholiday;
        }

        public void setIsholiday(boolean mIsholiday) {
            this.mIsholiday = mIsholiday;
        }

        public String getmDate() {
            return mDate;
        }

        public void setmDate(String mDate) {
            this.mDate = mDate;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mDate);
            dest.writeByte(this.mIsholiday ? (byte) 1 : (byte) 0);
        }

        public Holiday() {
        }

        protected Holiday(Parcel in) {
            this.mDate = in.readString();
            this.mIsholiday = in.readByte() != 0;
        }

        public static final Parcelable.Creator<Holiday> CREATOR = new Parcelable.Creator<Holiday>() {
            @Override
            public Holiday createFromParcel(Parcel source) {
                return new Holiday(source);
            }

            @Override
            public Holiday[] newArray(int size) {
                return new Holiday[size];
            }
        };
    }

    private void getCurrentWeekDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat.getDateInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd");
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        //星期开始是周日，所以当今天是周日时，日期要减7天
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        mWeekDate = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            Holiday holiday = new Holiday();
            holiday.setmDate(df.format(calendar.getTime()));
            mWeekDate.add(holiday);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
    }
}
