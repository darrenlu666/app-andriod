package com.codyy.erpsportal.groups.models.entities;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 圈组集合
 * Created by poe on 16-1-8.
 */
public class GroupList {
    /**
     * 圈组的　组别配置信息
     */
    private String groupCateType;//"INTEREST,TEACH"
    /**
     * 推荐圈组
     */
    @SerializedName("recommendedlist")
    private List<Group> recommendedList;
    /**
     * 教研组
     */
    @SerializedName("teachinglist")
    private List<Group> teachList;
    /**
     * 兴趣组
     */
    @SerializedName("interestinglist")
    private List<Group> interestingList;

    public List<Group> getRecommendedList() {
        return recommendedList;
    }

    public void setRecommendedList(List<Group> recommendedList) {
        this.recommendedList = recommendedList;
    }

    public List<Group> getTeachList() {
        return teachList;
    }

    public void setTeachList(List<Group> teachList) {
        this.teachList = teachList;
    }

    public List<Group> getInterestingList() {
        return interestingList;
    }

    public void setInterestingList(List<Group> interestingList) {
        this.interestingList = interestingList;
    }

    public String getGroupCateType() {
        return groupCateType;
    }

    public void setGroupCateType(String groupCateType) {
        this.groupCateType = groupCateType;
    }

    /**
     * 判断是否拥有圈组类型
     * {@link Group#TYPE_TEACHING,Group#TYPE_INTEREST}
     * @param groupType
     * @return
     */
    public boolean hasTeam(String groupType){
        if(TextUtils.isEmpty(groupCateType) || TextUtils.isEmpty(groupType)) return false;

        return groupCateType.contains(groupType);

    }
}
