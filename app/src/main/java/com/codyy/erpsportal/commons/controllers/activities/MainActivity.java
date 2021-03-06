package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.baidu.mobstat.StatService;
import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.ChannelFragment;
import com.codyy.erpsportal.commons.controllers.fragments.FunctionFragment;
import com.codyy.erpsportal.commons.controllers.fragments.UserFragment;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.engine.VersionChecker;
import com.codyy.erpsportal.commons.models.entities.LocationBean;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.UpdatePortalEvent;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.configs.AppConfig;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.receivers.WifiBroadCastReceiver;
import com.codyy.erpsportal.commons.receivers.WifiBroadCastReceiver.WifiChangeListener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.MyTabWidget;
import com.codyy.erpsportal.exam.services.PollingService;
import com.codyy.erpsportal.exam.utils.PollingUtils;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements MyTabWidget.OnTabClickListener,
        Callback {

    private final static String TAG = "MainActivity";
    public final static int REQUEST_AREA = 13;
    public final static int REQUEST_SETTING = 14;
    public final static int MSG_GOTO = 0x98;
    /**
     * 从登录界面或完善个人信息进入首页时无需检查更新，因为登录界面检查过了
     */
    public final static String EXTRA_NO_NEED_TO_CHECK_UPDATE = "com.codyy.erpsportal.EXTRA_NO_NEED_TO_CHECK_UPDATE";

    @Bind(android.R.id.tabs)
    protected MyTabWidget mTabs;

    @Bind(android.R.id.tabhost)
    protected FragmentTabHost mTabHost;

    private LayoutInflater mInflater;

    private UserInfo mUserInfo;

    private ModuleConfig mModuleConfig;

    private Handler mHandler;

    private WifiBroadCastReceiver mWifiBroadCastReceiver;

    private VersionChecker mVersionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkUserInfo(savedInstanceState)){
            return;
        }
        setContentView(R.layout.activity_main);
        findViews();
        mInflater = LayoutInflater.from(this);
        mHandler = new Handler(this);

        mTabs.setOnTabClickListener(this);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("channel").setIndicator(makeTabIndicator(R.string.channel, R.drawable.tab_channel)),
                ChannelFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("myFunc").setIndicator(makeTabIndicator(R.string.function, R.drawable.tab_function)),
                FunctionFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("user").setIndicator(makeTabIndicator(R.string.my, R.drawable.tab_user)),
                UserFragment.class, null);

        loadModuleConfig();

        mWifiBroadCastReceiver = new WifiBroadCastReceiver(mWifiChangeListener);

        if (BuildConfig.DEBUG) ViewServer.get(this).addWindow(this);

        UiMainUtils.setNavigationTintColor(this, R.color.main_green);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt("tab"));
        }
        checkNewVersion();
        StatService.start(this);
    }

    /**
     * 获取并检查用户是否登录
     * @param savedInstanceState
     * @return 是否获取到用户信息
     */
    private boolean checkUserInfo(Bundle savedInstanceState) {
        Cog.d(TAG, "checkUserInfo");
        if (savedInstanceState != null) {
            mUserInfo = savedInstanceState.getParcelable(Constants.USER_INFO);
            Cog.d(TAG, "checkUserInfo saveUserInfo=", mUserInfo);
            UserInfoKeeper.getInstance().setUserInfo(mUserInfo);
        }

//        //如果从登陆界面过来，可以从Intent中获取到登陆信息。
//        obtainInfoFromIntent(getIntent());
        if (mUserInfo == null) {
            mUserInfo = UserInfoDao.find(this);
            UserInfoKeeper.getInstance().setUserInfo(mUserInfo);
        }

        if (mUserInfo == null) {
            LoginActivity.startOnLaunching(this);
            finish();
            return false;
        }
        Cog.d(TAG, "checkUserInfo UserInfoKeeper=", UserInfoKeeper.obtainUserInfo());
        return true;
    }

    /**
     * 检查新版本
     */
    private void checkNewVersion() {
        boolean noNeedToCheck = getIntent().getBooleanExtra(EXTRA_NO_NEED_TO_CHECK_UPDATE, false);
        if (!noNeedToCheck) {
            mVersionChecker = VersionChecker.getInstance();
            mVersionChecker.checkNewVersion(this);
        }
    }

    private void findViews() {
        mTabs = (MyTabWidget) findViewById(android.R.id.tabs);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mTabHost != null) {
            outState.putInt("tab", mTabHost.getCurrentTab());
        }
        UserInfo userInfo = UserInfoKeeper.obtainUserInfo();
        outState.putParcelable(Constants.USER_INFO, userInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiBroadCastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWifiBroadCastReceiver != null) {
            unregisterReceiver(mWifiBroadCastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) ViewServer.get(this).setFocusedWindow(this);
    }

    public void onEvent(UpdatePortalEvent updatePortalEvent) {
        Cog.d(TAG, "onEvent");
        UserInfo userInfo = UserInfoKeeper.obtainUserInfo();
        loadModuleConfig(userInfo.getBaseAreaId(), userInfo.getSelectedChild().getSchoolId());
    }

    public void loadModuleConfig(String areaId, String schoolId) {
        Cog.d(TAG, "loadModuleConfig areaId=", areaId, ",schoolId=", schoolId);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("schoolId", schoolId);
        } else if (!TextUtils.isEmpty(areaId)) {
            params.put("baseAreaId", areaId);
        } else if (mUserInfo != null) {
            if (!TextUtils.isEmpty(mUserInfo.getSchoolId())) {
                params.put("schoolId", mUserInfo.getSchoolId());
                schoolId = mUserInfo.getSchoolId();
            }
            if (!TextUtils.isEmpty(mUserInfo.getBaseAreaId())) {
                params.put("baseAreaId", mUserInfo.getBaseAreaId());
                areaId = mUserInfo.getBaseAreaId();
            }
        }
        Cog.d(TAG, "loadModuleConfig url=", URLConfig.CONFIG, params);
        ConfigBus.postLoading();
        WebApi webApi = RsGenerator.create(WebApi.class);
        webApi.getHomePageInitInfo(schoolId, areaId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<JSONObject, ObservableSource<ModuleConfig>>() {
                    @Override
                    public ObservableSource<ModuleConfig> apply(JSONObject jsonObject) throws Exception {
                        Cog.d(TAG, "loadModuleConfig jsonObject:", jsonObject);
                        String result = jsonObject.optString("result");
                        if ("success".equals(result)) {
                            ModuleConfig moduleConfig = ModuleConfig.parseJsonObject(jsonObject);
                            mModuleConfig = moduleConfig;
                            return Observable.just(moduleConfig);
                        } else {
                            return Observable.error(new RuntimeException("result=" + result));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ModuleConfig>() {
                    @Override
                    public void accept(ModuleConfig moduleConfig) throws Exception {
                        ConfigBus.post(moduleConfig);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "loadModuleConfig error:", throwable.getMessage());
                        ConfigBus.postError();
                    }
                });
    }

    /**
     * 加载模块配置
     */
    public void loadModuleConfig() {
        loadModuleConfig(null, null);
    }

    /**
     * 获取模块配置
     * @return 模块配置
     */
    public ModuleConfig getConfig() {
        return mModuleConfig;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Cog.d(TAG, "+onNewIntent intent=" + intent);
        obtainInfoFromIntent(intent);
        Cog.d(TAG, "-onNewIntent");
    }

    /**
     * 从意图中获取信息，主要包括登陆信息与index。
     *
     * @param intent 意图
     */
    private void obtainInfoFromIntent(Intent intent) {
        if (intent != null) {
            String schoolId = intent.getStringExtra(Extra.SCHOOL_ID);
            if ( !TextUtils.isEmpty(schoolId)) {//来看学校主页的
                Message.obtain(mHandler, MSG_GOTO, 0, 0).sendToTarget();
                loadModuleConfig(null, schoolId);
            } else {
                mUserInfo = intent.getParcelableExtra(Extra.USER_INFO);
                final int index = intent.getIntExtra(LoginActivity.EXTRA_INDEX_GOTO, 0);
                Message.obtain(mHandler, MSG_GOTO, index, 0).sendToTarget();
                loadModuleConfig();
            }
        }
    }

    /**
     * 创建底下标签
     *
     * @param titleStrId 标签名
     * @param drawableId 标签图标
     * @return 标签组件
     */
    private View makeTabIndicator(@StringRes int titleStrId, @DrawableRes int drawableId) {
        View view = mInflater.inflate(R.layout.tab_indicator, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_indicator_image);
        imageView.setBackgroundResource(drawableId);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_tab);
        titleTv.setText(titleStrId);
        return view;
    }

    /**
     * 启动主界面
     *
     * @param activity 原活动
     * @param userInfo 登陆的用户信息
     * @param index 0为频道页，1为应用页，2为我的页
     */
    public static void start(Activity activity, UserInfo userInfo, int index) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(LoginActivity.EXTRA_INDEX_GOTO, index);
        activity.startActivity(intent);
        UIUtils.addExitTranAnim(activity);
    }

    public static void startNoNeedToCheckUpdate(Activity activity, UserInfo userInfo, int index) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_NO_NEED_TO_CHECK_UPDATE, true);
        intent.putExtra(LoginActivity.EXTRA_INDEX_GOTO, index);
        activity.startActivity(intent);
        UIUtils.addExitTranAnim(activity);
    }

    /**
     * 进入学校首页
     * @param activity 原活动
     * @param schoolId 学校id
     */
    public static void start(Activity activity, String schoolId) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Extra.SCHOOL_ID, schoolId);
        activity.startActivity(intent);
        UIUtils.addExitTranAnim(activity);
    }

    @Override
    public void onTabClick(int index) {
        if (UserInfoKeeper.getInstance().getUserInfo() == null && index > 0) {
            LoginActivity.startClearTask(this);
            return;
        }

        mTabHost.setCurrentTab(index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfigBus.clear();
        AppConfig.instance().destroy();
        Cog.d(TAG, "Stop polling service...");
        PollingUtils.stopPollingService(EApplication.instance(), PollingService.class, PollingService.ACTION);
        if (BuildConfig.DEBUG) ViewServer.get(this).removeWindow(this);
        if (mVersionChecker != null) {
            mVersionChecker.release();
        }
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_AREA && resultCode == Activity.RESULT_OK) {
            LocationBean area = data.getParcelableExtra("area");
            if (area.isSchool()) {
                loadModuleConfig(null, area.getId());
            } else {
                loadModuleConfig(area.getId(), null);
            }

        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_GOTO) {
            mTabHost.setCurrentTab(msg.arg1);
            return true;
        }
        return false;
    }

    private WifiChangeListener mWifiChangeListener = new WifiChangeListener() {
        @Override
        public void onWifiClose() {
            UIUtils.toast(MainActivity.this, "已关闭Wifi", Toast.LENGTH_SHORT);
        }

        @Override
        public void onWifiOpen() {
            //UIUtils.toast(this, "已打开Wifi", Toast.LENGTH_SHORT);
        }
    };
}
