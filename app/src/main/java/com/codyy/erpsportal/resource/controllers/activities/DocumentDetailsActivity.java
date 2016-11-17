package com.codyy.erpsportal.resource.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.resource.controllers.fragments.ResourceDetailsFragment;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;

/**
 * 资源详情界面
 * Created by gujiajia
 */
public class DocumentDetailsActivity extends FragmentActivity {

    private final static String ARG_DOC_DETAILS = "arg_doc_details";

    private ResourceDetailsFragment mResourceDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_details);
        mResourceDetailsFragment = (ResourceDetailsFragment) getSupportFragmentManager()
                .findFragmentByTag("tag_doc_details");
        if (getIntent() != null) {
            String classId = getIntent().getStringExtra(Extra.CLASS_ID);
            if (classId != null) mResourceDetailsFragment.setShowSharer(true);
            ResourceDetails resourceDetails = getIntent().getParcelableExtra(ARG_DOC_DETAILS);
            mResourceDetailsFragment.setResourceDetails(resourceDetails);
        }
    }

    public void onBackClick(View view) {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    public static void start(Activity activity, ResourceDetails resourceDetails) {
        start(activity, resourceDetails, null);
    }

    public static void start(Activity activity, ResourceDetails resourceDetails, String classId) {
        Intent intent = new Intent(activity, DocumentDetailsActivity.class);
        intent.putExtra(ARG_DOC_DETAILS, resourceDetails);
        if (classId != null) {
            intent.putExtra(Extra.CLASS_ID, classId);
        }
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
