package com.codyy.erpsportal.commons.data.source.remote;

import org.json.JSONObject;

import io.reactivex.Observable;
import retrofit2.http.GET;


/**
 * Created by gujiajia on 2017/5/15.
 */

public interface VersionApi {

    @GET("app/version/getAndroidPhoneCurrentVersionInfo.do?applicationId=4")
    Observable<JSONObject> getVersion();

}
