package com.codyy.erpsportal.commons.widgets.TimeTable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.TimeTableContent;
import com.codyy.erpsportal.commons.utils.Cog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * create by kmdai 17-2-8
 */
public class SuperTimeTableLayout extends FrameLayout {
    public final static String TAG = "SuperTimeTableLayout-----:";
    private final static String[] WEEK_STARTWITH_MONDAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private final static String[] WEEK_STARTWITH_SUNDAY = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private ScrollView mScrollView;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mTitleLayout;
    private TimeTableBase mTimeTableBase;
    private List<TimeTableView2.Holiday> mWeekDate;
    private String[] mWeek;
    private boolean mIsShowToday;
    private String mToday;
    /**
     * 星期几开始这一周
     */
    private int mWeekStart;
    private int mCurrentYear;
    private int mYear;
    private boolean mShowDate;

    public SuperTimeTableLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SuperTimeTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SuperTimeTableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SuperTimeTableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        int titleHeight = 0;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperTimeTableLayout);
            titleHeight = a.getDimensionPixelSize(R.styleable.SuperTimeTableLayout_titleHeight, 60);
            mShowDate = a.getBoolean(R.styleable.SuperTimeTableLayout_showDate, true);
            a.recycle();
        }
        mIsShowToday = true;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mTitleLayout = new LinearLayout(context);
        mTitleLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight));
        mTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(mTitleLayout);

        mTimeTableBase = new TimeTableBase(context, attrs);
        mTimeTableBase.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTimeTableBase.setBackgroundColor(Color.WHITE);
        mWeekStart = mTimeTableBase.getWeekStart();
        mScrollView = new ScrollView(context, attrs);
        mScrollView.setFillViewport(true);
        mScrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mScrollView.addView(mTimeTableBase);
        mHorizontalScrollView = new HorizontalScrollView(context, attrs);
        mHorizontalScrollView.setFillViewport(true);
        mHorizontalScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.addView(mScrollView);

        mHorizontalScrollView.addView(linearLayout);
        addView(mHorizontalScrollView);
        initDate();
        getCurrentWeekDate();
        for (int i = 0; i < 7; i++) {
            TitleLayout titleLayout = new TitleLayout(context, attrs);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            if (mToday.equals(mWeekDate.get(i).getmDate())) {
                titleLayout.setTextColor(Color.parseColor("#19AB20"));
            }
            String title = mWeek[i];
            if (mShowDate) {
                title += "\n" + mWeekDate.get(i).getmDate();
            }
            titleLayout.setText(title);
            titleLayout.setLayoutParams(layoutParams);
            titleLayout.setBackgroundColor(Color.parseColor("#e6e6e6"));
            mTitleLayout.addView(titleLayout);
        }
    }

    private void initDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = mCurrentYear = c.get(Calendar.YEAR);
        SimpleDateFormat df = new SimpleDateFormat("MM-dd");
        mToday = df.format(c.getTime());
        if (mWeekStart == 0) {
            mWeek = WEEK_STARTWITH_SUNDAY;
        } else {
            mWeek = WEEK_STARTWITH_MONDAY;
        }
    }

    class TitleLayout extends RelativeLayout {
        TextView mTextView;
        ImageView mImageView;
        int mWidth;
        int mheight;
        private Paint mLinePT;
        private ColorStateList mColorStateList;

        public TitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context, attrs);
        }

        public TitleLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        private void init(Context context, AttributeSet attrs) {
            this.setWillNotDraw(false);
            mLinePT = new Paint();
            mLinePT.setStyle(Paint.Style.FILL);
            mLinePT.setStrokeWidth(1);
            mLinePT.setColor(Color.parseColor("#e9e9e9"));
            mTextView = new TextView(context, attrs);
            mTextView.setGravity(Gravity.CENTER);
            mColorStateList = mTextView.getTextColors();
            mTextView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mImageView = new ImageView(context);
            mImageView.setVisibility(GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mImageView.setImageResource(R.drawable.holiday_icon);
            mImageView.setLayoutParams(layoutParams);
            addView(mTextView);
            addView(mImageView);
        }

        public void colorReset() {
            mTextView.setTextColor(mColorStateList);
        }

        public void setTextColor(int color) {
            mTextView.setTextColor(color);

        }

        void isHoliday(boolean is) {
            if (is) {
                mImageView.setVisibility(VISIBLE);
            } else {
                mImageView.setVisibility(GONE);
            }
        }

        void setText(String text) {
            mTextView.setText(text);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mWidth = w;
            mheight = h;
            invalidate();
        }
    }

    private void setTitleView() {
        if (mTitleLayout == null) {
            return;
        }
        int childCount = mTitleLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TitleLayout titleLayout = (TitleLayout) mTitleLayout.getChildAt(i);
            if (mToday.equals(mWeekDate.get(i).getmDate()) && mIsShowToday) {
                titleLayout.setTextColor(Color.parseColor("#19AB20"));
            } else {
                titleLayout.colorReset();
            }
            String title = mWeek[i];
            if (mShowDate) {
                title += "\n" + mWeekDate.get(i).getmDate();
            }
            titleLayout.setText(title);
            titleLayout.isHoliday(mWeekDate.get(i).isHoliday());
        }
    }

    public void setTimeTableListener(TimeTableView2.TimeTableListener timeTableListener) {
        if (mTimeTableBase != null) {
            mTimeTableBase.setTimeTableListener(timeTableListener);
        }
    }

    /**
     * @param timeTableContents
     */
    public void setClass(List<TimeTableContent> timeTableContents) {
        mTimeTableBase.setClass(timeTableContents);
    }

    public void setTimeTable(List<TimeTableView2.TimeTable> timeTables) {
        mTimeTableBase.setTimeTable(timeTables);
    }

    public void setIsShowToday(boolean isShowToday) {
        mIsShowToday = isShowToday;
        mTimeTableBase.setIsShowToday(isShowToday);
    }

    /**
     * 设置上午、下午节次
     *
     * @param amRow
     * @param pmRow
     */
    public void setClassCount(int amRow, int pmRow) {
        if (mTimeTableBase != null) {
            Cog.d(TAG, amRow + "-" + pmRow);
            mTimeTableBase.setClassCount(amRow, pmRow);
        }
    }

    public void setWeekDate(List<TimeTableView2.Holiday> week) {
        mWeekDate = week;
        boolean flag = false;
        for (int i = 0; i < week.size(); i++) {
            if (mToday.equals(week.get(i).getmDate())) {
                if (mCurrentYear == week.get(i).getYear()) {
                    flag = true;
                }
                break;
            }
        }
        setIsShowToday(flag);
        setTitleView();
    }

    public void setYear(int year) {
        mYear = year;
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
        if (mWeekStart == 0) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        mWeekDate = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            TimeTableView2.Holiday holiday = new TimeTableView2.Holiday();
            holiday.setmDate(df.format(calendar.getTime()));
            mWeekDate.add(holiday);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
