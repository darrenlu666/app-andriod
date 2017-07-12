package com.codyy.erpsportal.commons.models.network;

import com.codyy.erpsportal.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 用于产生假数据的拦截器
 * Created by gujiajia on 2017/3/20.
 */

public class FakeInterceptor implements Interceptor {

    private final static String TAG = "FakeInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(BuildConfig.DEBUG) {
//            HttpUrl httpUrl = chain.request().url();
//            String urlStr = httpUrl.url().toString();
//            if (urlStr.equals(URLConfig.VERSION + "?applicationId=4")) {
//                Cog.d(TAG, "urlStr:", urlStr);
//                Response response = chain.proceed(chain.request());
//                ResponseBody responseBody = response.body();
//                String bodyStr = responseBody.string();
//                try {
//                    JSONObject jsonObject = new JSONObject(bodyStr);
//                    jsonObject.put("upgrade_ind", "N");
//                    bodyStr = jsonObject.toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Cog.d(TAG, "bodyStr:", bodyStr);
//                return buildResponse(chain, bodyStr);
//            }
        }
        return chain.proceed(chain.request());
    }

    private <T> Response buildResponse(Chain chain, List<T> list) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("total", list.size());
        responseMap.put("result", "success");
        responseMap.put("list", list);
        Gson gson = new Gson();
        String responseString = gson.toJson(responseMap);
        return buildResponse(chain, responseString);
    }

    private Response buildResponse(Chain chain, String responseBody) {
        return new Response.Builder()
                .code(200)
                .message(responseBody)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseBody.getBytes()))
                .addHeader("content-type", "application/json")
                .build();
    }
}
