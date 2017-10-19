package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.commons.models.entities.MeetingAction;
import com.codyy.erpsportal.commons.models.entities.SpeakerEntity;
import com.codyy.erpsportal.commons.receivers.ScreenBroadcastReceiver;
import com.codyy.erpsportal.commons.utils.CoCoUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingAnimationUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.BNLiveControlView;
import com.codyy.erpsportal.commons.widgets.BNMultipleLiveControlView;
import com.codyy.erpsportal.commons.widgets.BnChatVideoView;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.groups.utils.SnackToastUtils;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.models.entities.DMSEntity;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.CoCoCommand;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.MeetingCommand;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**

 * 视频会议-视频模式
 * @author poe
 */
public class OnlineInteractVideoFragment extends OnlineFragmentBase implements Handler.Callback{
    private static final String TAG = OnlineInteractVideoFragment.class.getSimpleName();
    /** 请求照相机权限*/
    public static final int MY_PERMISSION_REQUEST_CAMERA = 0x123;
    /** 请求录音权限*/
    public static final int MY_PERMISSION_REQUEST_RECORD_AUDIO = 0x124;
    /** 打开我的视频     */
    private static final int MSG_OPEN_MY_VIDEO = 0x201;
    /** 关闭我的视频 **/
    private static final int MSG_CLOSE_MY_VIDEO = 0x202;

    /** 打开我的音频 **/
    private static final int MSG_OPEN_MY_AUDIO = 0x301;
    /** 关闭我的音频　**/
    private static final int MSG_CLOSE_MY_AUDIO = 0x302;

    /** 打开我的视频输入 **/
    private static final int MSG_OPEN_MY_VIDEO_INPUT = 0x401;
    /** 关闭我的视频输入 **/
    private static final int MSG_CLOSE_MY_VIDEO_INPUT = 0x402;

    /** 切换前置摄像头　**/
    private static final int MSG_OPEN_FOREGROUND_CAMERA = 0x501;
    /** 切换后置摄像头　**/
    private static final int MSG_OPEN_BACKGROUND_CAMERA = 0x502;

    public static final String ACTION_CHOOSE_SPEAKER_START = "action.event.choose.speaker.start";//选择发言人
    public static final String ACTION_CHOOSE_SPEAKER_END = "action.event.choose.speaker.end";//选择发言人


    @Bind(R.id.bv_main_online_interact_video)BnVideoLayout2 mMainBnVideoView;//主讲人视频
    @Bind(R.id.lin_my_video_control)LinearLayout mMyVideoControlLinearLayout;//我的视频控制视图
    @Bind(R.id.iv_audio_control_online_interact_video)ImageView mAudioControlImageView;//音量控制输入
    @Bind(R.id.iv_video_control_online_interact_video)ImageView mVideoControlImageView;//视频控制输入
    @Bind(R.id.iv_exchange_online_interact_video)ImageView mModelExchangeImageView;//摄像头前后替换
    @Bind(R.id.surface_partner)SurfaceView mPartnerSurfaceView; //发言人的视频
    @Bind(R.id.tv_open_my_video)TextView mOpenMyVideoTextView;//开启我的视频
    @Bind(R.id.tv_select_speaker)TextView mChooseSpeakerTextView;//选择发言人
    @Bind(R.id.bn_live_control_view)BNMultipleLiveControlView mPartnerBNLiveControlView;//参会者视频控制器
    @Bind(R.id.bn_live_control_view_main)BNLiveControlView mMainBNLiveControlView;//主讲人视频控制器
    @Bind(R.id.lin_control)LinearLayout mPartnerLinearLayout;//包裹 “我的视频” 和 “参会者视频”
    @Bind(R.id.rlt_my_video)RelativeLayout mMyVideoRlt;
    @Bind(R.id.rlt_partner)RelativeLayout mPartnerRlt ;

