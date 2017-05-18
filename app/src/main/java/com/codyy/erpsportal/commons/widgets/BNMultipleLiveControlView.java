package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.bennu.sdk.BNMediaPlayer;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.receivers.ScreenBroadcastReceiver;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ScreenBroadCastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 多个音频播放+一个视频（SurfaceView)
 * @use for 视频会议#发言人
 * Created by poe on 2016/7/11.
 */
public class BNMultipleLiveControlView extends RelativeLayout implements AutoHide,Handler.Callback ,SurfaceHolder.Callback{
    private String TAG = "MultipleLiveControlView";
    /**通知隐藏*/
    private static final int MSG_NOTIFY_HIDE_VIEW = 100 ;
    /**设置标题*/
    private static final int MSG_SET_TITLE = 101;
    /**闪现*/
    private static final int MSG_RESOLVE_DISPLAY = 102;
    /**隐藏control view*/
    private static final int MSG_HIDE_VIEW = 103;
    @Bind(R.id.tv_name)TextView mTitleText;
    @Bind(R.id.close_video_image_view)ImageView mCloseVideoIv;
    @Bind(R.id.next_video_image_view)ImageView mNextVideoIv;
    @Bind(R.id.expand_video_image_view)ImageView mExpandIv;

    /**一秒单位*/
    private static final int SECOND = 1000 ;
    /**自动隐藏的事件间隔*/
    public static final int HIDE_PERIOD = 3 * SECOND;
    private Surface mSurfaceView;
    private DisplayListener mDisplayListener;
    private ExpandListener  mExpandListener;
    private boolean isOnTouch = false;
    private boolean isExpanding = false;
    /**线程hide的控制handler*/
    private Handler mHandlerHide;
    private Handler mHandler = new Handler(this);
    private BNAudioMixer mAudioMixer;
    /**尝试重连的次数 < 2*/
    private int mRetryCount = 0 ;
    /**  *  第一次 ：10 第二次：20 ...40 ..... 60 * 1000     */
    public static final long MAX_WAIT_TIME = 60*SECOND;
    private List<MediaPlayer> mediaPlayerList = new ArrayList<>();
    private HashMap<String , BNMediaPlayer> mPlayerHash = new HashMap<>();// 播放器集合
    private HashMap<String , String > mTitles = new HashMap<>();//标题的集合
    private List<String> mUrlList = new ArrayList<>();//播放地址集合
    /**上一个打开视频的参会者*/
    private String mTempUrl = "";
    private boolean mDelayPlayingAfterSurfaceObtained = false;
    /**是否开始播放视频**/
    public boolean isPlaying = false;
    private ICloseVideo mOnCloseVideoListener;//关闭视频
    private INextVideo mOnNextVideoListener;//下一个视频

    public BNMultipleLiveControlView(Context context) {
        super(context, null, R.attr.videoControlViewStyle);
        init(context);
    }

