package com.codyy.erpsportal.groups.models.entities;

import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;

import java.io.Serializable;

/**
 * 圈组-资源
 * Created by poe on 16-6-17.
 */

public class GroupResource extends BaseTitleItemBar implements Serializable {
    public final static String TYPE_DOCUMENT = "doc";
    public final static String TYPE_VIDEO = "video";
    private String id;
    private String title;
    private String iconUrl;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
