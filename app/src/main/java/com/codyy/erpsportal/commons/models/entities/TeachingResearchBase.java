package com.codyy.erpsportal.commons.models.entities;

import android.support.annotation.NonNull;

import com.codyy.erpsportal.commons.utils.NumberUtils;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.erpsportal.commons.models.Titles;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/8/10.
 */
public class TeachingResearchBase {
    /**
     * 标题
     */
    public static final int TITLE_VIEW = 0x001;
    /**
     * 集体备课
     */
    public static final int PREPARE_LESSON = TITLE_VIEW + 1;
    /**
     * 分隔
     */
    public static final int DIVIDE_VIEW = PREPARE_LESSON + 1;
    /**
     * 互动听课
     */
    public static final int INTERAC_LESSON = DIVIDE_VIEW + 1;
    /**
     * 评课议课
     */
    public static final int EVALUATION_LESSON = INTERAC_LESSON + 1;
    /**
     * 个人备课
     * personPrepareLesson
     */
    public static final int PERSON_PREPARE = EVALUATION_LESSON + 1;
    /**
     * 教学反思
     * rethinkRethink
     */
    public static final int RETHINK_RETHINK = PERSON_PREPARE + 1;
    /**
     * 没有相关数据
     */
    public static final int NO_DATA = RETHINK_RETHINK + 1;
    private int mType;
    private String mTitleStr;
    private int mTitleType;
    private int mSpanSize;

    public int getTitleType() {
        return mTitleType;
    }

    public void setTitleType(int mTitleType) {
        this.mTitleType = mTitleType;
    }

    public int getSpanSize() {
        return mSpanSize;
    }

    public void setSpanSize(int spanSize) {
        this.mSpanSize = spanSize;
    }

    public String getTitleStr() {
        return mTitleStr;
    }

    public void setTitleStr(String titleStr) {
        mTitleStr = titleStr;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    /**
     * @param object
     * @param teachingResearchBases
     */
    public static void getTeachingResear(JSONObject object, List<TeachingResearchBase> teachingResearchBases, MainPageConfig mMainPageConfig) {
        if ("success".equals(object.optString("result"))) {
            if (teachingResearchBases == null) {
                teachingResearchBases = new ArrayList<>();
            }
            JSONObject groupPreparation = object.optJSONObject("groupPreparation");
            JSONObject interactionListen = object.optJSONObject("interactionListen");
            JSONObject evaluationAndDiscussion = object.optJSONObject("evaluationAndDiscussion");
            JSONObject personPrepareLesson = object.optJSONObject("personPrepareLesson");
//            JSONObject rethinkRethink = object.optJSONObject("rethinkRethink");
            JSONArray personArray = personPrepareLesson.optJSONArray("list");
            TeachingResearchBase teachingResearchBase = new TeachingResearchBase();
            teachingResearchBase.setType(TeachingResearchBase.TITLE_VIEW);
            teachingResearchBase.setTitleType(TeachingResearchBase.PERSON_PREPARE);
            teachingResearchBase.setTitleStr(Titles.sPagetitleNetteachPrepare);
            teachingResearchBases.add(teachingResearchBase);
            if (personArray.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < personArray.length(); i++) {
                    JSONObject person = personArray.optJSONObject(i);
                    TeachingResearchPrepare teachingResearchPrepare = gson.fromJson(person.toString(), TeachingResearchPrepare.class);
                    teachingResearchPrepare.setType(TeachingResearchBase.PERSON_PREPARE);
                    teachingResearchPrepare.setSubjectPic(getPic(person.optString("subjectPic")));
                    teachingResearchBases.add(teachingResearchPrepare);
                }
            } else {
                TeachingResearchBase nodata = new TeachingResearchBase();
                nodata.setType(TeachingResearchBase.NO_DATA);
                teachingResearchBases.add(nodata);
            }

            if (mMainPageConfig.hasPrepareLesson()) {
                JSONArray jsonArray = groupPreparation.optJSONArray("list");
                TeachingResearchBase perpareLesson = new TeachingResearchBase();
                perpareLesson.setType(TeachingResearchBase.TITLE_VIEW);
                perpareLesson.setTitleType(TeachingResearchBase.PREPARE_LESSON);
                perpareLesson.setTitleStr(Titles.sPagetitleNetteachAllprepare);
                teachingResearchBases.add(perpareLesson);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        TeachingResearchPrepare teachingResearchPrepare = new TeachingResearchPrepare();
                        teachingResearchPrepare.setType(TeachingResearchBase.PREPARE_LESSON);
                        teachingResearchPrepare.setId(jsonObject1.optString("id"));
                        teachingResearchPrepare.setTitle(jsonObject1.optString("title"));
                        teachingResearchPrepare.setMainTeacher(jsonObject1.optString("mainTeacher"));
                        teachingResearchPrepare.setStartTime(jsonObject1.optLong("startTime"));
                        teachingResearchPrepare.setTotalScore(jsonObject1.optString("totalScore"));
                        teachingResearchPrepare.setAverageScore(NumberUtils.floatOf(jsonObject1.optString("averageScore")));
                        teachingResearchPrepare.setViewCount(jsonObject1.optInt("viewCount", 0));
                        teachingResearchPrepare.setSubjectPic(getPic(jsonObject1.optString("subjectPic")));
                        teachingResearchBases.add(teachingResearchPrepare);
                    }
                } else {
                    TeachingResearchBase nodata = new TeachingResearchBase();
                    nodata.setType(TeachingResearchBase.NO_DATA);
                    teachingResearchBases.add(nodata);
                }
//                TeachingResearchBase teachingResearchBase1 = new TeachingResearchBase();
//                teachingResearchBase1.setType(TeachingResearchBase.DIVIDE_VIEW);
//                teachingResearchBases.add(teachingResearchBase1);
            }
            if (mMainPageConfig.hasInteractLesson()) {
                JSONArray jsonArray = interactionListen.optJSONArray("list");
                TeachingResearchBase teachingResearchBase2 = new TeachingResearchBase();
                teachingResearchBase2.setType(TeachingResearchBase.TITLE_VIEW);
                teachingResearchBase2.setTitleType(TeachingResearchBase.INTERAC_LESSON);
                teachingResearchBase2.setTitleStr(Titles.sPagetitleNetteachInteract);
                teachingResearchBases.add(teachingResearchBase2);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        TeachingResearchPrepare teachingResearchPrepare = new TeachingResearchPrepare();
                        teachingResearchPrepare.setType(TeachingResearchBase.INTERAC_LESSON);
                        teachingResearchPrepare.setId(jsonObject1.optString("id"));
                        teachingResearchPrepare.setTitle(jsonObject1.optString("title"));
                        teachingResearchPrepare.setMainTeacher(jsonObject1.optString("mainTeacher"));
                        teachingResearchPrepare.setStartTime(jsonObject1.optLong("startTime"));
                        teachingResearchPrepare.setTotalScore(jsonObject1.optString("totalScore"));
                        teachingResearchPrepare.setAverageScore(NumberUtils.floatOf(jsonObject1.optString("averageScore")));
                        teachingResearchPrepare.setViewCount(jsonObject1.optInt("viewCount", 0));
                        teachingResearchPrepare.setSubjectPic(getPic(jsonObject1.optString("subjectPic")));
                        teachingResearchBases.add(teachingResearchPrepare);
                    }
                } else {
                    TeachingResearchBase nodata = new TeachingResearchBase();
                    nodata.setType(TeachingResearchBase.NO_DATA);
                    teachingResearchBases.add(nodata);
                }


