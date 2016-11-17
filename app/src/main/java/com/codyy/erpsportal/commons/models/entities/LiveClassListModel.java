package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.Cog;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/4/10.
 */
public class LiveClassListModel {

    private String id;
    private String index;//索引
    private String icon;//图片icon的url
    private String school;//学校名称
    private String subject;//disciplineName 学科
    private String grade;   //班级
    private String teacher;//授课老师
    private String serverAddress;//rtmp地址
    private String realBeginTime;//课程录制时间2015/05/25
    private boolean hasPlayBack;//是否转化成功

    private String classRoomId;//教室id
    private String streamingServerType;
    private String dmsServerHost;

    public String getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(String classRoomId) {
        this.classRoomId = classRoomId;
    }

    public String getStreamingServerType() {
        return streamingServerType;
    }

    public void setStreamingServerType(String streamingServerType) {
        this.streamingServerType = streamingServerType;
    }

    public String getDmsServerHost() {
        return dmsServerHost;
    }

    public void setDmsServerHost(String dmsServerHost) {
        this.dmsServerHost = dmsServerHost;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getRealBeginTime() {
        return realBeginTime;
    }

    public void setRealBeginTime(String realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public boolean isHasPlayBack() {
        return hasPlayBack;
    }

    public void setHasPlayBack(boolean hasPlayBack) {
        this.hasPlayBack = hasPlayBack;
    }

    //解析json
    public static LiveClassListModel parse(JSONObject jsonObject) {
        LiveClassListModel mclassMode = new LiveClassListModel();
        mclassMode.setId(jsonObject.optString("id"));
        mclassMode.setIndex(jsonObject.optString("index"));
        mclassMode.setGrade(StringUtils.replaceHtml(jsonObject.optString("gradeName")).toString());
        mclassMode.setSchool(StringUtils.replaceHtml(jsonObject.optString("schoolName")).toString());
        mclassMode.setTeacher(StringUtils.replaceHtml(jsonObject.optString("teacherName")).toString());
        mclassMode.setSubject(StringUtils.replaceHtml(jsonObject.optString("disciplineName")).toString());
        mclassMode.setServerAddress(jsonObject.optString("videoUrl"));
        mclassMode.setIcon(jsonObject.optString("disciplineIconUrl"));
        mclassMode.setRealBeginTime(jsonObject.optString("realBeginTime"));
        mclassMode.setHasPlayBack(jsonObject.optBoolean("hasPlayBack", true));
        mclassMode.setStreamingServerType(jsonObject.optString("streamingServerType"));
        mclassMode.setDmsServerHost(jsonObject.optString("dmsServerHost"));
        mclassMode.setClassRoomId(jsonObject.optString("classRoomId"));
        return mclassMode;
    }

    public static List<LiveClassListModel> parseList(JSONObject jsonObject) {

        List<LiveClassListModel> mList = new ArrayList<>();
        JSONArray mJsonArray = jsonObject.optJSONArray("list");
        if (null != mJsonArray && mJsonArray.length() > 0) {
            Cog.i("Json", "mJsonArray length ==>>" + mJsonArray.length() + "::" + mJsonArray);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject object = (JSONObject) mJsonArray.opt(i);
                mList.add(parse(object));
            }
        }

        return mList;
    }

}
