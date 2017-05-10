package com.codyy.erpsportal.repairs.models.entities;

/**
 * 常见问题
 * Created by gujiajia on 2017/3/23.
 */

public class Malfunction {
    /**
     * createTime : 1450424495123
     * description : dd
     * guideCatalogId1 : 8f8218efdafe46eb87541a918f47bdb6
     * guideCatalogId2 : 9d69c91262d14486bb01d79537e24836
     * guideCatalogName1 : 在线课堂模块
     * guideCatalogName2 : 不知道
     * malGuideId : ad52b76304ea4a27bab81d43f0c88a3b
     * secondLevelList : []
     * shortSummary : 测试网页抓取图片
     * summary : 测试网页抓取图片
     * viewCount : 32
     */

    private long createTime;
    private String description;
    private String guideCatalogId1;
    private String guideCatalogId2;
    private String guideCatalogName1;
    private String guideCatalogName2;
    private String malGuideId;
    private String shortSummary;
    private String summary;
    private int viewCount;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuideCatalogId1() {
        return guideCatalogId1;
    }

    public void setGuideCatalogId1(String guideCatalogId1) {
        this.guideCatalogId1 = guideCatalogId1;
    }

    public String getGuideCatalogId2() {
        return guideCatalogId2;
    }

    public void setGuideCatalogId2(String guideCatalogId2) {
        this.guideCatalogId2 = guideCatalogId2;
    }

    public String getGuideCatalogName1() {
        return guideCatalogName1;
    }

    public void setGuideCatalogName1(String guideCatalogName1) {
        this.guideCatalogName1 = guideCatalogName1;
    }

    public String getGuideCatalogName2() {
        return guideCatalogName2;
    }

    public void setGuideCatalogName2(String guideCatalogName2) {
        this.guideCatalogName2 = guideCatalogName2;
    }

    public String getMalGuideId() {
        return malGuideId;
    }

    public void setMalGuideId(String malGuideId) {
        this.malGuideId = malGuideId;
    }

    public String getShortSummary() {
        return shortSummary;
    }

    public void setShortSummary(String shortSummary) {
        this.shortSummary = shortSummary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
