package com.codyy.erpsportal.commons.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.codyy.bennu.sdk.BNPublisher;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WeakHandler;
import com.codyy.erpsportal.commons.widgets.BnChatVideoLayout;
import com.codyy.erpsportal.commons.widgets.BnChatVideoView;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;

/**
 * 视频采集服务
 * Created by poe on 15-10-17.
 */
public class OnlineMeetingService extends Service{
    private static final String TAG = OnlineMeetingService.class.getSimpleName();
    private static final int ACTION_RE_PUBLISH = 0 ;// 重新发送
    private IBinder mBinder =   new MyBinder();
    private String mUri ;//视频发送地址
    private BnChatVideoLayout mBnChatVideoLayout;
    private WindowManager mWindowManager;
    private BnChatVideoView.IPublishCallBack mPublishListener;
    private int mX = 0;//
    private int mY = 60;//
    private int mInitY = 0;
    private int mWidth = 1;
    private int mHeight = 1;
    private WindowManager.LayoutParams mParamsF ;
    private long mLastPressTime ;
    private OnClickListener mOnClickListener;
    private OnlineMeetingHandler mHandler = new OnlineMeetingHandler(this);
    private BNAudioMixer mAudioMixer;
    private static int MAX_PUBLISH_RETRY = 3 ;//链接ｄｍｓ失败，采集本地视频失败最多尝试３次
    private int mRetryPublishCount = 0 ;//
    private boolean mIsPlaying = false ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
      /*  if(Build.VERSION.SDK_INT < 21){//5.0之下 ...多了一个ｓｔａｔｕｓ　ｂａｒ　的高度 .
            mInitY = UIUtils.dip2px(this , 25+56+45+1+200);
        }else{*/
            mInitY  = UIUtils.dip2px(this,56+45+1+200+5)+1;
//        }
        Log.i(TAG," Y　：　"+mInitY);
        mWidth  =  1;// getResources().getDisplayMetrics().widthPixels/2-UIUtils.dip2px(this,1);
        mHeight =  1;// UIUtils.dip2px(this,135-1);
        createFloatingView();
    }

    public void publish(){
        Cog.i(TAG,"publish()~");
        if(null !=mBnChatVideoLayout && null!= mBnChatVideoLayout.getChatView()) {
            Cog.i(TAG , " publish : "+mUri);
            mBnChatVideoLayout.publish(mUri, mAudioMixer);
            mBnChatVideoLayout.setStopDoneListener(new BNPublisher.StopDoneListener() {
                @Override
                public void stopped() {
                    Cog.i(TAG,"publisher stopped ~ ");
                    mRetryPublishCount ++ ;
//                    ToastUtil.showToast(EApplication.instance() , "Publisher Error ！");
                    //republish
                    if(mRetryPublishCount < MAX_PUBLISH_RETRY){
                        mHandler.sendEmptyMessage(ACTION_RE_PUBLISH);
                    }else{
                        Cog.e(TAG,"请检查当前的ＤＭＳ链接状况：："+mUri);
                    }
                }
            });

            //重新采集 !
            mBnChatVideoLayout.setErrorListener(new BNPublisher.ErrorListener() {
                @Override
                public void error(final int errorCode, final String errorMsg) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            ToastUtil.showToast(EApplication.instance() , "Publisher Error "+ errorCode + " :"+errorMsg);
                            //republish
                            if(mRetryPublishCount < MAX_PUBLISH_RETRY){
                                mRetryPublishCount ++ ;
                                mHandler.sendEmptyMessage(ACTION_RE_PUBLISH);
                            }else{
                                Cog.e(TAG,"请检查当前的ＤＭＳ链接状况：："+errorMsg);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 消除杂音
     */
    public void audioNoiseClean(){
        if(null != mBnChatVideoLayout && null != mBnChatVideoLayout.getChatView()){
            Cog.i(TAG, "audioNoiseClean~");
            mBnChatVideoLayout.getChatView().KillAudioNoise();
        }
    }

    public void cancelAEC(){
        if(null != mBnChatVideoLayout && null != mBnChatVideoLayout.getChatView()){
            Cog.i(TAG, "cancelAEC~");
            mBnChatVideoLayout.getChatView().cancelAEC();
        }
    }

    /**
     * 关联AudioMixer到Publisher
     * @param mixer
     */
    public void setAudioMixer(BNAudioMixer mixer){
        if(null == mixer) return;
        Cog.e(TAG,"mix start set publisher !" +mBnChatVideoLayout.getChatView().getPublisher());
        if(null != mBnChatVideoLayout.getChatView().getPublisher()){
            mixer.setPublisher(mBnChatVideoLayout.getChatView().getPublisher());
            mBnChatVideoLayout.getChatView().KillAudioNoise();
        }
    }

    /**
     * 设置点击监听功能
     * @param mOnClickListener
     */
    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    /**
     * 创建浮动聊天视图
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createFloatingView() {
        Cog.i(TAG," createFloatingView ()");
        if(mBnChatVideoLayout == null) {
            Cog.i(TAG , " new ChatView : y  "+mInitY);
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mBnChatVideoLayout = new BnChatVideoLayout(this);
            mParamsF = new WindowManager.LayoutParams(
                    mWidth, mHeight,
                    WindowManager.LayoutParams.TYPE_TOAST| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,//.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);

            mParamsF.gravity = Gravity.TOP | Gravity.LEFT;
            mParamsF.x = 0;
            mParamsF.y = mInitY;

            mWindowManager.addView(mBnChatVideoLayout, mParamsF);
            mBnChatVideoLayout.onResize(mParamsF.width,mParamsF.height);
            //set action listener ...
            mBnChatVideoLayout.setOnViewControlListener(new BnChatVideoLayout.OnViewControlListener() {
                @Override
                public void closeVideo() {
                    OnlineMeetingActivity.mShowMyViewState = false;
                    hideView();
                }

                @Override
                public void expandCollapse() {
                    if(mParamsF.width >mWidth || mParamsF.width == -1){
                        collapse();
                    }else{
                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                        int screenHeight = getResources().getDisplayMetrics().heightPixels ;
                        mBnChatVideoLayout.onResize(screenWidth,screenHeight);
                    }
                }

                @Override
                public void surfaceCrated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int width, int height) {

                    // TODO: 16-9-28 判断如果比例不是４：３　则全屏
                    if(null != mBnChatVideoLayout && mBnChatVideoLayout.isNeedExpand()){
                        expand();
                    }
                }
            });
        }else{
            Cog.i(TAG , " init y  "+mInitY);
            Cog.i(TAG , " ChatView already exist : y  "+mBnChatVideoLayout.getY());
        }
    }

    /**
     * 全屏
     */
    public void expand(){
        Cog.i(TAG," expand ()");
        if(null != mBnChatVideoLayout) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels ;
            Cog.e(TAG,"w:"+screenWidth + " h:"+screenHeight);
            mParamsF.width =screenWidth;// WindowManager.LayoutParams.MATCH_PARENT;
            if(Build.VERSION.SDK_INT>=23){
                mParamsF.height = WindowManager.LayoutParams.MATCH_PARENT;//(screenHeight-UIUtils.dip2px(EApplication.instance(),34));//
            }else{
                mParamsF.height = (screenHeight-UIUtils.dip2px(EApplication.instance(),46+25));//WindowManager.LayoutParams.MATCH_PARENT;
            }
            mParamsF.x = 0;
            mParamsF.y = 0;
            mWindowManager.updateViewLayout(mBnChatVideoLayout, mParamsF);
        }
    }

    /**
     * 全屏后调用
     */
    private void resizeAfterExpand(){
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels ;
        if(null != mBnChatVideoLayout){
            mBnChatVideoLayout.onResize(screenWidth,screenHeight);
        }
    }

    /**
     * 缩放
     */
    public void collapse(){
        Cog.i(TAG," collapse ()");
        if(null != mBnChatVideoLayout && mBnChatVideoLayout.getWidth()>1){
            showView();
        }
    }

    /**
     * 改变的y位置 。
     */
    public void changeView(int y ,int statusBarHeight){
       /* if(Build.VERSION.SDK_INT<21){
            mInitY = y;
            mY = y;
        }else{*/
            mInitY = y -statusBarHeight;
            mY = mInitY;
//        }
        Cog.i(TAG," changeView ()　Y:"+mInitY);
    }

    /**
     * 控制显示我的视频 。
     * @return  false:隐藏 true:显示
     */
    public  boolean showOrHideView(){
        Cog.i(TAG," showOrHideView ()");
        if(null!= mBnChatVideoLayout && mBnChatVideoLayout.getWidth()>1){
            hideView();
            return  false;
        }else{
            showView();
            return true;
        }
    }

    /**
     * 我的视频-隐藏视频采集视频 。
     */
    public void  hideView(){
        Cog.i(TAG,"hideView()");
        if(null != mBnChatVideoLayout){
            mParamsF.width = 1;
            mParamsF.height = 1;
            mParamsF.x = mX;
            mParamsF.y = mInitY;
            mWindowManager.updateViewLayout(mBnChatVideoLayout, mParamsF);
            mBnChatVideoLayout.onResize(mParamsF.width,mParamsF.height);
        }
    }

    /**
     * 是否可见
     * @return true ：可见 false：隐藏
     */
    public boolean getMyVideoVisibility(){
        return mParamsF.width > 1;
    }

    public boolean isPlaying(){
        if(null != mBnChatVideoLayout){
            mIsPlaying = mBnChatVideoLayout.isPlaying();
        }
        return mIsPlaying;
    }

    /**
     * 我的视频-回复正常尺寸
     */
    public void showView(){
        Cog.i(TAG,"showView()");
        if(null != mBnChatVideoLayout){
            mParamsF.width =  mWidth;
            mParamsF.height = mHeight;
            mParamsF.x = mX;
            mParamsF.y = mInitY;
            mWindowManager.updateViewLayout(mBnChatVideoLayout,mParamsF);
            mBnChatVideoLayout.onResize(mParamsF.width,mParamsF.height);
        }
    }

    /**
     * start publish video to the uri
     * @param uri
     * @param needInitial 如果已经发布了视频是否需要重新发送。
     * @param listener
     */
    public void start(String uri ,boolean needInitial, BNAudioMixer mixer ,BnChatVideoView.IPublishCallBack listener){
        this.mPublishListener   =   listener;
        this.mAudioMixer = mixer ;
        mBnChatVideoLayout.setPublishListener(listener);
        start(uri, needInitial);
    }

    /**
     * start publish .
     * @param uri
     */
    public void start(String uri ,boolean needInitial){
        setURI(uri);
        setPublish(needInitial);
    }

    private void setPublish(boolean needInitial) {
        Cog.i(TAG,"setPublish()");
        Log.i(TAG, "setPublish()~ needInitial : "+needInitial);
        mHeight =  UIUtils.dip2px(this,135-1);
        mWidth  =  getResources().getDisplayMetrics().widthPixels/2-UIUtils.dip2px(this,1);
        createFloatingView();
        //是否打开麦克风.
        if(mBnChatVideoLayout.isPlaying()){
            if(!needInitial){//仅仅显示view .
                //已经发布则直接返回发布成功回调
                if(null != mPublishListener)
                    mPublishListener.onPublishSuccess();
            }else{//重新发送视频
                Log.i(TAG, "setPublish()~ 重新发送视频 ");
                mBnChatVideoLayout.getChatView().stop(new BNPublisher.StopDoneListener() {
                    @Override
                    public void stopped() {
                        mRetryPublishCount = 0;
                        mHandler.sendEmptyMessage(ACTION_RE_PUBLISH);
                    }
                });
             }
        }else{
            mRetryPublishCount = 0 ;
            mHandler.sendEmptyMessage(ACTION_RE_PUBLISH);
        }
    }

    /**
     * 去除浮动的视图层
     * 关闭视频采集
     */
    public void stop(){
        Cog.i(TAG,"stop()");
        if(null != mBnChatVideoLayout){
            Cog.i(TAG, "stop publish ~~");
            mBnChatVideoLayout.stop(new BNPublisher.StopDoneListener() {
                @Override
                public void stopped() {
                    if(null != mBnChatVideoLayout) {
                        mBnChatVideoLayout.getChatView().release();
                        //remove view .
                        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                        mWindowManager.removeView(mBnChatVideoLayout);
                        mBnChatVideoLayout = null;
                        mWindowManager = null;
                    }
                }
            });
        }
        mBinder = null;
    }

    /**
     * set the local uri .
     * @param uri
     */
    public void setURI(String uri) {
        if(null == uri) return;
        if(mUri==null ||!uri.equals(mUri)){
            this.mUri = uri;
        }
    }

    public void openLocalAudio(){
        if(null != mBnChatVideoLayout)
            mBnChatVideoLayout.getChatView().enableAudio();
    }

    public void closeLocalAudio(){
        if(null != mBnChatVideoLayout)
            mBnChatVideoLayout.getChatView().disableAudio();
    }

    public void openLocalVideo(){
        if(null != mBnChatVideoLayout)
            mBnChatVideoLayout.getChatView().enableVideo();
    }

    public void closeLocalVideo(){
        if(null != mBnChatVideoLayout)
            mBnChatVideoLayout.getChatView().disableVideo();
    }

    /**
     * 是否旋转了屏幕
     * @param isRotated  false:不改变摄像机位 只发送新的流 true :只改变相机的机位
     */
    public void turnBackgroundOrForeground(boolean isRotated){
        if(null != mBnChatVideoLayout){
            // TODO: 16-9-5 判断 If  Camera is already stopped!
            mBnChatVideoLayout.getChatView().exchangeCamera(isRotated);
        }
    }

    @Override
    public void onDestroy() {
        Cog.i(TAG,"onDestroy~ ");
        stop();
        super.onDestroy();
    }

    public class MyBinder extends Binder{

        public OnlineMeetingService getService(){
            return  OnlineMeetingService.this;
        }
    }

    public interface  OnClickListener{
        /**
         * 单击
         */
        void onClick();
    }

    private static class OnlineMeetingHandler extends  WeakHandler<OnlineMeetingService> {

        public OnlineMeetingHandler(OnlineMeetingService owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(getOwner() == null) return;
            switch (msg.what){
                case  ACTION_RE_PUBLISH://重新发送
                    getOwner().publish();
                    break;
            }
        }
    }

}
