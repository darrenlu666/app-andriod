package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.CryptoUtils;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.OnFiveEvenClickListener;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.ServerAddressDao;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.models.tasks.SavePasswordTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 登陆界面
 */
public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private final static String SP_LOGIN_INFO = "com.codyy.erpsportal.LoginInfo";

    private final static String KEY_USERNAME = "AA";

    private final static String KEY_PASSWORD = "AAA";

    public final static int REQUEST_LOGIN = 0xf1;

    public static final String IS_BACK_TO_MAIN = "go-to-main";

    public final static String EXTRA_INDEX_GOTO = "com.codyy.lrticlassroom.index";

    private boolean isBackToMain = true;

    private TextView mTitleTv;

    private EditText mUserNameEt;

    private EditText mPasswordEt;

    private AutoCompleteTextView mServerAddressEt;

    private int mIndexGoto;

    private DialogUtil mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.d(TAG, "+onCreate");
        setContentView(R.layout.activity_login);
        mIndexGoto = getIntent().getIntExtra(EXTRA_INDEX_GOTO, 0);
        isBackToMain = getIntent().getBooleanExtra(IS_BACK_TO_MAIN, true);
        init();

        //显示原接口地址，去除http头
        mServerAddressEt.setText(URLConfig.BASE.substring(7));

        mTitleTv.setOnTouchListener(new OnFiveEvenClickListener() {
            @Override
            public void onFiveEvenClick() {
                Cog.d(TAG, "onFiveEvenClick");
                mServerAddressEt.setVisibility(View.VISIBLE);
            }
        });

        restoreLoginInfo();

        //添加键盘高度
        InputUtils.getKeyboardHeight(this);
    }

    private void restoreLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences( SP_LOGIN_INFO, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        String passwordEncrypted = sharedPreferences.getString(KEY_PASSWORD, null);
        if (username != null) {
            try {
                String password = CryptoUtils.decrypt(KEY_PASSWORD, passwordEncrypted);
                Cog.d(TAG, "restoreLoginInfo username=", username,
                        ",passwordEncrypted=", passwordEncrypted,
                        ",password=", password);
                mUserNameEt.setText(username);
                mPasswordEt.setText(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化view
     */
    private void init() {
        mLoadingDialog = new DialogUtil(this);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mUserNameEt = (EditText) findViewById(R.id.et_username);
        mPasswordEt = (EditText) findViewById(R.id.et_password);
        mServerAddressEt = (AutoCompleteTextView) findViewById(R.id.et_server_address);

        List<String> serverAddressList = ServerAddressDao.findServerAddresses(this);
        if (serverAddressList != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this
                    , R.layout.item_dropdown_urls
                    , serverAddressList);
            mServerAddressEt.setAdapter(adapter);
        }
//        mServerAddressEt.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mServerAddressEt.showDropDown();
//                }
//            }
//        });
//        mServerAddressEt.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mServerAddressEt.showDropDown();
//            }
//        });
    }

    /**
     * 点击登陆
     *
     * @param v
     */
    public void login(View v) {
        final String username = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        String serverAddress = mServerAddressEt.getText().toString();

        String newBaseUrl = null;

        if (TextUtils.isEmpty(serverAddress.trim())) {
            Toast.makeText(this, "服务地址为空，保持原地址", Toast.LENGTH_SHORT).show();
        } else if (serverAddress.startsWith("http://")){
            newBaseUrl = serverAddress;
        } else {
            newBaseUrl = "http://" + serverAddress;
        }

        if (newBaseUrl != null && !URLConfig.BASE.equals(newBaseUrl)) {
            URLConfig.updateUrls(newBaseUrl);
            saveBaseUrl();
        }

        if (TextUtils.isEmpty(username)) {
            UIUtils.toast(R.string.username_cant_be_empty, Toast.LENGTH_SHORT);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            UIUtils.toast(R.string.password_cant_be_empty, Toast.LENGTH_SHORT);
            return;
        }

        final String passwordMd5 = StringUtils.md5StringFor(password);

        Map<String, String> params = new HashMap<>();

        params.put("userName", username);
        params.put("pwmd5", passwordMd5);

        putDeviceScreenParams(params);

        RequestQueue requestQueue = RequestManager.getRequestQueue();
        mLoadingDialog.showDialog();
        Cog.d(TAG, URLConfig.LOGIN, params);
        requestQueue.add(new NormalPostRequest(URLConfig.LOGIN, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mLoadingDialog.cancel();
                Cog.d(TAG, "login response:" + response);
                String result = response.optString("result");
                if ("success".equals(result)) {
                    new SavePasswordTask(LoginActivity.this).execute(username, passwordMd5);
                    saveServerAddress();
                    UIUtils.toast(R.string.login_successfully, Toast.LENGTH_SHORT);
                    UserInfo userInfo = UserInfo.parseJson(response);
                    UserInfoKeeper.getInstance().setUserInfo(userInfo);
                    executeSaveUserInfo(userInfo);
                    doSaveLoginInfo(username, password);
                    gotoMain(userInfo);
                } else if ("error".equals(result)) {
                    String message = response.optString("message");
                    if (TextUtils.isEmpty(message)) {
                        message = getString(R.string.username_or_pw_wrong);
                    }
                    UIUtils.toast(LoginActivity.this, message, Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "login error:" + error);
                mLoadingDialog.cancel();
                UIUtils.toast("网络连接失败！", Toast.LENGTH_SHORT);
            }
        }));
    }

    /**
     * 保存地址
     */
    private void saveServerAddress() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ServerAddressDao.saveServerAddress(LoginActivity.this, URLConfig.BASE.substring("http://".length()));
            }
        });
    }

    private void doSaveLoginInfo(String username, String password) {
        Cog.d(TAG, "doSaveLoginInfo username=", username, ", password=", password);
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(SP_LOGIN_INFO, MODE_PRIVATE);
            String passwordEncrypted = CryptoUtils.encrypt(KEY_PASSWORD, password);
            Cog.d(TAG, "doSaveLoginInfo passwordEncrypted=", passwordEncrypted);
            Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, passwordEncrypted);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBaseUrl() {
        SharedPreferences sp = getSharedPreferences("url", MODE_PRIVATE);
        sp.edit().putString("base", URLConfig.BASE).apply();
    }

    /**
     * 将屏幕高宽添加进参数
     *
     * @param params
     */
    private void putDeviceScreenParams(Map<String, String> params) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.put("height", displayMetrics.heightPixels + "");
        params.put("width", displayMetrics.widthPixels + "");
    }

    private void gotoMain(UserInfo userInfo) {
        if (isBackToMain) {
            MainActivity.start(LoginActivity.this, userInfo, mIndexGoto);
//                    setResult(RESULT_OK, intent);
            UIUtils.addExitTranAnim(this);
        }
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        if (!mUuidOverdue) {
//            super.onBackPressed();
//        } else {
//            gotoMain(null);
//        }
//    }
//
//    public void onBackClick(View view) {
//        if (!mUuidOverdue) {
//            finish();
//        } else {
//            gotoMain(null);
//        }
//    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    public static void startClearTask(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity)
            UIUtils.addEnterAnim((Activity) context);
    }

    public static void start(Fragment mFragment) {
        Intent intent = new Intent(mFragment.getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mFragment.startActivityForResult(intent, REQUEST_LOGIN);
    }

    /**
     * @param context
     */
    public static void startNoBack(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(IS_BACK_TO_MAIN, false);
        context.startActivity(intent);
    }

    /**
     * 从主界面进登陆
     *
     * @param activity
     * @param index    用户点击应用或是我的进的登陆，待会登陆完了传回给主界面直接跳转到应用或是我的
     */
    public static void start(Activity activity, int index) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(EXTRA_INDEX_GOTO, index);
        activity.startActivity(intent);
    }

    private void executeSaveUserInfo(final UserInfo userInfo) {
        new Thread() {
            @Override
            public void run() {
                UserInfoDao.save(LoginActivity.this, userInfo);
            }
        }.start();
    }
}
