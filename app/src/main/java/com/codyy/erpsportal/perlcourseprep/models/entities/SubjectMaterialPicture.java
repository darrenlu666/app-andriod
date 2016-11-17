package com.codyy.erpsportal.perlcourseprep.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 学科素材名称
 * Created by gujiajia on 2016/1/22.
 */
public class SubjectMaterialPicture implements Parcelable {

    private String id;

    private String name;

    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public final static JsonParser<SubjectMaterialPicture> JSON_PARSER = new JsonParser<SubjectMaterialPicture>() {

        @Override
        public SubjectMaterialPicture parse(JSONObject jsonObject) {
            SubjectMaterialPicture subjectMaterialPicture = new SubjectMaterialPicture();
            subjectMaterialPicture.setId(jsonObject.optString("prepareImageId"));
            subjectMaterialPicture.setName(jsonObject.optString("imageName"));
            subjectMaterialPicture.setUrl(jsonObject.optString("imagePath"));
            return subjectMaterialPicture;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
    }

    public SubjectMaterialPicture() {
    }

    protected SubjectMaterialPicture(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.url = in.readString();
    }

    public static final Creator<SubjectMaterialPicture> CREATOR = new Creator<SubjectMaterialPicture>() {
        public SubjectMaterialPicture createFromParcel(Parcel source) {
            return new SubjectMaterialPicture(source);
        }

        public SubjectMaterialPicture[] newArray(int size) {
            return new SubjectMaterialPicture[size];
        }
    };
}
