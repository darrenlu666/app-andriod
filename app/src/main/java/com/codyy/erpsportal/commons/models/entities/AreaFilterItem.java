package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;

import org.json.JSONObject;

/**
 * Created by gujiajia on 2016/3/7.
 */
public class AreaFilterItem extends ChoicesOption {

    private boolean isSchool;

    public boolean isSchool() {
        return isSchool;
    }

    public void setSchool(boolean school) {
        isSchool = school;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public AreaFilterItem() {
    }

    protected AreaFilterItem(Parcel in) {
        super(in);
    }

    public static final Creator<AreaFilterItem> CREATOR = new Creator<AreaFilterItem>() {
        public AreaFilterItem createFromParcel(Parcel source) {
            return new AreaFilterItem(source);
        }

        public AreaFilterItem[] newArray(int size) {
            return new AreaFilterItem[size];
        }
    };

    public static final JsonParser<Choice> AREA_CHOICE_PARSER = new JsonParser<Choice>() {
        @Override
        public Choice parse(JSONObject jsonObject) {
            Choice choice = new Choice();
            choice.setId(jsonObject.optString("areaId"));
            choice.setTitle(jsonObject.optString("areaName"));
            return choice;
        }
    };

    public static final JsonParser<Choice> SCHOOL_CHOICE_PARSER = new JsonParser<Choice>() {
        @Override
        public Choice parse(JSONObject jsonObject) {
            Choice choice = new Choice();
            choice.setId(jsonObject.optString("id"));
            choice.setTitle(jsonObject.optString("name"));
            return choice;
        }
    };
}
