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

    private final static String EXTRA_TYPE = "com.codyy.erpsportal.EXTRA_TYPE";

    /**
     * 按类型获取常见问题列表
     */
    private final static int TYPE_CATALOG = 0;

    /**
     * 获取最热门问题
     */
    private final static int TYPE_HOT = 1;

    /**
     * 获取最新问题
     */
    private final static int TYPE_LATEST = 2;

    @Bind(R.id.title_bar)
    TitleBar mTitleBar;

    @Bind(R.id.rv_list)
    RecyclerView mListRv;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.tv_empty)
    TextView mEmptyTv;

    private UserInfo mUserInfo;

    private int mType;

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
        mType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_CATALOG);
        mTitle = getIntent().getStringExtra(Extra.TITLE);
        mCatalogId = getIntent().getStringExtra(EXTRA_CATALOG_ID);
        mChildCatalogId = getIntent().getStringExtra(EXTRA_CHILD_CATALOG_ID);
    }

    private void initComponents() {
        if (mType == TYPE_CATALOG) {
            mTitleBar.setText(mTitle);
        } else if (mType == TYPE_HOT) {
            mTitleBar.setText("热门问题");
        } else {
            mTitleBar.setText("最新问题");
        }
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
                        MalfunctionDetailsActivity.start(MalfunctionListActivity.this, mUserInfo, item.getMalGuideId());
                    }
                })
                .build();
        mLoader.showDivider();
    }

    private void loadData() {
        mLoader.addParam("uuid", mUserInfo.getUuid());
        if (mType == TYPE_CATALOG) {
            mLoader.addParam("guideCatalog1", mCatalogId);
            if (!TextUtils.isEmpty(mChildCatalogId)) {
                mLoader.addParam("guideCatalog2", mChildCatalogId);
            }
        }
        mLoader.loadData(true);
    }

    @Override
    public String getUrl() {
        if (mType == TYPE_CATALOG) {
            return URLConfig.GET_MALFUNCTIONS_BY_CATALOG;
        } else if (mType == TYPE_HOT) {
            return URLConfig.GET_HOT_MALFUNCTIONS;
        } else {
            return URLConfig.GET_LATEST_MALFUNCTIONS;
        }
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
        start(context, userInfo, TYPE_CATALOG, title, catalogId, childCatalogId);
    }

    /**
     * 进入最热门问题
     * @param context 上下文
     * @param userInfo 登录用户信息
     */
    public static void startHot(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_HOT, null, null, null);
    }

    /**
     * 进入最新问题
     * @param context 上下文
     * @param userInfo 登录用户信息
     */
    public static void startLatest(Context context, UserInfo userInfo) {
        start(context, userInfo, TYPE_LATEST, null, null, null);
    }

    /**
     * 按分类启动常见问题
     *
     * @param context        上下文
     * @param userInfo       用户信息
     * @param type           类型：按类型、最热、最新
     * @param title          标题
     * @param catalogId      父级类型id
     * @param childCatalogId 子集类型id
     */
    public static void start(Context context, UserInfo userInfo, int type, String title,
                             String catalogId, String childCatalogId) {
        Intent intent = new Intent(context, MalfunctionListActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        intent.putExtra(EXTRA_TYPE, type);
        if (type == TYPE_CATALOG) {
            intent.putExtra(Extra.TITLE, title);
            intent.putExtra(EXTRA_CATALOG_ID, catalogId);
            intent.putExtra(EXTRA_CHILD_CATALOG_ID, childCatalogId);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }
}
