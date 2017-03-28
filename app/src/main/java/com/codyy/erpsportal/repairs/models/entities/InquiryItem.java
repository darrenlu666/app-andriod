package com.codyy.erpsportal.repairs.models.entities;

/**
 * 问题追踪项
 * Created by gujiajia on 2017/3/25.
 */

public class InquiryItem {

    private long time;

    private String handlerName;

    private String content;

    /**
     * 图片，追问时才有
     */
    private String[] images;

    /**
     * true 为回复，false 为追问
     */
    private boolean isReply;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }
}