//                TeachingResearchBase teachingResearchBase3 = new TeachingResearchBase();
//                teachingResearchBase3.setType(TeachingResearchBase.DIVIDE_VIEW);
//                teachingResearchBases.add(teachingResearchBase3);
            }
            if (mMainPageConfig.hasEvaluationMeeting()) {
                JSONArray jsonArray = evaluationAndDiscussion.optJSONArray("list");
                TeachingResearchBase teachingResearchBase4 = new TeachingResearchBase();
                teachingResearchBase4.setType(TeachingResearchBase.TITLE_VIEW);
                teachingResearchBase4.setTitleType(TeachingResearchBase.EVALUATION_LESSON);
                teachingResearchBase4.setTitleStr(Titles.sPagetitleNetteachDisucss);
                teachingResearchBases.add(teachingResearchBase4);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        TeachingResearchPrepare teachingResearchPrepare = new TeachingResearchPrepare();
                        teachingResearchPrepare.setType(TeachingResearchBase.EVALUATION_LESSON);
                        teachingResearchPrepare.setId(jsonObject1.optString("id"));
                        teachingResearchPrepare.setTitle(jsonObject1.optString("title"));
                        teachingResearchPrepare.setMainTeacher(jsonObject1.optString("mainTeacher"));
                        teachingResearchPrepare.setStartTime(jsonObject1.optLong("startTime"));
                        teachingResearchPrepare.setTotalScore(jsonObject1.optString("totalScore"));
                        teachingResearchPrepare.setAverageScore(NumberUtils.floatOf(jsonObject1.optString("averageScore")));
                        teachingResearchPrepare.setViewCount(jsonObject1.optInt("viewCount", 0));
                        teachingResearchPrepare.setScoreType(jsonObject1.optString("scoreType"));
                        teachingResearchPrepare.setSubjectPic(getPic(jsonObject1.optString("subjectPic")));
                        teachingResearchBases.add(teachingResearchPrepare);
                    }
                } else {
                    TeachingResearchBase nodata = new TeachingResearchBase();
                    nodata.setType(TeachingResearchBase.NO_DATA);
                    teachingResearchBases.add(nodata);
                }
            }
        }
    }

    public static String getPic(@NonNull String str) {
        if (str.startsWith(URLConfig.IMAGE_URL)) {
            return str;
        }
        if (!"null".equals(str)) {
            return UriUtils.getImageUrl(str);
        }
        return URLConfig.BASE + "/images/subjectDefault.png";
    }
}
