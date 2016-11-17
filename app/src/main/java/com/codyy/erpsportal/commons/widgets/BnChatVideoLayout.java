package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codyy.bennu.sdk.BNPublisher;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * 视频会议-我的视频
 * Created by poe on 2016/07/08.
 */
public class BnChatVideoLayout extends RelativeLayout implements SurfaceHolder.Callback2{
    public static final String TAG = "BnChatVideoLayout";
    private BnChatVideoView mBnVideoView;
    private TextView mHintTv;
    private ImageView mCloseVideoIv;
    private ImageView mExpandCollapseIv;

    private OnViewControlListener mOnViewControlListener;

    public BnChatVideoLayout(Context context) {
        super(context);
        init(context);
    }

    public BnChatVideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BnChatVideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BnChatVideoLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        Cog.i(TAG , " init ~ ");
        LayoutInflater.from(context).inflate(R.layout.bn_chat_video_layout, this, true);
        onFinishInflate();
        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Cog.i(TAG , " onFinishInflate ~ ");
        mBnVideoView = (BnChatVideoView) findViewById(R.id.bnVideoView);
        mHintTv = (TextView) findViewById(R.id.hintText);
        mCloseVideoIv   = (ImageView) findViewById(R.id.close_video_image_view);
        mExpandCollapseIv   = (ImageView) findViewById(R.id.expand_video_image_view);
        //set listener ...
        mCloseVideoIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭视频采集
               if(null != mOnViewControlListener){
                   mOnViewControlListener.closeVideo();
            }
            }
        });

        mExpandCollapseIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mOnViewControlListener){
                    mOnViewControlListener.expandCollapse();
                }
            }
        });
        mBnVideoView.getHolder().addCallback(this);
        mBnVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Cog.i(TAG , "bn video view click ~!");
//                onResize(getWidth(),getHeight());
                return false;
            }
        });
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        Cog.i(TAG , " onViewAdded(View child) ");
    }


    public void setOnViewControlListener(OnViewControlListener viewControlListener) {
        this.mOnViewControlListener = viewControlListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cog.i(TAG , " onAttachedToWindow() ");
    }

    /**
     * 获取聊天的View
     * @return
     */
    public BnChatVideoView getChatView() {
        return mBnVideoView;
    }

    public boolean isPlaying() {
        if(null != mBnVideoView){
            return mBnVideoView.isPlaying();
        }
        return false;
    }

    public void publish(String url , BNAudioMixer mixer){
        if(null != mBnVideoView) mBnVideoView.publish(url,mixer);
    }

    public void setStopDoneListener(BNPublisher.StopDoneListener stopDoneListener) {
        if(null != mBnVideoView) mBnVideoView.setStopDoneListener(stopDoneListener);
    }

    public void setErrorListener(BNPublisher.ErrorListener errorListener){
        if(null != mBnVideoView) mBnVideoView.setErrorListener(errorListener);
    }

    public void setPublishListener(BnChatVideoView.IPublishCallBack publishListener) {
        if(null != mBnVideoView) mBnVideoView.setPublishListener(publishListener);
    }

    public void release(){
        if(null != mBnVideoView) mBnVideoView.release();
    }

    public void stop(BNPublisher.StopDoneListener listener){
        if(null != mBnVideoView) mBnVideoView.stop(listener);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        Cog.d(TAG," surfaceRedrawNeeded ~");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Cog.i(TAG , "surfaceCreated~ ");
       //disappear the hint text ...
        if(null != mHintTv){
            mHintTv.setVisibility(GONE);
        }
        if(null != mOnViewControlListener) mOnViewControlListener.surfaceCrated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Cog.i(TAG , "surfaceChanged~ "+" width:"+width +" height:"+height);

        if(null != mOnViewControlListener) mOnViewControlListener.surfaceChanged(holder,width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Cog.i(TAG , "surfaceDestroyed~ ");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Cog.i(TAG,"onMeasure width x height : "+getWidth()+"x"+getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Cog.d(TAG,"changed : "+changed+" l: "+l +" t:"+t+" r:"+r+" b:"+b);
    }

    public void preFullScree(){
        RelativeLayout.LayoutParams params ;
        params = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        mBnVideoView.setLayoutParams(params);
    }

    public void onResize(int width ,int height){
        Cog.d(TAG,"onResize : "+width+"*"+height);
        if(width>1){
            double now = (double) height/(width*1.0f);
            double that = (double) 4/(3*1.0f);
            RelativeLayout.LayoutParams params ;
            if(now > that){
                height =(int) (width*that);
            }else{
                width = (int)(height/that);
            }
            params = new LayoutParams(width,height);
            params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            mBnVideoView.setLayoutParams(params);
        }
    }

    public boolean isNeedExpand(){
        int width = mBnVideoView.getWidth();
        int height = mBnVideoView.getHeight();
        if(width == 1||height == 1) return false;
        boolean result = false ;
        double now = (double) height/(width*1.0f);
        double that = (double) 4/(3*1.0f);

        if(now<that){
            result = true;
        }
        return result;
    }

    public interface OnViewControlListener{
        /**
         * 关闭/隐藏视频
         */
        void closeVideo();

        /**
         * 全屏 or 恢复
         */
        void expandCollapse();

        /**
         *
         */
        void surfaceCrated(SurfaceHolder holder);

        void surfaceChanged(SurfaceHolder holder, int width, int height);
    }
}
