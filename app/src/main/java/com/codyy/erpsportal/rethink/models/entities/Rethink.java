package com.codyy.erpsportal.rethink.models.entities;

import com.codyy.erpsportal.commons.models.parsers.JsonParsable;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 教学反思列表项
 * Created by gujiajia on 2015/12/31.
 */
public class Rethink {

    private String id;

    private String title;

    private String summary;

    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static JsonParsable<Rethink> JSON_PARSER = new JsonParser<Rethink>() {
        @Override
        public Rethink parse(JSONObject jsonObject) {
            Rethink rethink = new Rethink();
            rethink.setId(jsonObject.optString("rethinkId"));
            rethink.setTitle(jsonObject.optString("rethinkTitle"));
            rethink.setSummary(optStrOrNull(jsonObject, "rethinkContentText"));
            rethink.setIcon(optStrOrNull(jsonObject, "subjectPic"));
            return rethink;
        }
    };
}
