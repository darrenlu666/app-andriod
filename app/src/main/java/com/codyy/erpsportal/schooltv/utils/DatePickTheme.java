package com.codyy.erpsportal.schooltv.utils;

import com.codyy.erpsportal.R;

import cn.aigestudio.datepicker.bizs.themes.DPTheme;

/**
 * 日历选择空间的属性theme
 * Created by poe on 17-3-17.
 */

public class DatePickTheme extends DPTheme{
    @Override
    public int colorBG() {
        return 0xFFFFFFFF;
    }

    @Override
    public int colorBGCircle() {
        return 0x44000000;
    }

    @Override
    public int colorTitleBG() {
        return 0xff69be40;
    }

    @Override
    public int colorTitle() {
        return 0xEEFFFFFF;
    }

    @Override
    public int colorToday() {
        return 0xFF38ADFF;
    }

    @Override
    public int colorG() {
        return 0xEE333333;
    }

    @Override
    public int colorF() {
        return 0xEEC08AA4;
    }

    @Override
    public int colorWeekend() {
        return 0xFFFF0000;
    }

    @Override
    public int colorHoliday() {
        return 0x80FED6D6;
    }
}
