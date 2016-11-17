package com.codyy.erpsportal.commons.models.entities.mainpage;

/**
 * 首页无直播，推荐的圈组
 * Created by gujiajia on 2016/8/14.
 */
public class MainResGroup {

    /**
     * categoryName :
     * classlevelName : 中国一年级
     * groupId : 21c57e4bcbca45b184574e5b5cd1f4af
     * groupName : 口香糖不能经常嚼
     * limited : 0
     * memberCount : 3
     * pic : http://10.1.70.15:8080/ResourceServer/images/http://10.1.70.15:8080/ResourceServer/images/3ba2a4e4d6c24b46a26a935f51d77300_8e0da78a26914f498a532324d07b24fc.jpg
     * realName : guojia老师
     * subjectName : 中国语文
     */

    private String categoryName;
    private String classlevelName;
    private String groupId;
    private String groupName;
    private int limited;
    private int memberCount;
    private String pic;
    private String realName;
    private String subjectName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getClasslevelName() {
        return classlevelName;
    }

    public void setClasslevelName(String classlevelName) {
        this.classlevelName = classlevelName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getLimited() {
        return limited;
    }

    public void setLimited(int limited) {
        this.limited = limited;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
