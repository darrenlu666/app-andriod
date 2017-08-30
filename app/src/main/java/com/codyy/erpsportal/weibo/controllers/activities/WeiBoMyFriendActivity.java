package com.codyy.erpsportal.weibo.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.WeiBoAtDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.InputUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;
import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoMyFriendAdapter;
import com.codyy.erpsportal.weibo.models.entities.WeiBoGroup;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class WeiBoMyFriendActivity extends ToolbarActivity implements WeiBoMyFriendAdapter.OnItemClick, RefreshRecycleView.OnStateChangeLstener {
    public final static String RESULT_DATA = "result_data";
    public final static String RESULT_DATA_GROUP = "result_data_group";
    public final static String TYPE_STYLE = "type";
    /**
     * 我的好友
     */
    public final static int TYPE_MY_FRIEND = 0x001;
    /**
     * 我的圈组
     */
    public final static int TYPE_MY_GROUP = 0x002;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.weibo_myfriden_recycler)
    RefreshRecycleView mRefreshRecycleView;
    @Bind(R.id.weibo_search_edittext)
    EditText mEditText;
    @Bind(R.id.weibo_myfriend_find)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    private String mKeyCache;
    private int mStart;
    private final int mCont = 9;
    private int mEnd;
    private WeiBoMyFriendAdapter mMyFriendAdapter;
    private List<WeiBoSearchPeople> mDatas;
    private List<WeiBoSearchPeople> mMyFrends;
    private LinearLayoutManager mLinearLayoutManager;
    private WeiBoAtDao mWeiBoAtDao;
    /**
     * 好友数
     */
    private int mSize;
    private String mUrl;
    private int mType;
    public ArrayList<WeiBoGroup> mWeiBoGroups;

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mMyFriendAdapter.clearDatas();
                if (TextUtils.isEmpty(mEditText.getText())) {
                    ArrayList<WeiBoSearchPeople> searchPeoples = mWeiBoAtDao.getAtHistory(mUserInfo.getBaseUserId());
                    WeiBoSearchPeople entity = new WeiBoSearchPeople();
                    entity.setmHolderType(WeiBoMyFriendAdapter.TYPE_DIVIDE_MYFRIEND);
                    searchPeoples.add(entity);
                    mMyFriendAdapter.addListDatas(searchPeoples);
                    mKeyCache = "";
                } else {
                    mKeyCache = mEditText.getText().toString();
                }
                InputUtils.hideSoftInputFromWindow(WeiBoMyFriendActivity.this, mEditText);
                mStart = 0;
                mEnd = mStart + mCont;
                loadData(true);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mType == TYPE_MY_FRIEND) {
            mUrl = URLConfig.GET_MY_FRIENDLIST2;
            mKeyCache = "";
            if (WeiBoNewActivity.mMyFriends != null && WeiBoNewActivity.mMyFriends.size() > 0) {
                mMyFriendAdapter.addListDatas(WeiBoNewActivity.mMyFriends);
            } else {
                loadData(true);
            }
        } else {
            mUrl = URLConfig.WEIBO_FIND_GROUP;
            loadData(true);
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_wei_bo_my_friend;
    }

    @Override
    protected void initToolbar() {
        initToolbar(mToolbar);
        mType = getIntent().getIntExtra(TYPE_STYLE, TYPE_MY_FRIEND);
        mWeiBoAtDao = new WeiBoAtDao(this);
        mToolbar.setFocusable(true);
        mToolbar.setFocusableInTouchMode(true);
        mEditText.clearFocus();
        mEditText.setFocusable(false);
        mEditText.setFocusableInTouchMode(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mRefreshRecycleView.setColorSchemeColors(getResources().getColor(R.color.refresh_scheme_main), getResources().getColor(R.color.refresh_scheme_pink), getResources().getColor(R.color.refresh_scheme_blue));
        mDatas = new ArrayList<>();
        mMyFrends = new ArrayList<>();
        mEditText.setOnEditorActionListener(mOnEditorActionListener);
        mMyFriendAdapter = new WeiBoMyFriendAdapter(this, mDatas);
        mMyFriendAdapter.setOnItemClick(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRefreshRecycleView.setLayoutManager(mLinearLayoutManager);
        mRefreshRecycleView.setAdapter(mMyFriendAdapter);
        mStart = 0;
        mEnd = mStart + mCont;
        mWeiBoGroups = new ArrayList<>();
        if (mType == TYPE_MY_FRIEND) {
            mTextView.setText("我的好友");
        } else {
            mTextView.setText("我的圈组");
            mRelativeLayout.setVisibility(View.GONE);
        }
        mRequestSender = new RequestSender(this);
        mRefreshRecycleView.setOnStateChangeLstener(this);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    mRefreshRecycleView.setEnabled(true);
                } else {
                    mRefreshRecycleView.setEnabled(false);
                }
            }
        });
    }

    private void loadData(final boolean isRefresh) {
        Map<String, String> param = new Hashtable<>();
        if (mUserInfo != null) {
            param.put("uuid", mUserInfo.getUuid());
        }
        if (mType == TYPE_MY_FRIEND) {
            param.put("key", mKeyCache);
            param.put("start", String.valueOf(mStart));
            param.put("end", String.valueOf(mEnd));
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(mUrl, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                mRefreshRecycleView.setRefreshing(false);
                if (mType == TYPE_MY_FRIEND) {
                    if ("success".equals(response.optString("result"))) {
                        if (isRefresh) {
                            addAtHistory();
                        }
                        List<WeiBoSearchPeople> weiBoSearchPeoples = WeiBoSearchPeople.getWeiBoSearchPeople(response, WeiBoMyFriendAdapter.TYPE_MY_FRIEND);
                        if ((weiBoSearchPeoples == null || weiBoSearchPeoples.size() == 0) && !TextUtils.isEmpty(mKeyCache)) {
                            Snackbar.make(mRefreshRecycleView, "未搜索到数据！", Snackbar.LENGTH_SHORT).show();
                        } else {
                            mSize += weiBoSearchPeoples.size();
                            mMyFriendAdapter.addListDatas(weiBoSearchPeoples);
                        }
                    }
                    if (mSize >= mEnd) {
                        mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_UP_LOADEMORE);
                    } else {
                        mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                    }
                } else if (mType == TYPE_MY_GROUP) {
                    if ("success".equals(response.optString("result"))) {
                        if (isRefresh) {
                            mMyFriendAdapter.clearDatas();
                        }
                        List<WeiBoSearchPeople> weiBoGroups = WeiBoGroup.getList(response);
                        ArrayList<WeiBoGroup> weiBoGroups1 = getIntent().getParcelableArrayListExtra(RESULT_DATA_GROUP);
                        if (weiBoGroups1.size() > 0) {
                            mWeiBoGroups.clear();
                            for (WeiBoGroup weiBoGroup : weiBoGroups1) {
                                for (WeiBoSearchPeople weiBoGroup1 : weiBoGroups) {
                                    WeiBoGroup weiBoGroup2 = (WeiBoGroup) weiBoGroup1;
                                    if (weiBoGroup.getGroupId().equals(weiBoGroup2.getGroupId())) {
                                        weiBoGroup2.setIscheck(true);
                                        mWeiBoGroups.add(weiBoGroup2);
                                        break;
                                    }
                                }
                            }
                        }
                        mMyFriendAdapter.addListDatas(weiBoGroups);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (isFinishing()) {
                    return;
                }
                mRefreshRecycleView.setRefreshing(false);
                if (mType == TYPE_MY_GROUP) {
                    mStart = mSize;
                    mEnd = mStart + mCont;
                }
                Snackbar.make(mEditText, "加载失败！", Snackbar.LENGTH_SHORT).show();
                mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_LOADE_ERROR);
            }
        }));
    }

    private boolean removeFocus() {
        if (mEditText.hasFocus()) {
            InputUtils.hideSoftInputFromWindow(this, mEditText);
            mEditText.clearFocus();
            //取消焦点
            mEditText.setFocusable(false);
            //设置点击时获取焦点
            mEditText.setFocusableInTouchMode(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mType == TYPE_MY_GROUP) {
            getMenuInflater().inflate(R.menu.menu_weibo_new, menu);
            TextView textView = (TextView) menu.findItem(R.id.weibo_new_send).getActionView();
            textView.setText("确定");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(RESULT_DATA_GROUP, mWeiBoGroups);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (removeFocus()) {
            mEditText.setText(mKeyCache);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop();
        super.onDestroy();
    }

    @Override
    public void OnItemClick(WeiBoSearchPeople entity, int position) {
        mWeiBoAtDao.insertAtData(mUserInfo.getBaseUserId(), entity);
        Intent intent = new Intent();
        intent.putExtra(RESULT_DATA, entity);
        setResult(RESULT_OK, intent);
        mDatas.clear();
        finish();
    }

    @Override
    public void OnGroupCheck(WeiBoGroup weiBoGroup) {
        if (weiBoGroup.ischeck()) {
            mWeiBoGroups.add(weiBoGroup);
        } else {
            mWeiBoGroups.remove(weiBoGroup);
        }

    }

    private void addAtHistory() {
        mMyFriendAdapter.clearDatas();
        ArrayList<WeiBoSearchPeople> searchPeoples = mWeiBoAtDao.getAtHistory(mUserInfo.getBaseUserId());
        WeiBoSearchPeople entity = new WeiBoSearchPeople();
        entity.setmHolderType(WeiBoMyFriendAdapter.TYPE_DIVIDE_MYFRIEND);
        searchPeoples.add(entity);
        mMyFriendAdapter.addListDatas(searchPeoples);
    }

    @Override
    public void onRefresh() {
        if (mType == TYPE_MY_FRIEND) {
            mStart = 0;
            mEnd = mStart + mCont;
            loadData(true);
        } else {
            mStart = 0;
            mEnd = mStart + mCont;
            loadData(true);
        }
    }

    @Override
    public boolean onBottom() {
        if (mType == TYPE_MY_FRIEND) {
            if (mSize >= mEnd) {
                mStart = mSize;
                mEnd = mSize + mCont;
                loadData(false);
                return true;
            } else {
                mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
            }
        } else {
            mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        UIUtils.addExitTranAnim(this);
    }
}