    private static boolean isVideoOn = true;//default： 打开视频
    private static boolean isAudioOn = true;//default ： 打开音频
    private static boolean isForeground = true;//default：前置摄像头
    private static boolean isCardViewEnable = true;//控制我的CardView是否显示...
    private List<OnlineUserInfo> mAllUsers;//所有的视频参与人
    private List<OnlineUserInfo> mSpeakers  = new ArrayList<>();//在线的发言人
    private String mUri;//视频服务期返回地址
    private String mMainStream;//视频流的stream拼接地址
    private String mPublishStream;
    private BNAudioMixer mAudioMixer = null ;
    private Handler mHandler;
    private Handler mMainHandler = new Handler(this);
    public static final int PERIOD_DELAY = 500 ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        startAudioMixer();
        HandlerThread thread = new HandlerThread("online");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

  /*  private void startAudioMixer() {
        mAudioMixer = new BNAudioMixer();
        mAudioMixer.init(3, 16000, 1, 2, 10);
        mAudioMixer.start();
    }
*/
    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_interact_video;
    }
    //set the application data .
    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        mAudioControlImageView.setEnabled(false);
        mVideoControlImageView.setEnabled(false);



        Cog.i(" viewLoadCompleted() ~ ");
        mMainStream = UiOnlineMeetingUtils.getStream(mMeetingBase, mMeetingBase.getBaseDMS().getDmsMainSpeakID())+"_mobile";
        mPublishStream = UiOnlineMeetingUtils.getStream(mMeetingBase, mUserInfo.getBaseUserId());
        UiOnlineMeetingAnimationUtils.set16vs9Height(mMainBnVideoView, mMainBNLiveControlView);
        mPartnerBNLiveControlView.bindVideoView(mPartnerSurfaceView,"参会者",0);
        initBNVideoListener();
        //if the role is MeetingBase.role_3(观摩者）去除控制视频采集cardView
        if(mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3){
            //隐藏我的视频
            mOpenMyVideoTextView.setVisibility(View.GONE);
            hideCardView();
            //隐藏我的视频
            mMyVideoRlt.setVisibility(View.INVISIBLE);
        }else{
            mMyVideoRlt.setVisibility(View.VISIBLE);
            showCardView();
        }
        //打开发言人视频（默认打开自己的视频）
        tryOpenSecondView();
        //隐藏发言人的控件
        hidePartnerControlViewUI();
    }

    private void hidePartnerControlViewUI() {
        mChooseSpeakerTextView.setVisibility(View.VISIBLE);
        mPartnerBNLiveControlView.setVisibility(View.GONE);
        mPartnerSurfaceView.setVisibility(View.GONE);
    }

    /**
     * 设置发言人和主讲人的名字 (包括主讲和自己的) 及设置 监听（播放/横竖屏）
     */
    private void initBNVideoListener() {
        Cog.i(TAG ,"setSpeakerName()");
        mMainBNLiveControlView.setOnPlayingListener(new BnVideoView2.OnPlayingListener() {
            @Override
            public void onPlaying() {
                Cog.i(TAG,"主讲播放了 回音消除~");
                killAudioNoise();
            }

        });

        mMainBNLiveControlView.setOnExpandListener(new BNLiveControlView.ExpandListener() {

            @Override
            public void expand() {
                hideBottom();
                if(null != getChatService()){
                    getChatService().hideView();
                }
                getParentActivity().expand(0 + "");
            }

            @Override
            public void collapse() {
                getParentActivity().collapse();
                //开始转屏时执行动画
                UiOnlineMeetingAnimationUtils.restoreVideoView(mMainBnVideoView, mPartnerSurfaceView, mMainBNLiveControlView, mPartnerBNLiveControlView,mPartnerLinearLayout,mMyVideoRlt,mPartnerRlt);

                if(null != getChatService() && OnlineMeetingActivity.mShowMyViewState){
                    getChatService().showView();
                }
            }
        });

        mPartnerBNLiveControlView.setOnNextVideoListener(new BNMultipleLiveControlView.INextVideo() {
            @Override
            public void nextVideo() {
                Cog.i(TAG , " choose next video ~ ");
                if(mPartnerSurfaceView.getVisibility() == View.GONE){
                    mPartnerSurfaceView.setVisibility(View.VISIBLE);
                }
            }
        });

        mPartnerBNLiveControlView.setOnCloseVideoListener(new BNMultipleLiveControlView.ICloseVideo() {
            @Override
            public void close() {
                //关闭后UI操作
                hidePartnerControlViewUI();
                //UI视图操作
                getParentActivity().collapse();
                //开始转屏时执行动画
                UiOnlineMeetingAnimationUtils.restoreVideoView(mMainBnVideoView, mPartnerSurfaceView, mMainBNLiveControlView, mPartnerBNLiveControlView,mPartnerLinearLayout,mMyVideoRlt,mPartnerRlt);
                restoreBottom();
                if(null != getChatService() && OnlineMeetingActivity.mShowMyViewState){
                    getChatService().showView();
                }
            }
        });

        mPartnerBNLiveControlView.setOnExpandListener(new BNMultipleLiveControlView.ExpandListener() {
            @Override
            public void expand() {
                hideBottom();
                getParentActivity().expand(1 + "");
                //开始转屏时执行动画
                UiOnlineMeetingAnimationUtils.ExpandVideoView(1, mMainBnVideoView, mPartnerSurfaceView, mMainBNLiveControlView, mPartnerBNLiveControlView,mPartnerLinearLayout,mMyVideoRlt,mPartnerRlt);
                //隐藏“我的视频”
                if(null != getChatService()){
                    getChatService().hideView();
                }
           }

            @Override
            public void collapse() {
                getParentActivity().collapse();
                //开始转屏时执行动画
                UiOnlineMeetingAnimationUtils.restoreVideoView(mMainBnVideoView, mPartnerSurfaceView, mMainBNLiveControlView, mPartnerBNLiveControlView,mPartnerLinearLayout,mMyVideoRlt,mPartnerRlt);
                restoreBottom();
                //16-7-12 判断是否之前打开过...
                if(null != getChatService() && OnlineMeetingActivity.mShowMyViewState){
                    getChatService().showView();
                }
            }
        });
    }

    /**
     * restore the bottom visibility state after collapse .
     */
    private void restoreBottom() {
        if(isCardViewEnable){
            mMyVideoControlLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * hide bottom view when full screen .
     */
    private void hideBottom() {
        if(isCardViewEnable){
//            mControlCardView.setVisibility(View.GONE);
            mMyVideoControlLinearLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 非主讲视角（参会者 or 我的视频：参会者 ）
     */
    private void tryOpenSecondView() {
        Cog.i(TAG ,"tryOpenSecondView() ..... getUsers...");
        if(mAllUsers == null){
            getParentActivity().getUsers(new OnlineMeetingActivity.ILoader() {
                @Override
                public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                    Cog.i(TAG,("online...tryOpenSecondView...getUsers :" + (users != null ? users.size() : 0)));
                    mAllUsers = users;
                    setControlViewInfo();
                }
            }, false,true);
        }else{
            Cog.i(TAG,("online...tryOpenSecondView...already has Users :" + mAllUsers.size()));
            setControlViewInfo();
        }
    }

    //设置主讲人和默认发言人
    private void setControlViewInfo() {
        Cog.i(TAG ,"用户数据：" + (mAllUsers != null ? mAllUsers.size() : 0));
        if(mAllUsers == null) return;
        if(null == mMainBNLiveControlView) return;
        //设置主播的名字
        for (OnlineUserInfo user : mAllUsers) {
            if (user.getRole() == MeetingBase.BASE_MEET_ROLE_0) {//主讲人
                String mainSpeak = user.getName();
                mMainBNLiveControlView.bindVideoView(mMainBnVideoView, mainSpeak,BnVideoView2.BN_ENCODE_HARDWARE);
                break;
            }
        }
        //设置发言人列表显示的内容 . 或 隐藏
        if(mMeetingBase.getBaseSpeakers()!=null && mMeetingBase.getBaseSpeakers().size()>0){
            mSpeakers = new ArrayList<>();
            List<SpeakerEntity> speakerEntities = mMeetingBase.getBaseSpeakers();
            if(null != speakerEntities && speakerEntities.size()>0){
                for(SpeakerEntity se :speakerEntities){
                    //过滤掉自己的id
                    if(!mUserInfo.getBaseUserId().equals(se.getSpeakID())){
                        String id = se.getSpeakID();
                        checkAddSpeaker(id);
                    }
                }
            }
        }

        mMeetingBase.getBaseDMS().getServer(getParentActivity(), mMeetingBase, new DMSEntity.ICallBack() {
            @Override
            public void onSuccess(String serverURL) {
                Cog.i(TAG ,"视频模式：视频地址～返回成功～" + serverURL);
                if(TextUtils.isEmpty(serverURL)) return;
                mUri = serverURL;
                //开启视频（主讲人）.
                startChat();
                //开启发言人
                //开启 partner video .
                startAllSpeaker();
                //开启我的视频
                if(mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_1){
                    openMyVideo();
                }

            }

            @Override
            public void onError(Throwable error) {
                Snackbar.make(mMainBnVideoView, getResources().getString(R.string.net_error), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示我的视频控制视图 .
     */
    private void showCardView() {
        isCardViewEnable = true;
        mMyVideoControlLinearLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        param3.addRule(RelativeLayout.BELOW, R.id.rlt_header);
        param3.setMargins(UIUtils.dip2px(EApplication.instance(), 5), UIUtils.dip2px(EApplication.instance(), 5), UIUtils.dip2px(EApplication.instance(), 5), 0);
        mMyVideoControlLinearLayout.setLayoutParams(param3);
    }

    /**
     * 隐藏我的视频控制视图 .
     */
    private void hideCardView() {
        isCardViewEnable    =   false ;
        mMyVideoControlLinearLayout.setVisibility(View.GONE);
    }

    /**
     * 开启所有的发言人声音
     */
    private void startAllSpeaker() {
        Cog.i("startAllSpeaker()~");
        if(mSpeakers.size()>0){
//            mPartnerBNLiveControlView.setVisibility(View.VISIBLE);
            for(OnlineUserInfo user : mSpeakers){
                startPartner(user,false);
            }
        }
    }

    @OnClick(R.id.tv_open_my_video)
    void openMyVideo(){
        Cog.i(TAG ,"clicked my selft `~");
        Cog.i(TAG ,"check my camera permission  `~");
        if(Build.VERSION.SDK_INT >= 23){
            // display over lay from service
            if(getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                // Should we show an explanation?
                if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Snackbar.make(mPartnerSurfaceView , "申请照相机权限！",Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSION_REQUEST_CAMERA);
                        }
                    }).show();
                } else {
                    // No explanation needed, we can request the permission.
                    getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSION_REQUEST_CAMERA);
                }
            }else{
                //取得了摄像头 -> 继续申请录音功能 >
                requireAudioPriority();
            }

        }else{
            openCamera();
        }
    }

    /**
     * 打开本地摄像头 - 采集
     */
    private void openCamera() {
        Cog.i(TAG , " open Camera start ..."+getChatService());
        //check the net connected state . if no net return back .
        if(!NetworkUtils.isConnected()){
            ToastUtil.showSnake(getString(R.string.net_error),mOpenMyVideoTextView);
            return;
        }
        if(null != getChatService()){
            if(getHostTabIndex()==0 && getTabIndex() == 0){
                //定位初始化的y坐标
                Rect rct = new Rect();
                mMyVideoRlt.getGlobalVisibleRect(rct);
                Log.i(TAG,"我的视频:L " +rct.left+" T:"+rct.top + " R:"+rct.right+" B:"+rct.bottom);
                Log.i(TAG,"我的视频：系统StatusBarHeight : "+UiOnlineMeetingUtils.getSystemStatusBarHeight(getActivity()));
                int y = rct.top;
                int statusHeight = UiOnlineMeetingUtils.getSystemStatusBarHeight(getActivity());
                getChatService().changeView(y,statusHeight);
            }
            String  dest = mUri + "/" + mPublishStream;
            getChatService().start(dest, false,mAudioMixer, new BnChatVideoView.IPublishCallBack() {
                @Override
                public void onPublishSuccess() {
                    if(mAudioControlImageView == null) return;
                    mAudioControlImageView.setEnabled(true);
                    mVideoControlImageView.setEnabled(true);
                    if (null != getChatService()) {

//                        Cog.i(TAG , "show or hide My Video ~");
                        OnlineMeetingActivity.mShowMyViewState = true;
                        //初始化如果处于演示模式，防止出现在演示模式
                        if(getTabIndex() == 0 && getHostTabIndex()==0 ){
                            getChatService().showView();
                        }else{
                            getChatService().hideView();
                        }
                    }
                }

                @Override
                public void onPublishFailure(String error) {
                    if (null != getChatService()) {
                        getChatService().hideView();
                    }
                }
            });
        }
        Cog.i(TAG , " open Camera end ...");
    }

    @TargetApi(23)
    private void requireAudioPriority(){
        Cog.i(TAG ,"check my audio record permission  `~");
        if(getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            // Should we show an explanation?
            if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(mPartnerSurfaceView , "申请录音权限！",Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSION_REQUEST_RECORD_AUDIO);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSION_REQUEST_RECORD_AUDIO);
            }
        }else{
            openCamera();
        }
    }

    @OnClick(R.id.tv_select_speaker)
    void chooseSpeaker(){
        Cog.i(TAG ,"choose an speaker");
        //1.判断发言人个数（不包含自己）
        if(mSpeakers.size() == 0){
            ToastUtil.showSnake("暂无发言人！",mMainBnVideoView);
            return;
        }
        //2.跳转到用户列表区选择发言人
        EventBus.getDefault().post(ACTION_CHOOSE_SPEAKER_START);
    }

    private Runnable mOpenOrCloseAudioRunnable = new Runnable() {
        @Override
        public void run() {
            if(getChatService()!=null && getChatService().isPlaying()) {
                isAudioOn = !isAudioOn;
                if (isAudioOn) {
                    if (null != getChatService()) {
                        getChatService().openLocalAudio();
                    }
                    mMainHandler.sendEmptyMessage(MSG_OPEN_MY_AUDIO);
                } else {
                    if (null != getChatService()) {
                        getChatService().closeLocalAudio();
                    }
                    mMainHandler.sendEmptyMessage(MSG_CLOSE_MY_AUDIO);
                }
            }else{
                SnackToastUtils.toastShort(mPartnerSurfaceView,"我的视频采集失败！");
            }
        }
    };

    //打开或者关闭本地麦克风 or 禁止音频传输 .
    @OnClick(R.id.iv_audio_control_online_interact_video)
    void openOrCloseLocalAudio() {
        mHandler.removeCallbacks(mOpenOrCloseAudioRunnable);
        mHandler.postDelayed(mOpenOrCloseAudioRunnable , PERIOD_DELAY);
    }

    private Runnable mOpenOrCloseRunnable = new Runnable() {
        @Override
        public void run() {
            if(getChatService()!=null && getChatService().isPlaying()){
                isVideoOn = !isVideoOn;
                if (isVideoOn) {
                    if(null != getChatService()) {
                        getChatService().openLocalVideo();
                    }
                    mMainHandler.sendEmptyMessage(MSG_OPEN_MY_VIDEO_INPUT);
                } else {
                    if(null != getChatService()){
                        getChatService().closeLocalVideo();
                    }
                    mMainHandler.sendEmptyMessage(MSG_CLOSE_MY_VIDEO_INPUT);
                }
            }else{
                SnackToastUtils.toastShort(mPartnerSurfaceView,"我的视频采集失败！");
            }
        }
    };

    //打开或关闭本地摄像头 .
    @OnClick(R.id.iv_video_control_online_interact_video)
    void openOrCloseLocalVideo() {
        // 16-9-5 挡住５００ｍｓ之内的连续点击
        mHandler.removeCallbacks(mOpenOrCloseRunnable);
        mHandler.postDelayed(mOpenOrCloseRunnable,PERIOD_DELAY);
    }

    private Runnable mExchangeCameraRunnable = new Runnable() {
        @Override
        public void run() {
            if(OnlineMeetingActivity.mShowMyViewState){
                isForeground = !isForeground;
                if(isForeground){
                    mMainHandler.sendEmptyMessage(MSG_OPEN_FOREGROUND_CAMERA);
                }else{
                    mMainHandler.sendEmptyMessage(MSG_OPEN_BACKGROUND_CAMERA);
                }
                if(null != getChatService()){
                    getChatService().turnBackgroundOrForeground(true);
                }
            }else{
                SnackToastUtils.toastShort(mPartnerSurfaceView,"请打开＇我的视频＇后尝试！");
            }
        }
    };

    //前后摄像头切换 .
    @OnClick(R.id.iv_exchange_online_interact_video)
    void turnBackgroundOrForeground() {
        mHandler.removeCallbacks(mExchangeCameraRunnable);
        mHandler.postDelayed(mExchangeCameraRunnable,PERIOD_DELAY);
    }

    private void startChat() {
        Cog.i(TAG,"startChat() ～");
        if(!TextUtils.isEmpty(mUri)){
            String url  =   mUri    + "/" +mMainStream ;
            Cog.i("startChat(): ", url);
            mMainBNLiveControlView.setVideoPath(url , mAudioMixer);
        }
    }

    /**
     * 开启参会者视频
     */
    private void startPartner(OnlineUserInfo speaker ,boolean isVideoOn){
        Cog.e(TAG," startPartner " + " isVideoOn : "+isVideoOn);
        if(speaker == null) return;
        mPartnerBNLiveControlView.bindVideoView(mPartnerSurfaceView,speaker.getName(),BnVideoView2.BN_ENCODE_SOFTWARE);
        String speaker_stream = UiOnlineMeetingUtils.getStream(mMeetingBase, speaker.getId());
        String url = mUri + "/" + speaker_stream ;
        Cog.i("startPartner()~~~: ", url);
        mPartnerBNLiveControlView.player(url , isVideoOn);
    }

    /**
     * 接受在线用户ids .
     * @param ids collection .
     */
    public void onEvent(List<String> ids) {
        mSpeakers.clear();
        if(null != ids && ids.size()>0){
            for(int i=0; i< ids.size() ;i++){
                checkAddSpeaker(ids.get(i));
            }
        }
        //初始化数据
    }

    /**
     * 检查用户是否为发言人,是则加入到发言热中
     * @param id ids
     */
    private void checkAddSpeaker(String id) {
        OnlineUserInfo user = UiOnlineMeetingUtils.getOnlineUserByID(id, mAllUsers) ;
        //如果是主讲人
        if(user!=null && user.getRole() != MeetingBase.BASE_MEET_ROLE_3 &&user.getRole() > MeetingBase.BASE_MEET_ROLE_0){
            mSpeakers.add(user);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CAMERA://申请相机
                //取得了摄像头 -> 继续申请录音功能 >
                requireAudioPriority();
                break;
            case MY_PERMISSION_REQUEST_RECORD_AUDIO://申请录音
                openCamera();
                break;
        }
    }

    /**
     * 全屏完成之后回调函数 .
     * @param action 全屏／最小化
     * @throws RemoteException　
     */
    public void onEventMainThread(MeetingAction action) throws RemoteException {
        Cog.i(TAG,"onEvent receviced success ~");
        switch (action.getType()){
            case MeetingAction.ACTION_TYPE_EXPAND:
                Cog.i(TAG,"旋转结束！expand ~");
                int index = Integer.parseInt(action.getAction());
                //转屏结束后调整尺寸，防止旋转的过程种造成误差
                UiOnlineMeetingAnimationUtils.ExpandVideoView(index, mMainBnVideoView, mPartnerSurfaceView, mMainBNLiveControlView, mPartnerBNLiveControlView,mPartnerLinearLayout,mMyVideoRlt,mPartnerRlt);
                break;
            case MeetingAction.ACTION_TYPE_COLLAPSE:
                Cog.i(TAG, "旋转结束！collapse ~");
                //转屏结束后调整尺寸，防止旋转的过程种造成误差
                restoreBottom();
                UiOnlineMeetingAnimationUtils.restoreVideoView(mMainBnVideoView, mPartnerSurfaceView, mMainBNLiveControlView, mPartnerBNLiveControlView,mPartnerLinearLayout,mMyVideoRlt,mPartnerRlt);
                break;
        }
    }

    public void onEventMainThread(OnlineUserInfo speaker) throws RemoteException {
        if(null == speaker) return;
        Cog.i(TAG,"choose speaker success ~" +speaker.getName());
        // 计算发言人的位置 打开视频视图
        mPartnerSurfaceView.setVisibility(View.VISIBLE);
        mPartnerBNLiveControlView.setVisibility(View.VISIBLE);
        mChooseSpeakerTextView.setVisibility(View.GONE);
        startPartner(speaker , true);
    }

    /**
     * 处理COCO回来的信息 ,主动推送的即时命令 如:禁言/免打扰/踢出房间/会议结束.
     * @throws RemoteException
     */
    public void onEventMainThread(CoCoAction action) throws RemoteException {
        switch (action.getActionType()) {
            case CoCoCommand.TYPE_ONLINE://某人上线了
                String userID  =  action.getActionResult();
                String nickName = action.getNickName();
                OnlineUserInfo comer = UiOnlineMeetingUtils.getOnlineUserByID(userID, mAllUsers);
                if(null == comer){
                    if(nickName!=null && !nickName.contains("watcher")){
                        Log.i(TAG, "来宾新入～刷新用户数据！！！！getUsers...");
                        //如果是 来宾或者观摩者 获取新的用户数据
                        if(getParentActivity() != null ){
                            getParentActivity().getUsers(new OnlineMeetingActivity.ILoader() {
                                @Override
                                public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                                    mAllUsers = users;
                                }
                            }, true,true);
                        }
                    }
                }else{
                    //初始化(链接coco)完成后调用我的视频
                    if(userID.equals(mUserInfo.getBaseUserId())) {//您的账号在别处登录！
                        if(mIsLoginInit){
                            tryOpenSecondView();
                        }
                    }
                    //if userList contains the id and type = 0 (主讲人） ，开启视频 ！
                    Cog.i("new come user  : ", comer.getName(), "//", comer.getId());
                    if(comer.getRole() == MeetingBase.BASE_MEET_ROLE_0){//主讲人进入会议 或者 web端执行了refresh操作
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(getChatService() != null){//防止应用退出后继续加载
                                    startChat();
                                }
                            }
                        },5*1000);
                    }
                }
                break;
            case MeetingCommand.INFO_AGREE_SPEAKER_BACK: //把你设为发言人
            case MeetingCommand.INFO_AGREE_SPEAKER_BACK＿ALL://设置其他人为发言人,同意某人的发言申请.
                //增加发言人的个数
                final OnlineUserInfo newSpeaker = UiOnlineMeetingUtils.getOnlineUserByID(action.getByOperationObject(),mAllUsers);
                if(null != newSpeaker){
                    if (mUserInfo.getBaseUserId().equals(action.getByOperationObject())) {
                        OnlineMeetingActivity.mShowMyViewState = true;
                        if(null != getChatService()){
                            openMyVideo();
                        }
                    }else{
                        mSpeakers.add(newSpeaker);
                        showCardView();
                        startAllSpeaker();
                    }
                }
                break;
            case MeetingCommand.INFO_CANCEL_SPEAKER://取消发言人
            case MeetingCommand.WEB_CANCEL_SPEAKER_ALL://取消发言人
                //如果当前含有需要减少发言者 .
                OnlineUserInfo deleteUser = UiOnlineMeetingUtils.getOnlineUserByID(action.getByOperationObject(),mSpeakers);
                //更新我的发言人状态 .0
                if (mUserInfo.getBaseUserId().equals(action.getByOperationObject())) {
                    OnlineMeetingActivity.mShowMyViewState = false;
                    //隐藏我的视频
                    if(null != getChatService()){
                        getChatService().hideView();
                    }
                }else  if(deleteUser != null){
                    String speaker_stream = UiOnlineMeetingUtils.getStream(mMeetingBase, deleteUser.getId());
                    String url = mUri + "/" + speaker_stream ;
                    Cog.i("startPartner()~~~: ", url);
                    mPartnerBNLiveControlView.remove(url);
                    mSpeakers.remove(deleteUser);
                    if(mSpeakers.size()>0){
                        startAllSpeaker();
                    }
                }
                break;
            case MeetingCommand.WEB_STOP_AUDIO://参会者禁言(web)
                mAudioControlImageView.setEnabled(false);
                isAudioOn = true;
                mHandler.removeCallbacks(mOpenOrCloseAudioRunnable);
                mHandler.postDelayed(mOpenOrCloseAudioRunnable,PERIOD_DELAY);
                break;
            case MeetingCommand.WEB_PUBLISH_AUDIO://取消参会者禁言(web)
                mAudioControlImageView.setEnabled(true);
                isAudioOn = false;
                mHandler.removeCallbacks(mOpenOrCloseAudioRunnable);
                mHandler.postDelayed(mOpenOrCloseAudioRunnable,PERIOD_DELAY);
                break;
            case MeetingCommand.WEB_STOP_VIDEO://设置参会者禁画面(web)
                //本地视频控制disable.并设置当前当前为打开状态.
                mVideoControlImageView.setEnabled(false);
                isVideoOn = true;
                mHandler.removeCallbacks(mOpenOrCloseRunnable);
                mHandler.postDelayed(mOpenOrCloseRunnable,PERIOD_DELAY);
                break;
            case MeetingCommand.WEB_PUBLISH_VIDEO://取消参会者禁画面
                mVideoControlImageView.setEnabled(true);
                isVideoOn = false;
                mHandler.removeCallbacks(mOpenOrCloseRunnable);
                mHandler.postDelayed(mOpenOrCloseRunnable,PERIOD_DELAY);
                break;
            /*case PullXmlUtils.COMMON_RECEIVE_PLAY://开启轮巡 {#手机端暂时不参与轮巡}
               *//* if(mMeetingBase.getBaseRole()<MeetingBase.BASE_MEET_ROLE_3){
                    if(null != getChatService()){
                        getChatService().start(mUri+"/"+mPublishStream , false);
                    }
                }*//*
                break;
            case PullXmlUtils.LOCATION_RELOAD://关闭轮巡
                //关闭视频采集
                break;*/
            default:
                break;
        }
    }


    /**
     * 回音消除 .
     */
    private void killAudioNoise() {
        if(isDetached() ) return;
        Cog.i(TAG,"killAudioNoise~");
        if(getChatService()!= null){
            getChatService().audioNoiseClean();
        }
    }


    @Override
    public void onDetach() {
        Cog.i("onDetach()～" + getParentActivity());
        super.onDetach();
        mHandler.removeCallbacks(mExchangeCameraRunnable);
        mHandler.removeCallbacks(mOpenOrCloseRunnable);
        mHandler.removeCallbacks(mOpenOrCloseAudioRunnable);
    }

    @Override
    public void onDestroy() {
        Cog.e(TAG,"onDestroy~");
        EventBus.getDefault().unregister(this);
        if(null != mMainBNLiveControlView){
            mMainBNLiveControlView.destroyView();
        }
        if(null != mPartnerBNLiveControlView){
            mPartnerBNLiveControlView.destroyView();
        }
        //停止混音器
        if(null != mAudioMixer){
            mAudioMixer.stop();
            mAudioMixer.release();
            mAudioMixer = null;
        }
        //停止参会者发言混音》
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_OPEN_MY_VIDEO:

                break;
            case MSG_CLOSE_MY_VIDEO:
                break;
            case MSG_OPEN_MY_AUDIO:
                mAudioControlImageView.setImageResource(R.drawable.ic_speak_on);
                break;
            case MSG_CLOSE_MY_AUDIO:
                mAudioControlImageView.setImageResource(R.drawable.ic_speak_close);
                break;
            case MSG_OPEN_MY_VIDEO_INPUT:
                mVideoControlImageView.setImageResource(R.drawable.ic_video_open);
                break;
            case MSG_CLOSE_MY_VIDEO_INPUT:
                mVideoControlImageView.setImageResource(R.drawable.ic_video_close);
                break;
            case MSG_OPEN_FOREGROUND_CAMERA:
                mModelExchangeImageView.setImageResource(R.drawable.ic_video_foreground);
                break;
            case MSG_OPEN_BACKGROUND_CAMERA:
                mModelExchangeImageView.setImageResource(R.drawable.ic_video_background);
                break;

        }
        return false;
    }
}
