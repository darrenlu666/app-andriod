package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import me.dm7.barcodescanner.zxing.CustomZXingScannerView;

public class ScanSerialActivity extends AppCompatActivity implements CustomZXingScannerView.ResultHandler {

    private final static String TAG = "ScanSerialActivity";

    public final static String EXTRA_CLASSROOM = "com.codyy.erpsportal.EXTRA_CLASSROOM";

    private CustomZXingScannerView mZBarScannerView;

    private UserInfo mUserInfo;

    private Disposable mDisposable;

    private String mSearchingSerial;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_serial);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
//        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.fl_content);
        mZBarScannerView = (CustomZXingScannerView) findViewById(R.id.fl_content);
//        contentFrame.addView(mZBarScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mZBarScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mZBarScannerView.startCamera();          // Start camera on resume
        // If you would like to resume scanning, call this method below:
        mZBarScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mZBarScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Cog.d(TAG, "handleResult rawResult=",rawResult.getText()); // Prints scan results
        Cog.d(TAG, "handleResult barcodeFormat=",rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
//        Toast.makeText(ScanSerialActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();

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
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_CLASSROOM, classroomSelectItem);
                                setResult(RESULT_OK, intent);
                                finish();
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
