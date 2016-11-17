package com.codyy.erpsportal.commons.models.parsers;

import com.codyy.erpsportal.commons.models.entities.TourClassroom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 课堂巡视
 * Created by gujiajia on 2015/10/14.
 */
public class ClassTourClassroomNewParser implements JsonParsable<TourClassroom> {

    /**
     * 主讲教师的课堂id
     */
    @Override
    public TourClassroom parse(JSONObject jsonObject) {
        TourClassroom classroom = new TourClassroom();
//        classroom.setId(jsonObject.optString("id"));
//        classroom.setClassRoomId(jsonObject.isNull("classroomId") ? jsonObject.optString("classRoomId") : jsonObject.optString("classroomId"));
        classroom.setSchoolName(jsonObject.optString("schoolName"));
//        classroom.setPmsServerHost(jsonObject.optString("serverAddress"));
        classroom.setVideoUrl(jsonObject.optString("streamAddress"));
//        classroom.setDmsServerHost(jsonObject.optString("dmsServerHost"));
//        classroom.setStreamingServerType(jsonObject.optString("streamingServerType"));
        if (!jsonObject.isNull("roomType")) {
            classroom.setType(jsonObject.optString("roomType"));
        }
        return classroom;
    }

    public List<TourClassroom> parseArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0)
            return null;
        List<TourClassroom> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            TourClassroom classroom = parse(jsonObject);
            if ("main".equals(jsonObject.optString("roomType"))) {
                list.add(0, classroom);
            } else {
                list.add(classroom);
            }
        }
        return list;
    }
}
