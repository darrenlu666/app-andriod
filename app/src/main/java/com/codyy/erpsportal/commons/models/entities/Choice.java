package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 筛选选择项
 */
public class Choice implements Parcelable {

    public final static String ALL = "all";

    private String title;

    private String id;

    private String placeId;

    public Choice(){}

    public Choice(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Choice(String id, String placeId, String title) {
        this.id = id;
        this.placeId = placeId;
        this.title = title;
    }

    public Choice(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(JSONObject jsonObject, String key) {
        setTitle(StringUtils.replaceHtml(jsonObject.optString(key)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Choice choice = (Choice) o;
        return !(id != null ? !id.equals(choice.id) : choice.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isAll() {
        return ALL.equals(id);
    }

    /**
     * 无
     * @return
     */
    public boolean isNothing() {
        return id == null || ALL.equals(id);
    }

    /**
     * 通过解析器解析json数组字串
     * @param jsonArray
     * @param parser
     * @return
     */
    public static List<Choice> parseJsonArray(JSONArray jsonArray, BaseChoiceParser parser) {
        if (jsonArray == null || jsonArray.length() == 0) return null;
        List<Choice> choices = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            Choice choice = parser.parse(jsonObject);
            choices.add(choice);
        }
        return  choices;
    }

    /**
     * 基础解析器
     */
    public static class BaseChoiceParser extends JsonParser<Choice> {

        @Override
        public Choice parse(JSONObject jsonObject) {
            Choice choice = new Choice();
            choice.setId(jsonObject.optString("id"));
            choice.setTitle(StringUtils.replaceHtml(jsonObject.optString("name")));
            choice.setPlaceId(jsonObject.optString("placeId"));
            return choice;
        }
    }

    public static class BaseClassParser extends JsonParser<Choice>{
        @Override
        public Choice parse(JSONObject jsonObject) {
            Choice choice = new Choice();
            choice.setId(jsonObject.optString("classId "));
            choice.setTitle(jsonObject.optString("className"));
            return choice;
        }
    }

    public static class BaseTeacherParser extends JsonParser<Choice>{
        @Override
        public Choice parse(JSONObject jsonObject) {
            Choice choice = new Choice();
            choice.setId(jsonObject.optString("baseUserId "));
            choice.setTitle(jsonObject.optString("realName"));
            return choice;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.id);
        dest.writeString(this.placeId);
    }

    protected Choice(Parcel in) {
        this.title = in.readString();
        this.id = in.readString();
        this.placeId = in.readString();
    }

    public static final Creator<Choice> CREATOR = new Creator<Choice>() {
        @Override
        public Choice createFromParcel(Parcel source) {
            return new Choice(source);
        }

        @Override
        public Choice[] newArray(int size) {
            return new Choice[size];
        }
    };
}
