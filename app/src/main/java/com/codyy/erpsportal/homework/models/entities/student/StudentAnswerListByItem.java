package com.codyy.erpsportal.homework.models.entities.student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 按题批阅学生基本信息及答案实体类
 * Created by ldh on 2016/1/19.
 */
public class StudentAnswerListByItem {

    /**
     * headPicStr : /headPic/pic.png
     * studentId : **************
     * studentName : 张三
     * isRead : false
     * studentAnswer : B
     * resrouceUrl : ***************
     */

    private String headPicStr;
    private String studentId;
    private String studentName;
    private Boolean isRead;
    private String studentAnswer;
    private String textAnswer;
    private String resrouceUrl;
    private String correctFlag;
    private int color = 0;


    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public void setHeadPicStr(String headPicStr) {
        this.headPicStr = headPicStr;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public void setResrouceUrl(String resrouceUrl) {
        this.resrouceUrl = resrouceUrl;
    }

    public String getHeadPicStr() {
        return headPicStr;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public String getResrouceUrl() {
        return resrouceUrl;
    }

    public String getCorrectFlag() {
        return correctFlag;
    }

    public void setCorrectFlag(String correctFlag) {
        this.correctFlag = correctFlag;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static List<StudentAnswerListByItem> parseResponse(JSONObject response){
        List<StudentAnswerListByItem> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for(int i = 0; i< jsonArray.length();i++){
                StudentAnswerListByItem studentAnswer = new StudentAnswerListByItem();
                JSONObject object = jsonArray.getJSONObject(i);
                studentAnswer.setStudentId(object.isNull("studentId")?"":object.optString("studentId"));
                studentAnswer.setStudentName(object.isNull("studentName")?"":object.optString("studentName"));
                studentAnswer.setHeadPicStr(object.isNull("headPicStr")?"":object.optString("headPicStr"));
                studentAnswer.setIsRead(object.isNull("isRead")?false:object.optString("isRead").equals("Y")?true:false);
                studentAnswer.setTextAnswer(object.isNull("textAnswer")?"":object.optString("textAnswer"));
                studentAnswer.setStudentAnswer(object.isNull("studentAnswer")?"":object.optString("studentAnswer"));
                studentAnswer.setResrouceUrl(object.isNull("resrouceUrl")?"":object.optString("resrouceUrl"));
                studentAnswer.setCorrectFlag(object.isNull("correctFlag")?"":object.optString("correctFlag"));
                list.add(studentAnswer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
