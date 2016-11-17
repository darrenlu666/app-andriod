package com.codyy.erpsportal.commons.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合首页的自动上下翻滚的资讯，推荐的
 * Created by kmdai on 2015/8/7.
 */
public class SwitchInformation {

    /**
     * infoType : NEWS
     * informationId : 332ea37e9447495a83e5c439c6df112c
     * title : guwei3
     */
    private String infoType;
    private String informationId;
    private String title;

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public void setInformationId(String informationId) {
        this.informationId = informationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoType() {
        return infoType;
    }

    public String getInformationId() {
        return informationId;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @param object
     * @param firstPageMixInformations
     */
    public static void getFirstPageMixInformation(JSONObject object, List<SwitchInformation> firstPageMixInformations) {
        if ("success".equals(object.optString("result"))) {
            if (firstPageMixInformations == null) {
                firstPageMixInformations = new ArrayList<>();
            }
            JSONArray array = object.optJSONArray("info");
            for (int i = 0; i < array.length(); i++) {
                SwitchInformation firstPageMixInformation = new SwitchInformation();
                JSONObject object1 = array.optJSONObject(i);
                firstPageMixInformation.setInformationId(object1.optString("informationId"));
                firstPageMixInformation.setInfoType(object1.optString("infoType"));
                firstPageMixInformation.setTitle(object1.optString("title"));
                firstPageMixInformations.add(firstPageMixInformation);
            }
        }
    }

    public static final JsonParser<SwitchInformation> PARSER = new JsonParser<SwitchInformation>() {
        @Override
        public SwitchInformation parse(JSONObject jsonObject) {
            SwitchInformation switchInformation = new SwitchInformation();
            switchInformation.setInformationId(jsonObject.optString("informationId"));
            switchInformation.setInfoType(jsonObject.optString("infoType"));
            switchInformation.setTitle(jsonObject.optString("title"));
            return switchInformation;
        }
    };
}
