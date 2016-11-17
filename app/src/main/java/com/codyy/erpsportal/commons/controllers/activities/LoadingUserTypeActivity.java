package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoadingUserTypeActivity extends AppCompatActivity {

    private final static String TAG = "LoadingUserTypeActivity";

    private RequestSender mRequestSender;

    private Object mRequestTag = new Object();

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_user_type);
        initAttributes();
        loadData();
    }

    private void initAttributes() {
        mUserId = getIntent().getStringExtra(Extra.USER_ID);
        mRequestSender = new RequestSender(this);
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("baseUserId", mUserId);
        mRequestSender.sendRequest(new RequestData(URLConfig.GET_USER_TYPE, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "loadData onResponse=", response);
                        if ("success".equals(response.optString("result"))) {
                            String userType = response.optString("userType");
                            if (UserInfo.checkIsManager(userType)) {
                                Toast.makeText(LoadingUserTypeActivity.this, "无法查看管理员信息！", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                PublicUserActivity.start(LoadingUserTypeActivity.this, mUserId);
                                finish();
                            }
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.d(TAG, "loadData onErrorResponse=", error);
                finish();
            }
        }, mRequestTag));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestSender.stop(mRequestTag);
    }

    public static void start(Activity activity, String baseUserId) {
        Intent intent = new Intent(activity, LoadingUserTypeActivity.class);
        intent.putExtra(Extra.USER_ID, baseUserId);
        activity.startActivity(intent);
    }
}
