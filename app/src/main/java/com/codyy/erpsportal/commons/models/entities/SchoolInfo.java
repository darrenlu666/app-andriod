package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/4/15.
 */
public class SchoolInfo extends AreaBase {
    public SchoolInfo() {
        type = "school";
    }

    /**
     * 获取学校列表
     *
     * @param object
     */
    public static void getSchoolInfo(JSONObject object, AreaBase schoolInfos) {
        schoolInfos.setLevel("学校");
        if ("success".equals(object.optString("result"))) {
            List<AreaBase> areaBases = new ArrayList<>();
            JSONArray jsonArray = object.optJSONArray("schools");
            for (int i = 0; i <= jsonArray.length(); i++) {
                SchoolInfo schoolInfo = new SchoolInfo();
                schoolInfo.setType("school");
                schoolInfo.setLevel("学校");
                if (i == 0) {
                    schoolInfo.setAreaName("全部");
                    schoolInfo.setDirect(true);
                    schoolInfo.setAreaId(schoolInfos.getAreaId());
                    schoolInfo.setType("school_all");
                } else {
                    JSONObject jsonObject = jsonArray.optJSONObject(i - 1);
                    schoolInfo.setSchoolID(jsonObject.optString("id"));
                    schoolInfo.setAreaName(jsonObject.optString("name"));
                    schoolInfo.setAreaId(schoolInfos.getAreaId());
                }
                areaBases.add(schoolInfo);
            }
            schoolInfos.setAreaBases(areaBases);
        }
    }
}
