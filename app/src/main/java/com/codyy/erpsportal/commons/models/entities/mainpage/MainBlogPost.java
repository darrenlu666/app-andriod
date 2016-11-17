package com.codyy.erpsportal.commons.models.entities.mainpage;

/**
 * Created by gujiajia on 2016/8/19.
 */
public class MainBlogPost {
    /**
     * blogId : 46289674b4ba413889bbcc2faadf72d2
     * blogTextContent : 乌云在我们心里搁下一块阴影我聆听沉寂已久的心情清晰透明就像美丽的风景总在回忆里才看的清被伤透的心能不...
     * blogTitle : 枫
     * headPic : http://10.1.70.15:8080/ResourceServer/images/3ba2a4e4d6c24b46a26a935f51d77300_67d1156b565c458d950449e12a7d545d.jpg
     * serverAddress : http://10.1.70.15:8080/ResourceServer
     */

    private String blogId;
    private String baseUserId;
    private String blogTextContent;
    private String blogTitle;
    private String headPic;

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getBlogTextContent() {
        return blogTextContent;
    }

    public void setBlogTextContent(String blogTextContent) {
        this.blogTextContent = blogTextContent;
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

    @Override
    public String toString() {
        return blogId + "|" + blogTitle;
    }
}
