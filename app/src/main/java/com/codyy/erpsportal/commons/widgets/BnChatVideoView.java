package com.codyy.erpsportal.commons.widgets;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import com.codyy.bennu.dependence.publish.gl.SurfaceView;
import com.codyy.bennu.sdk.BNPublisher;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.bennu.sdk.impl.BNLivePublisher;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.WeakHandler;

/**
 * 视频聊天类.
 */
public class BnChatVideoView extends SurfaceView {
    private static final String TAG = "BnChatVideoView";
    /**
     * 发送成功
     */
    private static final int MSG_PUBLISH_SUCCESS = 0 ;
    /**
     * 发送失败
     */
    private static final int MSG_PUBLISH_FAILED = 1 ;
    /**
     * 发送停止结束
     */
    private static final int MSG_PUBLISH_STOP_DONE = 10 ;

    private BNPublisher mPublisher = null;
    private String mUrl;
    private SurfaceView mSurfaceView;
    private boolean isPlaying   =   false ;//播放状态
    private IPublishCallBack mPublishListener;
    private BnChatHandler mHandler = new BnChatHandler(this);
    private BNAudioMixer mAudioMixer ;
    private BNPublisher.StopDoneListener mStopDoneListener ;

    public BnChatVideoView(Context context) {
        super(context);
        Cog.i(TAG , " BnChatVideoView(Context context) ~ ");
    }

    public BnChatVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Cog.i(TAG , " BnChatVideoView(Context context, AttributeSet attrs) ~ ");
        init(attrs, 0);
    }

    public void setStopDoneListener(BNPublisher.StopDoneListener stopDoneListener) {
        this.mStopDoneListener = stopDoneListener;
    }

    public BnChatVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        Cog.i(TAG , " BnChatVideoView(Context context, AttributeSet attrs, int defStyle) ~ ");
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs , int defStyle){
        Cog.i(TAG , " init(AttributeSet attrs , int defStyle) ");
        mSurfaceView    =   this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.i(TAG   , " onFinishInflate ()");
    }

    public void setErrorListener(BNPublisher.ErrorListener errorListener){
        if(null != mPublisher) mPublisher.setErrorListener(errorListener);
    }

    public void setPublishListener(IPublishCallBack publishListener) {
        this.mPublishListener = publishListener;
    }

    public BNPublisher getPublisher() {
        return mPublisher;
    }

    public void setPublisher(BNPublisher mPublisher) {
        this.mPublisher = mPublisher;
    }

    /**
     * 回音消除，set输入流
     */
    public void KillAudioNoise(){
        //回音消除
        if(mPublisher != null){
            mPublisher.setAECMFlag(true);
            Cog.i(TAG, "回音消除成功！");
        }
    }

    /**
     * 关联AudioMixer到Publisher
     */
    public void setAudioMixer(){
        if(null == mAudioMixer) return;
        Cog.e(TAG,"mix start set publisher !" +getPublisher());
        if(null != getPublisher()){
            Cog.e(TAG,"mix set publisher !" +getPublisher());
            mAudioMixer.setPublisher(getPublisher());
            KillAudioNoise();
        }
    }

    public void cancelAEC(){
        if(null != mPublisher){
            //回音消除se
            mPublisher.setAECMFlag(false);
        }
    }

    /**
     * 开始发布线程
     * @param url
     */
    public void publish(String url , final BNAudioMixer mixer){
//        this.mAudioMixer = mixer ;
        setURL(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG , "mPublisher == null ?" + (mPublisher == null));
                if(mPublisher == null){
                    Cog.i(TAG,"初始化new publish()~");
                    mPublisher = new BNLivePublisher();
                    if(mPublisher == null){
                        return;
                    }
                    mPublisher.init();
                    if(mAudioMixer == null ){
                        mAudioMixer = mixer;
                        setAudioMixer();
                    }
                }else{
                    // .. do nothing ...
                }

                mPublisher.setStopDoneListener(new BNPublisher.StopDoneListener() {
                    @Override
                    public void stopped() {
                        mHandler.sendEmptyMessage(MSG_PUBLISH_FAILED);
                        Log.d(TAG, "Stop done");
                    }
                }
                );

//                mPublisher.setVideoSize(640, 480);
                //２０１６－０９－２１　发送视频　３20 x 240 .
                mPublisher.setVideoSize(320, 240);
                mPublisher.setMediaType(BNLivePublisher.AUDIOANDVIDEO);
                //设置启用的摄像头 0 ：后置摄像头 1：前置摄像头
                mPublisher.setCamera(1, getResources().getConfiguration().orientation);
                //回音消除
                int ret = mPublisher.start(mUrl, mSurfaceView);
                if (ret == 0) {
                    Cog.e(TAG, "publish success!");
                    isPlaying   =   true;
                        mPublisher.setStartDoneListener(new BNPublisher.StartDoneListener() {
                            @Override
                            public void started() {
                                mHandler.sendEmptyMessage(MSG_PUBLISH_SUCCESS);
                            }
                        });
                } else {
                    Cog.e(TAG, "publish failed!");
                    isPlaying   =   false;
                    mHandler.sendEmptyMessage(MSG_PUBLISH_FAILED);
                }
                Cog.i(TAG,"publish :"+mPublisher);
            }
        }).start();
    }

    public void stop(BNPublisher.StopDoneListener listener){
        isPlaying   =   false;
        if(null != mPublisher){
            Cog.i(TAG,"stop()");
            cancelAEC();
            mPublisher.setStopDoneListener(listener);
            mPublisher.stop();
            Log.e(TAG,"mPublisher  == NULL ? " + (mPublisher == null)) ;
        }
    }

    public void release(){
        if(null != mPublisher){
            Cog.i(TAG, "release()");
            mPublisher.release();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setURL(String URL) {
        this.mUrl = URL;
    }

    public void disableAudio(){
        if(null != mPublisher)
            mPublisher.disableAudio();
    }

    public void enableAudio(){
        if(null != mPublisher)
            mPublisher.enableAudio();
    }

    /**
     * 是否旋转了屏幕
     * @param isRotated  false:不改变摄像机位 只发送新的流 true :只改变相机的机位
     */
    public void exchangeCamera(boolean isRotated){
        if(null != mPublisher) {
            int aa = getResources().getConfiguration().orientation;
            mPublisher.changeCamera(isRotated,aa);
        }
    }

    public void disableVideo(){

        if(null != mPublisher){
            mPublisher.disableVideo();
        }
    }

    public void enableVideo(){
        if(null != mPublisher){
            mPublisher.enableVideo();
        }
    }

    public interface  IPublishCallBack{
        /**
         * 视频发布成功返回
         */
        void onPublishSuccess();

        /**
         * 视频发布失败了
          */
        void onPublishFailure(String error);
    }

    private static class BnChatHandler extends WeakHandler<BnChatVideoView>{

        public BnChatHandler(BnChatVideoView owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Cog.i(TAG,"mHandler message :"+msg.what);
            if(getOwner() == null) return;
            switch (msg.what){
                case  MSG_PUBLISH_SUCCESS://publish success
                    if(null != getOwner().mPublishListener){
                        getOwner().mPublishListener.onPublishSuccess();
                    }
                    break;
                case  MSG_PUBLISH_FAILED://publish failed
                    if(null !=getOwner().mPublishListener){
                        getOwner().mPublishListener.onPublishFailure("publish failed !");
                    }
                    break;
                case MSG_PUBLISH_STOP_DONE://发送停止
                    if(null != getOwner().mStopDoneListener){
                        getOwner().mStopDoneListener.stopped();
                    }
                    break;
            }
        }
    }
}
