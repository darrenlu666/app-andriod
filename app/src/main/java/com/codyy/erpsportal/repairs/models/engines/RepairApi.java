package com.codyy.erpsportal.repairs.models.engines;

import org.json.JSONObject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 接口
 * Created by gujiajia on 2017/4/17.
 */

public interface RepairApi {
    @GET("mobile/malfunction/getClassRoom.do")
    Observable<JSONObject> getClassRoom(
            @Query("skey") String skey,
            @Query("uuid") String uuid);

}
