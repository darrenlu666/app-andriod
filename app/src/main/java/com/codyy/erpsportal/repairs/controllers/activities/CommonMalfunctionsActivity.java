package com.codyy.erpsportal.repairs.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.RequestSender.RequestData;
import com.codyy.erpsportal.commons.models.network.Response.ErrorListener;
import com.codyy.erpsportal.commons.models.network.Response.Listener;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.models.entities.MalfunctionCatalog;
import com.codyy.url.URLConfig;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 常见问题界面
 */
public class CommonMalfunctionsActivity extends AppCompatActivity {

    private final static String TAG = "CommonMalfunctionsActivity";

    /**
     * 最新问题栏
     */
    @Bind(R.id.ib_latest_malfunction)
    ImageView mLatestMalfunctionIb;

    /**
     * 最热问题栏
     */
    @Bind(R.id.ib_hot_malfunction)
    ImageView mHotMalfunctionIb;

    @Bind(R.id.fbl_malfunction_categories)
    FlexboxLayout mMalfunctionCategoriesFbl;

    /**
     * 登录用户信息
     */
    private UserInfo mUserInfo;

    private RequestSender mSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_malfunctions);
        ButterKnife.bind(this);
        initAttributes();
        loadData();
    }

    /**
     * 初始化属性
     */
    private void initAttributes() {
        mSender = new RequestSender(this);
        mUserInfo = getIntent().getParcelableExtra(Extra.USER_INFO);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        mSender.sendRequest(new RequestData(URLConfig.GET_MAL_GUIDE_CATALOGS, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Cog.d(TAG, "loadData response = ", response);
                        if (response.optBoolean("result")) {
                            Type type = new TypeToken<List<MalfunctionCatalog>>(){}.getType();
                            List<MalfunctionCatalog> malfunctionCatalogs = new Gson()
                                    .fromJson(response.optString("data"), type);
                            Context context = CommonMalfunctionsActivity.this;
                            for (int i = 0; i < malfunctionCatalogs.size(); i++) {
                                //添加父类别
                                MalfunctionCatalog catalog = malfunctionCatalogs.get(i);
                                TextView highLevelCatalogTv = new TextView(context);
                                highLevelCatalogTv.setTextColor(0xff444444);
                                highLevelCatalogTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                                highLevelCatalogTv.setText(catalog.getCatalogName());
                                mMalfunctionCategoriesFbl.addView(highLevelCatalogTv,
                                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                //添加“全部”子类别
                                addAllCatalogItemComponent(catalog);
                                //添加子类别
                                List<MalfunctionCatalog> childCatalogs = catalog.getChildCatalog();
                                if (childCatalogs != null) {
                                    for (MalfunctionCatalog childCatalog : childCatalogs) {
                                        addCatalogItemComponent(catalog, childCatalog);
                                    }
                                }
                                //添加分隔线
                                if (i != malfunctionCatalogs.size() - 1) {//结尾无需分隔线
                                    View divider = new View(context);
                                    divider.setBackgroundResource(R.drawable.divider_horizontal_line);
                                    mMalfunctionCategoriesFbl.addView(divider,
                                            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                }
                            }
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {
                        Cog.d(TAG, "loadData error = ", error.getMessage());
                    }
                }
        ));
    }

    /**
     * 添加子类别项
     * @param parentCatalog 父级类别
     * @param childCatalog 子级类别
     */
    private void addCatalogItemComponent(final MalfunctionCatalog parentCatalog, final MalfunctionCatalog childCatalog) {
        TextView childCatalogTv = createChildCatalogItemTv();
        childCatalogTv.setText(childCatalog.getCatalogName());
        childCatalogTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MalfunctionListActivity.start(v.getContext(), mUserInfo,
                        parentCatalog.getCatalogName() + "-" + childCatalog.getCatalogName(),
                        parentCatalog.getMalGuideCatalogId(),
                        childCatalog.getMalGuideCatalogId());
            }
        });
        mMalfunctionCategoriesFbl.addView(childCatalogTv,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * 添加“全部”子类别
     * @param catalog
     */
    private void addAllCatalogItemComponent(final MalfunctionCatalog catalog) {
        TextView allChildCatalogTv = createChildCatalogItemTv();
        allChildCatalogTv.setText(R.string.all);
        allChildCatalogTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MalfunctionListActivity.start(v.getContext(), mUserInfo,
                        catalog.getCatalogName(),
                        catalog.getMalGuideCatalogId(), null);
            }
        });
        mMalfunctionCategoriesFbl.addView(allChildCatalogTv,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * 创建子类别TextView
     * @return 子类别TextView
     */
    @NonNull
    private TextView createChildCatalogItemTv() {
        TextView allChildCatalogTv = new TextView(this);
        allChildCatalogTv.setTextColor(0xff999999);
        allChildCatalogTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        return allChildCatalogTv;
    }

    @OnClick(R.id.btn_return)
    public void onReturnBtnClick() {
        finish();
        UIUtils.addExitTranAnim(this);
    }

    @OnClick(R.id.btn_search)
    public void onSearchBtn() {
        SearchMalfunctionsActivity.start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSender.stop();
    }

    public static void start(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, CommonMalfunctionsActivity.class);
        intent.putExtra(Extra.USER_INFO, userInfo);
        context.startActivity(intent);
        if (context instanceof Activity) {
            UIUtils.addEnterAnim((Activity) context);
        }
    }
}
