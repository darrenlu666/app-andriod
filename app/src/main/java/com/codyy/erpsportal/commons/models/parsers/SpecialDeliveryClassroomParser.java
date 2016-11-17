package com.codyy.erpsportal.commons.models.parsers;

import com.codyy.erpsportal.commons.models.entities.Classroom;

import org.json.JSONObject;

/**
 * 专递课堂课堂巡视中课堂的json解析器
 * Created by gujiajia on 2015/9/29.
 */
public class SpecialDeliveryClassroomParser extends JsonParser<Classroom> {

    @Override
    public Classroom parse(JSONObject jsonObject) {
        Classroom classroom = new Classroom();
        classroom.setId(jsonObject.optString("id"));
        classroom.setClassRoomId(jsonObject.optString("classRoomId"));
        classroom.setSchoolName(jsonObject.optString("schoolName"));
        classroom.setPmsServerHost(jsonObject.optString("videoUrl"));
        classroom.setDmsServerHost(jsonObject.optString("dmsServerHost"));
        classroom.setStreamingServerType(jsonObject.optString("streamingServerType"));
        if ("PMS".equals(classroom.getStreamingServerType())) {
            classroom.setVideoUrl(classroom.getPmsServerHost() + "/class_" + classroom.getClassRoomId() + "_u_" + classroom.getId() + "__main");
        }
        return classroom;
    }
}
