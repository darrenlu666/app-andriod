package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.utils.UriUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/10.
 */
public class TeachingResearchPrepare extends TeachingResearchBase {

    /**
     * mainTeacher : 张三
     * startTime : 2015-07-09
     * id : 9b5a97d0274b40b49ce72c016e1a14a8
     * viewCount : 3
     * title : ddddd
     * totalScore : null
     * averageScore : null
     */
    private String mainTeacher;
    private long startTime;
    private String id;
    private int viewCount;
    private String title;
    private String totalScore;
    private String averageScore;
    private String subjectPic;
    private String scoreType;

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getId() {
        return id;
    }

    public int getViewCount() {
        return viewCount;
    }

    public String getTitle() {
        return title;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public String getAverageScore() {
        return averageScore;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    /**
     * 获取集体备课
     *
     * @param object
     * @param teachingResearchPrepares
     */
    public static void getTeachingResearchPrepare(JSONObject object, ArrayList<TeachingResearchBase> teachingResearchPrepares) {
        if ("success".equals(object.optString("result"))) {
            if (teachingResearchPrepares == null) {
                teachingResearchPrepares = new ArrayList<>();
            }
            JSONObject jsonObject = object.optJSONObject("groupPreparation");
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                TeachingResearchPrepare teachingResearchPrepare = new TeachingResearchPrepare();
                teachingResearchPrepare.setType(PREPARE_LESSON);
                teachingResearchPrepare.setId(jsonObject1.optString("id"));
                teachingResearchPrepare.setTitle(jsonObject1.optString("title"));
                teachingResearchPrepare.setMainTeacher(jsonObject1.optString("mainTeacher"));
                teachingResearchPrepare.setStartTime(jsonObject1.optLong("startTime"));
                teachingResearchPrepare.setTotalScore(jsonObject1.optString("totalScore"));
                teachingResearchPrepare.setAverageScore(jsonObject1.optString("averageScore"));
                teachingResearchPrepare.setViewCount(jsonObject1.optInt("viewCount", 0));
                teachingResearchPrepare.setSubjectPic(UriUtils.getImageUrl(jsonObject1.optString("subjectPic")));
                teachingResearchPrepares.add(teachingResearchPrepare);
            }
        }
    }

    /**
     * 互动听课
     *
     * @param object
     * @param teachingResearchPrepares
     */
    public static void getTeachingResearchInterac(JSONObject object, ArrayList<TeachingResearchBase> teachingResearchPrepares) {
        if ("success".equals(object.optString("result"))) {
            if (teachingResearchPrepares == null) {
                teachingResearchPrepares = new ArrayList<>();
            }
            JSONObject jsonObject = object.optJSONObject("interactionListen");
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                TeachingResearchPrepare teachingResearchPrepare = new TeachingResearchPrepare();
                teachingResearchPrepare.setType(INTERAC_LESSON);
                teachingResearchPrepare.setId(jsonObject1.optString("id"));
                teachingResearchPrepare.setTitle(jsonObject1.optString("title"));
                teachingResearchPrepare.setMainTeacher(jsonObject1.optString("mainTeacher"));
                teachingResearchPrepare.setStartTime(jsonObject1.optLong("startTime"));
                teachingResearchPrepare.setTotalScore(jsonObject1.optString("totalScore"));
                teachingResearchPrepare.setAverageScore(jsonObject1.optString("averageScore"));
                teachingResearchPrepare.setViewCount(jsonObject1.optInt("viewCount", 0));
                teachingResearchPrepare.setSubjectPic(UriUtils.getImageUrl(jsonObject1.optString("subjectPic")));
                teachingResearchPrepares.add(teachingResearchPrepare);
            }
        }
    }

    /**
     * 互动听课
     *
     * @param object
     * @param teachingResearchPrepares
     */
    public static void getTeachingResearchEvaluation(JSONObject object, ArrayList<TeachingResearchBase> teachingResearchPrepares) {
        if ("success".equals(object.optString("result"))) {
            if (teachingResearchPrepares == null) {
                teachingResearchPrepares = new ArrayList<>();
            }
            JSONObject jsonObject = object.optJSONObject("evaluationAndDiscussion");
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                TeachingResearchPrepare teachingResearchPrepare = new TeachingResearchPrepare();
                teachingResearchPrepare.setType(EVALUATION_LESSON);
                teachingResearchPrepare.setId(jsonObject1.optString("id"));
                teachingResearchPrepare.setTitle(jsonObject1.optString("title"));
                teachingResearchPrepare.setMainTeacher(jsonObject1.optString("mainTeacher"));
                teachingResearchPrepare.setStartTime(jsonObject1.optLong("startTime"));
//                teachingResearchPrepare.setTotalScore(jsonObject1.optInt("totalScore", 0));
//                teachingResearchPrepare.setAverageScore(jsonObject1.optInt("averageScore", 0));
                teachingResearchPrepare.setViewCount(jsonObject1.optInt("viewCount", 0));
                teachingResearchPrepare.setSubjectPic(UriUtils.getImageUrl(jsonObject1.optString("subjectPic")));
                teachingResearchPrepares.add(teachingResearchPrepare);
            }
        }
    }

}
