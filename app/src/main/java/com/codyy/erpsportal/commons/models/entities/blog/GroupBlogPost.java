package com.codyy.erpsportal.commons.models.entities.blog;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 单条博文
 * Created by poe on 16-1-8.
 * {
 "realName": "老师2",
 "headPic": "http://10.5.32.45/reserver/images/8e44e8a16a4747cf8e86dfd1450ee777_146828d880cb4c9e905b67df2c020a6d.jpg",
 "textContent": "按时发顺丰撒按时",
 "serverAddress": "http://10.5.32.45/reserver",
 "blogTitle": "啥地方萨法",
 "blogId": "c5528b5b8bc245af9e10a9f126643d68"
 }
 {
 blogId: "2e1fcfc6649d49d19e4ef023576f948f",
 headPic: "http://www.test5.com/reserver/images/4397707a9484431ea80a1a76d5e4a2e0_5a90429c37a3435a82b05d566c1e728e.jpg",
 baseUserId: "cc9b7baa82f24fcb95f9fc1390c2f976",
 categoryName: null,
 blogTitle: "分享到班级的博文",
 createTime: 1459145000433,
 blogTextContent: "分享到班级的博文",
 serverAddress: "http://www.test5.com/reserver"
 }
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
