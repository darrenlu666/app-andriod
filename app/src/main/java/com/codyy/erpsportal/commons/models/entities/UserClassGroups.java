package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/9/1.
 */
public class UserClassGroups {

    /**
     * groupId : all
     * groupName : 全部
     * studentCount : 2
     */

    private String groupId;
    private String groupName;
    private String studentCount;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setStudentCount(String studentCount) {
        this.studentCount = studentCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getStudentCount() {
        return studentCount;
    }

    /**
     * @param array
     * @param groupses
     */
    public static void getClassGroup(JSONArray array, List<UserClassGroups> groupses) {
        for (int i = 0; i < array.length(); i++) {
            UserClassGroups classGroups = new UserClassGroups();
            JSONObject object = array.optJSONObject(i);
            classGroups.setGroupId(object.optString("groupId"));
            classGroups.setGroupName(object.optString("groupName"));
            classGroups.setStudentCount(object.optString("studentCount"));
            groupses.add(classGroups);
        }
    }
}
