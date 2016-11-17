package com.codyy.erpsportal.commons.models.entities.filter;

import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.controllers.fragments.filters.GroupFilterFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/6/11.
 */
public class AreaList {
    /**
     * 区域类型 - 数据解析
     */
    public static final int TYPE_PARSE_AREA = 1;
    /**
     * 学校类型 - 数据解析
     */
    public static final int TYPE_PARSE_SCHOOL = 2;
    /**
     * 年纪类型 - 数据解析
     */
    public static final int TYPE_PARSE_GRADE = 3;
    /**
     * 学科类型 - 数据解析
     */
    public static final int TYPE_PARSE_SUBJECT = 4;
    /**
     * 学段类型 - 数据解析
     */
    public static final int TYPE_PARSE_SEMESTER = 5;
    /**
     * 分类类型 - 数据解析
     */
    public static final int TYPE_PARSE_CATEGORY = 6;


	private String levelName;

	private String hasDirect;

    private List<AreaItem> areaItemlist = new ArrayList<>();

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getHasDirect() {
		return hasDirect;
	}

	public void setHasDirect(String hasDirect) {
		this.hasDirect = hasDirect;
	}

	public List<AreaItem> getAreaItemlist() {
        return areaItemlist;
    }

    public void setAreaItemlist(List<AreaItem> areaItemlist) {
        this.areaItemlist = areaItemlist;
    }

    public static AreaList parse(JSONObject jsonObj1,int type,String selectArea) throws JSONException{
        AreaList areaList=null;
        String s= StringUtils.replaceHtml(jsonObj1.toString());
        JSONObject jsonObj = new JSONObject(s);
        String result = jsonObj.optString("result");
        if (!result.equals("success")) {
            return areaList;
        }
        areaList = new AreaList();
        if (type==TYPE_PARSE_AREA){//地区
            if (jsonObj.isNull("levelName")) {
                areaList.setLevelName(" ");
            } else {
                areaList.setLevelName(jsonObj.optString("levelName"));
            }
            areaList.setHasDirect(jsonObj.optString("hasDirect"));
            if (jsonObj.has("areas")) {
                JSONArray areas = jsonObj.optJSONArray("areas");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem=new AreaItem(areasItem.optString("areaName"),areasItem.optString("areaId"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
        }else if (type==TYPE_PARSE_SCHOOL){//学校
            areaList.setLevelName(GroupFilterFragment.STR_SCHOOL);
            if (jsonObj.has("schools")) {
                JSONArray areas = jsonObj.optJSONArray("schools");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem = new AreaItem(areasItem.optString("name"),areasItem.optString("id"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
            if (jsonObj.has("list")) {
                JSONArray areas = jsonObj.optJSONArray("list");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem = new AreaItem(areasItem.optString("name"),areasItem.optString("id"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
        }else if(type==TYPE_PARSE_GRADE) {//年级
            areaList.setLevelName(GroupFilterFragment.STR_LEVEL);
            if (jsonObj.has("list")) {
                JSONArray areas = jsonObj.optJSONArray("list");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem = new AreaItem(areasItem.optString("name"),areasItem.optString("id"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
        }else if(type==TYPE_PARSE_SUBJECT){//  4： 学科
            areaList.setLevelName(GroupFilterFragment.STR_SUBJECT);
            if (jsonObj.has("list")) {
                areaList.setLevelName(GroupFilterFragment.STR_SUBJECT);
                JSONArray areas = jsonObj.optJSONArray("list");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem = new AreaItem(areasItem.optString("name"),areasItem.optString("id"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
        }else if(type==TYPE_PARSE_SEMESTER){// 5: 学段
            if (jsonObj.has("list")) {
                areaList.setLevelName(GroupFilterFragment.STR_SEMESTER);
                JSONArray areas = jsonObj.optJSONArray("list");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem = new AreaItem(areasItem.optString("name"),areasItem.optString("id"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
        }else if(type == TYPE_PARSE_CATEGORY){//6. 分类 -兴趣组
            if (jsonObj.has("list")) {
                areaList.setLevelName(GroupFilterFragment.STR_CATEGORY);
                JSONArray areas = jsonObj.optJSONArray("list");
                List<AreaItem> mAreaItemlist = new ArrayList<>();
                AreaItem areaItem1=new AreaItem("全部","",selectArea);
                mAreaItemlist.add(areaItem1);
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject areasItem = (JSONObject) areas.opt(i);
                    AreaItem areaItem = new AreaItem(areasItem.optString("categoryName"),areasItem.optString("groupCategoryId"),selectArea);
                    mAreaItemlist.add(areaItem);
                }
                areaList.setAreaItemlist(mAreaItemlist);
            }
        }
        return areaList;
    }
}
