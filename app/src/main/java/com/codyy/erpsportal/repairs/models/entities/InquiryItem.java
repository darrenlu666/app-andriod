package com.codyy.erpsportal.repairs.models.entities;

import java.util.List;

/**
 * 问题追踪项
 * Created by gujiajia on 2017/3/25.
 */

public class InquiryItem {

    private long createTime;

    private String answererName;

    private String appendDescription;

    /**
     * 图片，追问时才有
     */
    private List<ImageBean> imgs;

    private String appendType;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAnswererName() {
        return answererName;
    }

    public void setAnswererName(String answererName) {
        this.answererName = answererName;
    }

    public String getAppendDescription() {
        return appendDescription;
    }

    public void setAppendDescription(String appendDescription) {
        this.appendDescription = appendDescription;
    }

    public List<ImageBean> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImageBean> imgs) {
        this.imgs = imgs;
    }

    public String getAppendType() {
        return appendType;
    }

    public void setAppendType(String appendType) {
        this.appendType = appendType;
    }

    public boolean isReply() {
        return !"ASK".equals(appendType);
    }

}
