package com.codyy.erpsportal.repairs.controllers.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.data.source.remote.WebApi;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.RxBus;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.adapters.CategoriesTabsAdapter;
import com.codyy.erpsportal.repairs.models.entities.MalfuncCategory;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gujiajia on 2017/3/28.
 */

public class SelectCategoriesDgFragment extends DialogFragment {

    private final static String TAG = "SelectCategoriesDgFragment";

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private CategoriesTabsAdapter mTabsAdapter;

    private WebApi mWebApi;
    private Disposable mDisposable;

    public SelectCategoriesDgFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebApi = RsGenerator.create(WebApi.class);
        setStyle(STYLE_NO_FRAME, R.style.BottomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dg_select_categories, container, false);
        ButterKnife.bind(this, view);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabsAdapter = new CategoriesTabsAdapter(getActivity(), getChildFragmentManager(), mViewPager);
        mViewPager.setAdapter(mTabsAdapter);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebApi.post4Json(URLConfig.GET_MALFUNC_CATEGORIES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if ("success".equals(jsonObject.optString("result"))) {
                            MalfuncCategory[] categories = new Gson()
                                    .fromJson(jsonObject.optJSONArray("list").toString(), MalfuncCategory[].class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArray("list", categories);
                            mTabsAdapter.addTab("请选择", MalfuncCategoriesFragment.class, bundle);
                            setNewTabMargin();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "throwable=", throwable.getMessage());
                    }
                });

        mDisposable = RxBus.getInstance().toObservable(MalfuncCategory.class)
                .subscribe(new Consumer<MalfuncCategory>() {
                    @Override
                    public void accept(MalfuncCategory malfuncCategory) throws Exception {
                        Cog.d(TAG, "receive onItemClick=", malfuncCategory);
                        int tabPosition = mTabLayout.getSelectedTabPosition();
                        Tab tab = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition());
                        if (tab != null) {
                            tab.setText(malfuncCategory.getName());
                            mTabsAdapter.setTitle(tabPosition, malfuncCategory.getName());
                        }
                        if (tabPosition < 2) {
                            tryToAddTab(malfuncCategory);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "toObservable throwable=", throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 为新加的tab增加margin
     */
    private void setNewTabMargin() {
        for(int i=0; i < mTabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(UIUtils.dip2px(getContext(), 16), 0, 0, 0);
            tab.requestLayout();
        }
    }

    private void tryToAddTab(MalfuncCategory malfuncCategory) {
        mWebApi.post4Json(URLConfig.GET_MALFUNC_CATEGORIES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if ("success".equals(jsonObject.optString("result"))) {
                            MalfuncCategory[] categories = new Gson()
                                    .fromJson(jsonObject.optJSONArray("list").toString(), MalfuncCategory[].class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArray("list", categories);
                            mTabsAdapter.remove(mViewPager.getCurrentItem() + 2);
                            MalfuncCategoriesFragment mcf = (MalfuncCategoriesFragment) mTabsAdapter
                                    .getFragmentAt(mViewPager.getCurrentItem() + 1);
                            if (mcf == null) {
                                mTabsAdapter.addTab("请选择", MalfuncCategoriesFragment.class, bundle);
                            } else {
                                mcf.setCategoryArr(categories);
                            }
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                            setNewTabMargin();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "tryToAddTab throwable=", throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }

    @OnClick(R.id.iv_close)
    public void onCloseClick() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
