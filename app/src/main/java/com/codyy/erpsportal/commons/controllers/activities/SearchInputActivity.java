package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.SearchAdapter;
import com.codyy.erpsportal.commons.models.ConfigBus;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.SearchDao;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;
import com.codyy.erpsportal.commons.models.entities.SearchBase;
import com.codyy.erpsportal.commons.models.entities.SearchGroup;
import com.codyy.erpsportal.commons.models.entities.SearchHistory;
import com.codyy.erpsportal.commons.models.entities.SearchResource;
import com.codyy.erpsportal.commons.models.entities.SearchVideo;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索activity
 */
public class SearchInputActivity extends Activity implements SearchAdapter.OnHistoryClickListener, ConfigBus.OnModuleConfigListener {

    private final static String EXTRA_SEARCH_FLAY = "EXTRA_SEARCH_FLAY";
    /**
     * 有资讯
     */
    private final static int HAS_INFO = 0x4;
    /**
     * 有资源和文档
     */
    private final static int HAS_DOC_VIDEO = 0x2;
    /**
     * 有圈组
     */
    private final static int HAS_GROUP = 0x1;
    private final static int INIT = 0x001;
    private final static int REFRESH = 0x02;
    @Bind(R.id.search_title)
    RelativeLayout mTitleRlt;
    @Bind(R.id.search_layout_recycleview)
    RecyclerView mRecyclerView;
    @Bind(R.id.search_text_category)
    TextView mCategoryText;
    @Bind(R.id.search_editext_search)
    EditText mSearchEdit;
    @Bind(R.id.search_layout_swipeRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.search_btn_search)
    ImageView mDeleteIV;
    private PopupWindow mPopupWindow;

    private View mCategoryView;

    private RadioGroup mRadioGroup;

    private RequestSender mSender;
    private UserInfo mUserInfo;

    private SearchDao mSearchDao;
    private SearchAdapter mSearchAdapter;
    private ArrayList<SearchBase> mSearch;
    private int start = 0;
    private int cont = 9;
    private int end = start + cont;

    private PopupWindow mSearchPopupWindow;

