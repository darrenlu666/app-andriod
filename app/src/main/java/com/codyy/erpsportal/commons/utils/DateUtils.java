package com.codyy.erpsportal.commons.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.codyy.erpsportal.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jia on 2015/4/23.
 */
public class DateUtils {

    public static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static SimpleDateFormat sTimeFormat = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat sDayFormat = new SimpleDateFormat("MM-dd");

    public static SimpleDateFormat sDayWithYearFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 1) 发布时间小于1分钟，显示：刚刚
     * 2) 1分钟≤发布时间<1小时，显示：mm分钟前
     * 3) 1小时≦发布时间，并且在今天0：00后的，显示：今天 hh：mm
     * 4) 发布时间在今天0：00前的，且在今年的，显示：MM-DD
     * 5) 发布年份≦当前年份，显示：YYYY-MM-DD
     */
    public static String formatCreateTime(Context context, String dateStr) {
        Date date = null;
        try {
            date = sDateFormat.parse( dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return formatDate(context, date);
    }

    /**
     * {@link #formatCreateTime(Context, String)},但是传入格式为yyyy-MM-dd HH:mm
     *
     * @param context
     * @param dateStr
     * @return
     */
    public static String formatSimpleTime(Context context, String dateStr){
        if (TextUtils.isEmpty(dateStr)) return null;
        Date date = null;
        try {
            date = sSimpleDateFormat.parse( dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return formatDate(context, date);
    }

    /**
     * {@link #formatCreateTime(Context, String)},但是传入格式为long
     *
     * @param context
     * @param dateLong
     * @return
     */
    public static String formatLongTime(Context context, long dateLong) {
        return formatDate(context, new Date(dateLong));
    }

    @Nullable
    private static String formatDate(Context context, Date date) {
        long time = date.getTime();
        long now = System.currentTimeMillis();
        long interval = now - time;
        Cog.d("DateUtils", "interval=", interval);
        if (interval < 0) {
            String dayWithYear = sDayWithYearFormat.format(date);
            return dayWithYear;
        } else if (interval < 60 * 1000L) {
            return context.getString( R.string.just_now);
        } else if ( interval >= 60 * 1000L && interval < 60 * 60 * 1000L) {
            long minutes = interval / 60 /1000;
            return context.getString( R.string.beforeMin, minutes);
        } else if ( interval >= 60 * 60 * 1000L) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
            int year = cal.get(Calendar.YEAR);
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTimeInMillis(now);
            int nowDayOfYear = nowCal.get(Calendar.DAY_OF_YEAR);
            int nowYear = nowCal.get(Calendar.YEAR);
            if ( year == nowYear && dayOfYear == nowDayOfYear) {
                String shortTime = sTimeFormat.format(date);
                return context.getString( R.string.today, shortTime);
            } else if ( year == nowYear && dayOfYear != nowDayOfYear) {
                String dayWithoutYear = sDayFormat.format(date);
                return dayWithoutYear;
            } else if ( year != nowYear) {
                String dayWithYear = sDayWithYearFormat.format(date);
                return dayWithYear;
            }
        }
        return null;
    }



}
