package com.codyy.erpsportal.onlinemeetings.models.entities.coco;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 基础湖区list数据集合.
 * Created by poe on 17-9-19.
 */

public class UserListEntity {

    private List<cUser> userList;

    public List<cUser> getUserList() {
        return userList;
    }

    public void setUserList(List<cUser> userList) {
        this.userList = userList;
    }
}
