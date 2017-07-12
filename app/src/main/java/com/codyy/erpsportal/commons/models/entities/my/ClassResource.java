package com.codyy.erpsportal.commons.models.entities.my;


import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 班级空间－优客资源　
 * Created by poe on 16-8-16.
 */
public class ClassResource extends BaseTitleItemBar {
    private String realName;
    private String resThumb;
    private String resourceId;
    private String resourceName;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getResThumb() {
        return resThumb;
    }

    public void setResThumb(String resThumb) {
        this.resThumb = resThumb;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
