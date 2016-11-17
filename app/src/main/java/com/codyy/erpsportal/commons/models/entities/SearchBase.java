package com.codyy.erpsportal.commons.models.entities;

/**
 * Created by kmdai on 2015/8/27.
 */
public class SearchBase {
    /**
     * 历史标题
     */
    public final static int HISTORY_TITLE = 0x001;
    /**
     * 历史内容
     */
    public final static int HISTORY_CONT = 0x002;
    /**
     * 删除历史
     */
    public final static int HISTORY_DELETE = 0x003;
    /**
     * 资源标题
     */
    public final static int RESOURCE_TITLE = 0x004;
    /**
     * z资源内容
     */
    public final static int RESOURCE_CONT = 0x005;
    /**
     * 视频内容
     */
    public final static int VIDEO_CONT = 0x006;
    /**
     * 文档内容
     */
    public final static int DOC_CONT = 0x007;
    /**
     * 圈组内容
     */
    public final static int GROUP_CONT = 0x008;
    private int mType;
    private String mTitle;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }
}
