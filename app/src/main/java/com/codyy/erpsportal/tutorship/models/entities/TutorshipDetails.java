package com.codyy.erpsportal.tutorship.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParsable;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 辅导详情
 * Created by gujiajia on 2015/12/30.
 */
public class TutorshipDetails extends Tutorship {

    private String creatorName;

    private String createTime;

    private String endTime;

    private String gradeName;

    /**
     * 助教名
     */
    private String assistantName;

    /**
     * 辅导简介
     */
    private String summary;

    /**
     * 签到者们
     */
    private String registers;

    /**
     * 未签到者们
     */
    private String unregisters;

    /**
     * 到场者
     */
    private String entrants;

    /**
     * 缺席者
     */
    private String absentees;

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRegisters() {
        return registers;
    }

    public void setRegisters(String registers) {
        this.registers = registers;
    }

    public String getUnregisters() {
        return unregisters;
    }

    public void setUnregisters(String unregisters) {
        this.unregisters = unregisters;
    }

    public String getEntrants() {
        return entrants;
    }

    public void setEntrants(String entrants) {
        this.entrants = entrants;
    }

    public String getAbsentees() {
        return absentees;
    }

    public void setAbsentees(String absentees) {
        this.absentees = absentees;
    }

    public static JsonParsable<TutorshipDetails> PARSER = new JsonParser<TutorshipDetails>() {
        @Override
        public TutorshipDetails parse(JSONObject jsonObject) {
            TutorshipDetails tutorshipDetails = new TutorshipDetails();
            tutorshipDetails.setTitle(jsonObject.optString("lessonTitle"));
            tutorshipDetails.setCreatorName(jsonObject.optString("creator"));
            tutorshipDetails.setCreateTime(jsonObject.optString("createTime"));
            tutorshipDetails.setStartTime(jsonObject.optString("startTime"));
            tutorshipDetails.setEndTime(jsonObject.optString("endTime"));
            tutorshipDetails.setSubjectName(jsonObject.optString("subject"));
            tutorshipDetails.setGradeName(jsonObject.optString("grade"));
            tutorshipDetails.setAbsentees(jsonObject.optString("assistant"));
            tutorshipDetails.setSummary(jsonObject.optString("lessonInfo"));
            tutorshipDetails.setRegisters(strArrayToStr(jsonObject, "registers"));
            tutorshipDetails.setUnregisters(strArrayToStr(jsonObject, "unregisters"));
            tutorshipDetails.setEntrants(strArrayToStr(jsonObject, "enters"));
            tutorshipDetails.setAbsentees(strArrayToStr(jsonObject, "notEnters"));
            return tutorshipDetails;
        }

        /**
         * 将jsonObject中的字串数组转为一个字串，用空格分隔
         * @param jsonObject 有字串数组的jsonObject
         * @param name 字串数组名
         * @return 字串
         */
        private String strArrayToStr(JSONObject jsonObject, String name) {
            JSONArray jsonArray = jsonObject.optJSONArray(name);
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<jsonArray.length(); i++) {
                sb.append(jsonArray.optString(i)).append(' ');
            }
            return sb.toString();
        }
    };
}