    public BNMultipleLiveControlView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.videoControlViewStyle);
        init(context);
    }

    public BNMultipleLiveControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BNMultipleLiveControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * 播放参会者 ...音频
     * you should invoke this method after {@link #bindVideoView(SurfaceView, String, int)}
     * @param url
     */
    public void player(String url ,boolean isVideoOpen){

        BNMediaPlayer player = null;
        mTitles.put(url,mTitleText.getText().toString());
        if(!mPlayerHash.containsKey(url)){
            player = BNMediaPlayer.createPlayer();
            mPlayerHash.put(url ,player);
            mUrlList.remove(url);
            mUrlList.add(url);
        }else{
            player  =   mPlayerHash.get(url);
            if(player == null){
                mPlayerHash.remove(url);
                player(url , isVideoOpen);
                return;
            }
            if(!mUrlList.contains(url)){
                mUrlList.add(url);
            }
        }
        setPlayUrl(url, player);
//        player.setSurface(mSurfaceView);
        player.setDecodeType(true);
        player.setForceAspectRatio(true);
        //判断是否打开视频
        if(isVideoOpen){
            mTempUrl    =   url;
            if(mSurfaceView == null){
                mTempUrl = url ;
                Cog.e(TAG , "poe:: player failed !!! mSurface is NULL ！！");
                mDelayPlayingAfterSurfaceObtained = true;
                player.setReceiveVideo(false , null);
//                return;
            }else{
                player.setReceiveVideo(true , mSurfaceView);
            }

        }else{
            player.setReceiveVideo(false , null);
        }
        player.playWithChat();
    }

    /**
     * 新开 or 切换 到此视频中
     */
    public void showSpeaker(String url){
        Cog.i(TAG , "showSpeaker ~ "+url);
        if(TextUtils.isEmpty(url) || mTempUrl.equals(url)) return;
        mTitleText.setText(mTitles.get(url));
        if(!mUrlList.contains(url)){
            Cog.i(TAG,"新打开一个发言人　!");
            //新打开一个发言人
            player(url , true);
        }else{//已经存在发言人列表-恢复视频 . . .
            Cog.i(TAG,"已经存在发言人列表-恢复视频　!");
            //1.暂停当前播放的视频
            BNMediaPlayer player = mPlayerHash.get(mTempUrl);
            if(player != null){
                player.setReceiveVideo(false , null);
            }
            player(url , true);
        }
    }

    /**
     * 删除一个视频播放 .
     * @param url
     */
    public void remove(String url){
        if(mPlayerHash.keySet().contains(url)){
            BNMediaPlayer player = mPlayerHash.get(url);
            mPlayerHash.remove(url);
            mUrlList.remove(url);
            if(player != null){
                player.stop();
                player.release();
            }
        }
    }

    public ICloseVideo getOnCloseVideoListener() {
        return mOnCloseVideoListener;
    }

    public void setOnCloseVideoListener(ICloseVideo mOnCloseVideoListener) {
        this.mOnCloseVideoListener = mOnCloseVideoListener;
    }

    public INextVideo getOnNextVideoListener() {
        return mOnNextVideoListener;
    }

    public void setOnNextVideoListener(INextVideo mOnNextVideoListener) {
        this.mOnNextVideoListener = mOnNextVideoListener;
    }

    /**
     * 设置播放地址
     * rtmp：//xx { live = 1} //{} new add suffix .
     * @param url
     * @param player
     */
    private void setPlayUrl(String url, BNMediaPlayer player) {
        if (url.startsWith("rtmp")) {
            String playUrl = url + " live=1" ;
            Cog.i(TAG , "poe:: url : "+playUrl);
            player.setUri(playUrl);
        } else {
            Cog.i(TAG , "poe:: url : "+url);
            player.setUri(url);
        }
    }

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_control_multiple_live_layout, this, true);
        HandlerThread hideThread = new HandlerThread("hide"+TAG);
        hideThread.start();
        mHandlerHide = new Handler(hideThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mHandler.sendEmptyMessage(MSG_HIDE_VIEW);
            }
        };
        registerScreenReceiver();
    }
    private ScreenBroadCastUtils mScreenBroadCastUtils;
    private void registerScreenReceiver() {
        mScreenBroadCastUtils = new ScreenBroadCastUtils(getContext(),new ScreenBroadCastUtils.ScreenLockListener() {
            @Override
            public void onScreenOn() {
                Log.i(TAG,"onScreenOn()");
                startResume();
            }

            @Override
            public void onScreenLock() {
                Log.i(TAG,"onScreenLock()");
                stop();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Cog.e(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.d(TAG, "Surface changed to format " + format + " width " + width + " height " + height);
        mSurfaceView    =   holder.getSurface() ;
        startResume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Cog.e(TAG, "surfaceDestroyed");
        mRetryCount = 0 ;
        mSurfaceView = null;
        mDelayPlayingAfterSurfaceObtained = true;
        stop();
    }

    public void setIsExpanding(boolean isExpanding) {
        this.isExpanding = isExpanding;
    }

    public void setDisplayListener(DisplayListener mDisplayListener) {
        this.mDisplayListener = mDisplayListener;
    }

    /**
     * 绑定播放的组件 /设置 编解码格式和演讲者
     * @param view
     * @param title
     * @param encodeType    0:hardware encode  1:software encode .
     */
    public void bindVideoView(SurfaceView view ,String title ,int encodeType) {
        Cog.i(TAG," bindVideoView ~ " + title);
        if (view == null) {
            throw new NullPointerException("VideoView is NULL~!!");
        }
        if(null != view){
            view.getHolder().addCallback(this);
        }
        mTitleText.setText(title);
        //设置点击事件
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 点击显示，再次点击消失 .
                if (getVisibility() == View.GONE) {
                    showControl();
                }else{
                    hideControl();
                }
                if (!isPlaying && null != mDisplayListener) {
                    mDisplayListener.show();
                }
                return false;
            }
        });
    }

    public void setAudioMixer(BNAudioMixer mAudioMixer) {
        this.mAudioMixer = mAudioMixer;
    }

    public void stop(){
        mRetryCount = 0;
        mDelayPlayingAfterSurfaceObtained = false ;
        if(mPlayerHash.keySet().size()>0){
            for(String key :mPlayerHash.keySet()){
                if(key.equals(mTempUrl)){
                    BNMediaPlayer player = mPlayerHash.get(key);
                    player.setReceiveVideo(false , null );
                }
            }
        }
    }


    /**
     * 恢复播放所有的音频 .
     */
    public void startResume() {
        Cog.i(TAG , " startResume () @start～ " );
        for(String key : mPlayerHash.keySet()){
            Cog.i(TAG , " startResume () ~ key :: " + key);
            if(key.equals(mTempUrl) && !TextUtils.isEmpty(mTempUrl)){
                BNMediaPlayer player = mPlayerHash.get(key);
                player.setReceiveVideo(true,mSurfaceView);
            }
        }
        Cog.i(TAG , " startResume () @end～ " );
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.e(TAG, "onFinishInflate()");
        this.setVisibility(GONE);
        ButterKnife.bind(this);

        mExpandIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                touchControl();
                Activity activity = (Activity) getContext();
                /*if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    isExpanding =   true;
                    if(null != mExpandListener){
                        mExpandListener.expand();
                    }
                } else if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    isExpanding =   false ;
                    if(null != mExpandListener){
                        mExpandListener.collapse();
                    }
                }*/

                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                if (getHeight() < screenHeight/2) {
                    isExpanding =   true;
                    if(null != mExpandListener){
                        mExpandListener.expand();
                    }
                } else {
                    isExpanding =   false ;
                    if(null != mExpandListener){
                        mExpandListener.collapse();
                    }
                }
            }
        });

        mNextVideoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Cog.i(TAG," next video will be selected ~! mTempUrl :"+mTempUrl);
                if(null != mOnNextVideoListener) mOnNextVideoListener.nextVideo();

                if(!TextUtils.isEmpty(mTempUrl)){
                    int index = mUrlList.indexOf(mTempUrl);
                    index++;
                    if(index>=mUrlList.size()){
                        index = 0 ;
                    }
                    String nextUrl = mUrlList.get(index) ;
                    Cog.i(TAG," next video will selected ："+nextUrl);
                    showSpeaker(nextUrl);
                }else{
                    Cog.e(TAG , "mTempUrl is NULL !!!");
                }
            }
        });

        mCloseVideoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeVideo();
                if(null != mOnCloseVideoListener) mOnCloseVideoListener.close();
            }
        });
    }

    /**
     * 关闭当前视频
     */
    private void closeVideo() {
        if(!TextUtils.isEmpty(mTempUrl)){
            BNMediaPlayer player = mPlayerHash.get(mTempUrl);
            if(null != player){
                player.setReceiveVideo(false , null );
            }
        }

        mHandler.sendEmptyMessage(MSG_HIDE_VIEW);
    }

    //延时消失
    @Override
    public void touchControl() {
        Cog.e(TAG, " touchControl()~");
        mHandlerHide.removeMessages(MSG_NOTIFY_HIDE_VIEW);
        mHandlerHide.sendEmptyMessageDelayed(MSG_NOTIFY_HIDE_VIEW,HIDE_PERIOD);
    }

    @Override
    public void destroyView() {
        Cog.i(TAG , " destroyView () @start～ " );
        //清空BnMediaPlayer对象 .
        for(String key : mPlayerHash.keySet()){
            BNMediaPlayer player = mPlayerHash.get(key);
            if(null != player){
                player.release();
                Cog.i(TAG , " player release () ！ " );
            }
        }
        Cog.i(TAG , " destroyView () @end～ " );
    }

    @Override
    public void showControl() {
        Cog.e(TAG, "showControl()~");
        touchControl();
        if (getVisibility() == View.GONE) {

            this.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(EApplication.instance(), R.anim.abc_fade_in);
            this.startAnimation(anim);

            if (null != mDisplayListener) {
                mDisplayListener.show();
            }
        }
    }

    @Override
    public void hideControl() {
        Cog.e(TAG, "hideControl");
        //无效的动画会引起视图钻咱的闪现　
        if(getVisibility() != VISIBLE){
            return;
        }
        this.setVisibility(View.GONE);
        this.startAnimation(AnimationUtils.loadAnimation(EApplication.instance(), R.anim.abc_fade_out));

        if (null != mDisplayListener) {
            mDisplayListener.hide();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Cog.i(TAG , " onDetachedFromWindow() ~");
        super.onDetachedFromWindow();
        if(null != mScreenBroadCastUtils){
            mScreenBroadCastUtils.destroy(getContext());
        }
    }


    public void setOnExpandListener(ExpandListener expandListener) {
        this.mExpandListener = expandListener;
    }

    private void hideView(){
        if (!isOnTouch) {
            hideControl();
        }
    }

    private void setTitle(String title){
        if (getVisibility() == View.VISIBLE) {
            mTitleText.setText(title);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle bd = (Bundle) msg.obj;
        switch (msg.what) {
            case MSG_SET_TITLE://set the title
                String title = bd.getString("title");
                setTitle(title);
                break;
            case MSG_RESOLVE_DISPLAY:
                //do nothing ...
                break;
            case MSG_HIDE_VIEW://hide control view
                hideView();
                break;
        }
        return false;
    }

    /**
     * 监听本控件的隐藏状态
     */
    public interface DisplayListener {

        /**
         * control view show on screen
         */
        void show();

        /**
         * I am hide
         */
        void hide();
    }

    /**
     * 横竖屏监听
     */
    public interface ExpandListener{
        /**
         * 点击了 Expand 图标
         */
        void expand();

        /**
         * 回到竖屏
         */
        void collapse();
    }

    public interface ICloseVideo{
        /**
         * 关闭视频
         */
        void close();
    }

    public interface INextVideo{
        /**
         * 下一个视频
         */
        void nextVideo();
    }

}
