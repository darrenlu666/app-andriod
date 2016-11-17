package com.codyy.erpsportal.info.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.debug.hv.ViewServer;
import com.codyy.erpsportal.BuildConfig;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.info.controllers.fragments.InfoListFragment;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.erpsportal.commons.widgets.TitleBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 更多资讯
 * Created by gujiajia on 2015/9/1.
 */
public class MoreInformationActivity extends AppCompatActivity {

    public final static String EXTRA_AREA_ID = "areaId";

    public final static String EXTRA_SCHOOL_ID = "schoolId";

    public final static String EXTRA_TYPE = "type";

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_news);
        if (BuildConfig.DEBUG) ViewServer.get(this).addWindow(this);
        ButterKnife.bind(this);

        String areaId = getIntent().getStringExtra(EXTRA_AREA_ID);
        String schoolId = getIntent().getStringExtra(EXTRA_SCHOOL_ID);
        String type = getIntent().getStringExtra(EXTRA_TYPE);

        if (Info.TYPE_NEWS.equals(type)) {
            mTitleBar.setTitle(Titles.sPagetitleIndexInfoNew);
        } else if (Info.TYPE_NOTICE.equals(type)) {
            mTitleBar.setTitle(Titles.sPagetitleIndexInfoNotice);
        } else {
            mTitleBar.setTitle(Titles.sPagetitleIndexInfoAnnouncement);
        }

//        InformationListFragment fragment = InformationListFragment.newInstance(type, areaId, schoolId);
        InfoListFragment infoListFragment = InfoListFragment.newInstance(type, areaId, schoolId);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_content, infoListFragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) ViewServer.get(this).removeWindow(this);
    }

    public static void startMore(View view, String type) {
        if (!(view.getContext() instanceof MainActivity)) throw new IllegalArgumentException("The view must be in MainActivity");
        MainActivity mainActivity = (MainActivity) view.getContext();
        ModuleConfig moduleConfig = ConfigBus.getInstance().getModuleConfig();
        Intent it = new Intent(mainActivity, MoreInformationActivity.class);
        it.putExtra(EXTRA_TYPE, type);
        it.putExtra(EXTRA_SCHOOL_ID, moduleConfig.getSchoolId());
        it.putExtra(EXTRA_AREA_ID, moduleConfig.getBaseAreaId());
        mainActivity.startActivity(it);
    }
}
