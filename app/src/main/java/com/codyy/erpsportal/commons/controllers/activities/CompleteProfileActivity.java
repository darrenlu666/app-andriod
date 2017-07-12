package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.Regexes;
import com.codyy.erpsportal.commons.utils.StringUtils;
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

import static com.codyy.erpsportal.commons.utils.Regexes.PASSWORD_REGEX;

/**
 * 完善个人资料页
 * Created by gujiajia on 2016/12/22.
 */

public class CompleteProfileActivity extends AppCompatActivity {

    private final static String TAG = "CompleteProfileActivity";

    private UserInfo mUserInfo;

    private RequestSender mSender;

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
        MainActivity.startNoNeedToCheckUpdate(this, mUserInfo, 0);
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
                        if ("success".equals(response.optString("result"))) {
                            jumpToMain();
                        } else {
                            if (response.optInt("code") == 3) {
                                ToastUtil.showToast(CompleteProfileActivity.this, response.optString("message"));
                            } else {
                                ToastUtil.showToast(CompleteProfileActivity.this, "完善个人信息失败！");
                            }
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.d(TAG, "onSubmitClick error=", error);
                        ToastUtil.showToast(CompleteProfileActivity.this, R.string.net_error);
                    }
                }
        ));
    }

    /**
     * 验证输入并加入请求参数
     *
     * @param params 请求参数
     * @return true通过本地验证 false未通过验证
     */
    private boolean validateAddParams(Map<String, String> params) {
        if (!validateUsername(params)) {
            return false;
        }

        if (!validatePassword(params)) {
            return false;
        }

        if (!validatePhone(params)) {
            return false;
        }

        if (!validateEmail(params)) {
            return false;
        }
        return true;
    }

    /**
     * 验证用户名是否合法，如果合法会加入请求参数
     *
     * @param params 请求参数
     * @return true 通过验证 false未通过
     */
    private boolean validateUsername(Map<String, String> params) {
        String username = mUsernameEt.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast(this, "请输入用户名");
            return false;
        }

        if (username.length() < 5 || username.length() > 30) {
            ToastUtil.showToast(this, "用户名长度需要是5到30个英文字符");
            return false;
        }

        if (!username.matches(Regexes.USERNAME_REGEX)) {
            ToastUtil.showToast(this, "对不起，用户名请输入英文字母、数字、符号（除特殊字符），或组合。");
            return false;
        }
        params.put("userName", username);
        return true;
    }

    /**
     * 验证密码是否合法，如果合法会加入请求参数
     *
     * @param params 请求参数
     * @return true 通过验证 false未通过
     */
    private boolean validatePassword(Map<String, String> params) {
        String password = mPasswordEt.getText().toString();
        String confirmPassword = mConfirmPasswordEt.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password.trim())) {
            ToastUtil.showToast(this, "请输入新密码");
            return false;
        }

        if (password.length() < 6 || password.length() > 18) {
            ToastUtil.showToast(this, "密码长度需要是6到18个英文字符");
            return false;
        }

        if (!password.matches(PASSWORD_REGEX)) {
            ToastUtil.showToast(this, "对不起，密码请输入英文字母、数字、符号（除特殊字符），或组合。");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.showToast(this, "请输入确认密码");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            ToastUtil.showToast(this, "两次输入的新密码不一致！请重新确认");
            return false;
        }
        params.put("password", StringUtils.md5StringFor(password));
        return true;
    }

    /**
     * 验证电话
     *
     * @param params 请求参数
     * @return true 通过验证 false未通过
     */
    private boolean validatePhone(Map<String, String> params) {
        String contactPhone = mPhoneEt.getText().toString();
        if (!TextUtils.isEmpty(contactPhone) && !contactPhone.matches(Regexes.PHONE_REGEX)) {
            ToastUtil.showToast(this, "联系电话格式不正确！");
            return false;
        }
        if (!TextUtils.isEmpty(contactPhone)) {
            params.put("contactPhone", contactPhone);
        }
        return true;
    }

    /**
     * 验证电邮
     *
     * @param params 请求参数
     * @return true 通过验证 false未通过
     */
    private boolean validateEmail(Map<String, String> params) {
        String email = mEmailEt.getText().toString();
        if (!TextUtils.isEmpty(email) && !email.matches(Regexes.EMAIL_REGEX)) {
            ToastUtil.showToast(this, "电子邮箱格式不正确！");
            return false;
        }
        if (!TextUtils.isEmpty(email)) {
            params.put("email", email);
        }
        return true;
    }

    /**
     * 启动完善个人资料页
     *
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
