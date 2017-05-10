package com.codyy.erpsportal.commons.data.source.remote;

import com.codyy.erpsportal.commons.data.Login;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


/**
 * 网络接口
 * Created by gujiajia on 2016/11/9.
 */

public interface WebApi {
    @POST("newLogin.do")
    Observable<Login> doLogin(
            @Query("width") int width,
            @Query("height") int height,
            @Query("userName") String username,
            @Query("pwmd5") String passwordMd5);

    @GET("index/getHomePageInitInfo.do")
    Observable<JSONObject> getHomePageInitInfo(
            @Query("schoolId") String schoolId,
            @Query("baseAreaId") String baseAreaId);

    @POST
    @FormUrlEncoded
    Observable<JSONObject> post4Json(@Url String url, @FieldMap Map<String, String> params);

    @POST
    Observable<JSONObject> post4Json(@Url String url);

    @POST
    @FormUrlEncoded
    Observable<JSONArray> post4Ja(@Url String url, @FieldMap Map<String, String> params);

    @POST
    Observable<JSONArray> post4Ja(@Url String url);

    @POST
    Observable<String> post4Str(@Url String url);

    @POST
    @FormUrlEncoded
    Observable<String> post4Str(@Url String url, @FieldMap Map<String, String> params);

    @GET
    Observable<JSONObject> getJson(@Url String url, @QueryMap Map<String, String> params);

    @GET
    Observable<JSONObject> getJson(@Url String url);

    @GET
    Observable<String> get4Str(@Url String url);
}
