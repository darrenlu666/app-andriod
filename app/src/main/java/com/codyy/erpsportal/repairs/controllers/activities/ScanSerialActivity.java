/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */

package com.codyy.erpsportal.repairs.controllers.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.models.engines.RepairApi;
import com.codyy.erpsportal.repairs.models.entities.ClassroomSelectItem;
import com.google.gson.Gson;
import com.google.zxing.Result;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.core.BarcodeScannerView.NoCameraListener;
import me.dm7.barcodescanner.zxing.CustomZXingScannerView;

public class ScanSerialActivity extends AppCompatActivity implements CustomZXingScannerView.ResultHandler {

    private final static String TAG = "ScanSerialActivity";

    public final static String EXTRA_CLASSROOM = "com.codyy.erpsportal.EXTRA_CLASSROOM";

    private final static int REQUEST_CAMERA = 4190;

    private CustomZXingScannerView mZBarScannerView;

    private UserInfo mUserInfo;

    private Disposable mDisposable;

    private String mSearchingSerial;

    private LoadingDialog mLoadingDialog;

    private boolean mInited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_serial);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mZBarScannerView = (CustomZXingScannerView) findViewById(R.id.fl_content);
        mZBarScannerView.setNoCameraListener(new NoCameraListener() {
            @Override
            public void onNoCamera() {
                ToastUtil.showToast(ScanSerialActivity.this, "请在设置中打开摄像头权限");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        int cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraGranted == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                ToastUtil.showToast(ScanSerialActivity.this, "请在设置中打开摄像头权限");
            }
        }
    }

    private void startCamera() {
        mInited = true;
        mZBarScannerView.setResultHandler(ScanSerialActivity.this); // Register ourselves as a handler for scan results.
        mZBarScannerView.startCamera();          // Start camera on resume
        // If you would like to resume scanning, call this method below:
        mZBarScannerView.resumeCameraPreview(ScanSerialActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mInited) {
            mZBarScannerView.stopCamera();           // Stop camera on pause
            mInited = false;
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Cog.d(TAG, "handleResult rawResult=",rawResult.getText()); // Prints scan results
        Cog.d(TAG, "handleResult barcodeFormat=",rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if (rawResult.getText().equals(mSearchingSerial)) {
            return;
        }
        mSearchingSerial = rawResult.getText();
        RepairApi webApi = RsGenerator.create(RepairApi.class);
        mDisposable = webApi.getClassRoom( rawResult.getText(), mUserInfo.getUuid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Cog.d(TAG, "doOnSubscribe ", Thread.currentThread());
                    }
                })
                .doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Cog.d(TAG, "doOnNext:", jsonObject);
                        mLoadingDialog = LoadingDialog.newInstance(true);
                        mLoadingDialog.show(getSupportFragmentManager(), "loading");
                    }
                })
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Cog.d(TAG, "handleResult:", jsonObject);
                        dismissLoadingDialog();
                        if ("success".equals(jsonObject.optString("result"))) {
                            String dataStr = jsonObject.optString("data");
                            if (TextUtils.isEmpty(dataStr)) {
                                showCustomToast("发现此教室编码非您本校教室编码\n如有问题请联系管理员");
                            } else {
                                ClassroomSelectItem classroomSelectItem = new Gson()
                                        .fromJson(dataStr, ClassroomSelectItem.class);
                                if (classroomSelectItem != null && !TextUtils.isEmpty(classroomSelectItem.getClsClassroomId())) {
                                    Intent intent = new Intent();
                                    intent.putExtra(EXTRA_CLASSROOM, classroomSelectItem);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    showCustomToast("发现此教室编码非您本校教室编码\n如有问题请联系管理员");
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissLoadingDialog();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissLoadingDialog();
                    }
                });
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mZBarScannerView.post(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    /**
     * 弹出一个白底的toast
     */
    private void showCustomToast(String msg){
        TextView textView = new TextView(this);
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        int padding = UIUtils.dip2px(this, 16);
        textView.setPadding(padding, padding, padding, padding);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        textView.setTextColor(0xff444444);
        textView.setText(msg);
        Toast toast=new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(textView);
        toast.show();
    }

    public static void start(Activity activity, UserInfo userInfo, int requestCode) {
        Intent intent = new Intent(activity, ScanSerialActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        activity.startActivityForResult(intent, requestCode);
    }
}
