package com.codyy.erpsportal.commons.models.parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gujiajia on 2015/9/14.
 */
public class JsonParseUtil {

    public static <T> T parse(JSONObject jsonObject, JsonParsable<T> parser) {
        return parser.parse(jsonObject);
    }

    public static <T> List<T> parseArray(JSONArray jsonArray, JsonParsable<T> parser) {
        return parser.parseArray(jsonArray);
    }
}
