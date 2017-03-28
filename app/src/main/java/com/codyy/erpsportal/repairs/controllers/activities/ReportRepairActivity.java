package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codyy.erpsportal.R;

/**
 * 报修
 */
public class ReportRepairActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repair);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RepairDetailsActivity.class);
        context.startActivity(intent);
    }
}
