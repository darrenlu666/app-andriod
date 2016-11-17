package com.codyy.erpsportal.commons.models.entities.my;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 班级空间
 * Created by poe on 16-2-29.
 */
public class MyClassRoom extends BaseTitleItemBar {
    private String result;
    private String message;
    private String classId;
    private String schoolName;
    private String className;
    private String headPic;
    private MyAnnounce announcement ;
    @SerializedName("menuNames")
    private List<MyBaseTitle> blogTitleList;
    private List<MyBlog> blogPostList;
    // TODO: 16-3-1 资源分享/话题/... pending to add ..
    private List<ClassResource> resourceList;//资源列表

    public List<ClassResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ClassResource> resourceList) {
        this.resourceList = resourceList;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public MyAnnounce getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(MyAnnounce announcement) {
        this.announcement = announcement;
    }

    public List<MyBaseTitle> getBlogTitleList() {
        return blogTitleList;
    }

    public void setBlogTitleList(List<MyBaseTitle> blogTitleList) {
        this.blogTitleList = blogTitleList;
    }

    public List<MyBlog> getBlogPostList() {
        return blogPostList;
    }

    public void setBlogPostList(List<MyBlog> blogPostList) {
        this.blogPostList = blogPostList;
    }

}
