package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 登陆界面
 */
public class ResetPasswordActivity extends FragmentActivity {


    @Bind(R.id.login_editext_name)
    EditText mPasswordEt;
    @Bind(R.id.login_editext_passwd2)
    EditText mNewPasswordEt;
    @Bind(R.id.login_editext_repasswd2)
    EditText loginEditextRepasswd2;

    private String normalStyleStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,.;~!@#$%^&*()_+-=\\\\/<>";

    @OnClick(R.id.login_bt_login2)
    public void onConfirmClick() {

        String newPassword= mNewPasswordEt.getText().toString();
        String repeatNewPassword=loginEditextRepasswd2.getText().toString();
        String pass= mPasswordEt.getText().toString();
        if (newPassword.length()<=0||repeatNewPassword.length()<=0||pass.length()<=0){
            Toast.makeText(ResetPasswordActivity.this, "原始密码和新密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }else{

            if (!newPassword.equals(repeatNewPassword)){
                Toast.makeText(ResetPasswordActivity.this, "两次输入的新密码不相同!", Toast.LENGTH_SHORT).show();
                mNewPasswordEt.setText("");
                loginEditextRepasswd2.setText("");
                return;
            }
            if(newPassword.length()<=5||repeatNewPassword.length()<=5||newPassword.length()>18||repeatNewPassword.length()>18){
                Toast.makeText(ResetPasswordActivity.this, "密码字节长度需为6-18字符", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!containNormalCharacter(newPassword)){
                Toast.makeText(ResetPasswordActivity.this, "只可包含字母、数字、常用英文符号", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        RequestSender requestSender = new RequestSender(this);
        Map<String, String> params = new HashMap<>();
        UserInfoKeeper userInfoKeeper = UserInfoKeeper.getInstance();
        UserInfo userInfo = userInfoKeeper.getUserInfo();
        params.put("uuid", userInfo.getUuid());
        params.put("baseUserId", userInfo.getBaseUserId());
        params.put("oldPassword", pass);
        params.put("newPassword", newPassword);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.CHANGE_PASS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if ("success".equals(response.optString("result"))) {
                            Toast.makeText(ResetPasswordActivity.this, "密码修改成功!", Toast.LENGTH_SHORT).show();
                            finish();
                            UIUtils.addExitTranAnim(ResetPasswordActivity.this);
                        }else{
                            Toast.makeText(ResetPasswordActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ResetPasswordActivity.this, "网络请求出错!", Toast.LENGTH_SHORT).show();
            }
        }));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        ButterKnife.bind(this);
    }

    private boolean containNormalCharacter(String keyword){
        for(int i = 0; i<keyword.length();i++){
            if(!normalStyleStr.contains(keyword.substring(i,i+1)))
                return false;
        }

        return true;
    }

    public static void start(Context context ){
        Intent it = new Intent(context, ResetPasswordActivity.class);
        context.startActivity(it);
        UIUtils.addEnterAnim((Activity) context);
    }
}
