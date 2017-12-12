package com.codyy.erpsportal.classroom.utils;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 按需发流工具类.
 * Created by poe on 17-12-11.
 */

public class DMSUtils {
    private static final String TAG = "DMSUtils";

    /** 进入直播 **/
    public static void enterLiving(RequestSender mRequestSender, String scheduleId ,String uuid){
        // 观看人数加1
        HashMap<String,String> params = new HashMap<>();
        params.put("deviceSerialNo", DeviceUtils.getDeviceIMEI(EApplication.instance()));
        params.put("scheduleId",scheduleId);
        params.put("uuid",uuid);

        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.INTELLIGENT_DMS_FORWARD_ENTER, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Cog.d(TAG, "onResponse:" + response);
                                if(!"success".equals(response.optString("result",""))){
                                    //do nothing...
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final Throwable error) {
                        Cog.e(TAG, "onErrorResponse:" + error);
                    }
                })
        );
    }

    /** 退出直播 **/
    public static void exitLiving(RequestSender mRequestSender, String scheduleId , String uuid, final ILeave ileave){
        // 观看人数加1
        HashMap<String,String> params = new HashMap<>();
        params.put("deviceSerialNo", DeviceUtils.getDeviceIMEI(EApplication.instance()));
        params.put("scheduleId",scheduleId);
        params.put("uuid",uuid);

        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.INTELLIGENT_DMS_FORWARD_LEAVE, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Cog.d(TAG, "onResponse:" + response);
                                if(!"success".equals(response.optString("result",""))){
                                    //do nothing...
                                    if(null != ileave) ileave.onComplete();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final Throwable error) {
                        Cog.e(TAG, "onErrorResponse:" + error);
                        if(null != ileave) ileave.onComplete();
                    }
                })
        );
    }


    public interface ILeave{
        void onComplete();
    }
}
