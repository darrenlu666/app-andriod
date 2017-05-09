package com.codyy.erpsportal.onlinemeetings.controllers.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.IMeetingService;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.CollectivePrepareLessonsDetailActivity;
import com.codyy.erpsportal.commons.controllers.activities.ListenDetailsActivity;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.interfaces.IFragmentMangerInterface;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.commons.models.entities.LoginOut;
import com.codyy.erpsportal.commons.models.entities.LoopPatrol;
import com.codyy.erpsportal.commons.models.entities.MeetingAction;
import com.codyy.erpsportal.commons.models.entities.MeetingConfig;
import com.codyy.erpsportal.commons.models.entities.MeetingShow;
import com.codyy.erpsportal.commons.models.entities.SystemMessage;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.services.BackService;
import com.codyy.erpsportal.commons.services.IMeeting;
import com.codyy.erpsportal.commons.services.OnlineMeetingService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.MyDialog;
import com.codyy.erpsportal.commons.widgets.MyTabWidget;
import com.codyy.erpsportal.groups.utils.SnackToastUtils;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.ContactsFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnLineChatFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlineDocumentsFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlineGroupChatFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlineInteractFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlineInteractVideoFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlinePriorityFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlineUserOnlineFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.OnlineMeetingTabViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.onlinemeetings.models.entities.DMSEntity;
import com.codyy.erpsportal.onlinemeetings.models.entities.DeskShare;
import com.codyy.erpsportal.onlinemeetings.models.entities.DocumentDetailEntity;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.onlinemeetings.models.entities.TabInfo;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoShare;
import com.codyy.erpsportal.onlinemeetings.widgets.BGABadgeTextView;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 正在进行的视频会议（可通过 参与/加入 进入此页面）
 * Created by poe on 15-8-3.
 */
