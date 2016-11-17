package com.codyy.erpsportal.info.controllers.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.info.controllers.fragments.InfoEditListFragment;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 应用-资讯
 */
public class InfoActivity extends AppCompatActivity implements InfoEditListFragment.OnDeleteCompleteListener {

    @Bind(R.id.tb_info_title)
    protected TitleBar mTitleBar;

    @Bind(R.id.view_pager)
    protected ViewPager mViewPager;

    @Bind(R.id.sliding_tabs)
    protected SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.btn_edit)
    protected Button mEditBtn;

    private ChannelAdapter mAdapter;

    private int mCurrentPos;

    @OnClick(R.id.btn_edit)
    public void onEditClick() {
        int currentIndex = mViewPager.getCurrentItem();
        InfoEditListFragment infoEditListFragment = (InfoEditListFragment)mAdapter.getFragmentAt(currentIndex);
        infoEditListFragment.toggleSelectMode();
        updateEditBtn(infoEditListFragment.isSelectMode());
    }

    private void updateEditBtnByCurrentPage() {
        int currentIndex = mViewPager.getCurrentItem();
        InfoEditListFragment infoEditListFragment = (InfoEditListFragment)mAdapter.getFragmentAt(currentIndex);
        updateEditBtn(infoEditListFragment.isSelectMode());
    }

    public void updateEditBtn(boolean isSelectMode){
        if (isSelectMode){
            mEditBtn.setText(R.string.cancel);
        }else {
            mEditBtn.setText(R.string.edit);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        mTitleBar.setTitle(Titles.sWorkspaceInfo);
        mAdapter = new ChannelAdapter(this, getSupportFragmentManager(), mViewPager);
        addTab(Titles.sWorkspaceInfoNew, Info.TYPE_NEWS);
        addTab(Titles.sWorkspaceNoticeAnnouncementNotice, Info.TYPE_NOTICE);
        addTab(Titles.sWorkspaceNoticeAnnouncementAnnouncement, Info.TYPE_ANNOUNCEMENT);

        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                InfoEditListFragment infoEditListFragment = (InfoEditListFragment) mAdapter.getFragmentAt(mCurrentPos);
                infoEditListFragment.exitSelectMode();
                updateEditBtn(false);
                mCurrentPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void addTab(String title, String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(InfoEditListFragment.ARG_USER_INFO, UserInfoKeeper.getInstance().getUserInfo());
        bundle.putString(InfoEditListFragment.ARG_TYPE, type);
        mAdapter.addTab(title, InfoEditListFragment.class, bundle);
    }

    @Override
    public void onDeleteComplete() {
        updateEditBtnByCurrentPage();
    }
}
