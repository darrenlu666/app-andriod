package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by kmdai on 16-6-20.
 */
public class CalendarViewPager extends ViewPager {

    public int mMonth;
    public int mYear;
    private final static int MIDDLE_MAX_VALUE = Integer.MAX_VALUE / 2;
    private OnDateChange mOnDateChange;
    private int mSelectYear;
    private int mSelectMonth;
    private int mSelectDay;

    public CalendarViewPager(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mYear = calendar.get(Calendar.YEAR);
        mSelectYear = mYear;
        mSelectMonth = mMonth;
        mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);
        setAdapter(mAdapter);
        setCurrentItem(MIDDLE_MAX_VALUE, false);
    }

    private PagerAdapter mAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final MaterialCalendarView calendarView = new MaterialCalendarView(getContext());
            calendarView.setTag(getTAG(position));
            int a = (position - MIDDLE_MAX_VALUE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            if (a != 0) {
                calendar.add(Calendar.MONTH, a);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            calendarView.setDate(year, month);
            calendarView.setSelect(mSelectYear, mSelectMonth, mSelectDay);
            calendarView.setOnDateSelect(new MaterialCalendarView.OnDateSelect() {
                @Override
                public void onDateSelect(int year, int month, int day, int week) {
                    if (mOnDateChange != null) {
                        mSelectYear = year;
                        mSelectMonth = month;
                        mSelectDay = day;
                        calendarView.setSelect(mSelectYear, mSelectMonth, mSelectDay);
                        mOnDateChange.onDateSelect(year, month, day, week);
                    }
                }
            });
            container.addView(calendarView);
            return calendarView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    };
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mOnDateChange != null) {
                MaterialCalendarView calendarView = (MaterialCalendarView) findViewWithTag(getTAG(position));
                if (calendarView != null) {
                    calendarView.setSelect(mSelectYear, mSelectMonth, mSelectDay);
                    mOnDateChange.onDateChange(calendarView.getYear(), calendarView.getMonth(), calendarView.getDay());
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnPageChangeListener(mOnPageChangeListener);
    }

    public void setOnDateChange(OnDateChange onDateChange) {
        mOnDateChange = onDateChange;
    }

    public interface OnDateChange {
        void onDateChange(int year, int month, int day);

        void onDateSelect(int year, int month, int day, int week);
    }

    public int getSelectMonth() {
        return mSelectMonth;
    }

    public int getSelectYear() {
        return mSelectYear;
    }

    public int getSelectDay() {
        return mSelectDay;
    }

    private String getTAG(int position) {
        return "position-" + position;
    }

    public void reSetDate() {
        setCurrentItem(MIDDLE_MAX_VALUE, false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mSelectYear = calendar.get(Calendar.YEAR);
        mSelectMonth = calendar.get(Calendar.MONTH) + 1;
        mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);
        MaterialCalendarView calendarView = (MaterialCalendarView) findViewWithTag(getTAG(getCurrentItem()));
        if (calendarView != null) {
            calendarView.setSelect(mSelectYear, mSelectMonth, mSelectDay);
        }
    }

    public void setSelectDate(int year, int month, int day) {
        mSelectYear = year;
        mSelectMonth = month;
        mSelectDay = day;
        MaterialCalendarView calendarView = (MaterialCalendarView) findViewWithTag(getTAG(getCurrentItem()));
        if (calendarView != null) {
            calendarView.setSelect(mSelectYear, mSelectMonth, mSelectDay);
        }
    }
}
