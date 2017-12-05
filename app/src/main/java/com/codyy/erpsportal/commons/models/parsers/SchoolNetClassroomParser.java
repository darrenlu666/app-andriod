package com.codyy.erpsportal.commons.models.parsers;

import com.codyy.erpsportal.commons.models.entities.TourClassroom;

import org.json.JSONObject;

/**
 * 直录播课堂课堂巡视中课堂的json解析器
 * Created by gujiajia on 2015/9/29.
 */
public class SchoolNetClassroomParser extends JsonParser<TourClassroom> {
    @Override
    public TourClassroom parse(JSONObject jsonObject) {
        TourClassroom classroom = new TourClassroom();
        classroom.setId(jsonObject.optString("id"));
        classroom.setClassRoomId(jsonObject.optString("clsClassroomId"));
        classroom.setSchoolName(jsonObject.optString("schoolName"));
        //classroom.setPmsServerHost(jsonObject.optString("pmsAddr"));接口中无此字段
        classroom.setPmsServerHost(jsonObject.optString("serverAddress"));
        classroom.setDmsServerHost(jsonObject.optString("dmsServerHost"));
        classroom.setStreamingServerType(jsonObject.optString("streamingServerType"));
        classroom.setGradeName(jsonObject.isNull("classlevel")?jsonObject.optString("classlevelName"):jsonObject.optString("classlevel"));
        classroom.setCaptureUrl(jsonObject.optString("captureUrl"));
        classroom.setTeacherName(jsonObject.optString("teacherName"));
        classroom.setSubjectName(jsonObject.isNull("subject")?jsonObject.optString("subjectName"):jsonObject.optString("subject"));
        classroom.setType(jsonObject.optString("classroomType"));
        classroom.setAreaPath(jsonObject.optString("areaPath",""));
        if ("PMS".equals(classroom.getStreamingServerType())) {
            classroom.setVideoUrl(classroom.getPmsServerHost() + "/class_" + classroom.getClassRoomId() + "_u_" + classroom.getId() + "__main");
        }
        return classroom;
    }
}
