package com.codyy.erpsportal.commons.models.parsers;

import com.codyy.erpsportal.commons.models.entities.Classroom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 课堂巡视
 * Created by gujiajia on 2015/10/14.
 */
public class ClassTourClassroomParser implements JsonParsable<Classroom> {

    /**
     * 主讲教师的课堂id
     */
    public String mMainClassroomId;

    @Override
    public Classroom parse(JSONObject jsonObject) {
        Classroom classroom = new Classroom();
        classroom.setId(jsonObject.optString("id"));
        classroom.setClassRoomId(jsonObject.optString("classRoomId"));
        classroom.setSchoolName(jsonObject.optString("schoolName"));
        classroom.setPmsServerHost(jsonObject.optString("serverAddress"));
        classroom.setDmsServerHost(jsonObject.optString("dmsServerHost"));
        classroom.setStreamingServerType(jsonObject.optString("streamingServerType"));
        if (!jsonObject.isNull("roomType")) {
            classroom.setType(jsonObject.optString("roomType"));
        }
        return classroom;
    }

    public List<Classroom> parseArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0)
            return null;
        List<Classroom> list = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            Classroom classroom = parse(jsonObject);
            if ("main".equals(jsonObject.optString("roomType"))) {
                mMainClassroomId = classroom.getClassRoomId();
                list.add(0, classroom);
            } else {
                list.add(classroom);
            }
        }

        for (Classroom classroom: list) {
            if (classroom.isServerTypePms()) {
                if (classroom.isMain()) {
                    classroom.setVideoUrl(classroom.getPmsServerHost() + "/class_" + mMainClassroomId + "_u_" + classroom.getId() + "__main");
                } else {
                    classroom.setVideoUrl(classroom.getPmsServerHost() + "/class_" + mMainClassroomId + "_u_" + classroom.getId() + "_" + classroom.getClassRoomId());
                }
            }
        }
        return list;
    }
}
