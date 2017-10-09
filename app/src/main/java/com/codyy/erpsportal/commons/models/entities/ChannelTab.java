package com.codyy.erpsportal.commons.models.entities;

import android.os.Bundle;

import com.codyy.erpsportal.commons.controllers.adapters.ChannelPagerAdapter.ChannelTabInfo;
import com.codyy.erpsportal.commons.controllers.fragments.channels.ChannelCustomizedFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.ChannelLivingFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.ExcellentCoursesFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.FeiXianFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.HaiNingCustomizedFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.HaiNingResFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.InfoIntroFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.MainCompositeFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.MainGroupSchoolFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.MainResFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.ManagementFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.ResourceIntroFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.SipCustomizedFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.SipHomeFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.TeachingResearchFragment;
import com.codyy.erpsportal.commons.controllers.fragments.channels.TianJinFragment;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.groups.controllers.fragments.ChannelBlogPostFragment;
import com.codyy.erpsportal.groups.controllers.fragments.GroupFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * channel tab in channel
 * Created by gujiajia on 2015/8/14.
 */
public class ChannelTab {

    private final static String TAG = "ChannelTab";

    private static String[] sKnownIds = new String[]{"blogid", "classresourceid", "groupid","homepageid",
            "indexcustomehaiNingperiod","informationid","netclassid","netteachid","onlineclassid"};

    private String id;

    private String name;

    public ChannelTab() {
    }

    public ChannelTab(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return id + ":" + name;
    }

    public static ChannelTab parseJsonObject(JSONObject jsonObject) {
        ChannelTab channelTab = new ChannelTab();
        channelTab.setId(jsonObject.optString("baseHomePageMenuId"));
        channelTab.setName(jsonObject.optString("menuName"));
        return channelTab;
    }

    public static List<ChannelTab> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) return new ArrayList<>();
        List<ChannelTab> channelTabs = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            ChannelTab channelTab = parseJsonObject(jsonObject);
            if (Arrays.binarySearch(sKnownIds, channelTab.getId()) < 0) {
                continue;
            }
            channelTabs.add( channelTab);
        }
        return channelTabs;
    }

    /**
     * 根据传递的id，构建不同的Fragment的{@linkplain ChannelTabInfo}
     * @param indexTemplateId
     * @return
     */
    public ChannelTabInfo createTabInfo(String indexTemplateId) {
        Class<?> clazz = null;
        long tabId;
        Bundle bundle = null;
        switch (id) {
            case "homepageid":
                if (ModuleConfig.TEMPLATE_MONITOR.equals(indexTemplateId)
                        || ModuleConfig.TEMPLATE_PURE_MONITOR.equals(indexTemplateId)
                        || ModuleConfig.TEMPLATE_YX＿ZHY.equals(indexTemplateId)) {
                    clazz = ManagementFragment.class;
                    tabId = 0;
                } else if (ModuleConfig.TEMPLATE_COMPOSITE.equals(indexTemplateId)) {
                    clazz = MainCompositeFragment.class;
                    tabId = 1;
                } else if (ModuleConfig.TEMPLATE_RESOURCE.equals(indexTemplateId)){
                    clazz = MainResFragment.class;
                    tabId = 2;
                } else if (ModuleConfig.TEMPLATE_RESOURCE_NO_LIVE.equals(indexTemplateId)) {
                    bundle = new Bundle();
                    bundle.putBoolean(MainResFragment.ARG_NO_LIVE, true);
                    clazz = MainResFragment.class;
                    tabId = 10;
                } else if (ModuleConfig.TEMPLATE_TJ.equals(indexTemplateId)){
                    clazz = TianJinFragment.class;
                    tabId = 11;
                } else if (ModuleConfig.TEMPLATE_FX.equals(indexTemplateId)){//费县
                    clazz = FeiXianFragment.class;
                    tabId = 12;
                } else if (ModuleConfig.TEMPLATE_SIP.equals(indexTemplateId)){//苏州园区
                    clazz = SipHomeFragment.class;
                    tabId = 13;
                } else if (ModuleConfig.TEMPLATE_GROUP_SCHOOL.equals(indexTemplateId)) {//集团校
                    clazz = MainGroupSchoolFragment.class;
                    tabId = 14;
                } else if (ModuleConfig.TEMPLATE_HN_RES.equals(indexTemplateId)) {//海宁资源首页
                    clazz = HaiNingResFragment.class;
                    tabId = 15;
                } else {
                    clazz = FeiXianFragment.class;
                    tabId = 12;
                }

                break;
            case "onlineclassid"://专递课堂
                if(ModuleConfig.TEMPLATE_SIP.equals(indexTemplateId)){
                    bundle = new Bundle();
                    bundle.putString(SipCustomizedFragment.EXTRA_ARG_TITLE,name);
                    clazz = SipCustomizedFragment.class;
                } else {
                    clazz = ChannelCustomizedFragment.class;
                    clazz = HaiNingCustomizedFragment.class;
                }
                tabId = 3;
                break;
            case "netteachid"://网络教研
                clazz = TeachingResearchFragment.class;
                tabId = 4;
                break;
            case "netclassid"://名校网络课堂
                clazz = ChannelLivingFragment.class;
                tabId = 5;
                break;
            case "informationid"://资讯
                clazz = InfoIntroFragment.class;
                tabId = 6;
                break;
            case "classresourceid"://资源
                clazz = ResourceIntroFragment.class;
                tabId = 7;
                break;
            case "blogid"://博文
                clazz = ChannelBlogPostFragment.class;
                tabId = 8;
                break;
            case "groupid"://圈组
                clazz = GroupFragment.class;
                tabId = 9;
                break;
            case "indexcustomehaiNingperiod"://往期录播（海宁定制）
                clazz = ExcellentCoursesFragment.class;
                tabId = 91;
                break;
            default:
                throw new IllegalStateException("Unknown id!id=" + id);
        }
        Cog.d(TAG, "createTabInfo clazz=", clazz );
        return new ChannelTabInfo(name, clazz, bundle, tabId);
    }
}
