package com.codyy.erpsportal.commons.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * 处理6.0以上的通用请求.
 * Created by poe on 17/05/17.
 */

public class PermissionUtils {
    /**
     * 读取sd卡
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 0x220;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * if api < 23 , return {@link PermissionInterface#next()}
     */
    public static void verifyStoragePermissions(final Activity act, View view , PermissionInterface permissionInterface) {
        if(Build.VERSION.SDK_INT < 23){
           if(null != permissionInterface) permissionInterface.next();
            return;
        }
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(act,Manifest.permission.READ_EXTERNAL_STORAGE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(act,Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                Snackbar.make(view , "申请SD卡查看权限！",Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(act, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(act, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }else{
            if(null != permissionInterface) permissionInterface.next();
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * if api < 23 , return {@link PermissionInterface#next()}
     */
    public static void verifyStorageCameraPermissions(final Activity act, View view , PermissionInterface permissionInterface) {
        if(Build.VERSION.SDK_INT < 23){
            if(null != permissionInterface) permissionInterface.next();
            return;
        }
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(act,Manifest.permission.CAMERA)||
                    ActivityCompat.shouldShowRequestPermissionRationale(act,Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                Snackbar.make(view , "申请SD卡查看权限和拍照权限！",Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(act, PERMISSIONS_STORAGE_CAMERA,REQUEST_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(act, PERMISSIONS_STORAGE_CAMERA, REQUEST_EXTERNAL_STORAGE);
            }
        }else{
            if(null != permissionInterface) permissionInterface.next();
        }
    }


    public static void onRequestPermissionsResult(int requestCode,final Activity act, PermissionInterface permissionInterface){
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                if (ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //do nothing ．．．　ｏｒ
                    if(null != permissionInterface) permissionInterface.next();
                }
                break;
        }
    }

    public interface PermissionInterface {
        /**
         * 请求权限成功，继续下一步.
         */
        void next();

    }
}
