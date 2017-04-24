package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.IBackService;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.GridViewAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.NewClassListAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.ResRecordAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.SelectAdapter;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.ChangeMainVideo;
import com.codyy.erpsportal.commons.models.entities.InitPageAll;
import com.codyy.erpsportal.commons.models.entities.InitPageLogo;
import com.codyy.erpsportal.commons.models.entities.InitPageSubTitle;
import com.codyy.erpsportal.commons.models.entities.InitPageVideoBar;
import com.codyy.erpsportal.commons.models.entities.InitPageVideoEnd;
import com.codyy.erpsportal.commons.models.entities.InitPageVideoHead;
import com.codyy.erpsportal.commons.models.entities.LoginOut;
import com.codyy.erpsportal.commons.models.entities.MapInfo;
import com.codyy.erpsportal.commons.models.entities.PicMode;
import com.codyy.erpsportal.commons.models.entities.RecordEvent;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.models.entities.SetMode;
import com.codyy.erpsportal.commons.models.entities.VideoBarMapInfo;
import com.codyy.erpsportal.commons.services.RemoteBackService;
import com.codyy.erpsportal.commons.services.SendSocketCommand;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout;
import com.codyy.erpsportal.commons.widgets.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * 远程导播页面
 * Created by yangxinwu on 2015/8/4.
 */
public class RemoteDirectorActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    private final static String TAG = "RemoteDirectorActivity";

    private final static String EXTRA_CONFIG = "config";
    /**
     * 录制时间任务
     */
    private TimerTask mRecordTask = null;
    private Timer mRecordTimer = null;
    /**
     * 点击设置上滑的布局
     */
    private SlidingUpPanelLayout mSlidingLayout;
    private int mEnablePosition = -1;//可用预置位
    /**
     * 是否静音
     */
    private boolean isSendInDown = false;
    private boolean isSendOutDown = false;
    private boolean isCounterStart = false;
    /**
     * 保存录制模式切换前的状态
     */
    private String mSaveRecodeState = "false";
    /**
     * 是否已注册Bennu初始化接收器
     */
    private boolean mRegistered;

    /**
     * 保存当前主画面的机位
     */
    private String mMainVideoBarPosition = "0";
    /**
     * 保存当前台标值
     */
    private String mLogoIndex = "-1";
    /**
     * 保存当前字幕值
     */
    private String mSubtitleIndex = "-1";
    /**
     * 保存当前字幕值
     */
    private String mVideoHeadIndex = "-1";
    /**
     * 保存当前字幕值
     */
    private String mVideoEndIndex = "-1";

    private RemoteDirectorConfig mConfig;

    private SendSocketCommand mSendSocketCommand;
    /**
     * 视频控件的高
     */
    private int height = 0;
    /**
     * 视频控件的宽
     */
    private int width = 0;
    /**
     * 保存当前课堂导播模式
     */
    private int mMode = 0;
    /**
     * 当前seekbar的值
     */
    private int mProgressValue = 0;
    /**
     * 点击台标
     */
    private int mClickStation = 1;
    /**
     * 点击字幕
     */
    private int mClickSubtitle = 2;
    /**
     * 点击字幕
     */
    private int mClickVideoHead = 3;
    /**
     * 当前录制模式
     */
    private int mCurRecordMode = 0;
    /**
     * 视频控件
     */
    private BnVideoLayout mBnVideoLayout;
    /**
     * 点击弹出台标选择框
     */
    private RelativeLayout mStationSelectView;
    /**
     * 点击弹出字幕选择框
     */
    private RelativeLayout mSubtitleSelectView;
    /**
     * 点击片头选择框
     */
    private RelativeLayout mVideoHeadSelectView;
    /**
     * 点击片尾选择框
     */
    private RelativeLayout mVideoEndSelectView;
    /**
     * 录制结束
     */
    private Button mBtRecordEnd;
    /**
     * 录制开关
     */
    private Button mBtRecordSwitch;
    /**
     * 上
     */
    private Button mBtUp;
    /**
     * 下
     */
    private Button mBtDown;
    /**
     * 左
     */
    private Button mBtLeft;
    /**
     * 右
     */
    private Button mBtRight;
    /**
     * 台标按钮
     */
    private Button mBtStationControl;
    /**
     * 字幕按钮
     */
    private Button mBtSubtitleControl;
    /**
     * 片头按钮
     */
    private Button mBtVideoHeadControl;
    /**
     * 片尾按钮
     */
    private Button mBtVideoEndControl;
    /**
     * 录像控制文本
     */
    private TextView mTvRecordControl;
    /**
     * 录制结束文本
     */
    private TextView mTvRecordEnd;
    /**
     * 当前机位标题
     */
    private TextView mTvTitle;
    /**
     * 计时文本
     */
    private TextView mTvTime;
    /**
     * 台标文本
     */
    private TextView mTvStation;
    private TextView mTvVideoHead;
    private TextView mTvVideoEnd;
    /**
     * 字幕文本
     */
    private TextView mTvSubtitle;

    /**
     * 导播课程未开始的提示
     */
    private TextView mTvTipUnstart;
    /**
     * 变倍
     */
    private SeekBar mSbChangeDouble;
    /**
     * 变焦
     */
    private SeekBar mSbChangeFocus;
    /**
     * 五种特效按钮
     */
    private RadioButton mRbFadeInFadeOut, mRbLeftUp, mRbLeftDown, mRbRightUp, mRbRightDown;

    private RadioButton mMode0, mMode1, mMode2, mMode3;

    /**
     * 录制模式：电影/资源/电影+资源
     */
    private Button mRbMovieMode, mRbResMode, mRbMovieAndResMode;

    /**
     * 手动、自动按钮
     */
    private RadioButton mRbManualMode, mRbAutoMode, mRbAutoManual;

    /**
     * 预置位九宫格
     */
    private GridView mGridView;

    /**
     * 机位
     */
    private GridView mPositionGridView;

    /**
     * 字幕，台标的选择控件
     */
    private PopupWindow mPopupWindow;
    /**
     * 九宫格的适配器
     */
    private GridViewAdapter mGridViewAdapter;
    /**
     * 资源录制适配器
     */
    private ResRecordAdapter mResRecordAdapter;
