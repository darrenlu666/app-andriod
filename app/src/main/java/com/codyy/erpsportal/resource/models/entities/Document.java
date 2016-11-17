package com.codyy.erpsportal.resource.models.entities;

import android.support.annotation.IntDef;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档资源
 * Created by gujiajia on 2016/6/14.
 */
public class Document {

    public final static int TYPE_EXCEL = 0;

    public final static int TYPE_PDF = 1;

    public final static int TYPE_WORD = 2;

    private int type;

    private String id;

    private String name;

    private String thumbUrl;

    private int viewCount;

    private int downloadCount;

    public int getType() {
        return type;
    }

    public void setType(@DocumentType int type) {
        this.type = type;
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

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public static JsonParser<Document> sParser = new JsonParser<Document>() {
        @Override
        public Document parse(JSONObject jsonObject) {
            Document document = new Document();
            document.setId(jsonObject.optString("resourceId"));
            if (!jsonObject.isNull("thumbPathUrl")) {
                document.setThumbUrl(jsonObject.optString("thumbPathUrl").trim());
            }
            document.setName(jsonObject.optString("resourceName"));
            document.setViewCount(jsonObject.optInt("viewCnt"));
            document.setDownloadCount(jsonObject.optInt("downloadCnt"));
            return document;
        }
    };

    @IntDef({TYPE_EXCEL,TYPE_PDF,TYPE_WORD})
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    public @interface DocumentType{}
}
