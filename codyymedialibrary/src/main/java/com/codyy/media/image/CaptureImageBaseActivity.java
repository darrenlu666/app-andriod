package com.codyy.media.image;

import android.app.Activity;
import android.os.Bundle;


public class CaptureImageBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CaptureImageManager.getInst().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CaptureImageManager.getInst().removeActivity(this);
    }

}
