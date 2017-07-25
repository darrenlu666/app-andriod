package com.codyy.tpmp.filterlibrary.models;

/**
 * abstract Title bar parent Entity.
 * Created by poe on 16-1-18.
 */
public class BaseTitleItemBar {
    /**
     * 标题
     */
    private String baseTitle;
    /**
     * ViewHolder类型 默认 ： 0x00
     */
    private int baseViewHoldType = 0x00 ;

    private String id;//some id such as semester id .

    public BaseTitleItemBar() {
    }
    public BaseTitleItemBar(String baseTitle, int baseViewHoldType) {
        this.baseTitle = baseTitle;
        this.baseViewHoldType = baseViewHoldType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseTitle() {
        return baseTitle;
    }

    public void setBaseTitle(String baseTitle) {
        this.baseTitle = baseTitle;
    }

    public int getBaseViewHoldType() {
        return baseViewHoldType;
    }

    public void setBaseViewHoldType(int baseViewHoldType) {
        this.baseViewHoldType = baseViewHoldType;
    }
}
