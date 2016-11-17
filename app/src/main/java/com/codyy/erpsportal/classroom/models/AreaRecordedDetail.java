package com.codyy.erpsportal.classroom.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 区县管理员 往期录播 实体类
 * Created by ldh on 2016/7/5.
 */
public class AreaRecordedDetail {

    /**
     * errorCode : 测试内容t617
     * list : [{"area":"测试内容z8n6","schoolId":"测试内容6i72","schoolImgUrl":"测试内容273g","schoolName":"测试内容643r"}]
     * result : 测试内容0p45
     * total : 30628
     */

    private String errorCode;
    private String result;
    private int total;
    /**
     * area : 测试内容z8n6
     * schoolId : 测试内容6i72
     * schoolImgUrl : 测试内容273g
     * schoolName : 测试内容643r
     */

    private List<ListEntity> list;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public static class ListEntity {
        private String area;
        private String schoolId;
        private String schoolImgUrl;
        private String schoolName;

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(String schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolImgUrl() {
            return schoolImgUrl;
        }

        public void setSchoolImgUrl(String schoolImgUrl) {
            this.schoolImgUrl = schoolImgUrl;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }
    }

    public static List<ListEntity> parseResult(JSONObject response) {
        List<ListEntity> list = new ArrayList<>();
        if ("success".equals(response.optString("result"))) {
            JSONArray jsonArray = response.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                ListEntity listEntity = new ListEntity();
                JSONObject object = jsonArray.optJSONObject(i);
                listEntity.setArea(object.isNull("schoolAreaPath") ? "" : object.optString("schoolAreaPath"));
                listEntity.setSchoolId(object.isNull("clsSchoolId") ? "" : object.optString("clsSchoolId"));
                listEntity.setSchoolImgUrl(object.isNull("headPic") ? "" : object.optString("headPic"));
                listEntity.setSchoolName(object.isNull("schoolName") ? "" : object.optString("schoolName"));
                list.add(listEntity);
            }
        }
        return list;
    }
}