//    private BNPlayerFactory mBnPlayerFactory = new BNPlayerFactory();
    /**
     * 等待的dialog
     */
    private AlertDialog mWaitDialog;
    /**
     * 记录视频录制的时间
     */
    private int mRecordTime = 0;
    /**
     * 字幕数据源
     */
    private List<MapInfo> mSubtitleData = new ArrayList<>();
    /**
     * 台标数据源
     */
    private List<MapInfo> mStationData = new ArrayList<>();
    /**
     * 台标数据源
     */
    private List<MapInfo> mVideoHeadData = new ArrayList<>();
    /**
     * 台标数据源
     */
    private List<MapInfo> mVideoEndData = new ArrayList<>();
    /**
     * COCO接口
     */
    private IBackService mIBackService = null;
    private Intent mServiceIntent;
    private List<VideoBarMapInfo> mVideoBarData = new ArrayList<>();

    /**
     * 机位适配器
     */
    private NewClassListAdapter mPositionAdapter;

    private Handler mHandler;

    private Callback mCallback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mTvTime.setText(VideoDownloadUtils.switchTime(++mRecordTime));
            return true;
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIBackService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBackService = IBackService.Stub.asInterface(service);
            try {
                mIBackService.bindConfig(mConfig);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mSendSocketCommand = new SendSocketCommand(mIBackService, mConfig);
        }
    };

    /**
     * 处理COCO回来的信息
     *
     * @param msg coco消息
     * @throws RemoteException
     */
    public void onEventMainThread(String msg) throws RemoteException {
        switch (msg) {
            case Constants.CONNECT_COCO:
                mIBackService.login();
                break;
            case Constants.LOGIN_COCO_SUCCESS:
                mIBackService.noticeOnLine();
                break;
            case Constants.CLASS_OVER:
                //当前课堂已结束
                stopCounter();
                dismissWaitDialog();
                showExitDialog(getResources().getString(R.string.class_over));
                break;
            case Constants.SYSTEM_CONFIG_ERROR:
                //网络异常
                stopCounter();
                dismissWaitDialog();
                showExitDialog(getResources().getString(R.string.system_error));
                break;
        }
    }

    /**
     * 处理COCO回来的“其他”信息
     *
     * @param msg 其他消息
     */
    public void onEventMainThread(InitPageAll msg) {
        Cog.d(TAG, "InitPageAll");
        //初始化时根据COCO服务器返回的配置信息更新界面
        dismissWaitDialog();
        mCurRecordMode = msg.getRecordMode();
        if (mCurRecordMode == 0) {
            mSaveRecodeState = "true";
            mRbMovieMode.setSelected(true);
        } else if (mCurRecordMode == 1) {
            mRbResMode.setSelected(true);
        } else {
            mRbMovieAndResMode.setSelected(true);
        }
        //获取当前录制时间
        mRecordTime = msg.getRecordTime();
        mTvTime.setText(VideoDownloadUtils.switchTime(mRecordTime));
        switch (msg.getSceneUseIndex()) {
            case 0:
                mRbLeftUp.setChecked(true);
                break;
            case 1:
                mRbRightUp.setChecked(true);
                break;
            case 2:
                mRbLeftDown.setChecked(true);
                break;
            case 3:
                mRbRightDown.setChecked(true);
                break;
            case 4:
                mRbFadeInFadeOut.setChecked(true);
                break;
            default:
                break;
        }

        //初始时录制功能的状态
        switch (msg.getRecordState()) {
            case 0:
                startCounter();
                mTvRecordControl.setText(Constants.VIDEO_STOP);
                mBtRecordSwitch.setBackgroundResource(R.drawable.img_video_control__stop);
                mTvRecordEnd.setText(Constants.END);
                mBtRecordEnd.setBackgroundResource(R.drawable.img_stop_jiesu);
                break;
            case 1:
                mTvRecordControl.setText(Constants.VIDEO_START);
                mBtRecordSwitch.setBackgroundResource(R.drawable.img_video_control_start);
                mTvRecordEnd.setText(Constants.END);
                mBtRecordEnd.setBackgroundResource(R.drawable.img_stop_jiesu);
                break;
            case 2:
                mTvRecordControl.setText(Constants.VIDEO_START);
                mBtRecordSwitch.setBackgroundResource(R.drawable.img_video_control_start);
                mBtRecordEnd.setBackgroundResource(R.drawable.img_stop_init);
                break;
            default:
                break;
        }
    }

    /**
     * 处理COCO回来的台标信息
     *
     * @param msg 台标信息
     */
    public void onEventMainThread(InitPageLogo msg) {
        Cog.d(TAG, "InitPageLogo");
        mStationData = msg.getMapInfo();
        if (msg.getLogoUseIndex() != -1) {
            //当前台标已经开启
            mTvStation.setText(mStationData.get(msg.getLogoUseIndex() - 1).getTitle());
            mBtStationControl.setText(Constants.LOGO_CLOSED);
            mStationSelectView.setBackgroundResource(R.color.cover_color);
            mLogoIndex = msg.getLogoUseIndex() + "";
        } else {
            //当前台标已经关闭
            mBtStationControl.setText(Constants.LOGO_OPEN);
            mStationSelectView.setBackgroundResource(R.drawable.remote_director_background);
        }
    }

    /**
     * 处理COCO回来的片头信息
     *
     * @param msg 片头信息
     */
    public void onEventMainThread(InitPageVideoHead msg) {
        mVideoHeadData = msg.getMapInfo();
        for (int i = 0; i < mVideoHeadData.size(); i++) {
            Cog.d("片头信息----------" + i, mVideoHeadData.get(i).getTitle());
        }
        if (msg.getLogoUseIndex() != -1) {
            //当前片头已经开启
            mTvVideoHead.setText(mVideoHeadData.get(msg.getLogoUseIndex() - 1).getTitle());
            mBtVideoHeadControl.setText(Constants.VIDEO_HEAD_CLOSED);
            mVideoHeadSelectView.setBackgroundResource(R.color.cover_color);
            mVideoHeadIndex = msg.getLogoUseIndex() + "";
        } else {
            //当前片头已经关闭
            mBtVideoHeadControl.setText(Constants.VIDEO_HEAD_OPEN);
            mVideoHeadSelectView.setBackgroundResource(R.drawable.remote_director_background);
        }
    }

    /**
     * 处理COCO回来的片尾信息
     *
     * @param msg 片尾信息
     */
    public void onEventMainThread(InitPageVideoEnd msg) {
        mVideoEndData = msg.getMapInfo();
        if (msg.getLogoUseIndex() != -1) {
            //当前片尾已经开启
            mTvVideoEnd.setText(mVideoEndData.get(msg.getLogoUseIndex() - 1).getTitle());
            mBtVideoEndControl.setText(Constants.VIDEO_END_CLOSED);
            mVideoEndSelectView.setBackgroundResource(R.color.cover_color);
            mVideoEndIndex = msg.getLogoUseIndex() + "";
        } else {
            //当前片尾已经关闭
            mBtVideoEndControl.setText(Constants.VIDEO_END_OPEN);
            mVideoEndSelectView.setBackgroundResource(R.drawable.remote_director_background);
        }
    }

    /**
     * 处理COCO回来的字幕信息
     *
     * @param msg 字幕信息
     */
    public void onEventMainThread(InitPageSubTitle msg) {
        Cog.d("InitPageSubTitle=====", "InitPageSubTitle=====" + msg.getSubTitleUseIndex());
        mSubtitleData = msg.getMapInfos();

        if (msg.getSubTitleUseIndex() != -1) {
            //当前字幕已经开启
            mTvSubtitle.setText(mSubtitleData.get(msg.getSubTitleUseIndex() - 1).getTitle());
            mBtSubtitleControl.setText(Constants.SUBTITLE_CLOSED);
            mSubtitleSelectView.setBackgroundResource(R.color.cover_color);
            mSubtitleIndex = msg.getSubTitleUseIndex() + "";
        } else {
            //当前字幕已经关闭
            mBtSubtitleControl.setText(Constants.SUBTITLE_OPEN);
            mSubtitleSelectView.setBackgroundResource(R.drawable.remote_director_background);
        }
    }

    /**
     * 处理COCO回来的机位信息
     *
     * @param msg 机位信息
     */
    public void onEventMainThread(InitPageVideoBar msg) {
        Cog.d(TAG, "InitPageVideoBar");
        //初始化机位信息
        List<VideoBarMapInfo> mapInfos = msg.getMapInfos();
        Collections.sort(mapInfos);
        mVideoBarData.clear();
        mVideoBarData.addAll(mapInfos);
        Cog.d("mVideoBarData", mVideoBarData.size() + "");

        mPositionAdapter.notifyDataSetChanged();
        mResRecordAdapter.notifyDataSetChanged();
        mMainVideoBarPosition = String.valueOf(msg.getVideoMain());
        //判断当前主画面的标题和对位机位的选中位置
        for (int i = 0; i < mapInfos.size(); i++) {
            if (Integer.valueOf(mapInfos.get(i).getIndex()) == msg.getVideoMain()) {
                //当前主画面的标题
                mTvTitle.setText(mapInfos.get(i).getTitle());
                mPositionAdapter.setSelectPosition(i);
                if ("VGA".equals(mapInfos.get(i).getTitle())) {
                    mGridViewAdapter.setEnableNumber(0);
                    mGridViewAdapter.notifyDataSetChanged();
                    mPositionAdapter.notifyDataSetChanged();
                    disableCameraControl();
                    break;
                }
                if (!mapInfos.get(i).isPizEnable()) {
                    mGridView.setEnabled(false);
                    mGridViewAdapter.setAllDisabled(true);
                    disableCameraControl();
                }
                mEnablePosition = mVideoBarData.get(i).getPresetNum();
                mGridViewAdapter.setEnableNumber(mEnablePosition);
                //当前主画面对位机位的选中位置
                mPositionAdapter.notifyDataSetChanged();
                mGridViewAdapter.notifyDataSetChanged();
                break;
            }
        }
        if (Constants.MODE_AUTO.equals(msg.getDirectorMode())) {
            //当前主画面是自动模式
            mRbAutoMode.setChecked(true);
            setControlDisable();
        } else if (Constants.MODE_AUTO_MANUAL.equals(msg.getDirectorMode())) {
            mRbAutoManual.setChecked(true);
            setAutoManual();
        }
    }

    /**
     * 处理COCO回来的下线信息
     *
     * @param msg 下线信息
     */
    public void onEventMainThread(LoginOut msg) {
        stopCounter();//停止录制计时
        if (mConfig.getMainSpeak().equals(msg.getFrom())) {
            //判断from == MAIN_SPEAK(主讲教室uid)即主讲教室关闭  等待连接
            showExitDialog(getResources().getString(R.string.exit2));
        }
        if (mConfig.getUid().equals(msg.getFrom())) {
            //判断from == UID 下线
            showExitDialog(getResources().getString(R.string.exit_coco));
        }

    }

    /**
     * 处理COCO回来的课堂模式信息
     *
     * @param msg 课堂模式信息
     */
    public void onEventMainThread(SetMode msg) {
        Cog.d(TAG, "SetMode");
        mMode = msg.getMode();
        if (mMode == 2) {//自动模式
            Toast.makeText(RemoteDirectorActivity.this, getResources().getString(R.string.mode_2), Toast.LENGTH_SHORT).show();
            mRbAutoMode.setChecked(true);//当 mode=2时，切换为自动模式
            mRbManualMode.setEnabled(false);//当 mode=2时，手动导播不可以使用
            setControlDisable();
            mSendSocketCommand.directorMode(Constants.MODE_AUTO);
        } else {
            mRbManualMode.setEnabled(true);//当 mode=0或mode=1时，手动导播可以使用
        }
    }

    /**
     * 处理COCO回来的画面模式信息
     *
     * @param picMode 画面模式信息
     */
    public void onEventMainThread(PicMode picMode) {
        switch (picMode.getIndex()) {
            case 0:
                mMode0.setChecked(true);
                break;
            case 1:
                mMode1.setChecked(true);
                break;
            case 2:
                mMode2.setChecked(true);
                break;
            case 3:
                mMode3.setChecked(true);
                break;
        }
    }


    /**
     * 处理COCO回来的切换主画面信息
     *
     * @param msg 切换主画面信息
     */
    public void onEventMainThread(ChangeMainVideo msg) {
        Cog.d(TAG, "ChangeMainVideo");
        //切换主画面时需要更换主画面的标题，以及机位选中位置
        for (int i = 0; i < mVideoBarData.size(); i++) {
            if (mVideoBarData.get(i).getIndex().equals(msg.getPos())) {
                mTvTitle.setText(mVideoBarData.get(i).getTitle());
                mMainVideoBarPosition = mVideoBarData.get(i).getIndex();
                mEnablePosition = mVideoBarData.get(i).getPresetNum();
                mGridViewAdapter.setEnableNumber(mEnablePosition);
                mGridViewAdapter.notifyDataSetChanged();
                mPositionAdapter.setSelectPosition(i);
                mPositionAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void onEventMainThread(RecordEvent recordEvent) {
        Cog.d(TAG, "onEventMainThread recordEvent=", recordEvent);
        int state = recordEvent.getState();
        if (state == RecordEvent.STATE_START) {
            mRecordTime = recordEvent.getSecond();
            startRecord();
        } else if (state == RecordEvent.STATE_PAUSE) {
            pauseRecord();
            mRecordTime = recordEvent.getSecond();//暂停时统一一下时间
            mTvTime.setText(VideoDownloadUtils.switchTime(mRecordTime));
        } else {
            stopRecord();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_director2);
        EventBus.getDefault().register(this);
        mConfig = getIntent().getParcelableExtra(EXTRA_CONFIG);
        initViews();
        mHandler = new Handler(mCallback);
        registerBennuInitReceiver();
        if (getIntent().getBooleanExtra("isProgressing", false)) {
            showWaitDialog();
        } else {
            mTvTipUnstart.setVisibility(View.VISIBLE);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mBnVideoLayout != null && !mBnVideoLayout.isPlaying() && !mBnVideoLayout.isUrlEmpty()) {
            Cog.e(TAG, "videoview is playing ~~");
            mBnVideoLayout.stop();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startPlayVideo();
                }
            }, 1000);
        } else {
            Cog.e(TAG, "videoview is stop now play it ~ ");
            loadVideoView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBnVideoLayout != null && mBnVideoLayout.isPlaying()) {
            mBnVideoLayout.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVideoView();
        stopCounter();
        EventBus.getDefault().unregister(this);
        try {
            mIBackService.exitSystem();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(conn);
        unregisterBennuInitReceiver();
    }

    /**
     * 注册Bennu初始化接收器
     */
    private void registerBennuInitReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter(EApplication.ACTION_BENNU_INIT);
        manager.registerReceiver(mReceiver, intentFilter);
        mRegistered = true;
    }

    /**
     * 取消注册Bennu初始化接收器
     */
    private void unregisterBennuInitReceiver() {
        if (mRegistered) {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
            manager.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 视频组件Bennu加载完成接收器
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadVideoView();
            unregisterBennuInitReceiver();
        }
    };

    private void initViews() {
        mServiceIntent = new Intent(this, RemoteBackService.class);
        mTvTipUnstart = (TextView) findViewById(R.id.tv_tip_un_start);
        mMode0 = (RadioButton) findViewById(R.id.rb_mode_0);
        if (mMode0 != null) {
            mMode0.setOnClickListener(this);
        }
        mMode1 = (RadioButton) findViewById(R.id.rb_mode_1);
        if (mMode1 != null) {
            mMode1.setOnClickListener(this);
        }
        mMode2 = (RadioButton) findViewById(R.id.rb_mode_2);
        if (mMode2 != null) {
            mMode2.setOnClickListener(this);
        }
        mMode3 = (RadioButton) findViewById(R.id.rb_mode_3);
        if (mMode3 != null) {
            mMode3.setOnClickListener(this);
        }
        mRbMovieMode = (Button) findViewById(R.id.rb_movie_mode);
        if (mRbMovieMode != null) {
            mRbMovieMode.setOnClickListener(this);
        }
        mRbMovieAndResMode = (Button) findViewById(R.id.rb_movieandres_mode);
        if (mRbMovieAndResMode != null) {
            mRbMovieAndResMode.setOnClickListener(this);
        }
        mRbResMode = (Button) findViewById(R.id.rb_res_mode);
        if (mRbResMode != null) {
            mRbResMode.setOnClickListener(this);
        }
        mRbManualMode = (RadioButton) findViewById(R.id.bt_mnul_mode);
        if (mRbManualMode != null) {
            mRbManualMode.setOnClickListener(this);
        }
        mRbAutoMode = (RadioButton) findViewById(R.id.bt_auto_mode);
        if (mRbAutoMode != null) {
            mRbAutoMode.setOnClickListener(this);
        }
        mRbAutoManual = (RadioButton) findViewById(R.id.bt_auto_mnul_mode);
        if (mRbAutoManual != null) {
            mRbAutoManual.setOnClickListener(this);
        }
        mBnVideoLayout = (BnVideoLayout) findViewById(R.id.video_layout);
        mRbFadeInFadeOut = (RadioButton) findViewById(R.id.bt_special_1);
        if (mRbFadeInFadeOut != null) {
            mRbFadeInFadeOut.setOnClickListener(this);
        }
        mRbRightDown = (RadioButton) findViewById(R.id.bt_special_5);
        if (mRbRightDown != null) {
            mRbRightDown.setOnClickListener(this);
        }
        mRbLeftUp = (RadioButton) findViewById(R.id.bt_special_2);
        if (mRbLeftUp != null) {
            mRbLeftUp.setOnClickListener(this);
        }
        mRbLeftDown = (RadioButton) findViewById(R.id.bt_special_3);
        if (mRbLeftDown != null) {
            mRbLeftDown.setOnClickListener(this);
        }
        mRbRightUp = (RadioButton) findViewById(R.id.bt_special_4);
        if (mRbRightUp != null) {
            mRbRightUp.setOnClickListener(this);
        }
        mBtRecordEnd = (Button) findViewById(R.id.stop);
        if (mBtRecordEnd != null) {
            mBtRecordEnd.setOnClickListener(this);
        }
        mBtRecordSwitch = (Button) findViewById(R.id.video_control);
        if (mBtRecordSwitch != null) {
            mBtRecordSwitch.setOnClickListener(this);
        }
        /*
      返回按钮
     */
        Button btBack = (Button) findViewById(R.id.bt_back);
        if (btBack != null) {
            btBack.setOnClickListener(this);
        }
        mBtUp = (Button) findViewById(R.id.bt_up);
        if (mBtUp != null) {
            mBtUp.setOnTouchListener(this);
        }
        mBtDown = (Button) findViewById(R.id.bt_down);
        if (mBtDown != null) {
            mBtDown.setOnTouchListener(this);
        }
        mBtLeft = (Button) findViewById(R.id.bt_left);
        if (mBtLeft != null) {
            mBtLeft.setOnTouchListener(this);
        }
        mBtRight = (Button) findViewById(R.id.bt_right);
        if (mBtRight != null) {
            mBtRight.setOnTouchListener(this);
        }
        mBtStationControl = (Button) findViewById(R.id.bt_station_control);
        if (mBtStationControl != null) {
            mBtStationControl.setOnClickListener(this);
        }
        mBtSubtitleControl = (Button) findViewById(R.id.bt_sub_change_focustitle_control);
        if (mBtSubtitleControl != null) {
            mBtSubtitleControl.setOnClickListener(this);
        }
        mBtVideoHeadControl = (Button) findViewById(R.id.bt_video_head_control);
        if (mBtVideoHeadControl != null) {
            mBtVideoHeadControl.setOnClickListener(this);
        }
        mBtVideoEndControl = (Button) findViewById(R.id.bt_video_end_control);
        if (mBtVideoEndControl != null) {
            mBtVideoEndControl.setOnClickListener(this);
        }
        mStationSelectView = (RelativeLayout) findViewById(R.id.bt_station_control_view);
        if (mStationSelectView != null) {
            mStationSelectView.setOnClickListener(this);
        }
        mSubtitleSelectView = (RelativeLayout) findViewById(R.id.bt_change_focustitle_view);
        if (mSubtitleSelectView != null) {
            mSubtitleSelectView.setOnClickListener(this);
        }
        mVideoHeadSelectView = (RelativeLayout) findViewById(R.id.rl_change_head_view);
        if (mVideoHeadSelectView != null) {
            mVideoHeadSelectView.setOnClickListener(this);
        }
        mVideoEndSelectView = (RelativeLayout) findViewById(R.id.rl_change_end_view);
        if (mVideoEndSelectView != null) {
            mVideoEndSelectView.setOnClickListener(this);
        }
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvRecordControl = (TextView) findViewById(R.id.tv_video_control);
        mTvRecordEnd = (TextView) findViewById(R.id.tv_start_or_stop);
        mTvStation = (TextView) findViewById(R.id.tv_station_symbol);
        mTvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        /*
      学科文本
     */
        TextView tvSubject = (TextView) findViewById(R.id.tv_subject_name);
        /*
      主讲教师
     */
        TextView tvTeacher = (TextView) findViewById(R.id.tv_teacher_name);
        tvSubject.setText(getIntent().getStringExtra("subject"));
        tvTeacher.setText(getIntent().getStringExtra("teacher"));
        TextView masterTeacherLabel = (TextView) findViewById(R.id.tv_teacher);
        masterTeacherLabel.setText(Titles.sMasterTeacher);
        mTvVideoHead = (TextView) findViewById(R.id.tv_viode_head);
        mTvVideoEnd = (TextView) findViewById(R.id.tv_video_end);
        mTvTime = (TextView) findViewById(R.id.tv_start_time);
        mSbChangeFocus = (SeekBar) findViewById(R.id.seekbar_change_focus);
        if (mSbChangeFocus != null) {
            mSbChangeFocus.setOnSeekBarChangeListener(this);
        }
        if (mSbChangeFocus != null) {
            mSbChangeFocus.setOnTouchListener(this);
        }
        mSbChangeDouble = (SeekBar) findViewById(R.id.seekbar_change_double);
        if (mSbChangeDouble != null) {
            mSbChangeDouble.setOnSeekBarChangeListener(this);
        }
        if (mSbChangeDouble != null) {
            mSbChangeDouble.setOnTouchListener(this);
        }
        mGridView = (GridView) findViewById(R.id.gv_number);
        if (mGridView != null) {
            mGridView.setOnTouchListener(this);
        }
        mGridViewAdapter = new GridViewAdapter(RemoteDirectorActivity.this);
        mGridView.setAdapter(mGridViewAdapter);

        RelativeLayout btnMoreSetting = (RelativeLayout) findViewById(R.id.btn_more_setting);
        if (btnMoreSetting != null) {
            btnMoreSetting.setOnClickListener(this);
        }
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        if (mSlidingLayout != null) {
            mSlidingLayout.setTouchEnabled(false);
        }
        mPositionGridView = (GridView) findViewById(R.id.lv_class_list);
        mPositionAdapter = new NewClassListAdapter(this, mVideoBarData, mConfig);
        mPositionGridView.setAdapter(mPositionAdapter);

        GridView resRecordGridView = (GridView) findViewById(R.id.gv_resource);
        mResRecordAdapter = new ResRecordAdapter(this, mVideoBarData, mConfig);
        resRecordGridView.setAdapter(mResRecordAdapter);

        resRecordGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.ck_res);
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    UIUtils.toast(getApplicationContext(), "录制状态下无法操作", Toast.LENGTH_SHORT);
                    return;
                }
                if (isCounterStart) {
                    UIUtils.toast(getApplicationContext(), "录制状态下无法操作", Toast.LENGTH_SHORT);
                    return;
                }

                if (mCurRecordMode == 0) {
                    UIUtils.toast(getApplicationContext(), "电影模式下无法操作", Toast.LENGTH_SHORT);
                    return;
                }

                if (cb.isChecked()) {
                    cb.setChecked(false);
                    mVideoBarData.get(position).setVideoRecord(false);
                    mSendSocketCommand.setVideoRecord((position + 1) + "", "false");
                } else {
                    cb.setChecked(true);
                    mVideoBarData.get(position).setVideoRecord(true);
                    mSendSocketCommand.setVideoRecord((position + 1) + "", "true");
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mEnablePosition != -1 && position >= mEnablePosition) {
                    return;
                }
                mGridViewAdapter.setClickPosition(position);
                mGridViewAdapter.notifyDataSetChanged();
                mSendSocketCommand.setPresetPosition(Integer.valueOf(mMainVideoBarPosition), position);
            }
        });

        mPositionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cog.d(TAG, "onPositionGvItemClick position=", position);
                mEnablePosition = mVideoBarData.get(position).getPresetNum();
                mTvTitle.setText(mVideoBarData.get(position).getTitle());
                mMainVideoBarPosition = mVideoBarData.get(position).getIndex();
                mGridViewAdapter.setEnableNumber(mEnablePosition);
                mPositionAdapter.setSelectPosition(position);
                mGridViewAdapter.notifyDataSetChanged();
                mPositionAdapter.notifyDataSetChanged();
                //  setAutoManual();
                if (mMode == 2) {
                    Toast.makeText(RemoteDirectorActivity.this, getResources().getString(R.string.mode_2), Toast.LENGTH_SHORT).show();
                }
                if (mVideoBarData.get(position).isPizEnable() && mRbManualMode.isChecked()) {
                    mGridView.setEnabled(true);
                    mGridViewAdapter.setAllDisabled(false);
                    mGridViewAdapter.notifyDataSetChanged();
                    enableCameraControl();
                } else {
                    mGridView.setEnabled(false);
                    mGridViewAdapter.setAllDisabled(true);
                    mGridViewAdapter.notifyDataSetChanged();
                    disableCameraControl();
                }
                if ("VGA".equals(mVideoBarData.get(position).getTitle())) {
                    mSendSocketCommand.changeVideoMain(mVideoBarData.get(position).getIndex(), true);
                } else {
                    mSendSocketCommand.changeVideoMain(mVideoBarData.get(position).getIndex(), false);
                }
            }
        });

        mBnVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // if (!isReceive && mMode != 3 && mRbManualMode.isChecked()){
                if (mRbManualMode.isChecked()) {//fix bug 3912 自动导播时禁止视频点击跟踪功能//需求变更半自动导播取消点击跟踪功能
                    int x1 = getX1(motionEvent);
                    int y1 = getY1(motionEvent);
                    mSendSocketCommand.setSubViewCenter(Integer.valueOf(mMainVideoBarPosition),
                            x1, y1, virtualWidth, virtualHeight);//fix bug 3913 高度未减去title bar的高度导致摄像头切换方向不准确
                }
                //}
                return false;
            }
        });
    }

    /**
     * 点击跟踪导播插件不支持移动端屏幕，虚拟缩放比例182*103
     */
    private final int virtualWidth = 182;
    private final int virtualHeight = 103;

    private int getY1(MotionEvent motionEvent) {
        int toolbarHeight = 41;//toolbar的高度,单位DP
        int y = ((int) motionEvent.getY() - UIUtils.dip2px(RemoteDirectorActivity.this, toolbarHeight)) * virtualHeight / height;
        return y > virtualHeight ? virtualHeight : (y < 0 ? 0 : y);
    }

    private int getX1(MotionEvent motionEvent) {
        int x = ((int) motionEvent.getX() > width ? width : (int) motionEvent.getX()) * virtualWidth / width;
        return x < 0 ? 0 : (x > virtualWidth ? virtualWidth : x);
    }


    /**
     * 获取视频控件的宽和高
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        height = mBnVideoLayout.getHeight();
        width = mBnVideoLayout.getWidth();
    }

    /**
     * 弹出台标或字幕的选择框
     */
    private void initPopWindow(final List<MapInfo> map, final int flag) {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_layout, null, false);
        mPopupWindow = new PopupWindow(popupWindow_view,
                400, 400, false);
        ListView listview = (ListView) popupWindow_view.findViewById(R.id.lv_select);
        SelectAdapter selectAdapter = new SelectAdapter(this, map);
        listview.setAdapter(selectAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag == mClickStation) {
                    mTvStation.setText(map.get(position).getTitle());
                    mLogoIndex = map.get(position).getIndex();
                    mPopupWindow.dismiss();
                } else if (flag == mClickSubtitle) {
                    mTvSubtitle.setText(map.get(position).getTitle());
                    mSubtitleIndex = map.get(position).getIndex();
                    mPopupWindow.dismiss();
                } else if (flag == mClickVideoHead) {
                    mTvVideoHead.setText(map.get(position).getTitle());
                    mVideoHeadIndex = map.get(position).getIndex();
                    mPopupWindow.dismiss();
                } else {
                    mTvVideoEnd.setText(map.get(position).getTitle());
                    mVideoEndIndex = map.get(position).getIndex();
                    mPopupWindow.dismiss();
                }
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(mBnVideoLayout, Gravity.CENTER, 0, 0);

        // 点击其他地方消失
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
                return false;
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgressValue = seekBar.getProgress();
        Cog.d(TAG, "onProgressChangedprogress=" + seekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Cog.d(TAG, "onStartTrackingTouchprogress=" + seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mProgressValue = seekBar.getProgress();
        Cog.d(TAG, "onStopTrackingTouchprogress=" + seekBar.getProgress());
        seekBar.setProgress(50);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.bt_up:
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.UP, Constants.UP);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.UP, Constants.DOWN);
                }
                break;
            case R.id.bt_down:
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.DOWN, Constants.UP);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.DOWN, Constants.DOWN);
                }
                break;
            case R.id.bt_left:
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.LEFT, Constants.UP);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.LEFT, Constants.DOWN);
                }
                break;
            case R.id.bt_right:
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.RIGHT, Constants.UP);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.RIGHT, Constants.DOWN);
                }
                break;
            case R.id.seekbar_change_focus:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Nothing need to be done here
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Cog.d(TAG, "ACTION_UP=" + mProgressValue);
                    isSendInDown = false;
                    isSendOutDown = false;
                    if (mProgressValue > 50) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.NEAR, Constants.UP);
                        Cog.i(TAG, "变焦+ up in UP=" + mProgressValue);
                    }
                    if (mProgressValue < 50) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.FAR, Constants.UP);
                        Cog.i(TAG, "变焦- up in UP=" + mProgressValue);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Cog.d(TAG, "ACTION_MOVE=" + mProgressValue);
                    if (mProgressValue > 50 && isSendOutDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.FAR, Constants.UP);
                        isSendOutDown = false;
                        Cog.i(TAG, "变焦- up in UP=" + mProgressValue);
                    }
                    if (mProgressValue > 50 && !isSendInDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.NEAR, Constants.DOWN);
                        isSendInDown = true;
                        Cog.i(TAG, "变焦+ down in DOWN=" + mProgressValue);
                    }

                    if (mProgressValue < 50 && isSendInDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.NEAR, Constants.UP);
                        isSendInDown = false;
                        Cog.i(TAG, "变焦+ up in UP=" + mProgressValue);
                    }
                    if (mProgressValue < 50 && !isSendOutDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.FAR, Constants.DOWN);
                        isSendOutDown = true;
                        Cog.i(TAG, "变焦- down in DOWN=" + mProgressValue);
                    }
                }
                break;
            case R.id.seekbar_change_double:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Nothing need to be done here
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Cog.d(TAG, "ACTION_UP=" + mProgressValue);
                    isSendInDown = false;
                    isSendOutDown = false;
                    if (mProgressValue > 50) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.IN, Constants.UP);
                        Cog.i(TAG, "变倍+ up in UP=" + mProgressValue);
                    }
                    if (mProgressValue < 50) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.OUT, Constants.UP);
                        Cog.i(TAG, "变倍- up in UP=" + mProgressValue);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Cog.d(TAG, "ACTION_MOVE=" + mProgressValue);
                    if (mProgressValue > 50 && isSendOutDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.OUT, Constants.UP);
                        isSendOutDown = false;
                        Cog.i(TAG, "变倍- up in UP=" + mProgressValue);
                    }
                    if (mProgressValue > 50 && !isSendInDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.IN, Constants.DOWN);
                        isSendInDown = true;
                        Cog.i(TAG, "变倍+ down in DOWN=" + mProgressValue);
                    }

                    if (mProgressValue < 50 && isSendInDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.IN, Constants.UP);
                        isSendInDown = false;
                        Cog.i(TAG, "变倍+ up in UP=" + mProgressValue);
                    }
                    if (mProgressValue < 50 && !isSendOutDown) {
                        mSendSocketCommand.setVideoMove(mMainVideoBarPosition, Constants.OUT, Constants.DOWN);
                        isSendOutDown = true;
                        Cog.i(TAG, "变倍- down in DOWN=" + mProgressValue);
                    }
                }
                break;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                RemoteDirectorActivity.this.finish();
                break;
            case R.id.btn_more_setting:
                if (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else if (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                break;
            case R.id.stop://停止录制
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    stopRecord();
                    mSendSocketCommand.setRecordState(2);
                }
                break;
            case R.id.video_control://录制或暂停
                if (Constants.VIDEO_START.equals(mTvRecordControl.getText())) {
                    startRecord();
                    mSendSocketCommand.setRecordState(0);
                } else {
                    pauseRecord();
                    mSendSocketCommand.setRecordState(1);
                }
                break;
            case R.id.bt_station_control:
                if (Constants.LOGO_CLOSED.equals(mBtStationControl.getText())) {
                    mBtStationControl.setText(Constants.LOGO_OPEN);
                    mStationSelectView.setBackgroundResource(R.drawable.remote_director_background);
                    mSendSocketCommand.setLogo(Constants.CLOSED);
                } else {
                    if (!mLogoIndex.equals("-1")) {
                        mSendSocketCommand.setLogo(mLogoIndex);
                    } else {
                        ToastUtil.showToast(RemoteDirectorActivity.this, "请先选择台标哦");
                        return;
                    }
                    mBtStationControl.setText(Constants.LOGO_CLOSED);
                    mStationSelectView.setBackgroundResource(R.color.cover_color);
                }
                break;
            case R.id.bt_sub_change_focustitle_control:
                if (Constants.SUBTITLE_CLOSED.equals(mBtSubtitleControl.getText())) {
                    mBtSubtitleControl.setText(Constants.SUBTITLE_OPEN);
                    mSubtitleSelectView.setBackgroundResource(R.drawable.remote_director_background);
                    mSendSocketCommand.setSubTitle(Constants.CLOSED);
                } else {
                    if (!mSubtitleIndex.equals("-1")) {
                        mSendSocketCommand.setSubTitle(mSubtitleIndex);
                    } else {
                        ToastUtil.showToast(RemoteDirectorActivity.this, "请先选择字幕哦");
                        return;
                    }
                    mBtSubtitleControl.setText(Constants.SUBTITLE_CLOSED);
                    mSubtitleSelectView.setBackgroundResource(R.color.cover_color);

                }
                break;
            case R.id.bt_video_head_control:
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast("录制状态不能修改片头");
                    return;
                } else if (Constants.VIDEO_HEAD_CLOSED.equals(mBtVideoHeadControl.getText())) {
                    mBtVideoHeadControl.setText(Constants.VIDEO_HEAD_OPEN);
                    mVideoHeadSelectView.setBackgroundResource(R.drawable.remote_director_background);
                    mSendSocketCommand.setVideoHead(Constants.CLOSED);
                } else {
                    if (!mVideoHeadIndex.equals("-1")) {
                        mSendSocketCommand.setVideoHead(mVideoHeadIndex);
                    } else {
                        ToastUtil.showToast(RemoteDirectorActivity.this, "请先选择片头哦");
                        return;
                    }
                    mBtVideoHeadControl.setText(Constants.VIDEO_HEAD_CLOSED);
                    mVideoHeadSelectView.setBackgroundResource(R.color.cover_color);
                }
                break;
            case R.id.bt_video_end_control:
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast("录制状态不能修改片尾");
                    return;
                } else if (Constants.VIDEO_END_CLOSED.equals(mBtVideoEndControl.getText())) {
                    mBtVideoEndControl.setText(Constants.VIDEO_END_OPEN);
                    mVideoEndSelectView.setBackgroundResource(R.drawable.remote_director_background);
                    mSendSocketCommand.setVideoEnd(Constants.CLOSED);
                } else {
                    if (!mVideoEndIndex.equals("-1")) {
                        mSendSocketCommand.setVideoEnd(mVideoEndIndex);
                    } else {
                        ToastUtil.showToast(RemoteDirectorActivity.this, "请先选择片尾哦");
                        return;
                    }
                    mBtVideoEndControl.setText(Constants.VIDEO_END_CLOSED);
                    mVideoEndSelectView.setBackgroundResource(R.color.cover_color);

                }
                break;
            case R.id.bt_station_control_view:
                if (Constants.LOGO_CLOSED.equals(mBtStationControl.getText())) {
                    Toast.makeText(RemoteDirectorActivity.this, getResources().getString(R.string.logo_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    initPopWindow(mStationData, mClickStation);
                }
                break;
            case R.id.bt_change_focustitle_view:
                if (Constants.SUBTITLE_CLOSED.equals(mBtSubtitleControl.getText())) {
                    Toast.makeText(RemoteDirectorActivity.this, getResources().getString(R.string.subtitle_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    initPopWindow(mSubtitleData, mClickSubtitle);
                }
                break;
            case R.id.rl_change_head_view:
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast("录制状态不能修改片头");
                    return;
                } else if (Constants.VIDEO_HEAD_CLOSED.equals(mBtVideoHeadControl.getText())) {
                    Toast.makeText(RemoteDirectorActivity.this, getResources().getString(R.string.head_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    initPopWindow(mVideoHeadData, mClickVideoHead);
                }
                break;
            case R.id.rl_change_end_view:
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast("录制状态不能修改片尾");
                    return;
                } else if (Constants.VIDEO_END_CLOSED.equals(mBtVideoEndControl.getText())) {
                    Toast.makeText(RemoteDirectorActivity.this, getResources().getString(R.string.end_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //点击字幕
                    int clickVideoEnd = 4;
                    initPopWindow(mVideoEndData, clickVideoEnd);
                }
                break;
            case R.id.bt_special_1:
                mSendSocketCommand.sceneStyle(Constants.FADE_IN_FADE_OUT);
                break;
            case R.id.bt_special_2:
                mSendSocketCommand.sceneStyle(Constants.LEFT_UP);
                break;
            case R.id.bt_special_3:
                mSendSocketCommand.sceneStyle(Constants.LEFT_DOWN);
                break;
            case R.id.bt_special_4:
                mSendSocketCommand.sceneStyle(Constants.RIGHT_UP);
                break;
            case R.id.bt_auto_mode:
                setControlDisable();
                mSendSocketCommand.directorMode(Constants.MODE_AUTO);
                break;
            case R.id.bt_mnul_mode:
                setControlEnable();
                mSendSocketCommand.directorMode(Constants.MODE_MANUAL);
                break;
            case R.id.bt_auto_mnul_mode:
                setAutoManual();
                mSendSocketCommand.directorMode(Constants.MODE_AUTO_MANUAL);
                break;
            case R.id.bt_special_5:
                mSendSocketCommand.sceneStyle(Constants.RIGHT_DOWN);
                break;
            case R.id.rb_mode_0:
                mSendSocketCommand.videoStitchMode("0");//单画面模式
                break;
            case R.id.rb_mode_1:
                mSendSocketCommand.videoStitchMode("1");//画中画模式
                break;
            case R.id.rb_mode_2:
                mSendSocketCommand.videoStitchMode("2");//画外化模式
                break;
            case R.id.rb_mode_3:
                mSendSocketCommand.videoStitchMode("3");//双画面模式
                break;
            /*电影模式*/
            case R.id.rb_movie_mode:
//                if (Constants.VIDEO_STOP.equals(mTvRecordControl.getText())) {
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast(RemoteDirectorActivity.this, "录制状态下不可以修改录制模式");
                    return;
                }
                mRbMovieMode.setSelected(true);
                mRbResMode.setSelected(false);
                mRbMovieAndResMode.setSelected(false);
                mCurRecordMode = 0;
                mSaveRecodeState = "true";
                mSendSocketCommand.setChangeRecordMode("0", mSaveRecodeState);
                break;
            /*资源模式*/
            case R.id.rb_res_mode:
//                if (Constants.VIDEO_STOP.equals(mTvRecordControl.getText())) {
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast(RemoteDirectorActivity.this, "录制状态下不可以修改录制模式");
                    return;
                }
                mRbMovieMode.setSelected(false);
                mRbResMode.setSelected(true);
                mRbMovieAndResMode.setSelected(false);
                mCurRecordMode = 1;
                if ("true".equals(mSaveRecodeState)) {
                    mSendSocketCommand.setChangeRecordMode("1", "true", acquireRecordArrayStr());
                } else {
                    mSendSocketCommand.setChangeRecordMode("1", "false");
                }
                mSaveRecodeState = "false";
                break;
            /*电影+资源模式*/
            case R.id.rb_movieandres_mode:
//                if (Constants.VIDEO_STOP.equals(mTvRecordControl.getText())) {
                if (Constants.END.equals(mTvRecordEnd.getText())) {
                    ToastUtil.showToast(RemoteDirectorActivity.this, "录制状态下不可以修改录制模式");
                    return;
                }
                mRbMovieMode.setSelected(false);
                mRbResMode.setSelected(false);
                mRbMovieAndResMode.setSelected(true);
                mCurRecordMode = 2;
                if ("true".equals(mSaveRecodeState)) {
                    mSendSocketCommand.setChangeRecordMode("2", "true", acquireRecordArrayStr());
                } else {
                    mSendSocketCommand.setChangeRecordMode("2", "false");
                }
                mSaveRecodeState = "false";

                break;
            default:
                break;
        }
    }

    private String acquireRecordArrayStr() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < mVideoBarData.size(); i++) {
            VideoBarMapInfo info = mVideoBarData.get(i);
            if (info.isVideoRecord()) {
                indexes.add(i + 1);
            }
        }
        return Arrays.toString(indexes.toArray()).replaceAll(" ", "");
    }

    private void startRecord() {
        Cog.d(TAG, "startRecord");
        startCounter();
        mTvRecordControl.setText(Constants.VIDEO_STOP);
        mBtRecordSwitch.setBackgroundResource(R.drawable.img_video_control__stop);
        mTvRecordEnd.setText(Constants.END);
        mBtRecordEnd.setEnabled(true);
        mBtRecordEnd.setBackgroundResource(R.drawable.img_stop_jiesu);
    }

    private void pauseRecord() {
        Cog.d(TAG, "pauseRecord");
        stopCounter();
        mTvRecordControl.setText(Constants.VIDEO_START);
        mBtRecordSwitch.setBackgroundResource(R.drawable.img_video_control_start);
    }

    private void stopRecord() {
        Cog.d(TAG, "stopRecord");
        mRecordTime = 0;
        mTvTime.setText(getResources().getString(R.string.time));
        stopCounter();
        mTvRecordControl.setText(Constants.VIDEO_START);
        mTvRecordEnd.setText("");
        mBtRecordSwitch.setBackgroundResource(R.drawable.img_video_control_start);
        mBtRecordEnd.setBackgroundResource(R.drawable.img_stop_init);
    }

    /**
     * 启动VideoView加载视频
     */
    private void loadVideoView() {
        mBnVideoLayout.setUrl(mConfig.getMainStreamUrl());
        Cog.d(TAG, mConfig.getMainStreamUrl());
        if (!mBnVideoLayout.isPlaying() && !mBnVideoLayout.isUrlEmpty()) {
            mBnVideoLayout.play();
            Cog.d(TAG, "---mBnVideoLayout.play");
        }
    }


    /**
     * 启动VideoView加载视频
     */
    private void stopVideoView() {

        if (!mBnVideoLayout.isPlaying()) {
            mBnVideoLayout.stop();
        }
    }

    /**
     * 启动导播界面
     *
     * @param activity 原来的界面
     * @param config   远程导播配置
     */
    public static void start(Activity activity, RemoteDirectorConfig config, String subject, String teacher,
                             boolean isProgressing) {
        Intent intent = new Intent(activity, RemoteDirectorActivity.class);
        intent.putExtra(EXTRA_CONFIG, config);
        intent.putExtra("subject", subject);
        intent.putExtra("teacher", teacher);
        intent.putExtra("isProgressing", isProgressing);
        activity.startActivity(intent);
    }

    /**
     * 切换为半自动状态时按钮不可点击
     */
    private void setControlDisableByAutoManual() {
        mPositionGridView.setEnabled(true);
        mGridView.setEnabled(false);
        mGridViewAdapter.setAllDisabled(true);
        mGridViewAdapter.notifyDataSetChanged();
        disableCameraControl();
    }

    /**
     * 切换为自动状态时按钮不可点击
     */
    private void setControlDisable() {
        mGridView.setEnabled(false);
        mGridViewAdapter.setAllDisabled(true);
        mGridViewAdapter.notifyDataSetChanged();
        mPositionGridView.setEnabled(false);
        disableCameraControl();
    }

    /**
     * 切换为手动状态时按钮可点击
     */
    private void setControlEnable() {
        mGridView.setEnabled(true);
        mGridViewAdapter.setAllDisabled(false);
        mGridViewAdapter.notifyDataSetChanged();
        mPositionGridView.setEnabled(true);
        for (int i = 0; i < mVideoBarData.size(); i++) {
            if (mTvTitle.getText().equals(mVideoBarData.get(i).getTitle())) {
                if (mVideoBarData.get(i).isPizEnable()) {
                    enableCameraControl();
                    break;
                }
            }
        }
    }

    /**
     * 摄像机调控不可用
     */
    private void disableCameraControl() {
        mBtUp.setEnabled(false);
        mBtUp.setBackgroundResource(R.drawable.img_up_press);
        mBtDown.setEnabled(false);
        mBtDown.setBackgroundResource(R.drawable.img_down__press);
        mBtLeft.setEnabled(false);
        mBtLeft.setBackgroundResource(R.drawable.img_right_press);
        mBtRight.setEnabled(false);
        mBtRight.setBackgroundResource(R.drawable.img_left_press);
        mSbChangeDouble.setEnabled(false);
        mSbChangeFocus.setEnabled(false);
    }

    /**
     * 摄像机调控可用
     */
    private void enableCameraControl() {
        mBtUp.setEnabled(true);
        mBtUp.setBackgroundResource(R.drawable.img_up_nor);
        mBtDown.setEnabled(true);
        mBtDown.setBackgroundResource(R.drawable.img_down_nor);
        mBtLeft.setEnabled(true);
        mBtLeft.setBackgroundResource(R.drawable.img_right_nor);
        mBtRight.setEnabled(true);
        mBtRight.setBackgroundResource(R.drawable.img_left_nor);
        mSbChangeDouble.setEnabled(true);
        mSbChangeFocus.setEnabled(true);
    }

    /**
     * 半自动情况下的操作
     */
    private void setAutoManual() {
    /*if ("教师跟踪".equals(mTvTitle.getText().toString()) || "学生跟踪".equals(mTvTitle.getText().toString()) && isAutoManul )
    {
        setControlEnable();
        return;
    }else if (!"教师跟踪".equals(mTvTitle.getText().toString()) && !"学生跟踪".equals(mTvTitle.getText().toString()) && isAutoManul){*/
        setControlDisableByAutoManual();
    /*    return;
    }*/
    }


    /**
     * 开始播放
     */
    private void startPlayVideo() {
        loadVideoView();
    }

    /**
     * 开始计时
     */
    private void startCounter() {
        startTimer();
    }

    /**
     * 暂停计时
     */
    private void stopCounter() {
        stopTimer();
    }


    private void showWaitDialog() {
        createWaitDialog();
        mWaitDialog.show();
    }

    private void dismissWaitDialog() {
        if (mWaitDialog.isShowing())
            mWaitDialog.dismiss();
    }

    /**
     * 弹出退出dialog
     */
    private void createWaitDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.remote_loading_layout, null);
        mWaitDialog = new AlertDialog.Builder(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(R.string.disconnect)
                .setCancelable(true)
                .setView(view)
                .create();
        mWaitDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 弹出退出dialog
     */
    private void showExitDialog(String title) {
        String[] items = {getResources().getString(R.string.sure)};
        AlertDialog dialog = new AlertDialog.Builder(this,
                R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(title)
                .setCancelable(false)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RemoteDirectorActivity.this.finish();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //启动录制定时器
    private void startTimer() {
        isCounterStart = true;
        mRecordTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 3;
                mHandler.sendMessage(msg);
            }
        };
        if (mRecordTimer != null) {
            mRecordTimer.cancel();
            mRecordTimer.purge();
        }
        mRecordTimer = new Timer();
        mRecordTimer.schedule(mRecordTask, 1000, 1000);
    }

    //关闭录制定时器
    private void stopTimer() {
        isCounterStart = false;
        if (mRecordTask != null) {
            mRecordTask.cancel();
            mRecordTask = null;
        }
        if (mRecordTimer != null) {
            mRecordTimer.cancel();
            mRecordTimer.purge();
            mRecordTimer = null;
        }
    }
}

