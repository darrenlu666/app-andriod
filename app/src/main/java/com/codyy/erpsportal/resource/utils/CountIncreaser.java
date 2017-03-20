package com.codyy.erpsportal.resource.utils;


import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gujiajia on 2016/10/19.
 */

public class CountIncreaser {

    private final static String TAG = "CountIncreaser";

    /**
     * 增加下载次数
     */
    public static void increaseDownloadCount(RequestSender requestSender, Object requestTag,
                                             String uuid, String resourceId) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("resourceId", resourceId);
        requestSender.sendRequest(new RequestData(URLConfig.INCREASE_DOWNLOAD_COUNT, params, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "increaseDownloadCount response=", response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "increaseDownloadCount error=", error);
            }
        }, requestTag));
    }

    /**
     * 增加查看次数
     */
    public static void increaseViewCount(RequestSender requestSender, Object requestTag,
                                         String uuid, String resourceId) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("resourceId", resourceId);
        requestSender.sendRequest(new RequestData(URLConfig.INCREASE_VIEW_COUNT, params,
                new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "increaseViewCount response=", response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Cog.d(TAG, "increaseViewCount error=", error);
            }
        }, requestTag));
    }
}
