package com.codyy.erpsportal.commons.models.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmdai on 2015/4/15.
 */
public class AreaBase {
    private String selectName = "全部";
    /**
     * 名字
     */
    private String areaName;
    /**
     * id
     */
    private String areaId;
    /**
     * （地区级别）
     */
    public String level;
    /**
     * 类型（学校、地区）
     */
    public String type;
    public String schoolID;
    public List<AreaBase> areaBases = new ArrayList<>();
    public String hasDirect;
    public boolean isDirect;
    public boolean hasChild;

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getHasDirect() {
        return hasDirect;
    }

    public void setHasDirect(String hasDirect) {
        this.hasDirect = hasDirect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public List<AreaBase> getAreaBases() {
        return areaBases;
    }

    public void setAreaBases(List<AreaBase> areaBases) {
        this.areaBases = areaBases;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    /**
     * 获得地区
     *
     * @param object
     * @param areaBases
     */
    public static void getArea(JSONObject object, AreaBase areaBases) {
        if ("success".equals(object.optString("result"))) {
            JSONArray jsonArray = object.optJSONArray("areas");
            String level = object.optString("levelName");
            if ("".equals(level)) {
                areaBases.setLevel("学校");
            } else {
                areaBases.setLevel(level);
            }
            areaBases.setType("area");
            areaBases.setHasDirect(object.optString("hasDirect"));
            List<AreaBase> areaBases1 = new ArrayList<>();
            for (int i = 0; i <= jsonArray.length(); i++) {
                AreaBase areaBase = new AreaBase();
                areaBase.setType("area");
                areaBase.setLevel(level);
                if (i == 0) {
                    areaBase.setAreaId(areaBases.getAreaId());
                    areaBase.setAreaName("全部");
                    areaBase.setType("area_all");
                } else {
                    JSONObject jsonObject = jsonArray.optJSONObject(i - 1);
                    areaBase.setAreaId(jsonObject.optString("areaId"));
                    areaBase.setAreaName(jsonObject.optString("areaName"));
                }
                areaBases1.add(areaBase);
            }
            areaBases.setAreaBases(areaBases1);
            if (jsonArray.length() == 0) {
                areaBases.setHasChild(false);
            } else {
                areaBases.setHasChild(true);
                if ("Y".equals(areaBases.getHasDirect()) || areaBases.getAreaBases().size() == 0) {
                    AreaBase areaBase = new AreaBase();
                    areaBase.setType("area");
                    areaBase.setAreaName("直属校");
                    areaBase.setLevel(level);
                    areaBase.setAreaId(areaBases.getAreaId());
                    areaBases.getAreaBases().add(areaBase);
                }
            }
        }
    }
}
