package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.utils.UriUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/8/17.
 */
public class HomeWorkDetail {
    /**
     * date : 2015-07-22
     * teacherName : 王琦琦
     * subject : 语文
     * schoolName : 金鸡湖/%小学
     * order : 六
     */
    private String serverAddress;
    private String recourceServerId;
    private String date;
    private String teacherName;
    private String subject;
    private String schoolName;
    private String classroomName;
    private String subjectPic;
    private String grade;
    private List<ClassRoom> classRoomList;

    public static class ClassRoom {
        private String schoolName;//学校名称
        private String classRoomName;//教室名称
        private List<HomeWorkDetailItem> homeWorkDetailList;

        public ClassRoom() {

        }

        public List<HomeWorkDetailItem> getRoomworkInfoList() {
            return homeWorkDetailList;
        }

        public void setRoomworkInfoList(List<HomeWorkDetailItem> roomworkInfoList) {
            this.homeWorkDetailList = roomworkInfoList;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getClassRoomName() {
            return classRoomName;
        }

        public void setClassRoomName(String classRoomName) {
            this.classRoomName = classRoomName;
        }
    }

    public List<ClassRoom> getClassRoomList() {
        return classRoomList;
    }

    public void setClassRoomList(List<ClassRoom> classRoomList) {
        this.classRoomList = classRoomList;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getRecourceServerId() {
        return recourceServerId;
    }

    public void setRecourceServerId(String recourceServerId) {
        this.recourceServerId = recourceServerId;
    }

    /**
     * 列表
     */
    private ArrayList<HomeWorkDetailItem> mDetailItems;

    public ArrayList<HomeWorkDetailItem> getmDetailItems() {
        return mDetailItems;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public void setmDetailItems(ArrayList<HomeWorkDetailItem> mDetailItems) {
        this.mDetailItems = mDetailItems;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getDate() {
        return date;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getSubject() {
        return subject;
    }

    public String getSchoolName() {
        return schoolName;
    }


    public String getSubjectPic() {
        return subjectPic;
    }

    public void setSubjectPic(String subjectPic) {
        this.subjectPic = subjectPic;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * @param object
     */
    public static HomeWorkDetail getHomeWorkDetail(JSONObject object) {
        HomeWorkDetail homeWorkDetail = new HomeWorkDetail();
        if ("success".endsWith(object.optString("result"))) {
            homeWorkDetail.setServerAddress(object.optString("serverAddress"));
            homeWorkDetail.setRecourceServerId(object.optString("baseResourceServerId"));
            homeWorkDetail.setSchoolName(object.optString("schoolName"));
            homeWorkDetail.setGrade(object.optString("classlevelName"));
            homeWorkDetail.setDate(object.optString("realBeginTime"));
            homeWorkDetail.setSubject(object.optString("subjectName"));
            homeWorkDetail.setTeacherName(object.optString("speakerUserName"));
            JSONArray array = object.optJSONArray("schoolList");
            List<ClassRoom> classRoomList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                ClassRoom classRoom = new ClassRoom();
                JSONObject jsonObject = array.optJSONObject(i);
                //classRoom.setClassRoomName(jsonObject.isNull("classroomName") ? "" : jsonObject.optString("classroomName"));
                classRoom.setSchoolName(jsonObject.isNull("schoolName") ? "" : jsonObject.optString("schoolName"));
                JSONArray roomWorkArray = jsonObject.optJSONArray("schoolWorkList");
                classRoom.setRoomworkInfoList(HomeWorkDetailItem.getHomeWorkDetailItem(roomWorkArray));
                classRoomList.add(classRoom);
            }
            homeWorkDetail.setClassRoomList(classRoomList);
        }
        return homeWorkDetail;
    }

    public static String getPic(String str) {
        if (!"null".equals(str)) {
            return UriUtils.getSmall(str);
        }
        return str;
    }
}
