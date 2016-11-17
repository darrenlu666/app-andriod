package com.codyy.erpsportal.info.utils;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 资讯
 * Created by ningfeng on 2015/8/6.aintained by 顾佳佳
 */
public class Info {
    /**
     * 新闻
     */
    public final static String TYPE_NEWS = "NEWS";
    /**
     * 通知
     */
    public final static String TYPE_NOTICE = "NOTICE";
    /**
     * 公告
     */
    public final static String TYPE_ANNOUNCEMENT = "ANNOUNCEMENT";

    private String informationId;

    private String title;

    private String infoType;

    private String publishTime;

    private String content;

    private String thumb;

    public Info() {
    }

    public Info(String informationId, String title, String infoType, String publishTime, String content) {
        this.informationId = informationId;
        this.title = title;
        this.infoType = infoType;
        this.publishTime = publishTime;
        this.content = content;
    }

    public String getInformationId() {
        return informationId;
    }

    public void setInformationId(String informationId) {
        this.informationId = informationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public static final JsonParser<Info> JSON_PARSER = new JsonParser<Info>() {
        @Override
        public Info parse(JSONObject jsonObject) {
            Info news = new Info();
            news.setContent(optStrOrNull(jsonObject, "content"));
            news.setInformationId(jsonObject.optString("informationId"));
            news.setInfoType(jsonObject.optString("infoType"));
            news.setPublishTime(jsonObject.optString("publishTime"));
            news.setTitle(jsonObject.optString("title"));
            news.setThumb(optStrOrNull(jsonObject,"thumb"));
            return news;
        }
    };

}
