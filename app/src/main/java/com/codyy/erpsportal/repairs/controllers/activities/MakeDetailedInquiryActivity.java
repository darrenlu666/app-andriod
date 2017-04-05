package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codyy.erpsportal.R;

/**
 * 追问
 */
public class MakeDetailedInquiryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_detailed_inquiry);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MakeDetailedInquiryActivity.class);
        context.startActivity(intent);
    }
}
