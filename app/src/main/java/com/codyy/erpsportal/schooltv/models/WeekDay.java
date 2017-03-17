package com.codyy.erpsportal.schooltv.models;

/**
 * Created by poe on 17-3-15.
 */

public class WeekDay {
    private String weekDay;//星期一
    private String weekDate;//2-17-03-13
    private String shortWeekDate;//03-13

    public String getShortWeekDate() {
        return shortWeekDate;
    }

    public void setShortWeekDate(String shortWeekDate) {
        this.shortWeekDate = shortWeekDate;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getWeekDate() {
        return weekDate;
    }

    public void setWeekDate(String weekDate) {
        this.weekDate = weekDate;
    }
}
