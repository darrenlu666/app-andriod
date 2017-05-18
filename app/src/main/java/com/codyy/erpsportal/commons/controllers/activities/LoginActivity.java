package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.ChangeServerDialog;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.ChangeServerDialog.ServerChangedListener;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.UpdateDialog;
import com.codyy.erpsportal.commons.data.source.remote.VersionApi;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.models.tasks.SavePasswordTask;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.CryptoUtils;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.OnFiveEvenClickListener;
import com.codyy.erpsportal.commons.utils.Regexes;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 登陆界面
 */
public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private final static String SP_TOKEN = "com.codyy.erpsportal.st";

    private final static String KEY_TOKEN = "kt";

    private final static String SP_LOGIN_INFO = "com.codyy.erpsportal.LoginInfo";

    private final static String KEY_USERNAME = "AA";

    private final static String KEY_PASSWORD = "AAA";

    public final static int REQUEST_LOGIN = 0xf1;

    public static final String IS_BACK_TO_MAIN = "go-to-main";

    public final static String EXTRA_INDEX_GOTO = "com.codyy.lrticlassroom.index";

    private boolean mIsBackToMain = true;

    private boolean mPasswordShowing;

    private TextView mTitleTv;

    private EditText mUserNameEt;

    private EditText mPasswordEt;

    private ImageButton mShowPasswordIb;

    private EditText mVerifyCodeEt;

    private LinearLayout mVerifyCodeLl;

    private SimpleDraweeView mVerifyCodeDv;

    private int mIndexGoto;

    private DialogUtil mLoadingDialog;

    private String mLoginToken;

    /**
     * 是否正在获取token
     */
    private boolean mIsFetchingToken;

    private PendingLoginRequestData mPendingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.d(TAG, "+onCreate");
        setContentView(R.layout.activity_login);
        initAttributes();
        initViews();
        restoreLoginInfo();
        //添加键盘高度
        InputUtils.getKeyboardHeight(this);
        loadLoginToken();
        checkNewVersion();
    }

    private void initAttributes() {
        mIndexGoto = getIntent().getIntExtra(EXTRA_INDEX_GOTO, 0);
        mIsBackToMain = getIntent().getBooleanExtra(IS_BACK_TO_MAIN, true);
        SharedPreferences sharedPreferences = getSharedPreferences(SP_TOKEN, MODE_PRIVATE);
        mLoginToken = sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * 加载登录token
     */
    private void loadLoginToken() {
        WebApi webApi = RsGenerator.create(WebApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("securityCode", CryptoUtils.generateSecurityCode());
        if (!TextUtils.isEmpty(mLoginToken)) {
            params.put("token", mLoginToken);
        }
        Cog.d(TAG, "loadLoginToken url=", URLConfig.LOGIN_TOKEN, params);
        mIsFetchingToken = true;
        webApi.post4Json(URLConfig.LOGIN_TOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "loadLoginToken response:" + response);
                        mIsFetchingToken = false;
                        if ("success".equals(response.optString("result"))) {
                            mLoginToken = response.optString("token");
                            if (!showVerifyCodeDueToResponse(response)) {
                                if (mPendingRequest != null)
                                    sendPendingLoginRequest();
                            } else {
                                if (mPendingRequest != null) {
                                    ToastUtil.showToast(LoginActivity.this, "需要输入验证码！");
                                    mPendingRequest = null;
                                    mLoadingDialog.cancel();
                                }
                            }
                            saveToken();
                        } else {
                            if (mPendingRequest != null) {
                                UIUtils.toast(R.string.net_connect_error, Toast.LENGTH_SHORT);
                                mPendingRequest = null;
                                mLoadingDialog.cancel();
                            }
                            mLoginToken = null;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.e(TAG, "loadLoginToken error:" + error);
                        mIsFetchingToken = false;
                        if (mPendingRequest != null) {
                            UIUtils.toast(R.string.net_connect_error, Toast.LENGTH_SHORT);
                            mPendingRequest = null;
                            mLoadingDialog.cancel();
                        }
                    }
                });
    }

    /**
     * 保存登录token
     */
    private void saveToken() {
        SharedPreferences sp = getSharedPreferences(SP_TOKEN, MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, mLoginToken).apply();
    }

    /**
     * 清登录token
     */
    private void clearToken() {
        SharedPreferences sp = getSharedPreferences(SP_TOKEN, MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    /**
     * 检查新版本
     */
    private void checkNewVersion() {
        VersionApi versionApi = RsGenerator.create(VersionApi.class);
        versionApi.getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "checkNewVersion response=", response);
                        String result = response.optString("result");
                        String version = response.optString("version");
                        String forceUpdate = response.optString("upgrade_ind");
                        if ("success".equals(result) && !BuildConfig.VERSION_NAME.equals(version)) {
                            final String url = response.optString("appPhoneUrl");
                            Cog.d(TAG, "checkNewVersion url=", url);
                            UpdateDialog updateDialog = UpdateDialog.newInstance(
                                    "Y".equals(forceUpdate), url);
                            updateDialog.show(getSupportFragmentManager(), "update");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "checkNewVersion error", throwable);
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 显示验证码，根据json对象中的showVerifyCode判断是否显示
     * @param response json对象
     * @return 是否显示
     */
    private boolean showVerifyCodeDueToResponse(JSONObject response) {
        boolean showVerifyCode = response.optBoolean("showVerifyCode");
        if (showVerifyCode) {
            showVerifyCode();
            return true;
        } else {
            mVerifyCodeLl.setVisibility(View.GONE);
            return false;
        }
    }

    /**
     * 显示验证码
     */
    private void showVerifyCode() {
        mVerifyCodeLl.setVisibility(View.VISIBLE);
        loadVerifyCode();
    }

    /**
     * 加载验证码
     */
    private void loadVerifyCode() {
        Cog.d(TAG, "loadVerifyCode mLoginToken=", mLoginToken);
        String url = URLConfig.VERIFY_CODE_IMAGE
                + "?rsaToken=" + URLEncoder.encode(CryptoUtils.encryptText(mLoginToken));
        Uri uri = Uri.parse(url);
        Cog.d(TAG, "loadVerifyCode uri=", uri);
        mVerifyCodeDv.setImageURI(uri);
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
    private void initViews() {
        mLoadingDialog = new DialogUtil(this);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mUserNameEt = (EditText) findViewById(R.id.et_username);
        mPasswordEt = (EditText) findViewById(R.id.et_password);
        mShowPasswordIb = (ImageButton) findViewById(R.id.ib_show_password);
        mVerifyCodeEt = (EditText) findViewById(R.id.et_verify_code);
        mVerifyCodeLl = (LinearLayout) findViewById(R.id.ll_verify_code);
        mVerifyCodeDv = (SimpleDraweeView) findViewById(R.id.dv_verify_code);
        mShowPasswordIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordShowing) {
                    mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordShowing = false;
                } else {
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordShowing = true;
                }
                focusPasswordEnd();
            }
        });
        mTitleTv.setOnTouchListener(new OnFiveEvenClickListener() {
            @Override
            public void onFiveEvenClick() {
                Cog.d(TAG, "onFiveEvenClick");
                ChangeServerDialog changeServerDialog = ChangeServerDialog.newInstance();
                changeServerDialog.setServerChangedListener(new ServerChangedListener() {
                    @Override
                    public void onServerChangedListener() {
                        loadLoginToken();
                    }
                });
                changeServerDialog.show(getSupportFragmentManager(), "change_server");
            }
        });
        mVerifyCodeDv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVerifyCode();
            }
        });
        Button loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });
    }

    private void focusPasswordEnd() {
        mPasswordEt.requestFocus();
        mPasswordEt.setSelection(mPasswordEt.getText().length());
    }

    /**
     * 点击登陆
     */
    public void onLoginClick() {
        final String username = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        String verifyCode = mVerifyCodeEt.getText().toString();

        if (!validateInput(username, "用户名", 5 , 30)) return;
        if (!validateInput(password, "密码", 6, 18)) return;

        Map<String, String> params = new HashMap<>();
        if (mVerifyCodeEt.isShown()) {
            if (TextUtils.isEmpty(verifyCode)) {
                UIUtils.toast(R.string.please_input_verify_code, Toast.LENGTH_SHORT);
                return;
            }
            if (verifyCode.length() != 4) {
                UIUtils.toast(R.string.wrong_verify_code, Toast.LENGTH_SHORT);
                return;
            }
            params.put("verifyCode", verifyCode);
        }

        putDeviceScreenParams(params);

        mLoadingDialog.showDialog();
        if (mIsFetchingToken) {//如果token还在获取,等待token获取
            pendToLogin(username, password, params);
            return;
        }

        if (!TextUtils.isEmpty(mLoginToken)) {
            params.put("token", mLoginToken);
        }
        sendLoginRequest(username, password, params);
    }

    /**
     * 验证用户名或密码
     * @param text 用户名或密码
     * @return true 验证通过，false 验证未通过
     */
    private boolean validateInput(String text, String typeName, int minLength, int maxLength) {
        if (TextUtils.isEmpty(text)) {
            UIUtils.toast(getString(R.string.please_input_s, typeName), Toast.LENGTH_SHORT);
            return false;
        }

        if (text.length() < minLength || text.length() > maxLength ) {
            UIUtils.toast(getString(R.string.s_d_d_length_require, typeName, minLength, maxLength), Toast.LENGTH_SHORT);
            return false;
        }

        if (!text.matches(Regexes.USERNAME_REGEX)) {
            UIUtils.toast(getString(R.string.s_worry_chars, typeName), Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }


    /**
     * 挂起登录动作
     * @param username 用户名
     * @param password 密码
     * @param params 请求参数
     */
    private void pendToLogin(String username, String password, Map<String, String> params) {
        Cog.d(TAG, "pendToLogin");
        mPendingRequest = new PendingLoginRequestData();
        mPendingRequest.username = username;
        mPendingRequest.password = password;
        mPendingRequest.params = params;
    }

    /**
     * 发送被挂起的登录请求
     */
    private void sendPendingLoginRequest() {
        Cog.d(TAG, "sendPendingLoginRequest");
        if (mPendingRequest != null) {
            mPendingRequest.params.put("token", mLoginToken);
            sendLoginRequest(mPendingRequest.username, mPendingRequest.password, mPendingRequest.params);
            mPendingRequest = null;
        }
    }

    /**
     * 发送登录请求
     * @param username 用户名
     * @param password 密码
     * @param params 登录参数
     */
    private void sendLoginRequest(final String username, final String password, final Map<String, String> params) {
        params.put("userName", username);
        final String passwordMd5 = StringUtils.md5StringFor(password);
        params.put("pwmd5", passwordMd5);
        WebApi webApi = RsGenerator.create(WebApi.class);
        Cog.d(TAG, URLConfig.LOGIN_WITH_TOKEN, params);
        webApi.post4Json(URLConfig.LOGIN_WITH_TOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {
                        Cog.d(TAG, "onLoginClick response:" + response);
                        String result = response.optString("result");
                        if ("success".equals(result)) {
                            mLoadingDialog.cancel();
                            new SavePasswordTask(LoginActivity.this).execute(username, passwordMd5);
                            UIUtils.toast(R.string.login_successfully, Toast.LENGTH_SHORT);
                            UserInfo userInfo = UserInfo.parseJson(response);
                            UserInfoKeeper.getInstance().setUserInfo(userInfo);
                            executeSaveUserInfo(userInfo);
                            clearToken();
                            doSaveLoginInfo(username, password);
                            if (response.optBoolean("firstLogin")) {
                                gotoCompleteProfile(userInfo);
                            } else {
                                gotoMain(userInfo);
                            }
                        } else if ("error".equals(result)) {
                            int errorCode = response.optInt("errorCode");
                            if (errorCode != 5) {//5.token过期时需获取验证码
                                mLoadingDialog.cancel();
                            }
                            //1.用户名或密码错 2.验证码错误3.验证码过期5.token过期
                            switch (errorCode) {
                                case 1:
                                    UIUtils.toast(R.string.username_or_pw_wrong, Toast.LENGTH_SHORT);
                                    showVerifyCodeDueToResponse(response);
                                    break;
                                case 2://2.验证码错误
                                    UIUtils.toast(R.string.wrong_verify_code, Toast.LENGTH_SHORT);
                                    refreshVerifyCode();
                                    break;
                                case 3://3.验证码过期
                                    UIUtils.toast(R.string.wrong_verify_code, Toast.LENGTH_SHORT);
                                    refreshVerifyCode();
                                    break;
                                case 5://5.token过期
                                    pendToLogin(username, password, params);
                                    loadLoginToken();
                                    break;
                                case 6://6.账号被锁定
                                    UIUtils.toast(R.string.user_locked, Toast.LENGTH_SHORT);
                                    break;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        Cog.e(TAG, "onLoginClick error:" + error);
                        mLoadingDialog.cancel();
                        UIUtils.toast(R.string.net_connect_error, Toast.LENGTH_SHORT);
                    }
                });
    }

    /**
     * 进入完善个人信息活动
     * @param userInfo 用户信息
     */
    private void gotoCompleteProfile(UserInfo userInfo) {
        CompleteProfileActivity.start(LoginActivity.this, userInfo);
        finish();
    }

    /**
     * 刷新验证码图片
     */
    private void refreshVerifyCode() {
        mVerifyCodeEt.getText().clear();
        mVerifyCodeEt.requestFocus();
        loadVerifyCode();
    }

    /**
     * 保存登录信息
     * @param username 用户名
     * @param password 密码
     */
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

    /**
     * 将屏幕高宽添加进请求参数
     *
     * @param params 请求参数
     */
    private void putDeviceScreenParams(Map<String, String> params) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.put("height", displayMetrics.heightPixels + "");
        params.put("width", displayMetrics.widthPixels + "");
    }

    private void gotoMain(UserInfo userInfo) {
        if (mIsBackToMain) {
            MainActivity.start(LoginActivity.this, userInfo, mIndexGoto);
        }
        finish();
    }

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
     * 启动登录
     * @param context 源上下文
     */
    public static void startNoBack(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(IS_BACK_TO_MAIN, false);
        context.startActivity(intent);
    }

    /**
     * 从主界面进登陆
     *
     * @param activity 源活动
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

    /**
     * 挂起的登录请求数据
     */
    class PendingLoginRequestData {
        private String username;
        private String password;
        private Map<String, String> params;
    }
}
