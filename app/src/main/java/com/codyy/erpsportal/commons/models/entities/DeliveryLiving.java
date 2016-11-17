package com.codyy.erpsportal.commons.models.entities;

import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.Titles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 2015/8/10.
 */
public class DeliveryLiving extends DeliveryBase {

    /**
     * daySeq : 0
     * subjectPic : 20787737-2be9-4a19-80c1-270d870fe1fa.gif
     * scheduleDetailId : 899582c8564249c2adaeb2d9d79ac754
     * classSeq : 0
     * teacherName : test5555
     * areaName : 苏州园区
     * liveType : schedule
     * realBeginTime : 1439169313527
     * schoolName : qinlong_school
     * classlevelName : 一年级
     * subjectName : 语文
     */
    private int daySeq;
    private String subjectPic;
    private String scheduleDetailId;
    private int classSeq;
    private String teacherName;
    private String areaName;
    private String liveType;
    private long realBeginTime;
    private String schoolName;
    private String classlevelName;
    private String subjectName;

    public void setDaySeq(int daySeq) {
        this.daySeq = daySeq;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public void setScheduleDetailId(String scheduleDetailId) {
        this.scheduleDetailId = scheduleDetailId;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public void setRealBeginTime(long realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getDaySeq() {
        return daySeq;
    }

    public String getSubjectPic() {
        return subjectPic;
    }

    public String getScheduleDetailId() {
        return scheduleDetailId;
    }

    public int getClassSeq() {
        return classSeq;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getLiveType() {
        return liveType;
    }

    public long getRealBeginTime() {
        return realBeginTime;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    /**
     * @param object
     * @param deliveryBases
     */
    public static void getDeliveryLiving(JSONObject object, ArrayList<DeliveryBase> deliveryBases) {
        if ("success".equals(object.optString("result"))) {
            if (deliveryBases == null) {
                deliveryBases = new ArrayList<>();
            }
            JSONArray array = object.optJSONArray("data");
            if (array.length() > 0) {
                DeliveryBase deliveryBase = new DeliveryBase();
                deliveryBase.setTitle(Titles.sPagetitleSpeclassLive);
                deliveryBase.setSpanSize(2);
                deliveryBase.setType(DeliveryBase.TITLE_DIVIDE);
                deliveryBases.add(deliveryBase);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.optJSONObject(i);
                    DeliveryLiving deliveryLiving = new DeliveryLiving();
                    deliveryLiving.setType(DeliveryBase.SCHEDULE_LIVE);
                    deliveryLiving.setSpanSize(2);
                    deliveryLiving.setScheduleDetailId(jsonObject.optString("scheduleDetailId"));
                    deliveryLiving.setDaySeq(jsonObject.optInt("daySeq"));
                    deliveryLiving.setClassSeq(jsonObject.optInt("classSeq"));
                    deliveryLiving.setSubjectName(jsonObject.optString("subjectName", ""));
                    deliveryLiving.setLiveType(jsonObject.optString("liveType"));
                    deliveryLiving.setSubjectPic(setPic(jsonObject.optString("subjectPic")));
                    deliveryLiving.setClasslevelName(jsonObject.optString("classlevelName"));
                    deliveryLiving.setTeacherName(jsonObject.optString("teacherName"));
                    deliveryLiving.setAreaName(jsonObject.optString("areaName"));
                    deliveryLiving.setRealBeginTime(jsonObject.optInt("realBeginTime"));
                    deliveryLiving.setSchoolName(jsonObject.optString("schoolName"));
                    deliveryBases.add(deliveryLiving);
                }
                DeliveryBase divide = new DeliveryBase();
                divide.setType(DeliveryBase.DIVIDE_VIEW);
                divide.setSpanSize(2);
                deliveryBases.add(divide);
            }
        }
    }

    private static String setPic(String pic) {
        if ("null".equals(pic)) {
            return URLConfig.BASE + "/images/subjectDefault.png";
        }
        return pic;
    }
}
