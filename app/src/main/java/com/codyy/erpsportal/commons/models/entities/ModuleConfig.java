package com.codyy.erpsportal.commons.models.entities;

import android.support.annotation.StringDef;

import com.codyy.erpsportal.commons.models.Titles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 模块配置
 * Created by gujiajia on 2015/8/12.
 */
public class ModuleConfig {

    /**
     * 首页模板：综合
     */
    public final static String TEMPLATE_COMPOSITE = "index.template.composite";

    /**
     * 首页模板：监管
     */
    public final static String TEMPLATE_MONITOR = "index.template.monitor";

    /**
     * 首页模板：资源
     */
    public final static String TEMPLATE_RESOURCE = "index.template.resource";

    /**
     * 首页模版：资源无直播
     */
    public final static String TEMPLATE_RESOURCE_NO_LIVE = "index.template.resource.noLive";

    /**
     * 首页模板：纯监管
     */
    public final static String TEMPLATE_PURE_MONITOR = "index.template.puremonitor";

    /**
     * 首页模板:玉溪/沾益
     */
    public final static String TEMPLATE_YX＿ZHY = "index.template.zhanyi";


    /**
     * 首页模板：天津
     */
    public final static String TEMPLATE_TJ = "index.template.TJ";

    /**
     * 首页模板：费县
     */
    public final static String TEMPLATE_FX = "index.template.fx";

    /**
     * 首页模板：苏州园区
     */
    public final static String TEMPLATE_SIP = "index.template.sipstl";

    /**
     * 首页模板：台州
     */
    public final static String TEMPLATE_TZ = "index.template.taizhou";

    /**
     * 首页模板: 台州(v.5.3.8补充版).
     */
    public final static String TEMPLATE_TZ_SCHOOL = "index.template.taizhou.school";

    /**
     * 首页模板：集团校
     */
    public final static String TEMPLATE_GROUP_SCHOOL = "index.template.jtx";

    /**
     * 首页模板：海宁资源
     */
    public final static String TEMPLATE_HN_RES = "index.template.haiNing";

    /**
     * 地区
     */
    public final static String VISIT_TYPE_AREA = "area";

    /**
     * 学校
     */
    public final static String VISIT_TYPE_SCHOOL = "school";

    /**
     * 地区名称
     */
    private String areaName;

    /**
     * 地区id
     */
    private String baseAreaId;

    private String areaCode;

    /**
     * 学校id
     */
    private String schoolId;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 频道页标签配置，加载哪些频道页
     */
    private List<ChannelTab> channelTabs;

    private MainPageConfig mainPageConfig;

    /**
     * 首页模板
     */
    private String indexTemplateId;

    /**
     * 访问类型
     */
    private
    @VisitType
    String visitType;

    @Override
    public String toString() {
        return "ModuleConfig:areaName=" + areaName + ",baseAreaId=" + baseAreaId + ",areaCode="
                + areaCode + ",schoolId=" + schoolId + ",schoolName=" + schoolName + ",visitType=" + visitType;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getBaseAreaId() {
        return baseAreaId;
    }

    public void setBaseAreaId(String baseAreaId) {
        this.baseAreaId = baseAreaId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getIndexTemplateId() {
        return indexTemplateId;
    }

    public void setIndexTemplateId(String indexTemplateId) {
        this.indexTemplateId = indexTemplateId;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public List<ChannelTab> getChannelTabs() {
        return channelTabs;
    }

    public void setChannelTabs(List<ChannelTab> channelTabs) {
        this.channelTabs = channelTabs;
    }

    public MainPageConfig getMainPageConfig() {
        return mainPageConfig;
    }

    public void setMainPageConfig(MainPageConfig mainPageConfig) {
        this.mainPageConfig = mainPageConfig;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }


    @StringDef(value = {
            VISIT_TYPE_AREA,
            VISIT_TYPE_SCHOOL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface VisitType {
    }

    /**
     * 解析配置数据
     * @param jsonObject
     * @return
     */
    public static ModuleConfig parseJsonObject(JSONObject jsonObject) {
        ModuleConfig config = new ModuleConfig();
        config.setBaseAreaId(jsonObject.optString("baseAreaId"));
        config.setAreaName(jsonObject.optString("areaName"));
        config.setAreaCode(jsonObject.optString("areaCode"));
        config.setSchoolId(jsonObject.optString("schoolId"));
        config.setSchoolName(jsonObject.optString("schoolName"));
        config.setIndexTemplateId(jsonObject.optString("indexTemplateId"));
        config.setVisitType(jsonObject.optString("visitType"));
        //获取标签页配置
        JSONArray tabArray = jsonObject.optJSONArray("homePageMenu");
        List<ChannelTab> channelTabs = ChannelTab.parseJsonArray(tabArray);
        config.setChannelTabs(channelTabs);

        //获取首页（综合）、首页（资源）、网络教研配置页内部显示模块配置
        JSONObject pagesObj = jsonObject.optJSONObject("indexFunc");
        MainPageConfig mainPageConfig = MainPageConfig.parseJson(pagesObj);
        config.setMainPageConfig(mainPageConfig);

        JSONObject titlesObj = jsonObject.optJSONObject("baseFrontConfig");
        Titles.parseTitles( titlesObj);
        return config;
    }
}
