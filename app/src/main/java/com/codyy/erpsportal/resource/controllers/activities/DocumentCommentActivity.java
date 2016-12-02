package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.entities.UserInfo;


/**
 * 文档资源评论
 * Created by gujiajia on 2016/7/6
 */
public class DocumentCommentActivity extends FragmentActivity {

    public final static String EXTRA_RESOURCE_ID = "arg_resource_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_comment);
    }

    public void onBackClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UIUtils.addExitTranAnim(this);
    }

    public static void start(Activity activity, UserInfo userInfo, String resourceId) {
        Intent intent = new Intent(activity, DocumentCommentActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_RESOURCE_ID, resourceId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
