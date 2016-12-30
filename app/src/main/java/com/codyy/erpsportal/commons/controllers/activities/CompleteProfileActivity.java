package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 完善个人资料页
 * Created by gujiajia on 2016/12/22.
 */

public class CompleteProfileActivity extends AppCompatActivity {

    private final static String TAG = "CompleteProfileActivity";

    private final static String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,4}";

    private UserInfo mUserInfo;

    private RequestSender mSender;

    private Object mRequestTag;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.et_username)
    EditText mUsernameEt;

    @Bind(R.id.et_password)
    EditText mPasswordEt;

    @Bind(R.id.et_confirm_password)
    EditText mConfirmPasswordEt;

    @Bind(R.id.et_phone)
    EditText mPhoneEt;

    @Bind(R.id.et_email)
    EditText mEmailEt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.bind(this);
        initAttributes();
        initViews();
    }

    private void initAttributes() {
        mSender = new RequestSender(this);
        mRequestTag = new Object();
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
    }

    private void initViews() {
        mUsernameEt.setText(mUserInfo.getUserName());
        mTitleBar.setOnReturnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMain();
            }
        });
    }

    @Override
    public void onBackPressed() {
        jumpToMain();
    }

    private void jumpToMain() {
        MainActivity.start(this, mUserInfo, 0);
        finish();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClick() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        if (!validateAddParams(params)) {
            return;
        }
        Cog.d(TAG, "onSubmitClick url=", URLConfig.COMPLETE_USER_INFO, params);
        mSender.sendRequest(new RequestData(URLConfig.COMPLETE_USER_INFO, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "onSubmitClick response=", response);
                        if (response.optBoolean("result")) {
                            jumpToMain();
                        } else {
                            ToastUtil.showToast(CompleteProfileActivity.this, "完善个人信息失败！");
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Cog.d(TAG, "onSubmitClick error=", error);
                        ToastUtil.showToast(CompleteProfileActivity.this, R.string.net_error);
                    }
                }, mRequestTag
        ));
    }

    /**
     * 验证输入并加入请求参数
     * @param params 请求参数
     * @return true通过本地验证 false未通过验证
     */
    private boolean validateAddParams(Map<String, String> params) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        String confirmPassword = mConfirmPasswordEt.getText().toString();
        String contactPhone = mPhoneEt.getText().toString();
        String email = mEmailEt.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast(this, "用户名不能为空！");
            return false;
        }

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password.trim())) {
            ToastUtil.showToast(this, "密码不能为空！");
            return false;
        }

        String trimPassword = password.trim();
        if (trimPassword.length() < 6 || trimPassword.length() > 20) {
            ToastUtil.showToast(this, "密码长度限制在6到20个字符之间！");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.showToast(this, "请确认密码！");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            ToastUtil.showToast(this, "密码输入不一致！");
            return false;
        }

        if (!TextUtils.isEmpty(email) && email.matches(EMAIL_REGEX)) {
            ToastUtil.showToast(this, "邮箱格式不正确！");
            return false;
        }

        params.put("userName", username);
//        params.put("password", StringUtils.md5StringFor(trimPassword));
        params.put("password", password);
        params.put("contactPhone", contactPhone);
        params.put("email", email);
        return true;
    }

    /**
     * 启动完善个人资料页
     * @param activity 前一个活动
     * @param userInfo 用户信息
     */
    public static void start(Activity activity, UserInfo userInfo) {
        Intent intent = new Intent(activity, CompleteProfileActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        activity.startActivity(intent);
        UIUtils.addExitTranAnim(activity);
    }
}
