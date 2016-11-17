package com.codyy.erpsportal.onlinemeetings.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.BNLiveControlView;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoLayout2;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 视频共享/桌面共享
 * Created by caixingming on 2015/4/24.
 */
public class OnlineLiveVideoPlayActivity extends AppCompatActivity {
    private String TAG = OnlineLiveVideoPlayActivity.class.getSimpleName();
    /** 共享视频 ＊*/
    public static final String TYPE_SHARE_VIDEO = "0";
    /** 共享桌面　**/
    public static final String TYPE_SHARE_DESK = "1" ;

    private static final String EXTRA_ID   = "id";//会议id
    private static final String EXTRA_TITLE = "title";//会议标题
    private static final String EXTRA_URL   =  "url" ;//当前播放地址
    private static final String EXTRA_SPEAKER_NAME = "name";//抓惊人名字
    private static final String EXTRA_MAIN_URL = "main.speaker";//主讲人地址
    /**
     * 演示的类型 0 :视频共享 1:桌面共享
     */
    private static final String EXTRA_ACTION = "action.type";
    @Bind(R.id.txtTitleOfLiveVideoPlay)TextView mTitleText;
    @Bind(R.id.bnVideoViewOfLiveVideoLayout)BnVideoLayout2 mBnVideoView;
    @Bind(R.id.relativeHeadOfLiveVideoPlay)RelativeLayout mRelativeLayout;
    @Bind(R.id.bn_live_control_view)BNLiveControlView mBNLiveControlView;
    @Bind(R.id.bn_video_view_main)BnVideoView2 mMainBnVideoView;
    @Bind(R.id.view_control)BNVideoControlView mMainVideoControl;
//    private BNPlayerFactory mBnPlayerFactory;
    private String mVideoResID;//视频会议id .
    private String mVideoTitle;//标题
    private String mVideoUrl;//视频地址
    private String mMainSpeakerUrl;//主讲人的视频地址  .
    private String mSpeakerName;//发言人
    private String mType = TYPE_SHARE_VIDEO;// 演示的类型 0 :视频共享 1:桌面共享
    private String mMainSpeaker;//主讲人地址 需要混音  .

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_online_live_video_play);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        //禁止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void init() {
        mVideoResID = getIntent().getStringExtra(EXTRA_ID);
        mVideoTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mVideoUrl = getIntent().getStringExtra(EXTRA_URL);
        mSpeakerName    =   getIntent().getStringExtra(EXTRA_SPEAKER_NAME);
        mType       =   getIntent().getStringExtra(EXTRA_ACTION);
        mMainSpeakerUrl    =   getIntent().getStringExtra(EXTRA_MAIN_URL);

        String format_title = getString(R.string.title_live_class);
        mTitleText.setText(String.format(format_title, mVideoTitle));
        //开始播放
        mBNLiveControlView.bindVideoView(mBnVideoView, mSpeakerName,BnVideoView2.BN_ENCODE_HARDWARE);
        check3GWifi();
        if(null != mMainSpeakerUrl){
            mMainVideoControl.bindVideoView(mMainBnVideoView,getSupportFragmentManager());
            mMainVideoControl.setVideoPath(mMainSpeakerUrl,BnVideoView2.BN_URL_TYPE_RTMP_LIVE,false);
        }
    }

    //检测是否为3G网络
    private void check3GWifi() {
        Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {
            @Override
            public void onNetError() {

            }

            @Override
            public void onContinue() {
                startPlay();
            }
        });
    }

    private void startPlay() {
        Cog.i("video:", mVideoUrl);
        if (!TextUtils.isEmpty(mVideoUrl)){
            mBNLiveControlView.setVideoPath(mVideoUrl , null );
        }
    }

    public void onBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainVideoControl.stop();
        mBNLiveControlView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     *
     * @param activity
     * @param meetingBase
     * @param speaker
     * @param playUrl
     * @param type 0:hardware encode  1:software encode .
     */
    public static void start(Activity activity, MeetingBase meetingBase , String speaker,String playUrl , String type , String mainURL) {
        Intent intent = new Intent(activity, OnlineLiveVideoPlayActivity.class);
        intent.putExtra(EXTRA_ID, meetingBase.getBaseMeetID());
        intent.putExtra(EXTRA_TITLE, meetingBase.getBaseTitle());
        intent.putExtra(EXTRA_SPEAKER_NAME, speaker);
        intent.putExtra(EXTRA_URL, playUrl);
        intent.putExtra(EXTRA_ACTION,type);
        intent.putExtra(EXTRA_MAIN_URL , mainURL);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

    /**
     * 处理COCO回来的信息 ,主动推送的即时命令 如:禁言/免打扰/踢出房间/会议结束.
     * @throws RemoteException
     */
    public void onEventMainThread(CoCoAction action) throws RemoteException {
        switch (action.getActionType()) {
            case PullXmlUtils.VS_CALL_STOP:
                Cog.e(TAG,"结束共享视频: ~");
                finish();
                break;
            case PullXmlUtils.RD_CALL_STOP:
                Cog.e(TAG,"结束共享桌面: ~");
                finish();
                break;
            default:
                break;
        }
    }
}
