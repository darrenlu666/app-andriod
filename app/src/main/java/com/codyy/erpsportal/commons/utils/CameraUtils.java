package com.codyy.erpsportal.commons.utils;

import android.hardware.Camera;
import android.util.Log;

/**
 * 摄像头相关调用工具类 .
 * Created by poe on 15-10-13.
 */
public class CameraUtils {

    public static final String TAG = CameraUtils.class.getSimpleName();
    public static final int CAMERA_RESOURCE_FRONT = 1 ;//后摄像头
    public static final int CAMERA_RESOURCE_BACKGROUND =0 ;//前置摄像头


    /**
     * 获取相机
     * @param cameraID
     * @return
     */
    public static Camera getCameraInstance(int cameraID) {
        Log.d(TAG, "getCameraInstance！");
        Camera c = null;
        try {
            c = Camera.open(cameraID);
            c.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.d(TAG, "打开Camera失败失败");
        }
        return c;
    }

}
