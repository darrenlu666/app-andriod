package com.codyy.erpsportal.commons.models.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gujiajia on 2015/9/14.
 */
public interface JsonParsable<T> {
    T parse(JSONObject jsonObject);
    List<T> parseArray(JSONArray jsonArray);

}
