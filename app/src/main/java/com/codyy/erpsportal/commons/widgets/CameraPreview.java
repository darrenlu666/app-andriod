package com.codyy.erpsportal.commons.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.codyy.erpsportal.R;

import java.io.IOException;

/**
 * 调用摄像头预览的surfaceView .
 * Created by poe on 15-10-12.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "main";
    public static final int CAMERA_RESOURCE_FRONT = 0 ;
    public static final int CAMERA_RESOURCE_BACKGROUND = 1 ;

    private SurfaceHolder mHolder;
    private Camera mCamera;
    //canvas .
    private int mLastStringId;
    private Paint mTextPaint;
    private static final Object lock = new Object();
    private int mCameraID = CAMERA_RESOURCE_BACKGROUND;


    public CameraPreview(Context context) {
        super(context);
        init();

    }

    private void init() {
        this.mCamera = getCameraInstance(mCameraID);
        // 通过SurfaceView获得SurfaceHolder
        mHolder = getHolder();
        // 为SurfaceHolder指定回调
        mHolder.addCallback(this);
        // 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面 在Android3.0之后弃用
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initPaint();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(33);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "surfaceCreated~") ;
//        drawText(R.string.app_name);
        // 当Surface被创建之后，开始Camera的预览
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "预览失败");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG,"surfaceChanged~") ;
        // Surface发生改变的时候将被调用，第一次显示到界面的时候也会被调用
        if (mHolder.getSurface() == null) {
            // 如果Surface为空，不继续操作
            return;
        }
        // 停止Camera的预览
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "当Surface改变后，停止预览出错");
        }
        // 在预览前可以指定Camera的各项参数
        // 重新开始预览
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "预览Camera出错");
        }
    }

    /**
     * 前后摄像头更换 .
     *
     * @param cameraID  前置摄像头 或者 后置 0/1
     */
    public void setCamera(int cameraID) {
        mCameraID   =   cameraID;
        if(null != mCamera){
            mCamera.stopPreview();
            mCamera.release();
        }
        //启用新的摄像头 .
        mCamera = getCameraInstance(cameraID);
        // 在预览前可以指定Camera的各项参数
        // 重新开始预览
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "预览Camera出错");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG,"surfaceDestroyed~") ;
    }

    //绘制错误提示 added by poe
    public void drawText(@StringRes int stringId) {

        synchronized (lock) {
            if (mLastStringId == stringId) return;
            String text = getResources().getString(stringId);
            if (TextUtils.isEmpty(text)) return;
            int width = getWidth();
            int height = getHeight();

            Canvas canvas = mHolder.lockCanvas(new Rect());//获取画布
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            //文本的宽度
            float textCenterVerticalBaselineY = height / 2 - fm.descent + (fm.descent - fm.ascent) / 2;

            canvas.drawText(text, width / 2, textCenterVerticalBaselineY, mTextPaint);

            mHolder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像
            canvas = null;
            mLastStringId = stringId;
            invalidate();
        }
    }

    /** 打开一个Camera */
    public  Camera getCameraInstance(int cameraID) {
        Camera c = null;
        try {
            c = Camera.open(cameraID);
        } catch (Exception e) {
            Log.d(TAG, "打开Camera失败失败");

            c = Camera.open(cameraID);
        }
        return c;
    }

    public void onDestroy(){
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

}
