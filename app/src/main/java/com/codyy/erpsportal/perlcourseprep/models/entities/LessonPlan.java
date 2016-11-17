package com.codyy.erpsportal.perlcourseprep.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 个人备课
 * Created by gujiajia on 2016/1/15.
 */
public class LessonPlan {

    private String id;

    private String title;

    private String mainTeacher;

    private float averageScore;

    private String viewCount;

    private String subjectPic;

    private long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static JsonParser<LessonPlan> JSON_PARSER = new JsonParser<LessonPlan>() {
        @Override
        public LessonPlan parse(JSONObject jsonObject) {
            LessonPlan lessonPlan = new LessonPlan();
            lessonPlan.setId(jsonObject.optString("id"));
            lessonPlan.setTitle(jsonObject.optString("lessonPlanName"));
            lessonPlan.setMainTeacher(optStrOrNull(jsonObject, "teacherName"));
            lessonPlan.setTime(jsonObject.optLong("operateTime"));
//            lessonPlan.setTime(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(jsonObject.optString("operateTime")).getMillis());

            if (!jsonObject.isNull("viewCount")) {
                lessonPlan.setViewCount(jsonObject.optString("viewCount"));
            } else {
                lessonPlan.setViewCount("0");
            }

            if (!jsonObject.isNull("averageScore")) {
                lessonPlan.setAverageScore(Float.parseFloat(jsonObject.optString("averageScore")));
            }

            lessonPlan.setSubjectPic(optStrOrNull(jsonObject, "subjectPic"));
            return lessonPlan;
        }
    };
}
