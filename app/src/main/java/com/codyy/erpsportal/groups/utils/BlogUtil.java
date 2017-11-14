/*
 *
 * 阔地教育科技有限公司版权所有(codyy.com/codyy.cn)
 * Copyright (c)  2017, Codyy and/or its affiliates. All rights reserved.
 *
 */

package com.codyy.erpsportal.groups.utils;

import android.app.Activity;

import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 博文详情工具类
 * 1. 网络请求.
 *
 * Created by poe on 17-10-23.
 */
public class BlogUtil {

    /**
     * 发送一个简单的http pos请求.
     * @param act
     * @param url
     * @param params
     * @param callBack
     */
    public static void SimpleHttpRequest(Activity act, String url, HashMap<String,String> params, final ICallBack callBack){
        RequestSender sender = new RequestSender(act);
        sender.sendRequest(new RequestSender.RequestData(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                callBack.onError(error);
            }
        }));
    }

    public interface ICallBack{

        void onSuccess(JSONObject response);

        void onError(Throwable error);
    }

}
