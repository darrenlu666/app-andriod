package com.codyy.erpsportal.commons.models.parsers;

import com.codyy.erpsportal.commons.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by gujiajia on 2015/9/14.
 */
public abstract class JsonParser<T> implements JsonParsable<T> {

    @Override
    final public List<T> parseArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0)
            return null;
        List<T> list = new ArrayList<>(jsonArray.length());
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            T t = parse(jsonObject);
            list.add(t);
        }
        return list;
    }

    public void parseArray(JSONArray jsonArray, OnParsedListener<T> listener) {
        if (jsonArray == null || jsonArray.length() == 0)
            return;
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            T t = parse(jsonObject);
            listener.handleParsedObj(t);
        }
    }

    /**
     * 既有回调，又返回列表
     * @param jsonArray
     * @param listener
     * @return
     */
    public List<T> parseArrayAdditionally(JSONArray jsonArray, OnParsedListener<T> listener) {
        if (jsonArray == null || jsonArray.length() == 0)
            return null;
        List<T> list = new ArrayList<>(jsonArray.length());
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            T t = parse(jsonObject);
            listener.handleParsedObj(t);
            list.add(t);
        }
        return list;
    }

    /**
     * 既有回调，又返回列表
     * @param jsonArray
     * @param listener
     * @return
     */
    public List<T> parseArrayAdditionally(JSONArray jsonArray, OnParsingListener<T> listener) {
        if (jsonArray == null || jsonArray.length() == 0)
            return null;
        List<T> list = new ArrayList<>(jsonArray.length());
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            T t = parse(jsonObject);
            listener.handleParsing(jsonObject, t);
            list.add(t);
        }
        return list;
    }

    /**
     * 检查对应键在json中是否有值，无值返回null，有值返回对应值。
     * @param jsonObject json实体
     * @param key 键
     * @return 空或值
     */
    public static String optStrOrNull(JSONObject jsonObject, String key) {
        if (jsonObject.isNull(key)) {
            return null;
        } else {
            return jsonObject.optString(key);
        }
    }

    /**
     * 检查对应键在json中是否有值，无值返回""，有值返回对应值。
     * @param jsonObject json实体
     * @param key 键
     * @return 空字串或值
     */
    public static String optStrOrBlank(JSONObject jsonObject, String key) {
        if (jsonObject.isNull(key)) {
            return "";
        } else {
            return jsonObject.optString(key);
        }
    }

    public static String optNoHtmlOrNull(JSONObject jsonObject, String key) {
        if (jsonObject.isNull(key)) {
            return null;
        } else {
            return StringUtils.replaceHtml(jsonObject.optString(key));
        }
    }

    public static String optNoHtmlOrBlank(JSONObject jsonObject, String key) {
        if (jsonObject.isNull(key)) {
            return "";
        } else {
            return StringUtils.replaceHtml(jsonObject.optString(key));
        }
    }

    public interface OnParsedListener<O>{
        void handleParsedObj(O obj);
    }

    public interface OnParsingListener<O> {
        void handleParsing(JSONObject jsonObject, O obj);
    }
}
