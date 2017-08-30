package com.codyy.erpsportal.commons.models.entities.blog;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 单条博文
 * Created by poe on 16-1-8.
 */
public class GroupBlogPost extends BaseTitleItemBar implements Serializable {

    private String blogId;
    @SerializedName("headPic")
    private String blogPicture;
    private String blogTitle;
    private String textContent;
    private String serverAddress;
    private String realName;
    private String baseUserId;

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

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }
}
