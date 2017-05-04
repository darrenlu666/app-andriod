package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerAdapter.OnItemClickListener;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.Builder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.ListExtractor;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.viewholders.MalfunctionVh;
import com.codyy.erpsportal.repairs.models.entities.Malfunction;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索常见问题
 */
public class SearchMalfunctionsActivity extends AppCompatActivity implements ListExtractor<Malfunction, MalfunctionVh> {

    private final static String TAG = "SearchMalfunctionsActivity";

    @Bind(R.id.btn_cancel)
    Button mCancelBtn;

    @Bind(R.id.et_search)
    EditText mSearchEt;

    @Bind(R.id.ib_clear_keywords)
    ImageButton mClearKeywordsIb;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    @Bind(R.id.fl_list_container)
    FrameLayout mListContainerFl;

    private UserInfo mUserInfo;

    private RvLoader<Malfunction, MalfunctionVh, Void> mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_malfunctions);
        ButterKnife.bind(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color));
        mSearchEt.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH) {//点击键盘上的搜索
                    String keyword = mSearchEt.getText().toString();
                    if (!TextUtils.isEmpty(keyword)) {
                        mLoader.addParam("searchContent", keyword);
                        mLoader.loadData(true);
                    } else {
                        mLoader.clearData();
                    }
                    InputMethodManager imManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imManager.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mClearKeywordsIb.setVisibility(View.VISIBLE);
                } else {
                    mClearKeywordsIb.setVisibility(View.INVISIBLE);
                }
            }
        });
        Builder<Malfunction, MalfunctionVh, Void> controllerBuilder = new Builder<>();
        mLoader = controllerBuilder
                .setActivity(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .setOnItemClickListener(new OnItemClickListener<Malfunction>() {
                    @Override
                    public void onItemClick(int position, Malfunction item) {
                        MalfunctionDetailsActivity.start(SearchMalfunctionsActivity.this, mUserInfo, item.getMalGuideId());
                    }
                })
                .build();
        mLoader.showDivider();
        mLoader.addParam("uuid", mUserInfo.getUuid());
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelClick() {
        finish();
    }

    @OnClick(R.id.ib_clear_keywords)
    public void onClearKeywordsClick() {
        mSearchEt.getText().clear();
    }

    @Override
    public String getUrl() {
        return URLConfig.SEARCH_MALFUNC;
    }

    @Override
    public List<Malfunction> extractList(JSONObject response) {
        Type type = new TypeToken<List<Malfunction>>(){}.getType();
        return new Gson().fromJson(response.optJSONArray("data").toString(), type);
    }

    @Override
    public ViewHolderCreator<MalfunctionVh> newViewHolderCreator() {
        return new EasyVhrCreator<>(MalfunctionVh.class);
    }

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, SearchMalfunctionsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity)context);
        }
    }
}
