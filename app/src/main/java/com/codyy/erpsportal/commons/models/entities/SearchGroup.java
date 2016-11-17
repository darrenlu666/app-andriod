package com.codyy.erpsportal.commons.models.entities;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-3-9.
 */
public class SearchGroup extends SearchBase {

    /**
     * groupId : d8aa7d33eeb64ab38ae409cf720e8643
     * createTime : 1447308476000
     * groupType : TEACH
     * groupName : hello world
     * groupDesc : null
     * pic : http://10.5.225.19:8080/ResourceServer/images/a8112c9d-b665-4782-b2ab-dd5c35d13e5b.jpg
     * approveStatus : null
     * serverAddress : http://10.5.225.19:8080/ResourceServer
     * realName : 国家学校
     * modules : null
     * areaPath : null
     * schoolName : null
     * categoryName : null
     * semesterName : 小学
     * classlevelName : 一年级
     * subjectName : 数学
     * recommendCount : 0
     * closeFlag : null
     * limited : 0
     * memberCount : 4
     */

    private String groupId;
    private long createTime;
    private String groupType;
    private String groupName;
    private String pic;
    private String categoryName;
    private String serverAddress;
    private String realName;
    private String semesterName;
    private String classlevelName;
    private String subjectName;
    private int recommendCount;
    private int limited;
    private int memberCount;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }

    public void setLimited(int limited) {
        this.limited = limited;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPic() {
        return pic;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getRealName() {
        return realName;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    public int getLimited() {
        return limited;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @param object
     * @param groups
     */
    public static void getSearchGroups(JSONObject object, ArrayList<SearchBase> groups, boolean hasHead) {
        JSONArray array = object.optJSONArray("data");
        if (hasHead) {
            SearchBase title = new SearchBase();
            title.setmType(RESOURCE_TITLE);
            title.setTotal(object.optInt("total"));
            groups.add(title);
        }
        Gson gson = new Gson();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            SearchGroup searchGroup = gson.fromJson(jsonObject.toString(), SearchGroup.class);
            searchGroup.setmType(GROUP_CONT);
            groups.add(searchGroup);
        }
    }
}
