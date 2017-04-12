package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

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
import com.codyy.erpsportal.commons.widgets.TitleBar;
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

/**
 * 常见问题列表
 */
public class MalfunctionListActivity extends AppCompatActivity implements ListExtractor<Malfunction, MalfunctionVh> {

    private final static String EXTRA_CATALOG_ID = "com.codyy.erpsportal.EXTRA_CATALOG_ID";

    private final static String EXTRA_CHILD_CATALOG_ID = "com.codyy.erpsportal.EXTRA_CHILD_CATALOG_ID";

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    private UserInfo mUserInfo;

    private String mTitle;

    private String mCatalogId;

    private String mChildCatalogId;

    private RvLoader<Malfunction, MalfunctionVh, Void> mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malfunction_list);
        ButterKnife.bind(this);
        initAttributes();
        initComponents();
        loadData();
    }

    private void initAttributes() {
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
        mTitle = getIntent().getStringExtra(Extra.TITLE);
        mCatalogId = getIntent().getStringExtra(EXTRA_CATALOG_ID);
        mChildCatalogId = getIntent().getStringExtra(EXTRA_CHILD_CATALOG_ID);
    }

    private void initComponents() {
        mTitleBar.setText(mTitle);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.main_color));
        Builder<Malfunction, MalfunctionVh, Void> controllerBuilder = new Builder<>();
        mLoader = controllerBuilder
                .setActivity(this)
                .setRefreshLayout(mRefreshLayout)
                .setRecyclerView(mListRv)
                .setEmptyView(mEmptyTv)
                .setOnItemClickListener(new OnItemClickListener<Malfunction>() {
                    @Override
                    public void onItemClick(int position, Malfunction item) {

                    }
                })
                .build();
        mLoader.showDivider();
    }

    private void loadData() {
        mLoader.addParam("guideCatalog1", mCatalogId);
        if (!TextUtils.isEmpty(mChildCatalogId)) {
            mLoader.addParam("guideCatalog2", mChildCatalogId);
        }
        mLoader.addParam("uuid", mUserInfo.getUuid());
        mLoader.loadData(true);
    }

    @Override
    public String getUrl() {
        return URLConfig.GET_MALFUNCTIONS_BY_CATALOG;
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

    public static void start(Context context, UserInfo userInfo, String title,
                             String catalogId, String childCatalogId) {
        Intent intent = new Intent(context, MalfunctionListActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(Extra.TITLE, title);
        intent.putExtra(EXTRA_CATALOG_ID, catalogId);
        intent.putExtra(EXTRA_CHILD_CATALOG_ID, childCatalogId);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }
}
