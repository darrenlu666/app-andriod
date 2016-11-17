package com.codyy.erpsportal.statistics.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 开课比分析，学期统计项
 * Created by gujiajia on 2016/6/6.
 */
public class CoursesProportion {

    public CoursesProportion() { }

    public CoursesProportion(String strDate, float ratio) {
        this.strDate = strDate;
        this.ratio = ratio;
    }

    /**
     * 日期
     */
    private String strDate;

    private String year;

    private String month;

    /**
     * 开课比例
     */
    private float ratio;

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
        String[] strs =strDate.split("-");
        this.year = strs[0];
        this.month = strs[1];
    }

    public String getYear(){
        return year;
    }

    public String getMonth() {
        return month;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public static final JsonParser<CoursesProportion> PARSER = new JsonParser<CoursesProportion>() {
        @Override
        public CoursesProportion parse(JSONObject jsonObject) {
            CoursesProportion coursesProportion = new CoursesProportion();
            coursesProportion.setRatio((float) jsonObject.optDouble("ratio"));
            coursesProportion.setStrDate(jsonObject.optString("strDate"));
            return coursesProportion;
        }
    };
}
