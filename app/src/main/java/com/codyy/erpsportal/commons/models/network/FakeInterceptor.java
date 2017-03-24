package com.codyy.erpsportal.commons.models.network;

import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.repairs.models.entities.ClassroomFilterItem;
import com.codyy.erpsportal.repairs.models.entities.Malfunction;
import com.codyy.erpsportal.repairs.models.entities.RepairRecord;
import com.codyy.erpsportal.repairs.models.entities.RepairSchool;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
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

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(BuildConfig.DEBUG) {
            HttpUrl httpUrl = chain.request().url();
            String urlStr = httpUrl.url().toString();
            if (urlStr.equals(URLConfig.GET_REPAIRS_SCHOOLS)) {
                List<RepairSchool> repairSchools = new ArrayList<>(5);
                for (int i=0; i < 5; i++) {
                    repairSchools.add(new RepairSchool(i+"aaa"
                            , i + "学校", i + "号地区" , 200, 100));
                }
                return buildResponse(chain, repairSchools);
            } else if (urlStr.equals(URLConfig.GET_REPAIR_RECORDS)) {
                List<RepairRecord> repairSchools = new ArrayList<>(5);
                for (int i=0; i < 5; i++) {
                    repairSchools.add(new RepairRecord(i + "aaa"
                            , i + "编号", i + "号教室" , "2B小姐姐", System.currentTimeMillis(), "内容" + i, 0));
                }
                return buildResponse(chain, repairSchools);
            } else if (urlStr.equals(URLConfig.GET_CLASSROOMS)) {
                List<ClassroomFilterItem> items = new ArrayList<>(20);
                for (int i=0; i<20; i++) {
                    items.add(new ClassroomFilterItem(i + ""
                            , "编号" + i * 1000
                            , "3年" + i + "班"));
                }
                return buildResponse(chain, items);
            } else if (urlStr.equals(URLConfig.SEARCH_MALFUNC)) {
                List<Malfunction> malfunctions = new ArrayList<>(8);
                for (int i=0; i<8; i++) {
                    malfunctions.add(new Malfunction("aaa" + i
                            , "问题1"
                            , i));
                }
                return buildResponse(chain, malfunctions);
            }
        }
        return chain.proceed(chain.request());
    }

    private <T> Response buildResponse(Chain chain, List<T> list) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("total", 5);
        responseMap.put("result", "success");
        responseMap.put("list", list);
        Gson gson = new Gson();
        String responseString = gson.toJson(responseMap);
        return new Response.Builder()
                .code(200)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("content-type", "application/json")
                .build();
    }
}
