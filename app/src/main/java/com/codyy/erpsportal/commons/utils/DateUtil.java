package com.codyy.erpsportal.commons.utils;

import android.text.TextUtils;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String DEF_FORMAT = "yyyy-MM-dd HH:mm";
    public final static String YEAR_MONTH_DAY = "yyyy-MM-dd";

    public static String getNow(String format) {
        if (null == format || "".equals(format)) {
            format = PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(new Date());
        return date;
    }

    public static boolean isToday(long time) {
        if (getNow(YEAR_MONTH_DAY).equals(getDateStr(time, YEAR_MONTH_DAY))) {
            return true;
        }
        return false;
    }

    /**
     * @param _date
     * @param format
     * @return
     */
    public static long stringToLong(String _date, String format) {
        Date date = stringToDate(_date, format);
        return date.getTime();
    }

    public static Date stringToDate(String _date, String format) {
        if (TextUtils.isEmpty(format)) {
            format = PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date, String format) {
        if (null == format || "".equals(format)) {
            format = PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);
    }

    public static Date intToDate(long lDate) {
        Date date = new Date(lDate);
        return date;
    }

    public static String getDateStr(long times, String format) {
        if (times == 0)
            return "";
        Date date = intToDate(times);
        return dateToString(date, format);
    }

    public static Date getDate(int year, int month, int weekInMonth,
                               int dayInWeek) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month - 1);
        date.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
        date.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
        return date.getTime();
    }

    public static Date getDate(int month, int weekInMonth, int dayInWeek) {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        date.clear();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month - 1);
        date.set(Calendar.DAY_OF_WEEK_IN_MONTH, weekInMonth);
        date.set(Calendar.DAY_OF_WEEK, dayInWeek + 1);
        return date.getTime();
    }

    public static String getDate(int month, int weekInMonth, int dayInWeek,
                                 String format) {
        Date date = getDate(month, weekInMonth, dayInWeek);
        return getDateStr(date.getTime(), format);
    }

    public final static long ONE_MINUTE = 1000 * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;

    /**
     * 详细过去时间
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        long current = System.currentTimeMillis();
        long inteval = current - time;
        if (isToday(time)) {
            if (inteval < ONE_MINUTE) {
                return "刚刚";
            } else if (inteval < ONE_HOUR) {
                return inteval / ONE_MINUTE + "分钟前";
            } else if (inteval < ONE_DAY) {
                return "今天 " + getDateStr(time, "HH:mm");
                //return inteval / ONE_HOUR + "小时前";
            }
            return "";
        } else {
            if (isTheSameYear(time)) {
                /*if (isYestoday(time)) {
                   return YESTODAY;
                }*/
                return getDateStr(time, "MM-dd HH:mm");
            } else {
                return getDateStr(time, "yyyy-MM-dd");
            }
        }
    }

    /**
     * 详细过去时间,私信聊天
     *
     * @param time
     * @return
     */
    public static String formatTime2(long time) {
        long current = System.currentTimeMillis();
        long inteval = current - time;
        if (isToday(time)) {
//            if (inteval < ONE_MINUTE) {
//                return "刚刚";
//            } else if (inteval < ONE_HOUR) {
//                return inteval / ONE_MINUTE + "分钟前";
//            } else if (inteval < ONE_DAY) {
//                return "今天 " + getDateStr(time, "HH:mm");
//                //return inteval / ONE_HOUR + "小时前";
//            }
            return getDateStr(time, "HH:mm");
        } else {
            if (isTheSameYear(time)) {
                /*if (isYestoday(time)) {
                   return YESTODAY;
                }*/
                return getDateStr(time, "MM-dd HH:mm");
            } else {
                return getDateStr(time, "yyyy-MM-dd");
            }
        }
    }

    private static boolean isTheSameYear(long time) {
        DateTime dt = new DateTime(time);
        DateTime now = new DateTime();
        return dt.getYear() == now.getYear();
    }

    public static String getDuration(long persisTime) {
        if (persisTime <= 0)
            return "";
        long minutes = persisTime/(60*1000);
        long day = minutes / (24 * 60);
        long hour = minutes % (24 * 60) / 60;
        long minute = minutes % 60;
        StringBuilder buf = new StringBuilder();
        buf.append((day == 0 ? "" : day + "天 "));
        buf.append((hour == 0 ? "" : hour + "小时 "));
        buf.append((minute == 0 ? "" : minute + "分 "));
        return buf.toString();
    }

    public static String stringToDate(String lo) {
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        return sd.format(date);
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 10000L;
    }
    /**
     * format time
     *
     * @param time Media duration
     * @return HH:mm:ss
     */
    public static String formatMediaTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(time));
    }
}