public class OnlineMeetingActivity extends AppCompatActivity implements MyTabWidget.OnTabClickListener,
        BaseRecyclerAdapter.OnItemClickListener
        , IMeeting
        , ContactsFragment.QueryAllOnLineUser
        , Handler.Callback
        , IFragmentMangerInterface
        , OnlineGroupChatFragment.onSoftKeyListener{
    private final static String TAG = OnlineMeetingActivity.class.getSimpleName();
    /**     * jump to video meeting pager .    */
    private static final int MSG_JUMP_TO_VIDEO_MEETING = 0 ;
    /**     * dismiss the progress bar .  and notify all loader the newest user list data .   */
    private static final int MSG_PROGRESS_BAR_DISMISS = 200 ;
    /** 请求悬浮窗权限**/
    public static final int MY_PERMISSION_REQUEST_FLOAT_WINDOW = 0x125;

    public final static String INTENT_ACTION_NOTICE2 = "com.codyy.erpsportal.INTENT_ACTION_HASMESSAGE";//会议系统消息通知
    public static final String EXTRA_MEETING_ID = "com.codyy.erpsportal.MEETING_ID";//会议id
    public static final String EXTRA_MEETING_BASE = "com.codyy.erpsportal.MEETING_BASE";//会议基本信息json数据

    @Bind(android.R.id.tabhost)FragmentTabHost mTabHost;
    @Bind(android.R.id.tabs)MyTabWidget mTabs;
    @Bind(R.id.view_tab_line)View mTabLineView;
    @Bind(R.id.toolbar_online_main)Toolbar mToolbar;
    @Bind(R.id.tv_title)TextView mTitleTextView;
    @Bind(R.id.chronometer_online_header)Chronometer mChronometer;

    private OnlineMeetingService mChatService;
    private UserInfo mUserInfo;
    private String mMyMeetingID;//会议id
    private View mPromptView;//tab view on the last one for prompt .
    private OnlineMeetingTabViewHolder mTabViewHolder;
    private IMeetingService mIMeetingService;
    private Intent mIntent;//coco service BackService .
    private MeetingBase mMeetingBase;
    private MeetingConfig mMeetingConfig;
    private View mView;
    private BGABadgeTextView mNoticeText;
    private String mPromptTitle = "申请发言";//最后一个tab的title
    private static List<OnlineUserInfo> mUserList;//所有参加会议的用户列表 .
    private boolean mIsLoginInit = false ;//是否进入会议的时候验证自己上线了，用于处理账号单点登录！default :false
    private List<String> mOnLineUserIds = new ArrayList<>();//当前在线用户id .
//    private  ProgressDialog mProgressBar;
    private static List<ILoader> mUserLoaders = new ArrayList<>();//长时间通知单位
    private static List<ILoader> mTemptUserLoaders = new ArrayList<>();//一次性查询
    /**     * 中专变量-互动当前的ITEM index     */
    private int mTabIndex = 0;
    /**     * 关闭/打开视频会议(测试或发布版本控制视频会议模块是否放开)     */
    public static boolean mShowMyViewState = false ;//是否显示我的视频  false：不显示 （default） true:显示 个人点击行为
    private static int mRequestFrom;//进入视频会议的界面（视频会议/互动听课/集体备课）
    public static boolean mFirstEnter =true;//是否第一进入演示模式
    private Handler mHandler = new Handler(this);

    // TODO: 16-9-7 缓存聊天区域的数据
    private String mToUserId ;//视频会议id
    /** 群聊数据　**/
    private List<ChatMessage> mGroupMessageCache = new ArrayList<>();
    /** 单聊数据　**/
    private List<ChatMessage> mSingleMessageCache = new ArrayList<>();
    /***系统提示**/
    private List<SystemMessage> mSystemCache = new ArrayList<SystemMessage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.e(TAG, "onCreate(Bundle savedInstanceState) ");
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_online_meeting_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        //根据tab信息设置不同的tab index .
        if (savedInstanceState != null) {
            mTabHost.setCurrentTab(savedInstanceState.getInt("tab"));
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        Cog.i(TAG , title);
        if(null != mTitleTextView){
            mTitleTextView.setText(title);
        }else{
            finish();
        }
    }

    private void init() {
        mUserInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
        if(null == mUserInfo){
            mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        }
        mMyMeetingID = getIntent().getStringExtra(EXTRA_MEETING_ID);
        mMeetingBase = getIntent().getParcelableExtra(EXTRA_MEETING_BASE);
        if(mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_0){
            Snackbar.make(mTitleTextView, "手机端暂不支持主讲人功能！", Snackbar.LENGTH_SHORT).show();
            finish();
            return;
        }
        mToUserId = Integer.valueOf(mMeetingBase.getBaseMeetID().substring(0,6),16)+"";
        //开启计时器
        UiOnlineMeetingUtils.startCount(mMeetingBase , mChronometer);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn_bg);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //自定义title ，禁用系统的title
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
//        mToolbar.setCollapsible(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });

        mTabs.setOnTabClickListener(this);

        Bundle bd = new Bundle();
        bd.putParcelable(Constants.USER_INFO , mUserInfo);

        mView = makeTabIndicator(this, getString(R.string.title_tab_online_meeting_messages), R.drawable.tab_online_message);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("interact").setIndicator(
                UiOnlineMeetingUtils.makeTabIndicator(this, getString(R.string.title_tab_online_meeting_interacts), R.drawable.tab_online_interact)),OnlineInteractFragment.class, bd);
        //聊天区　
        mTabHost.addTab(mTabHost.newTabSpec("messages").setIndicator(mView), OnLineChatFragment.class, bd);
        mTabHost.addTab(mTabHost.newTabSpec("documents").setIndicator(
                UiOnlineMeetingUtils.makeTabIndicator(this, getString(R.string.title_tab_online_meeting_documents), R.drawable.tab_online_document)),OnlineDocumentsFragment.class, bd);
        mTabHost.addTab(mTabHost.newTabSpec("users").setIndicator(
                        UiOnlineMeetingUtils.makeTabIndicator(this, getString(R.string.title_tab_online_meeting_user_lists), R.drawable.tab_online_user)),OnlineUserOnlineFragment.class, bd);
        //观摩者无法申请发言
        if(mMeetingBase.getBaseRole()<MeetingBase.BASE_MEET_ROLE_3){
            mTabHost.addTab(mTabHost.newTabSpec("priority").setIndicator(
                            UiOnlineMeetingUtils.makeTabIndicator(this, getString(R.string.title_tab_online_meeting_prompt), R.drawable.tab_online_priority)),OnlinePriorityFragment.class, bd);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("messages")) {
                    mNoticeText.showTextBadge("0");
                    mNoticeText.hiddenBadge();
                }
            }
        });

        IntentFilter filter = new IntentFilter(INTENT_ACTION_NOTICE2);
        this.registerReceiver(mNoticeBroadCast, filter);
        //视频采集
        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_PERMISSION_REQUEST_FLOAT_WINDOW);
            } else {
                startPublishService();
            }
        }else{
            startPublishService();
        }
        //connect to coco .
        connectToCoco();
        //检测共享模式
        checkShare();
        //同步申请的文字信息
        setPromptTab();
    }

    private void startPublishService() {
        Intent startService = new Intent(this,OnlineMeetingService.class);
        bindService(startService,mChatServiceConnection,Context.BIND_AUTO_CREATE);
    }

    public OnlineMeetingService getChatService() {
        return mChatService;
    }

    /**
     * 给所有的子Fragment提供数据
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    /**
     * 提供会议id给Fragment使用
     *
     * @return
     */
    public String getMeetingID() {
        return mMyMeetingID;
    }

    /**
     *
     * @param loader
     * @param refresh 是否需要重新拉取数据
     * @param singleMode 是否为一次注册
     */
    public synchronized void  getUsers(ILoader loader , boolean refresh ,boolean singleMode) {
        Cog.e(TAG,"getUsers()~");
        if(singleMode){
            mTemptUserLoaders.add(loader);
        }else{
            mUserLoaders.add(loader);
        }

        if(refresh){
            getMeetingUserList();
        }else{
            if(mUserList == null || mUserList.size() == 0 ){
                getMeetingUserList();
            }else{
                notifyAllLoader();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSION_REQUEST_FLOAT_WINDOW) {
            if(Build.VERSION.SDK_INT>=23){
                if (!Settings.canDrawOverlays(this)) {
                    SnackToastUtils.toastShort(mToolbar,"权限授予失败，无法开启悬浮窗");
                    finish();
                    UIUtils.addExitTranAnim(OnlineMeetingActivity.this);
                } else {
                    SnackToastUtils.toastShort(mToolbar,"权限授予成功！");
                    //启动视频采集
                    startPublishService();
                }
            }
        }
    }

    /**
     * 取消绑定
     * @param loader
     */
    public synchronized void unRegister(ILoader loader){
        if(null == loader) return;
        if(mUserLoaders.contains(loader)){
            mUserLoaders.remove(loader);
        }
    }

    /**
     * 通知所有获取用户信息的数据更新
     */
    private synchronized  static void notifyAllLoader(){
        if(mUserLoaders.size()>0){
            for (ILoader loader : mUserLoaders){
                if(null != loader){
                    loader.onLoadUserSuccess(mUserList);
                }
            }
        }

        if(mTemptUserLoaders.size()>0){
            for (ILoader loader : mTemptUserLoaders){
                if(null != loader){
                    loader.onLoadUserSuccess(mUserList);
                }
            }
            mTemptUserLoaders.clear();
        }
    }

    private synchronized static  void clearOnlineUserLoaders(){
        mUserLoaders.clear();
        mTemptUserLoaders.clear();
        mUserList.clear();
    }

    /**
     * 获取会议基本信息
     *
     * @return
     */
    public MeetingBase getMeetingBase() {
        return mMeetingBase;
    }

    public boolean isLoginInit() {
        return mIsLoginInit;
    }

    public IMeetingService getMeetingService() {
        return mIMeetingService;
    }

    private void checkShare() {
        //1. 桌面共享,视频共享，轮巡
        UiOnlineMeetingUtils.getShareDetail(this, mUserInfo.getUuid(), mMyMeetingID, URLConfig.GET_ONLINE_MEETING_SHARE_INFO, new UiOnlineMeetingUtils.ICallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Cog.d(TAG, response.toString());

                JSONObject vsJsonObject = response.optJSONObject("videoshare");
                JSONObject dsJsonObject = response.optJSONObject("deskshare");
                JSONObject lpJsonObject = response.optJSONObject("patrol");

                VideoShare vs = VideoShare.parseOneData(vsJsonObject);
                DeskShare ds = DeskShare.parseOneData(dsJsonObject);
                LoopPatrol lp = LoopPatrol.parseOneData(lpJsonObject);

                mMeetingBase.setBaseVideoShare(vs);
                mMeetingBase.setBaseDeskShare(ds);
                mMeetingBase.setBaseLoopPatrol(lp);

                printLog("开始获取DMS地址.....");
                mMeetingBase.getBaseDMS().getServer(OnlineMeetingActivity.this, mMeetingBase, new DMSEntity.ICallBack() {
                    @Override
                    public void onSuccess(String serverURL) {
                        Cog.i(TAG , "checkShare：开始获取视频地址～返回成功～"+serverURL);
                        VideoShare vs = mMeetingBase.getBaseVideoShare();
                        DeskShare ds = mMeetingBase.getBaseDeskShare();
                        LoopPatrol lp = mMeetingBase.getBaseLoopPatrol();
                        String mainUrl = serverURL+ "/" + UiOnlineMeetingUtils.getStream(mMeetingBase, mMeetingBase.getBaseDMS().getDmsMainSpeakID());
                        if (null != vs && vs.getVideoSwitch() == 1) {
                            String playUrl = serverURL + "/" + UiOnlineMeetingUtils.getShareVideoStream(mMeetingBase,vs.getVideoID());
                            Cog.e(TAG, "开始共享视频: " + playUrl);
                            OnlineLiveVideoPlayActivity.start(OnlineMeetingActivity.this, mMeetingBase, "", playUrl, OnlineLiveVideoPlayActivity.TYPE_SHARE_VIDEO,mainUrl);
                        } else if (null != ds && ds.getDeskSwitch() == 1) {
                          /*  String streamer = UiOnlineMeetingUtils.getDeskStream(mMeetingBase, ds.getDeskID());
                            String Url = serverURL + "/" + streamer;
                            Cog.e(TAG, "开始共享桌面: " + Url);
                            OnlineLiveVideoPlayActivity.start(OnlineMeetingActivity.this, mMeetingBase, "", Url, "1",mainUrl);*/
                        } else {
                            //轮巡 ,暂不处理  ...
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Snackbar.make(mTitleTextView, getResources().getString(R.string.net_error), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(JSONObject response) {

            }

            @Override
            public void onNetError() {

            }
        });
    }

    private void printLog(String msg){
        Log.i(TAG, msg);
    }

    /**
     * 获取用户列表
     */
    private void getMeetingUserList() {
        Cog.e(TAG,"getMeetingUserList~");
        //1.去获取视频会议基本信息
        UiOnlineMeetingUtils.loadMeetingData(this, mUserInfo.getUuid(), mMyMeetingID, URLConfig.GET_ONLINE_MEETING_USER_LIST, new UiOnlineMeetingUtils.ICallback() {
            @Override
            public void onSuccess(JSONObject response) {

                final JSONObject jsonObject = response;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<OnlineUserInfo> datas = OnlineUserInfo.parseList(jsonObject);
                        //记录之前的登陆状态
                        if (datas != null && datas.size() > 0) {
                            //获取在线用户列表。
                            mUserList = datas;
                        }

                        //do filter .
                        if (mOnLineUserIds != null) {
                            doFilterOnline();
                        }
                        Cog.d(TAG,"get user success callback...");
                        mHandler.sendEmptyMessage(MSG_PROGRESS_BAR_DISMISS);
                    }
                }).start();
            }

            @Override
            public void onFailure(JSONObject response) {
                //do someting ... except dialog .
                Cog.d(TAG,"onFailure()..."+response.toString());
                mHandler.sendEmptyMessage(MSG_PROGRESS_BAR_DISMISS);
            }

            @Override
            public void onNetError() {
                Cog.d(TAG,"onNetError()...");
                mHandler.sendEmptyMessage(MSG_PROGRESS_BAR_DISMISS);
            }
        });
    }


    /**
     * 链接coco服务器
     */
    private void connectToCoco() {
        //如果数据解析有问题,结束本次会议 .
        if (mMeetingBase == null)
            this.finish();
        mIntent = new Intent(this, BackService.class);
        mIntent.putExtra("ip",mMeetingBase.getBaseCoco().getCocoIP());
        mIntent.putExtra("port",mMeetingBase.getBaseCoco().getCocoPort());
        new Thread(new Runnable() {
            @Override
            public void run() {
                bindService(mIntent, mMeetingServiceConn, BIND_AUTO_CREATE);
            }
        }).start();
    }

    /**
     * 时间控制 每15s只能申请发言一次 .
     */
    private static long mStartPrompt = -1;

    @Override
    public void onTabClick(int index) {

        if (index < 4) {
            mTabHost.setCurrentTab(index);
            if(index>0){
                mChronometer.setVisibility(View.INVISIBLE);
            }else{
                mChronometer.setVisibility(View.VISIBLE);
            }

            if(index != 0){
                if(null != getChatService()){
                    getChatService().hideView();
                }
            }
        } else {
            long now        =   System.currentTimeMillis();
            if(mStartPrompt>0){
                if((now - mStartPrompt) > 15* 1000){
                    mStartPrompt = now;
                    prompt();
                }else{
                    Snackbar.make(mTitleTextView, getResources().getString(R.string.tips_duplicate_click_in_period), Snackbar.LENGTH_SHORT).show();
                }
            }else{
                //观摩者不能申请发言
                if(mMeetingBase.getBaseRole()<MeetingBase.BASE_MEET_ROLE_3){
                    //init
                    mStartPrompt    =  now;
                    prompt();
                }else{
                    Snackbar.make(mTitleTextView, getResources().getString(R.string.tips_no_auth_prompt), Snackbar.LENGTH_SHORT).show();
                }
            }
        }



    }

    /**
     * create prompt dialog .
     */
    private void prompt() {

        String content = getPromptTitle();

        //申请 or 取消发言
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        String type = MyDialog.DIALOG_STYLE_TYPE_0;
        if(mMeetingBase.getBaseNoDisturbing() == 1){
            type    =   MyDialog.DIALOG_STYLE_TYPE_1;
        }

        if(null!=mMeetingBase.getBaseLoopPatrol() && mMeetingBase.getBaseLoopPatrol().getLoopSwitch() == 1){
            type    =   MyDialog.DIALOG_STYLE_TYPE_1;
        }
        // Create and show the dialog.
        MyDialog promptDialog = MyDialog.newInstance(content,type,new MyDialog.OnclickListener() {

            @Override
            public void leftClick(MyDialog myDialog) {
                myDialog.dismiss();
                setPromptTab();
                //还原我的视频
                if(mTabHost.getCurrentTab()==0 && mTabIndex == 0 &&null != mChatService && mShowMyViewState){
                    getChatService().showView();
                }
            }

            @Override
            public void rightClick(MyDialog myDialog) {
                if(mMeetingBase.getBaseNoDisturbing()==0){
                    boolean isPrompt = false;
                    if(mMeetingBase.getBaseRole()>=MeetingBase.BASE_MEET_ROLE_2){
                        isPrompt = true;
                    }
                    //send coca info .
                    if (mIMeetingService != null) {
                        try {
                            mIMeetingService.setProposerSpeak(mUserInfo.getBaseUserId(),mMeetingBase.getBaseDMS().getDmsMainSpeakID(), isPrompt);
                            if(!isPrompt){//取消发言需要同步数据 。

                                UiOnlineMeetingUtils.loadMeetingData(OnlineMeetingActivity.this, mUserInfo.getUuid(), mMeetingBase.getBaseMeetID(), URLConfig.SET_ONLINE_MEETING_SPEAKER_DISABLE, new UiOnlineMeetingUtils.ICallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Cog.e(TAG,"取消发言成功同步到web服务器～"+response.toString());

                                    }

                                    @Override
                                    public void onFailure(JSONObject response) {
                                        Cog.e(TAG,"取消发言同步到web服务器失败～"+response.toString());
                                    }

                                    @Override
                                    public void onNetError() {

                                    }
                                });
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                myDialog.dismiss();
                //还原我的视频
                if(mTabHost.getCurrentTab()==0 && mTabIndex == 0 &&null != mChatService && mShowMyViewState){
                    getChatService().showView();
                }
            }

            @Override
            public void dismiss() {
                //还原我的视频
                if(mTabHost.getCurrentTab()==0 && mTabIndex == 0 &&null != mChatService && mShowMyViewState){
                    getChatService().showView();
                }
            }
        });

        promptDialog.show(ft, "dialog");
        //隐藏我的视频
        if(mTabHost.getCurrentTab()==0 &&null != mChatService){
            getChatService().hideView();
        }
    }

    /**
     * 申请发言 的不同状态下提示文字 .
     * @return
     */
    @NonNull
    private String getPromptTitle() {
        String content = "";
        //if(!免打扰)...
        if(mMeetingBase.getBaseNoDisturbing()==0){
            //开启轮训
            if(null!=mMeetingBase.getBaseLoopPatrol() && mMeetingBase.getBaseLoopPatrol().getLoopSwitch() == 1){
                content =   String.format(getString(R.string.format_dialog_loop_patrol),mPromptTitle);
            }else{
                if (mMeetingBase.getBaseRole()>1) {
                    content = getString(R.string.title_tab_online_meeting_prompt);
                } else {
                    content = getString(R.string.title_tab_online_meeting_prompt_cancel);
                }
            }
        }else if(mMeetingBase.getBaseNoDisturbing()==1){//免打扰
            content =   getString(R.string.txt_dialog_forbidden);
        }

        return content;
    }

    /**
     * 设置申请的tab信息
     */
    private void setPromptTab() {
        //观摩者和来宾无申请发言选项 .
        if(mMeetingBase.getBaseRole() >= MeetingBase.BASE_MEET_ROLE_3){
            return;
        }

        mPromptTitle = "";
        int icon = R.drawable.ic_priority_press;
        //if(!免打扰)...
        if(mMeetingBase.getBaseNoDisturbing()==0){
            if (mMeetingBase.getBaseRole()>1) {
                mPromptTitle = getString(R.string.title_tab_online_meeting_prompt);
                icon    =   R.drawable.ic_priority_normal ;
            } else {
                mPromptTitle = getString(R.string.title_tab_online_meeting_prompt_cancel);
                icon    =   R.drawable.ic_priority_press ;
            }
        }else if(mMeetingBase.getBaseNoDisturbing()==1){//免打扰
            mPromptTitle =   getString(R.string.title_tab_online_meeting_prompt_forbidden);
            //icon    =   R.drawable.ic_speak_close_press ;
        }

        TabInfo tab = new TabInfo(icon, mPromptTitle);

        if (null == mPromptView || mTabViewHolder == null) {
            mPromptView = mTabHost.getTabWidget().getChildTabViewAt(4);
            mTabViewHolder = new OnlineMeetingTabViewHolder(mPromptView);
        }

        mTabViewHolder.setDataToView(tab, EApplication.instance());
    }

    public int getTabIndex() {
        return mTabIndex;
    }

    public int getHostTabIndex(){
        if (mTabHost == null) return -1;
        return  mTabHost.getCurrentTab();
    }

    public void setTabIndex(int mTabIndex) {
        this.mTabIndex = mTabIndex;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    private String mExpandIndex;

    //全屏播放视频做一些操作 . 比如隐藏titlebar和 tabWidget .
    public void expand(String index) {
//        UIUtils.setLandscape(this);
        mTabLineView.setVisibility(View.GONE);
        mTabs.setVisibility(View.GONE);
        mToolbar.setVisibility(View.GONE);
        mExpandIndex = index;

        //当前为横屏， 在此处添加额外的处理代码
        MeetingAction mac = new MeetingAction();
        mac.setType(MeetingAction.ACTION_TYPE_EXPAND);
        mac.setAction(mExpandIndex);
        EventBus.getDefault().post(mac);
    }

    public void collapse() {
//        UIUtils.setPortrait(this);
        mTabLineView.setVisibility(View.VISIBLE);
        mTabs.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);

        //当前为竖屏， 在此处添加额外的处理代码
        MeetingAction mac = new MeetingAction();
        mac.setType(MeetingAction.ACTION_TYPE_COLLAPSE);
        EventBus.getDefault().post(mac);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        Cog.e(TAG,"onDestroy~");
        ExitMeetingPre();
        this.unregisterReceiver(mNoticeBroadCast);
//        if(null != mIMeetingService){
            unbindService(mMeetingServiceConn);
//        }
//        if(null != getChatService()){
        unbindService(mChatServiceConnection);
//        }
        OnlineMeetingActivity.mFirstEnter = true ;
        OnlineMeetingActivity.mShowMyViewState = false;
        clearOnlineUserLoaders();
        super.onDestroy();
    }

    /**
     * 发送群聊消息
     */
    @Override
    public void sendMsg(String msg) {
        try {
            mIMeetingService.sendMsg(msg);
            Cog.d(TAG, "------------>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送单聊消息
     */
    @Override
    public void sendSignalMsg(String msg, String mToChatUserId) {
        try {
            mIMeetingService.sendSignalMsg(msg, mToChatUserId);
            Cog.d(TAG, "------------>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAllOnLineUser() {
        try {
            mIMeetingService.getGroupOnlineUser();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出按钮被点击了
     */
    public void onBackClick() {
        String content = getString(R.string.txt_dialog_online_exit); //申请 or 取消发言
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        MyDialog promptDialog = MyDialog.newInstance(content,MyDialog.DIALOG_STYLE_TYPE_0 , new MyDialog.OnclickListener() {

            @Override
            public void leftClick(MyDialog myDialog) {
                myDialog.dismiss();
                //还原我的视频
                if(mTabHost.getCurrentTab()==0 && mTabIndex == 0 &&null != mChatService && mShowMyViewState){
                    getChatService().showView();
                }
            }

            @Override
            public void rightClick(MyDialog myDialog) {
                myDialog.dismiss();
                ExitMeetingPre();
                OnlineMeetingActivity.this.finish();
                UIUtils.addExitTranAnim(OnlineMeetingActivity.this);
            }

            @Override
            public void dismiss() {

            }
        });

        promptDialog.show(ft, "dialog");
        //还原我的视频
        if(mTabHost.getCurrentTab()==0 &&null != mChatService){
            getChatService().hideView();
        }
    }

    /**
     * 推出会议 并执行数据解绑 ！
     */
    private void ExitMeetingPre() {
        if (null != mIMeetingService) {
            try {
                mIMeetingService.loginOut();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if(null != mChatService) mChatService.stop();
        mChatService = null;
    }

    public static void start(Context context, String mid, MeetingBase meetingBase) {
            Intent intent = new Intent(context, OnlineMeetingActivity.class);
            intent.putExtra(OnlineMeetingActivity.EXTRA_MEETING_ID, mid);
            intent.putExtra(OnlineMeetingActivity.EXTRA_MEETING_BASE, meetingBase);
            context.startActivity(intent);
            UIUtils.addEnterAnim((Activity) context);
    }

    /**
     * 请求参数返回 .
     * @param context
     * @param mid
     * @param meetingBase
     * @param requestCode
     */
    public static void startForResult(Activity context, String mid,UserInfo userinfo , MeetingBase meetingBase ,int requestCode) {
        if(requestCode == VideoMeetingDetailActivity.REQUEST_ONLINE_MEETING_CODE){//视频会议
            mRequestFrom = VideoMeetingDetailActivity.RESPONSE_ONLINE_MEETING_CODE;
        }
        if(requestCode == CollectivePrepareLessonsDetailActivity.REQUEST_PREPARE_LESSON_CODE)//集体备课
        {
            mRequestFrom = CollectivePrepareLessonsDetailActivity.RESPONSE_PREPARE_LESSON_CODE;
        }
        if(requestCode == ListenDetailsActivity.REQUEST_LISTEN_LESSON_CODE){//互动听课
            mRequestFrom = ListenDetailsActivity.RESPONSE_LISTEN_LESSON_CODE;
        }
        Intent intent = new Intent(context, OnlineMeetingActivity.class);
        intent.putExtra(OnlineMeetingActivity.EXTRA_MEETING_ID, mid);
        intent.putExtra(OnlineMeetingActivity.EXTRA_MEETING_BASE, meetingBase);
        intent.putExtra(Constants.USER_INFO , userinfo);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Cog.e(TAG, "onConfigurationChanged");
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏， 在此处添加额外的处理代码
            MeetingAction mac = new MeetingAction();
            mac.setType(MeetingAction.ACTION_TYPE_EXPAND);
            mac.setAction(mExpandIndex);
            EventBus.getDefault().post(mac);
        } else if (newConfig.orientation  == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏， 在此处添加额外的处理代码
            MeetingAction mac = new MeetingAction();
            mac.setType(MeetingAction.ACTION_TYPE_COLLAPSE);
            EventBus.getDefault().post(mac);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_JUMP_TO_VIDEO_MEETING:// jump to video meeting pager .
                break;
            case MSG_PROGRESS_BAR_DISMISS://dismiss the progress bar .
                notifyAllLoader();
                break;
        }
        return false;
    }

    /**
     * 演示文档的中转
     */
    @Override
    public void onItemClicked(View v, int position, Object data) {
        DocumentDetailEntity documentDetailEntity = (DocumentDetailEntity) data;
        mTabHost.setCurrentTab(0);
        //切换模式 .
        setTabIndex(1);

        CoCoAction action = new CoCoAction();
        action.setActionType(PullXmlUtils.SWITCH_MODE);
        action.setActionResult(OnlineInteractFragment.ACTION_SHOW_VIDEO);
        EventBus.getDefault().post(action);

        MeetingShow meetingShow = new MeetingShow(documentDetailEntity.getDocName(), documentDetailEntity.getDocId(), "1", 0, 0,documentDetailEntity.getDocPath());
        meetingShow.setShowDelete(true);
        EventBus.getDefault().post(meetingShow);
    }

    @Override
    public void onKeyOpen() {
//        mTabHost.setVisibility(View.GONE);
    }

    @Override
    public void onKeyClose() {
//        mTabHost.setVisibility(View.VISIBLE);
    }

    @Override
    public FragmentManager getNewFragmentManager() {
        return getSupportFragmentManager();
    }

    public interface ILoader {
        /**
         * 成功获取所有在线的用户
         *
         * @param users
         */
        void onLoadUserSuccess(List<OnlineUserInfo> users);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            onBackClick();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public View makeTabIndicator(Context mContext, String title, int drawableId) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tab_indicator_online, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_tab_indicator);
        imageView.setBackgroundResource(drawableId);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_tab_indicator);
        titleTv.setText(title);
        mNoticeText = (BGABadgeTextView) view.findViewById(R.id.tv_tab_notice);
        return view;
    }

    /**
     * 给所有在线用户分配状态 .
     */
    private void doFilterOnline() {
        Cog.e(TAG,"doFilterOnline～");
        if (mUserList == null || mUserList.size() == 0) return;
        if (mOnLineUserIds == null || mOnLineUserIds.size() == 0) return;
        //排序
        for (int i = 0; i < mUserList.size(); i++) {
            boolean isOnline = false;
            for (String userID : mOnLineUserIds) {
                if (userID.equals(mUserList.get(i).getId())) {
                    isOnline = true;
                    break;
                }
            }
            mUserList.get(i).setIsOnline(isOnline);
        }
        //排序,
        Collections.sort(mUserList);
        //刷新数据,(在线用户列表)
       /* MeetingAction mc = new MeetingAction();
        mc.setType(MeetingAction.ACTION_TYPE_REFRESH_USER_ONLINE);
        EventBus.getDefault().post(mc);*/
    }

    /**
     * 处理COCO回来的信息
     *
     * @param msg
     * @throws RemoteException
     */
    public void onEventMainThread(String msg) throws RemoteException {
        switch (msg) {
            case Constants.CONNECT_COCO:
                mIMeetingService.login();
                break;
            case Constants.LOGIN_COCO_SUCCESS:
                mIMeetingService.noticeOnLine();
                mIMeetingService.getGroupOnlineUser();
                break;
            case OnlineInteractVideoFragment.ACTION_CHOOSE_SPEAKER_START://选择发言人-跳转到用户列表
                mTabHost.setCurrentTab(3);

                break;
            case OnlineInteractVideoFragment.ACTION_CHOOSE_SPEAKER_END://结束选择跳转回来
                mTabHost.setCurrentTab(0);
                setTabIndex(0);
                break;
        }
    }

    /**
     * 处理COCO回来的信息 ,主动推送的即时命令 如:禁言/免打扰/踢出房间/会议结束.
     * @throws RemoteException
     */
    public void onEventMainThread(CoCoAction action) throws RemoteException {
        switch (action.getActionType()) {
            case PullXmlUtils.TYPE_LOGIN://某人上线了
//                String userID  =  action.getActionResult();
                break;
            case    PullXmlUtils.WEB_NO_SPEAKER://开启－免打扰
                //重新计时，15s
                mStartPrompt    =   -1;
                if(null != mMeetingBase){
                    mMeetingBase.setBaseNoDisturbing(1);
                }
                setPromptTab();
                break;
            case PullXmlUtils.WEB_ALLOW_SPEAKER://关闭－免打扰
                //重新计时，15s
                mStartPrompt    =   -1;
                //恢复申请/取消发言状态 .
                if(null != mMeetingBase){
                    mMeetingBase.setBaseNoDisturbing(0);
                }
                setPromptTab();
                break;
            case PullXmlUtils.AGREE_SPEAKER_BACK://设为发言人
            case PullXmlUtils.AGREE_SPEAKER_BACK＿ALL://主持人同意某人为主讲人
                Cog.e(TAG, "设置发言!" + action.getByOperationObject());
                //设为发言人
                if (mUserInfo.getBaseUserId().equals(action.getByOperationObject())) {
                    Snackbar.make(mTitleTextView, "您被提升为发言人", Snackbar.LENGTH_SHORT).show();
                    //参会者 ,来宾 都可以申请发言 ...
                    if (mMeetingBase.getBaseRole() <= MeetingBase.BASE_MEET_ROLE_2) {
                        mMeetingBase.setBaseRole(MeetingBase.BASE_MEET_ROLE_1);
                        mStartPrompt    =   -1;
                    }
                    EventBus.getDefault().post(true);
                    setPromptTab();
                }
                break;
            case PullXmlUtils.CANCEL_SPEAKER://取消发言人
            case PullXmlUtils.WEB_CANCEL_SPEAKER://取消发言人
            case PullXmlUtils.WEB_CANCEL_SPEAKER_ALL://取消发言人
                mStartPrompt    =   -1;
                Cog.e(TAG, "取消发言:" + action.getTo());
                if (mUserInfo.getBaseUserId().equals(action.getTo())) {
                    Snackbar.make(mTitleTextView, "您现在不能发言", Snackbar.LENGTH_SHORT).show();
                    if (mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_1) {
                        mMeetingBase.setBaseRole(MeetingBase.BASE_MEET_ROLE_2);
                    }
                    EventBus.getDefault().post(false);
                    setPromptTab();

                    if(null != getChatService()){
                        getChatService().hideView();
                    }
                }
                break;
            case PullXmlUtils.COMMAND_WHITE_BOARD_MARK: //授予-白板标注权限
                // TODO: 16-8-22 判断ｐａｒｓ　ｔｒｕｅ　ｏｒ　ｆａｌｓｅ　
                String result = action.getActionResult();
                if(TextUtils.isEmpty(result)){
                    mMeetingBase.setWhiteBoardManager("1");
                }else{
                    boolean mark = Boolean.valueOf(result);
                    mMeetingBase.setWhiteBoardManager(mark?"1":"0");
                }
                break;
            case PullXmlUtils.APPLY_SPEAKER://申请发言
                /**
                 * setActionResult = "["4f45dd805db04cf683124a223de4e35b"]" 某人申请发言
                 */
                break;
            case PullXmlUtils.KICK_MEET://踢出会议

                String userid = action.getByOperationObject();

                Cog.e(TAG,"踢出会议～～～："+userid != null?userid:"null");

                if (userid.equals(mUserInfo.getBaseUserId())){
                    ExitByAction(TipProgressFragment.OUT_STATUS_TIP);
                }else{
                    // login ...
                    if(removeUser(userid,0)){
                        doFilterOnline();
                    }
                }

                break;
            case PullXmlUtils.VS_CREATE_PLAYER://开始共享视频
                mStartPrompt    =   -1;
                String videoId = action.getActionResult();

                //设置视频共享信息 .
                if(null == mMeetingBase.getBaseVideoShare()){
                    VideoShare  vs = new VideoShare();
                    vs.setVideoSwitch(1);
                    vs.setVideoID(videoId);
                    mMeetingBase.setBaseVideoShare(vs);
                }else{
                    mMeetingBase.getBaseVideoShare().setVideoSwitch(1);
                    mMeetingBase.getBaseVideoShare().setVideoID(videoId);
                }

                mMeetingBase.getBaseDMS().getServer(OnlineMeetingActivity.this, mMeetingBase, new DMSEntity.ICallBack() {
                    @Override
                    public void onSuccess(String serverURL) {
                        String playUrl = serverURL+"/"+UiOnlineMeetingUtils.getShareVideoStream(mMeetingBase,mMeetingBase.getBaseVideoShare().getVideoID());
                        String mainUrl = serverURL+ "/" + UiOnlineMeetingUtils.getStream(mMeetingBase, mMeetingBase.getBaseDMS().getDmsMainSpeakID());
                        Cog.e(TAG, "开始共享视频: " + playUrl);
                        OnlineLiveVideoPlayActivity.start(OnlineMeetingActivity.this, mMeetingBase, "", playUrl, "0",mainUrl);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Snackbar.make(mTitleTextView, getResources().getString(R.string.net_error), Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
            case PullXmlUtils.VS_CALL_STOP://结束视频共享
                Cog.e(TAG,"结束共享视频");
                mStartPrompt    =   -1;
                if(null != mMeetingBase.getBaseVideoShare()){
                    mMeetingBase.getBaseVideoShare().setVideoSwitch(0);
                }
                break;
            /*case PullXmlUtils.RD_CALL_PLAY://开始共享桌面
                mStartPrompt    =   -1;
                String id = action.getActionResult();
                if(null == mMeetingBase.getBaseDeskShare()){
                    DeskShare ds = new DeskShare();
                    ds.setDeskSwitch(1);
                    ds.setDeskID(id);
                    mMeetingBase.setBaseDeskShare(ds);
                }else{
                    mMeetingBase.getBaseDeskShare().setDeskSwitch(1);
                    mMeetingBase.getBaseDeskShare().setDeskID(id);
                }
                mMeetingBase.getBaseDMS().getServer(OnlineMeetingActivity.this, mMeetingBase, new DMSEntity.ICallBack() {
                    @Override
                    public void onSuccess(String serverURL) {
                        Cog.i(TAG,"来自控制台的命令：获取共享桌面播放地址成功～"+serverURL);
                        String streamer = UiOnlineMeetingUtils.getDeskStream(mMeetingBase,  mMeetingBase.getBaseDeskShare().getDeskID());
                        String playUrl = serverURL+"/"+streamer;
                        String mainUrl = serverURL+ "/" + UiOnlineMeetingUtils.getStream(mMeetingBase, mMeetingBase.getBaseDMS().getDmsMainSpeakID());
                        Cog.e(TAG, "开始共享桌面: " + playUrl);
                        OnlineLiveVideoPlayActivity.start(OnlineMeetingActivity.this, mMeetingBase, "", playUrl, "1",mainUrl);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Snackbar.make(mTitleTextView, getResources().getString(R.string.net_error), Snackbar.LENGTH_SHORT).show();
                    }
                });

                break;
            case PullXmlUtils.RD_CALL_STOP:
                Cog.e(TAG, "结束共享桌面");
                mStartPrompt    =   -1;
                if(null != mMeetingBase.getBaseDeskShare()){
                    mMeetingBase.getBaseDeskShare().setDeskSwitch(0);
                }
                break;*/
            case PullXmlUtils.WEB_COMMAND_PUBLISH:  //开启轮巡
                Cog.i(TAG,"开启轮巡：："+PullXmlUtils.WEB_COMMAND_PUBLISH);
                mStartPrompt    =   -1;
                if(null == mMeetingBase.getBaseLoopPatrol()){
                    LoopPatrol lp = new LoopPatrol();
                    lp.setLoopSwitch(1);
                    mMeetingBase.setBaseLoopPatrol(lp);
                }else{
                    mMeetingBase.getBaseLoopPatrol().setLoopSwitch(1);
                }
                break;
            case PullXmlUtils.WEB_COMMAND_UN_PUBLISH://结束轮巡
                Cog.i(TAG,"关闭轮巡：："+PullXmlUtils.WEB_COMMAND_UN_PUBLISH);
                mStartPrompt    =   -1;
                if(mMeetingBase.getBaseLoopPatrol() == null){
                    LoopPatrol lp2 = new LoopPatrol();
                    lp2.setLoopSwitch(0);
                    mMeetingBase.setBaseLoopPatrol(lp2);
                }else{
                    mMeetingBase.getBaseLoopPatrol().setLoopSwitch(0);
                }
                break;
            case PullXmlUtils.CHAT_IS_CLOSE_BACK:
                if (action.getActionResult().equals("true")) {
                    mMeetingBase.setBaseChat("1");
                } else {
                    mMeetingBase.setBaseChat("0");
                }
                break;
            case PullXmlUtils.END_MEET://会议结束 .
                Cog.i(TAG,"会议结束　：　"+PullXmlUtils.END_MEET);
                ExitByAction(TipProgressFragment.END_STATUS_TIP);
                break;
            default:
                break;
        }
    }

    public List<ChatMessage> getGroupMessageCache() {
        return mGroupMessageCache;
    }

    public List<ChatMessage> getSingleMessageCache() {
        return mSingleMessageCache;
    }

    public List<SystemMessage> getSystemCache() {
        return mSystemCache;
    }

    /**
     * 会议结束/被挤下线/被踢出房间 等操作回退提示 .
     * @param actionType
     */
    public void ExitByAction(String actionType){
        Cog.e(TAG," ExitByAction " +actionType);
        ExitMeetingPre();
        Intent intent = new Intent();
        intent.putExtra(TipProgressFragment.ARG_TIP_STATUS_TYPE,actionType);
        setResult(mRequestFrom,intent);
        if(mTabHost.getCurrentTab()==0 &&null != mChatService){
            getChatService().hideView();
        }
        OnlineMeetingActivity.this.finish();
        UIUtils.addExitTranAnim(OnlineMeetingActivity.this);
    }

    /**
     * 用户ID下线
     * @throws RemoteException
     */
    public void onEventMainThread(LoginOut loginOut) throws RemoteException {
        Cog.d(TAG, "loginOUt : " + loginOut.getFrom());
        if(loginOut.getFrom().equals(mUserInfo.getBaseUserId())){
            ExitByAction(TipProgressFragment.LOADED_STATUS_TIP);
        }else if(mUserList != null&&mUserList.size()>0){
            //更新在线用户状态
            if(removeUser(loginOut.getFrom(),1)){
                doFilterOnline();
            }
        }
    }

    /**
     * 是否删除成功
     * @param userID 废弃的用户id
     * @param  type  0:都删 1：删除在线id 2：删除 用户列表
     * @return
     */
    private boolean removeUser(String userID , int type) {
        boolean result = false;
        OnlineUserInfo user = UiOnlineMeetingUtils.getOnlineUserByID(userID, mUserList);
        if(null != user){
            switch (type){
                case 0:
                    mOnLineUserIds.remove(userID);
                    mUserList.remove(user);
                    break;
                case 1:
                    mOnLineUserIds.remove(userID);
                    break;
                case 2:
                    mUserList.remove(user);
                    break;
            }
            result  =   true;
        }
        return result;
    }

    /**
     * 在线用户ID
     * @throws RemoteException
     */
    public void onEventMainThread(String[] onLineUserId) throws RemoteException {

        if(onLineUserId == null || onLineUserId.length == 0) return;

        boolean needUpdate = false;
        for(String id : onLineUserId){
            if(!mOnLineUserIds.contains(id)){
                mOnLineUserIds.add(id);
                needUpdate  =   true;
            }
        }

        if(mUserList != null&&needUpdate){
            doFilterOnline();
        }

    }

    private BroadcastReceiver mNoticeBroadCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Cog.d(TAG, "0***************************");
            Cog.d(TAG, mTabHost.getCurrentTabTag() + "***************************");
            if (!mTabHost.getCurrentTabTag().equals("messages")) {
                mNoticeText.showTextBadge((mNoticeText.getCurCount() + 1) + "");
            }
        }
    };
    private ServiceConnection mMeetingServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMeetingService = IMeetingService.Stub.asInterface(service);
            try {
                mMeetingConfig = MeetingConfig.getSimulateConfig(mMeetingBase,mUserInfo);
                mIMeetingService.bindConfig(mMeetingConfig);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIMeetingService    = null ;
        }
    };
    private ServiceConnection mChatServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if(null != iBinder){
                mChatService  =  ((OnlineMeetingService.MyBinder)iBinder).getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mChatService        =   null;
        }
    };


    /**
     * 消息处理中心
     *
     * @throws RemoteException
     */
    public void onEventMainThread(ChatMessage msg) throws RemoteException {
        String userId = null;
        if (msg.getChatType() == ChatMessage.GROUP_CHAT) {
            //群聊的话就取coco消息中的 to=475487329537895387 作为发送此消息者的ID
            userId = msg.getTo();
        } else if (msg.getChatType() == ChatMessage.SINGLE_CHAT) {
            //单聊的话就取coco消息中的 from=475487329537895387 作为发送此消息者的ID
            userId = msg.getFrom();
        }
        //如果是当前会话的消息，刷新聊天页面
        if (userId.equals(mToUserId) && !msg.getFrom().equals(mUserInfo.getBaseUserId())) {
//            if (msg.getChatType() == Ty) {
            final ChatMessage cm = msg ;
            cm.setBaseViewHoldType(OnlineGroupChatFragment.TYPE_CHAT_GROUP_RECEIVER);
            getUsers(new OnlineMeetingActivity.ILoader() {
                @Override
                public void onLoadUserSuccess(List<OnlineUserInfo> users) {

                    if(users !=null && users.size()>0) {
                        for (int i = 0; i < users.size(); i++) {
                            if (cm.getFrom().equals(users.get(i).getId())) {
                                cm.setName(users.get(i).getName());
                                cm.setHeadUrl(users.get(i).getIcon());
                                break;
                            }
                        }
                        mGroupMessageCache.add(cm);
                        Intent intent = new Intent();
                        intent.setAction(INTENT_ACTION_NOTICE2);
                        sendBroadcast(intent);
                    }
                }
            },false,true);

        }
    }
}
