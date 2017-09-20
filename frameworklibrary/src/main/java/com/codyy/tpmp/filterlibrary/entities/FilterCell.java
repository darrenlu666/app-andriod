package com.codyy.tpmp.filterlibrary.entities;

import android.util.Log;

/**
 * 单个筛选元数据
 *
 * Created by poe on 28/04/17.
 */
public class FilterCell implements Cloneable{
    public static final String TAG = "FilterCell";

    private String id;//当前的id 获取数据用
    private String uuid ;//用户的uuid 获取数据用
    private String name;//当前选中的名称 -default：全部
    private int level;//等级 暂时分 省/市/县/直属校/学校/年纪/学科
    private String levelName ;
    private FilterCell parent ;//父类节点
    private boolean isCheck;//是否选中
    private boolean hasDirect ;//是否有直属校 -仅地区有效

    /**
     * 带第二个id为学校初始化使用》
     * @param uuid
     * @param id
     * @param name
     * @param level
     * @param levelName
     * @param isCheck
     * @param hasDirect
     */
    public FilterCell(String uuid , String id, String name, int level, String levelName, boolean isCheck, boolean hasDirect) {
        this.uuid = uuid;
        this.id = id;
        this.name = name;
        this.level = level;
        this.levelName = levelName;
        this.isCheck = isCheck;
        this.hasDirect = hasDirect;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isHasDirect() {
        return hasDirect;
    }

    public void setHasDirect(boolean hasDirect) {
        this.hasDirect = hasDirect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        Log.i(TAG ,"["+getName()+"]"+"leveName:"+getLevelName()+" set id : " +id);
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public FilterCell getParent() {
        return parent;
    }

    public void setParent(FilterCell parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Override
    protected FilterCell clone() {
        FilterCell fe = new FilterCell(uuid,id,name,level,levelName,isCheck,hasDirect);
        return  fe;
    }
}
