package com.codyy.erpsportal.commons.models.entities.my;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 我的-班级空间-博文
 * Created by poe on 16-2-29.
 */
public class MyBlog extends BaseTitleItemBar {
    private String blogId;
    private String blogTitle;
    private String headPic;
    private String blogTextContent;
    private String baseUserId;
    private String realName;
    private long createTime ;

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getBlogTextContent() {
        return blogTextContent;
    }

    public void setBlogTextContent(String blogTextContent) {
        this.blogTextContent = blogTextContent;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
