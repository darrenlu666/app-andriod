package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.CustomZXingScannerView;

public class ScanSerialActivity extends AppCompatActivity implements CustomZXingScannerView.ResultHandler {

    private final static String TAG = "ScanSerialActivity";

    private CustomZXingScannerView mZBarScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_serial);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content);
        mZBarScannerView = new CustomZXingScannerView(this);
        contentFrame.addView(mZBarScannerView);
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
        Cog.d(TAG, rawResult.getText()); // Prints scan results
        Cog.d(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Toast.makeText(ScanSerialActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ScanSerialActivity.class);
        activity.startActivity(intent);
    }
}
