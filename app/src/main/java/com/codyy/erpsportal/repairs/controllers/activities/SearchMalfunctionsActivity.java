package com.codyy.erpsportal.repairs.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.Builder;
import com.codyy.erpsportal.commons.controllers.fragments.RvLoader.ListExtractor;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
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

    private RvLoader<Malfunction, MalfunctionVh, Void> mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_malfunctions);
        ButterKnife.bind(this);
        mSearchEt.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH) {
                    mLoader.addParam("keywords", mSearchEt.getText().toString());
                    mLoader.loadData(true);
                    return true;
                }
                return false;
            }
        });
        Builder<Malfunction, MalfunctionVh, Void> controllerBuilder = new Builder<>();
        mLoader = controllerBuilder
                .setActivity(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .build();
        mLoader.showDivider();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SearchMalfunctionsActivity.class);
        context.startActivity(intent);
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
        return new Gson().fromJson(response.optJSONArray("list").toString(), type);
    }

    @Override
    public ViewHolderCreator<MalfunctionVh> newViewHolderCreator() {
        return new EasyVhrCreator<>(MalfunctionVh.class);
    }
}
