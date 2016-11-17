package com.codyy.erpsportal.commons.models.entities.blog;

import com.google.gson.annotations.SerializedName;

/**
 * Created by poe on 16-2-17.
 */
public class BlogDetail {
    private String message;
    private String result;
    private String  createTime;
    private String  publicToGroupFlag;
    private String  serverAddress;
    private String  blogId;
    private String  topFlag;
    private String  headPic;
    private String  commentCount;
    private String  blogTextContent;
    private String  blogCategoryId;
    @SerializedName("categoryName")
    private String  blogCategory;
    private String  publicToSchoolFlag;
    private String  publicToClassFlag;
    private String  publicFlag;
    @SerializedName("realName")
    private String  creatorName;
    private String  baseUserId;
    private String  draftFlag;
    private String  topTime;
    private String  blogLabel;
    private String  viewCount;
    private String  blogTitle;
    private String  baseUserName;
    private String  blogContent;
    private float   ratingAverage;//平均分 浮点
    private String  showAverage;//平均分 str

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public String getShowAverage() {
        return showAverage;
    }

    public void setShowAverage(String showAverage) {
        this.showAverage = showAverage;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPublicToGroupFlag() {
        return publicToGroupFlag;
    }

    public void setPublicToGroupFlag(String publicToGroupFlag) {
        this.publicToGroupFlag = publicToGroupFlag;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(String topFlag) {
        this.topFlag = topFlag;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getBlogTextContent() {
        return blogTextContent;
    }

    public void setBlogTextContent(String blogTextContent) {
        this.blogTextContent = blogTextContent;
    }

    public String getBlogCategoryId() {
        return blogCategoryId;
    }

    public void setBlogCategoryId(String blogCategoryId) {
        this.blogCategoryId = blogCategoryId;
    }

    public String getPublicToSchoolFlag() {
        return publicToSchoolFlag;
    }

    public void setPublicToSchoolFlag(String publicToSchoolFlag) {
        this.publicToSchoolFlag = publicToSchoolFlag;
    }

    public String getPublicToClassFlag() {
        return publicToClassFlag;
    }

    public void setPublicToClassFlag(String publicToClassFlag) {
        this.publicToClassFlag = publicToClassFlag;
    }

    public String getPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(String publicFlag) {
        this.publicFlag = publicFlag;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getDraftFlag() {
        return draftFlag;
    }

    public void setDraftFlag(String draftFlag) {
        this.draftFlag = draftFlag;
    }

    public String getTopTime() {
        return topTime;
    }

    public void setTopTime(String topTime) {
        this.topTime = topTime;
    }

    public String getBlogLabel() {
        return blogLabel;
    }

    public void setBlogLabel(String blogLabel) {
        this.blogLabel = blogLabel;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBaseUserName() {
        return baseUserName;
    }

    public void setBaseUserName(String baseUserName) {
        this.baseUserName = baseUserName;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogCategory() {
        return blogCategory;
    }

    public void setBlogCategory(String blogCategory) {
        this.blogCategory = blogCategory;
    }
}
