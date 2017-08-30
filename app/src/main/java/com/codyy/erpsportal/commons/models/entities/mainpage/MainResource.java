package com.codyy.erpsportal.commons.models.entities.mainpage;

import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 集团校－优课资源
 * Created by poe on 17-8-23.
 */

public class MainResource extends BaseTitleItemBar {

    /**
     * baseResourceServerId : 75d20617d2b14eaf8456b674c4ed4722
     * resourceColumn : video
     * resourceId : dad0dd31691b412b90e8a0fb495c25f3
     * resourceName : 啊啊看看名字超级长啊啊啊看看名字超级长啊啊啊看看名字超级长啊
     * thumbPath : http://10.1.70.15:8080/ResourceServer/images/000000_dcd0d1c8a3f244e48c86fa274d6354cb.jpg
     */

    private String baseResourceServerId;
    private String resourceColumn;
    private String resourceId;
    private String resourceName;
    private String thumbPath;

    public String getBaseResourceServerId() {
        return baseResourceServerId;
    }

    public void setBaseResourceServerId(String baseResourceServerId) {
        this.baseResourceServerId = baseResourceServerId;
    }

    public String getResourceColumn() {
        return resourceColumn;
    }

    public void setResourceColumn(String resourceColumn) {
        this.resourceColumn = resourceColumn;
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

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
