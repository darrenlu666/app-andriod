package com.codyy.erpsportal.commons.models.network;

import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.repairs.models.entities.ClassroomFilterItem;
import com.codyy.erpsportal.repairs.models.entities.InquiryItem;
import com.codyy.erpsportal.repairs.models.entities.Malfunction;
import com.codyy.erpsportal.repairs.models.entities.RepairDetails;
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
            } else if (urlStr.equals(URLConfig.GET_REPAIR_DETAILS)) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("result", "success");
                RepairDetails repairDetails = new RepairDetails();
                repairDetails.setId("1");
                repairDetails.setSerial("zx201512120001");
                repairDetails.setClassroomSerial("教室编号");
                repairDetails.setClassroomName("教室名称教室名称");
                repairDetails.setDescription("声音太小听不到？声音太小听不到？声音太小听不到？");
                repairDetails.setCategories("硬件故障-班班通故障-声卡问题");
                repairDetails.setReporter("雪诺");
                repairDetails.setPhone("1388888888");
                repairDetails.setReportTime(System.currentTimeMillis());
                repairDetails.setStatus(4);
                repairDetails.setHandlerName("张三");
                responseMap.put("data", repairDetails);
                return buildResponse(chain, new Gson().toJson(responseMap));
            } else if (urlStr.equals(URLConfig.GET_REPAIR_TRACKING)) {
                List<InquiryItem> inquiryItems = new ArrayList<>();
                InquiryItem inquiryItem = new InquiryItem();
                inquiryItem.setReply(false);
                inquiryItem.setContent("听不见，我听不见");
                inquiryItem.setTime(System.currentTimeMillis());
                inquiryItems.add(inquiryItem);

                InquiryItem inquiryItem1 = new InquiryItem();
                inquiryItem1.setReply(true);
                inquiryItem1.setContent("请找医生看眼科");
                inquiryItem1.setHandlerName("张三");
                inquiryItem1.setTime(System.currentTimeMillis());
                inquiryItem1.setReply(true);
                inquiryItems.add(inquiryItem1);

                InquiryItem inquiryItem2 = new InquiryItem();
                inquiryItem2.setReply(false);
                inquiryItem2.setContent("听不见，我还是听不见");
                inquiryItem2.setTime(System.currentTimeMillis());
                inquiryItems.add(inquiryItem2);

                InquiryItem inquiryItem3 = new InquiryItem();
                inquiryItem3.setReply(true);
                inquiryItem3.setContent("再去看");
                inquiryItem3.setHandlerName("张三");
                inquiryItem3.setTime(System.currentTimeMillis());
                inquiryItem3.setReply(true);
                inquiryItems.add(inquiryItem3);

                return buildResponse(chain, inquiryItems);
            }
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
