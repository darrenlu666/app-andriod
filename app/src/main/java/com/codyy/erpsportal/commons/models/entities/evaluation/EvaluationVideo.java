package com.codyy.erpsportal.commons.models.entities.evaluation;

/**
 * 评课议课
 * Created by poe on 16-9-22.
 */
public class EvaluationVideo {

    /**
     * classroomId : aa95e3dcb18e450bb31a0f28b3e937a9
     * roomType : main
     * schoolName : 移动端测试学校
     * streamAddress : rtmp://dms.needu.cn:1938/dms/class_aa95e3dcb18e450bb31a0f28b3e937a9_u_97cd4bb1a03d47678676aee63b362717__main
     */
    private String classroomId;
    private String roomType;
    private String schoolName;
    private String streamAddress;

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStreamAddress() {
        return streamAddress;
    }

    public void setStreamAddress(String streamAddress) {
        this.streamAddress = streamAddress;
    }
}
