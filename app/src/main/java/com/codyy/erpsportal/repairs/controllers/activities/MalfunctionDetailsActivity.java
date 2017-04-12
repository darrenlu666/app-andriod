package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问题详情
 */
public class MalfunctionDetailsActivity extends AppCompatActivity {

    private final static String TAG = "MalfunctionDetailsActivity";

    private final static String EXTRA_MALFUNCTION_ID = "com.codyy.erpsportal.EXTRA_MALFUNCTION_ID";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.wv_content)
    WebView mContentWv;

    private UserInfo mUserInfo;

    private String mMalfunctionId;

    private RequestSender mSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malfunction_details);
        ButterKnife.bind(this);
        mSender = new RequestSender(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mMalfunctionId = getIntent().getStringExtra(EXTRA_MALFUNCTION_ID);

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("malGuideId", mMalfunctionId);
        mSender.sendRequest(new RequestData(URLConfig.GET_MALFUNCTION_DETAILS, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "onResponse response=", response);
                        JSONObject jsonObject = response.optJSONObject("data");
                        if (jsonObject != null) {
                            mTitleTv.setText(jsonObject.optString("summary"));
                            mContentWv.loadData(jsonObject.optString("description"), "text/html; charset=UTF-8", null);
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.e(TAG, "onErrorResponse error=", error.getMessage());
                    }
                }
        ));
    }

    public static void start(Context context, UserInfo userInfo, String malfunctionId) {
        Intent intent = new Intent(context, MalfunctionDetailsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_MALFUNCTION_ID, malfunctionId);
        context.startActivity(intent);
    }
}
