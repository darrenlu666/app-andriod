package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codyy.bennu.framework.BNAVFramework;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.CacheResourceAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.CacheResourceAdapter.OnAllCheckedListener;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;
import com.codyy.erpsportal.commons.services.FileDownloadService;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.TransferManager;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.SwipeMenuListView.SwipeMenuListView;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.entities.CacheItem;
import com.codyy.erpsportal.resource.controllers.activities.AudioDetailsActivity;
import com.codyy.erpsportal.resource.controllers.activities.PicturesActivity;
import com.codyy.erpsportal.resource.models.entities.Audio;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 缓存界面
 * Created by kmdai on 2015/4/10.
 * modified by poe on 2015/6/10.
 */
public class CacheResourceActivity extends BaseHttpActivity implements Handler.Callback{
    private static String TAG = "CacheResourceActivity";
    private static final int DELAY_REFRESH_TIME = 500;
    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.cache_listview) SwipeMenuListView mListView;
    @Bind(R.id.cache_delete_layout) RelativeLayout mRelativeLayout;
    @Bind(R.id.cache_check_all) CheckBox mCheckBoxSelectAll;
    @Bind(R.id.tv_empty) TextView mEmptyTv;
    private TextView mEditTextView;//编辑按钮

    private CacheResourceAdapter mAdapter;
    private List<CacheItem> mDatas;
    private CacheDao mCacheDao;
    private DialogUtil dialogUtil;//二次确认框
    private Handler mHandler = new Handler(this);

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_cache_resource_layout;
    }

    @Override
    public String obtainAPI() {
        return null;
    }

    @Override
    public HashMap<String, String> getParam() {
        return null;
    }

    public void init() {
        mTitleTextView.setText(getString(R.string.cache_my));
        initToolbar(mToolBar);
        mListView.setEmptyView(mEmptyTv);
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        dialogUtil = new DialogUtil(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel
                dialogUtil.cancle();
            }
        },new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //sure
                if(null != mAdapter){
                    removeSelectedItems();
                    if(mAdapter.getCount() ==0){
                        mEmptyTv.setVisibility(View.VISIBLE);

                        //restore state
                        if(null != mEditTextView){
                            mEditTextView.setText("编辑");
                            mEditTextView.setVisibility(View.GONE);
                        }
                        mAdapter.setEdit(false);
                        mRelativeLayout.setVisibility(View.GONE);
                    }
                }
                dialogUtil.cancle();
            }
        });

        // On item click to pause the task
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CacheItem cacheItem = (CacheItem) mListView.getAdapter().getItem(position);
                if (FileDownloadService.hasCached(mUserInfo.getBaseUserId(), cacheItem.getId() + cacheItem.getSuffix())) {
                    switch (cacheItem.getType()) {
                        case CacheItem.DOWNLOAD_TYPE_AUDIO:
                            openAudios(cacheItem.getId());
                            break;
                        case CacheItem.DOWNLOAD_TYPE_IMAGE:
                            String imageFileName = cacheItem.getId() + cacheItem.getSuffix();
                            String filePath = FileDownloadService.getCachedFile(mUserInfo.getBaseUserId(), imageFileName);
                            Cog.d(TAG, "imagePath=", filePath);
                            List<String> filePaths = new ArrayList<>();
                            filePaths.add("file://" + filePath);
                            PicturesActivity.start(CacheResourceActivity.this, filePaths, 0);
                            break;
                        default:
                            CacheVideoPlayActivity.start(CacheResourceActivity.this, cacheItem);
                            break;
                    }
                } else {
                    cacheItem.setState( cacheItem.isDownloading()? CacheItem.STATE_PAUSE: CacheItem.STATE_DOWNLOADING);
                    //update the db state
                    mCacheDao.updateState(cacheItem);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        //set scroll listener
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(null != mAdapter){
                    if(scrollState == SCROLL_STATE_IDLE){
                        Cog.e(TAG, "SCROLL_STATE_IDLE");
                        mAdapter.setScrolling(false);
                    }else if(scrollState == SCROLL_STATE_TOUCH_SCROLL){
                        mAdapter.setScrolling(true);
                        Cog.e(TAG,"SCROLL_STATE_TOUCH_SCROLL");
                    }else if(scrollState == SCROLL_STATE_FLING){
                        Cog.e(TAG,"SCROLL_STATE_FLING");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        tryToLoadData();
        setFilterListener(new IFilterListener() {
            @Override
            public void onFilterClick(MenuItem item) {}

            @Override
            public void onPreFilterCreate(Menu menu) {
                menu.getItem(0).setActionView(R.layout.textview_filter_confirm_button);
                mEditTextView = (TextView) menu.getItem(0).getActionView().findViewById(R.id.tv_title);
                mEditTextView.setText("编辑");
                mEditTextView.setOnClickListener(mOptionClickListener);
                if(mDatas == null || mDatas.size() == 0){
                    mEditTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void removeSelectedItems() {
        final List<String> resIds = new ArrayList<>();
        List<CacheItem> remainedCacheItems = new ArrayList<>();
        List<CacheItem> caches = mAdapter.getCaches();
        for (CacheItem rd : caches) {
            if (rd.isCheck()) {
                resIds.add(rd.getId());
            } else {
                remainedCacheItems.add(rd);
            }
        }

        //删除不必要的下载
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransferManager.instance().deleteDownloads(getApplicationContext(), mUserInfo.getBaseUserId(), resIds);
            }
        }).start();
        mAdapter.setCaches(remainedCacheItems);
    }
    
    private void openAudios(String cacheId) {
        CacheDao cacheDao = new CacheDao(this);
        List<Audio> audioList = cacheDao.findAudioCaches(mUserInfo.getBaseUserId());
        int index = 0;
        for (int i = 0; i<audioList.size(); i++) {
            if (audioList.get(i).getId().equals(cacheId)){
                index = i;
                break;
            }
        }
        AudioDetailsActivity.startFromCache(this, mUserInfo,audioList, index);
    }

    private View.OnClickListener mOptionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mRelativeLayout.getVisibility() == View.GONE) {
                mRelativeLayout.setVisibility(View.VISIBLE);
                mRelativeLayout.startAnimation(AnimationUtils.loadAnimation(CacheResourceActivity.this, R.anim.cache_delete_show));
                for (CacheItem cacheItem : mDatas) {
                    cacheItem.setCheck(false);
                }
                if(null != mCheckBoxSelectAll){
                    mCheckBoxSelectAll.setChecked(false);
                }
                if(null !=mAdapter ){
                    mAdapter.setEdit(true);
                }
//                mListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(CacheResourceActivity.this, R.anim.list_item_show));
            } else {
                mRelativeLayout.setVisibility(View.GONE);
                mAdapter.setEdit(false);
            }
            //set state text .
            if (mRelativeLayout.getVisibility() == View.GONE) {
                mEditTextView.setText("编辑");
            } else {
                mEditTextView.setText("完成");
            }
        }
    };

    /**
     * 先判断播放组件是否加载好，未加载好再等一会儿，直到播放组件加载完成之后再加载数据
     */
    private void tryToLoadData() {
        if (BNAVFramework.isInited()) {
            LoadingDialog loadingDialog = (LoadingDialog) getSupportFragmentManager().findFragmentByTag("loading");
            if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
                loadingDialog.dismiss();
            }
            loadData();
        } else {
            LoadingDialog loadingDialog = (LoadingDialog) getSupportFragmentManager().findFragmentByTag("loading");
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog.newInstance(R.string.loading_video_framework);
            }
            if (!loadingDialog.isAdded()) {
                loadingDialog.show(getSupportFragmentManager(), "loading");
            }

            postDelayLoadData();
        }
    }

    private void postDelayLoadData() {
        mHandler.removeCallbacks(mLoadDataTask);
        mHandler.postDelayed(mLoadDataTask, DELAY_REFRESH_TIME);
    }

    private Runnable mLoadDataTask = new Runnable() {
        @Override
        public void run() {
            tryToLoadData();
        }
    };

    /**获取缓存数据**/
    private void loadData() {
        mCacheDao = new CacheDao(getApplication());
        mDatas = mCacheDao.getAllData(mUserInfo.getBaseUserId());
        if(mDatas != null && mDatas.size()>0){
            mAdapter = new CacheResourceAdapter(this, mDatas, new OnAllCheckedListener() {
                @Override
                public void onCheckAll(boolean selectAll) {
                    //如果当前状态有更新...
                    if(selectAll != mCheckBoxSelectAll.isChecked()){
                        mCheckBoxSelectAll.setChecked(selectAll);
                    }
                }
            });
            mListView.setAdapter(mAdapter);
            if(null != mEditTextView) mEditTextView.setVisibility(View.VISIBLE);
        }else{
            mEmptyTv.setVisibility(View.VISIBLE);
            if(null != mEditTextView) mEditTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(JSONObject response) { }

    @Override
    public void onFailure(VolleyError error) { }

    public void onBack(View view){
        this.finish();
    }

    @OnClick({R.id.cache_check_all,R.id.check_btn_delete})
    public void onLocalClick(View v) {
        switch (v.getId()) {
            case R.id.cache_check_all:
                if (mCheckBoxSelectAll.isChecked()) {
                    mAdapter.setCheckAll(true);
                } else {
                    mAdapter.setCheckAll(false);
                }
                break;
            case R.id.check_btn_delete://delete all
                //删除选中的ResId or 全部删除
                dialogUtil.showDialog("是否删除选中的文件？");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void start(Context from){
        Intent intent = new Intent(from, CacheResourceActivity.class);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(mAdapter != null){
            mAdapter.update();
        }
        return false;
    }
}
