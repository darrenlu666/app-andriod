package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codyy.erpsportal.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 常见问题界面
 */
public class CommonMalfunctionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_malfunctions);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_return)
    public void onReturnBtnClick() {
        finish();
    }

    @OnClick(R.id.btn_search)
    public void onSearchBtn() {
        SearchMalfunctionsActivity.start(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CommonMalfunctionsActivity.class);
        context.startActivity(intent);
    }
}
