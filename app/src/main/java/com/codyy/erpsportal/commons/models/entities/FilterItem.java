package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

import com.codyy.erpsportal.commons.models.entities.Choice.BaseChoiceParser;
import com.codyy.erpsportal.commons.models.parsers.JsonParsable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 过滤项，最终会转成筛选参数Map向服务端发请求
 */
public class FilterItem implements Parcelable {

    /**
     * url请求的响应类型，响应返回的是JsonObject
     */
    public final static int OBJECT = 0;

    /**
     * url请求的响应类型，响应返回的是数组JsonArray
     */
    public final static int ARRAY = 1;

    public final static String IS_DIRECT = "isDirect";

    /**
     * 过滤项类型名,中文的,如学科，年级
     */
    private String typeName;

    /**
     * 前置条件
     */
    private List<Pair<FilterItem, String>> preconditions;

    /**
     * 影响的筛选项
     */
    private Set<FilterItem> influencingItems;

    /**
     * 请求参数名
     */
    private String paramName;

    /**
     * 选择项名称
     */
    private Choice choice;

    private JsonParsable<Choice> choiceParser;

    /**
     * 请求项地址
     */
    private String url;

    /**
     * url请求的响应类型，默认为json实体
     */
    private int responseType;

    public FilterItem() { }

    public FilterItem(String paramName) { }

    public FilterItem(String typeName, String paramName, String url) {
        this(typeName, paramName, url, OBJECT, new BaseChoiceParser());
    }

    public FilterItem(String typeName, String paramName, String url, int responseType) {
        this(typeName, paramName, url, responseType, new BaseChoiceParser());
    }

    public FilterItem(String typeName, String paramName, String url, int responseType, JsonParsable<Choice> choiceParser) {
        this.typeName = typeName;
        this.paramName = paramName;
        this.url = url;
        this.responseType = responseType;
        this.choiceParser = choiceParser;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Choice getChoice() {
        return choice;
    }

    public boolean setChoice(Choice choice) {
        if (this.choice == null) {
            if (choice == null) {
                return false;
            } else {
                this.choice = choice;
                return true;
            }
        }
        if (this.choice.equals(choice)) {
            return false;
        }
        this.choice = choice;
        return true;
    }

    public JsonParsable<Choice> getChoiceParser() {
        return choiceParser;
    }

    public void setChoiceParser(JsonParsable<Choice> choiceParser) {
        this.choiceParser = choiceParser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResponseType() {
        return responseType;
    }

    public void setResponseType(int responseType) {
        this.responseType = responseType;
    }

    public List<Pair<FilterItem, String>> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<Pair<FilterItem, String>> preconditions) {
        this.preconditions = preconditions;
    }

    public void addPrecondition(FilterItem filterItem, String paramName) {
        if (preconditions == null) preconditions = new ArrayList<>();
        preconditions.add(new Pair<>(filterItem, paramName));
        filterItem.addInfluencingItem(this);//同时将此筛选项加入前置筛选项的被影响筛选项
    }

    public void addInfluencingItem(FilterItem filterItem) {
        if (influencingItems == null) influencingItems = new HashSet<>();
        influencingItems.add(filterItem);
    }

    public Set<FilterItem> getInfluencingItems() {
        return influencingItems;
    }

    public void setInfluencingItems(Set<FilterItem> influencingItems) {
        this.influencingItems = influencingItems;
    }

    public void clearChoice() {
        this.choice = null;
    }

    /**
     * 将已选项转为请求参数Map
     * @param filterItems
     * @return
     */
    public static Map<String, String> obtainParams(List<FilterItem> filterItems) {
        Map<String, String> params = new HashMap<>();
        for (FilterItem filterItem : filterItems) {
            Choice choice = filterItem.getChoice();
            if (choice != null && !choice.isAll() && choice.getId() != null){
                params.put(filterItem.getParamName(), choice.getId());
                if (choice instanceof DirectSchoolsChoice) {//如果地区选了直属校，添加直属校参数
                    params.put(IS_DIRECT, "true");
                }
            }
        }
        return params;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.typeName);
        dest.writeString(this.paramName);
        dest.writeParcelable(this.choice, 0);
        dest.writeString(this.url);
    }

    protected FilterItem(Parcel in) {
        this.typeName = in.readString();
        this.paramName = in.readString();
        this.choice = in.readParcelable(Choice.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Creator<FilterItem> CREATOR = new Creator<FilterItem>() {
        public FilterItem createFromParcel(Parcel source) {
            return new FilterItem(source);
        }

        public FilterItem[] newArray(int size) {
            return new FilterItem[size];
        }
    };
}
