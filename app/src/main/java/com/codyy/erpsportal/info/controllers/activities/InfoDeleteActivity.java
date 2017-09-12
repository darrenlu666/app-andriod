package com.codyy.erpsportal.info.controllers.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;
import com.codyy.erpsportal.commons.widgets.TitleBar;
import com.codyy.erpsportal.info.controllers.fragments.InfoEditRvListFragment;
import com.codyy.erpsportal.info.controllers.fragments.InfoEditRvListFragment.AllSelectedListener;
import com.codyy.erpsportal.info.utils.Info;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 应用资讯
 * Created by gujiajia on 2017/3/1.
 */

public class InfoDeleteActivity extends AppCompatActivity{

    private final static String TAG = "InfoDeleteActivity";

    /**
     * 资讯||通知公告，传入1、资讯2、通知公告
     */
    public final static String EXTRA_INFO = "EXTRA_INFO";

    /**
     * 1、资讯2、通知公告
     */
    private int mScope;

    @Bind(R.id.tb_info_title)
    protected TitleBar mTitleBar;

    @Bind(R.id.view_pager)
    protected ViewPager mViewPager;

    @Bind(R.id.sliding_tabs)
    protected SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.btn_edit)
    protected Button mEditBtn;

    @Bind(R.id.bar_delete)
    RelativeLayout mDeleteBarRl;

    @Bind(R.id.cb_select_all)
    CheckBox mSelectAllCb;

    @Bind(R.id.btn_delete)
    Button mDeleteBtn;

    private ChannelAdapter mAdapter;

    /**
     * 是否正在多选状态
     */
    private boolean mEditing;

    /**
     * 当前位置
     */
    private int mCurrentPos;

    private RequestSender mSender;

    private UserInfo mUserInfo;

    private LoadingDialog mLoadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_delete);
        ButterKnife.bind(this);

        initAttributes();
        mAdapter = new ChannelAdapter(this, getSupportFragmentManager(), mViewPager);
        if (mScope == 0) {
            mTitleBar.setTitle(Titles.sWorkspaceInfo);
            addTab(Titles.sWorkspaceInfoNew, Info.TYPE_NEWS);
            mSlidingTabLayout.setVisibility(View.GONE);
        } else {
            mTitleBar.setTitle(Titles.sWorkspaceNoticeAnnouncement);
            addTab(Titles.sWorkspaceNoticeAnnouncementNotice, Info.TYPE_NOTICE);
            addTab(Titles.sWorkspaceNoticeAnnouncementAnnouncement, Info.TYPE_ANNOUNCEMENT);
            mSlidingTabLayout.setVisibility(View.VISIBLE);
        }

        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                setCurrTabSelecting(false);
                mDeleteBarRl.setVisibility(View.GONE);
                mEditBtn.setText(R.string.edit);
                mCurrentPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        mLoadingDialog = LoadingDialog.newInstance(R.string.deleting);
    }

    private void initAttributes() {
        mSender = new RequestSender(this);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mScope = getIntent().getIntExtra(EXTRA_INFO, 0);
    }

    private void addTab(String title, String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(InfoEditRvListFragment.ARG_USER_INFO, mUserInfo);
        bundle.putString(InfoEditRvListFragment.ARG_TYPE, type);
        mAdapter.addTab(title, InfoEditRvListFragment.class, bundle);
    }

    @OnClick(R.id.btn_edit)
    public void onEditBtnClick() {
        if (mEditing) {
            quitEditing();
        } else {
            enterEditing();
        }
    }

    /**
     * 进入多选编辑
     */
    private void enterEditing() {
        mEditing = true;
        mEditBtn.setText(R.string.cancel);
        mDeleteBarRl.setVisibility(View.VISIBLE);
        setCurrTabSelecting(true);
    }

    /**
     * 退出多选编辑
     */
    private void quitEditing() {
        mEditing = false;
        mEditBtn.setText(R.string.edit);
        mDeleteBarRl.setVisibility(View.GONE);
        setCurrTabSelecting(false);
    }

    @Override
    public void onBackPressed() {
        if (mEditing) {
            quitEditing();
        } else {
            super.onBackPressed();
        }
    }

    private void setCurrTabSelecting(boolean selecting) {
        InfoEditRvListFragment fragment = (InfoEditRvListFragment) mAdapter.getFragmentAt(mCurrentPos);
        fragment.setSelecting(selecting);
        if (selecting) {
            fragment.setAllSelectedListener(mAllSelectedListener);
        } else {
            fragment.setAllSelectedListener(null);
        }
    }

    private AllSelectedListener mAllSelectedListener = new AllSelectedListener() {
        @Override
        public void onSelectAll() {
            mSelectAllCb.setChecked(true);
        }

        @Override
        public void onCancelSelectAll() {
            mSelectAllCb.setChecked(false);
        }

    };

    @OnClick(R.id.btn_delete)
    public void onDeleteClick() {
        final InfoEditRvListFragment fragment = (InfoEditRvListFragment) mAdapter.getFragmentAt(mCurrentPos);
        String idsStr = fragment.obtainSelectedIdsStr();
        if (idsStr == null) return;
        Map<String, String> params = new HashMap<>();
        if (mUserInfo == null) return;
        params.put("uuid", mUserInfo.getUuid());
        params.put("ids", idsStr);
        Cog.d(TAG, "onDeleteClick idsStr=" + idsStr);
        mLoadingDialog.show(getSupportFragmentManager(), "loading");
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.INFO_DELETE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "onDeleteClick response=", response);
                        mLoadingDialog.dismiss();
                        if ("success".equals(response.optString("result"))) {
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(InfoDeleteActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            quitEditing();
                            fragment.loadData(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                mLoadingDialog.dismiss();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(InfoDeleteActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
                quitEditing();
            }
        }));
    }

    @OnClick(R.id.cb_select_all)
    public void onSelectAllClick() {
        InfoEditRvListFragment fragment = (InfoEditRvListFragment) mAdapter.getFragmentAt(mCurrentPos);
        if (mSelectAllCb.isChecked()) {
            fragment.selectAll(true);
        } else {
            fragment.selectAll(false);
        }
    }
}
