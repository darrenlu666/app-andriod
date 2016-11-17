package com.codyy.erpsportal.statistics.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * 学期
 * Created by gujiajia on 2016/8/10.
 */
public class TermEntity implements android.os.Parcelable {
    private String id;
    private String name;

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public TermEntity() {
    }

    protected TermEntity(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<TermEntity> CREATOR = new Creator<TermEntity>() {
        @Override
        public TermEntity createFromParcel(Parcel source) {
            return new TermEntity(source);
        }

        @Override
        public TermEntity[] newArray(int size) {
            return new TermEntity[size];
        }
    };

    public static final JsonParser<TermEntity> PARSER = new JsonParser<TermEntity>() {
        @Override
        public TermEntity parse(JSONObject jsonObject) {
            TermEntity termEntity = new TermEntity();
            termEntity.setId(jsonObject.optString("id"));
            termEntity.setName(jsonObject.optString("name"));
            return termEntity;
        }
    };
}