    private RecyclerView mHistoryRecyclerView;
    private int mSearchFlag = 0;
    private boolean isShowHistory;
    private String mBaseAreaId;
    private String mSchoolId;
    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchBtnClick();
                return true;
            }
            return false;
        }
    };
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchinput);
        ButterKnife.bind(this);
        mSender = new RequestSender(this);
        mSearchFlag = getIntent().getIntExtra(EXTRA_SEARCH_FLAY, 0x7);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        init();
        mSearchDao = new SearchDao(this);
        mSearchAdapter = new SearchAdapter(this, mSearch);
        mSearchAdapter.setListener(this);
        mRecyclerView.setAdapter(mSearchAdapter);
        mSearch.addAll(mSearchDao.getSearchHistory(mUserInfo.getBaseUserId()));
        mSearchAdapter.notifyDataSetChanged();
        isShowHistory = true;
        ConfigBus.register(this);
    }

    /**
     * 初始化view
     */
    private void init() {
        mSearch = new ArrayList<>();
        mSearchEdit.setOnEditorActionListener(mOnEditorActionListener);
        mCategoryView = getLayoutInflater().inflate(R.layout.search_dialog, null);
        mPopupWindow = new PopupWindow(mCategoryView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mRadioGroup = (RadioGroup) mCategoryView.findViewById(R.id.search_dialog_radiogroup);
        ((RadioButton) mCategoryView.findViewById(R.id.search_dialog_resource)).setText(Titles.sHomepageInfo);
        mCategoryText.setText(Titles.sHomepageInfo);
        ((RadioButton) mCategoryView.findViewById(R.id.search_dialog_group)).setText(Titles.sHomepageGroup);
        /**
         * 没有资讯
         */
        if ((mSearchFlag & HAS_INFO) != HAS_INFO) {
            mCategoryView.findViewById(R.id.search_dialog_resource).setVisibility(View.GONE);
            mCategoryText.setText(getString(R.string.search_action_video));
        }

        /**
         * 没有资源
         */
        if ((mSearchFlag & HAS_DOC_VIDEO) != HAS_DOC_VIDEO) {
            mCategoryView.findViewById(R.id.search_dialog_video).setVisibility(View.GONE);
            mCategoryView.findViewById(R.id.search_dialog_doc).setVisibility(View.GONE);
            if (mCategoryView.findViewById(R.id.search_dialog_resource).getVisibility() == View.GONE) {
                mCategoryText.setText(Titles.sHomepageGroup);
            }
        }
        /**
         * 没有圈组
         */
        if ((mSearchFlag & HAS_GROUP) != HAS_GROUP) {
            mCategoryView.findViewById(R.id.search_dialog_group).setVisibility(View.GONE);
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.search_dialog_resource:
                        mCategoryText.setText(Titles.sHomepageInfo);
                        break;
                    case R.id.search_dialog_video:
                        mCategoryText.setText(getString(R.string.search_action_video));
                        break;
                    case R.id.search_dialog_doc:
                        mCategoryText.setText(getString(R.string.search_action_doc));
                        break;
                    case R.id.search_dialog_group:
                        mCategoryText.setText(Titles.sHomepageGroup);
                        break;
                }
                mPopupWindow.dismiss();
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.main_color));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(mSearchEdit.getText())) {
                    start = 0;
                    end = start + cont;
                    getData(INIT);
                } else {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    ToastUtil.showToast(SearchInputActivity.this, "请输入搜索关键字！");
                }

            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isShowHistory) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && linearLayoutManager.findLastVisibleItemPosition() == (mSearch.size() - 1)) {
                        if (mSearch.size() < (end + 1)) {

                        } else {
                            start = end + 1;
                            end = start + cont;
                            getData(REFRESH);
                        }
                    }
                }
            }
        });
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isShowHistory) {
                    mSearch.clear();
                    mSearch.addAll(mSearchDao.getSearchHistory(mUserInfo.getBaseUserId()));
                    mSearchAdapter.notifyDataSetChanged();
                    isShowHistory = true;
                }
                if (s.length() > 0) {
                    mDeleteIV.setVisibility(View.VISIBLE);
                } else {
                    mDeleteIV.setVisibility(View.GONE);
                }
            }
        });
        mHistoryRecyclerView = new RecyclerView(this);
        mHistoryRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mSearchPopupWindow = new PopupWindow(mHistoryRecyclerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogUtil = new DialogUtil(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUtil.cancel();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchDao.deleteHistory(null, mUserInfo.getBaseUserId());
                mSearch.clear();
                mSearch.addAll(mSearchDao.getSearchHistory(mUserInfo.getBaseUserId()));
                mSearchAdapter.notifyDataSetChanged();
                dialogUtil.cancel();
            }
        });
    }

    @OnClick(R.id.search_title_back)
    void onBack() {
        this.finish();
    }

    /**
     * 搜索按钮点击
     */
    @OnClick(R.id.search_btn_search)
    void clearInput() {
        mSearchEdit.setText("");
    }

    void onSearchBtnClick() {
        if (!TextUtils.isEmpty(mSearchEdit.getText())) {
            if (getCurrentFocus() != null) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            start = 0;
            end = start + cont;
            getData(INIT);
            mSearchDao.writeHistory(mSearchEdit.getText().toString(), mCategoryText.getText().toString(), mUserInfo.getBaseUserId());
        } else {
            if (mRefreshLayout.isRefreshing()) {
                mRefreshLayout.setRefreshing(false);
            }
            ToastUtil.showToast(SearchInputActivity.this, "请输入搜索关键字！");
        }
    }

    @OnClick(R.id.search_text_category)
    void onCategoryClick() {
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(mTitleRlt);
        }
    }


    /**
     * 获取数据
     */
    private void getData(final int state) {
        if (mBaseAreaId != null) {
            final String s = mCategoryText.getText().toString();
            HashMap<String, String> data = new HashMap<>();
            String url = null;
            if (Titles.sHomepageInfo.equals(s)) {
                url = URLConfig.INFO_SEARCH_ACTION;
            } else if (getString(R.string.search_action_video).equals(s)) {
                url = URLConfig.VIDEO_SEARCH_ACTION;
            } else if (getString(R.string.search_action_doc).equals(s)) {
                url = URLConfig.DOC_SEARCH_ACTION;
            } else if (Titles.sHomepageGroup.equals(s)) {
                url = URLConfig.GROUP_SEARCH_ACTION;
            }
            data.put("baseAreaId", mBaseAreaId);
            data.put("schoolId", mSchoolId);
            data.put("start", String.valueOf(start));
            data.put("end", String.valueOf(end));
            data.put("key", mSearchEdit.getText().toString());
            mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    isShowHistory = false;
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    boolean flag = false;
                    int start;
                    if (INIT == state) {
                        flag = true;
                        start = 0;
                        mSearchAdapter.notifyItemRangeRemoved(0, mSearch.size());
                        mSearch.clear();
                    } else {
                        start = mSearch.size();
                    }
                    if (Titles.sHomepageInfo.equals(s)) {
                        SearchResource.getSearchResource(response, mSearch, flag);
                    } else if (getString(R.string.search_action_video).equals(s)) {
                        SearchVideo.getSearchVideo(response, mSearch, SearchBase.VIDEO_CONT, flag);
                    } else if (getString(R.string.search_action_doc).equals(s)) {
                        SearchVideo.getSearchVideo(response, mSearch, SearchBase.DOC_CONT, flag);
                    } else if (Titles.sHomepageGroup.equals(s)) {
                        SearchGroup.getSearchGroups(response, mSearch, flag);
                    }
                    int cont = mSearch.size() - start;
                    mSearchAdapter.notifyItemRangeChanged(start, cont);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(Throwable error) {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            }));
        }
    }

    @Override
    public void deleteOne(SearchHistory str, int position) {
        mSearchDao.deleteHistory(str.getCont(), mUserInfo.getBaseUserId());
        int p = mSearch.indexOf(str);
        mSearch.remove(str);
        if (mSearch.size() <= 2) {
            mSearch.get(0).setmTitle("没有搜索记录");
            mSearchAdapter.notifyItemChanged(0);
            mSearch.remove(mSearch.size() - 1);
            mSearchAdapter.notifyItemRangeRemoved(p, 2);
        } else {
            mSearchAdapter.notifyItemRemoved(p);
        }
    }

    @Override
    public void deleteAll() {
        dialogUtil.showDialog("确定要清空搜索记录吗?");
    }

    @Override
    public void onHistoryClick(SearchHistory history) {
        mCategoryText.setText(history.getType());
        mSearchEdit.setText(history.getCont());
        setState(history.getType());
        onSearchBtnClick();
    }

    /**
     * 设置选着状态
     *
     * @param str
     */
    private void setState(String str) {
        if (Titles.sHomepageInfo.equals(str)) {
            mRadioGroup.check(R.id.search_dialog_resource);
        } else if ("视频".equals(str)) {
            mRadioGroup.check(R.id.search_dialog_video);
        } else if ("文档".equals(str)) {
            mRadioGroup.check(R.id.search_dialog_doc);
        } else if (Titles.sHomepageGroup.equals(str)) {
            mRadioGroup.check(R.id.search_dialog_group);
        }
    }

    @Override
    protected void onDestroy() {
        mSender.stop();
        super.onDestroy();
    }

    @Override
    public void onConfigLoaded(ModuleConfig config) {
        if (config == null) {
            return;
        }
        mBaseAreaId = config.getBaseAreaId();
        mSchoolId = config.getSchoolId();
    }

    public static void start(Activity activity, int searchFlag) {
        Intent intent = new Intent(activity, SearchInputActivity.class);
        intent.putExtra(EXTRA_SEARCH_FLAY, searchFlag);
        activity.startActivity(intent);
    }
}
