package com.codyy.erpsportal.commons.models.entities.blog;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 单条博文
 * Created by poe on 16-1-8.
 */
public class BlogPost extends BaseTitleItemBar implements Serializable {

    private String blogId;
    @SerializedName("headPic")
    private String blogPicture;
    private String blogTitle;
    private String blogContent;
    @SerializedName("blogTextContent")
    private String blogDesc;
    private String blogLabel;
    private String publicFlag;
    private String publicToClassFlag;
    private String publicToSchoolFlag;
    private String publicToGroupFlag;
    private String viewCount;
    private String commentCount;
    private String draftFlag;
    private String topFlag;
    private String topTime;
    private String baseUserId;
    private String baseUserName;
    private String createTime;
    /**
     * 博文分类id
     */
    private String blogCategoryId;
    private String categoryName;
    private String realName;

    public BlogPost() {
    }

    public BlogPost(String blogTitle,int viewHoldType) {
        setBaseViewHoldType(viewHoldType);
        setBaseTitle(blogTitle);
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBlogPicture() {
        return blogPicture;
    }

    public void setBlogPicture(String blogPicture) {
        this.blogPicture = blogPicture;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogDesc() {
        return blogDesc;
    }

    public void setBlogDesc(String blogDesc) {
        this.blogDesc = blogDesc;
    }

    public String getBlogLabel() {
        return blogLabel;
    }

    public void setBlogLabel(String blogLabel) {
        this.blogLabel = blogLabel;
    }

    public String getPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(String publicFlag) {
        this.publicFlag = publicFlag;
    }

    public String getPublicToClassFlag() {
        return publicToClassFlag;
    }

    public void setPublicToClassFlag(String publicToClassFlag) {
        this.publicToClassFlag = publicToClassFlag;
    }

    public String getPublicToSchoolFlag() {
        return publicToSchoolFlag;
    }

    public void setPublicToSchoolFlag(String publicToSchoolFlag) {
        this.publicToSchoolFlag = publicToSchoolFlag;
    }

    public String getPublicToGroupFlag() {
        return publicToGroupFlag;
    }

    public void setPublicToGroupFlag(String publicToGroupFlag) {
        this.publicToGroupFlag = publicToGroupFlag;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getDraftFlag() {
        return draftFlag;
    }

    public void setDraftFlag(String draftFlag) {
        this.draftFlag = draftFlag;
    }

    public String getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(String topFlag) {
        this.topFlag = topFlag;
    }

    public String getTopTime() {
        return topTime;
    }

    public void setTopTime(String topTime) {
        this.topTime = topTime;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getBaseUserName() {
        return baseUserName;
    }

    public void setBaseUserName(String baseUserName) {
        this.baseUserName = baseUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBlogCategoryId() {
        return blogCategoryId;
    }

    public void setBlogCategoryId(String blogCategoryId) {
        this.blogCategoryId = blogCategoryId;
    }
}
