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
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RsGenerator;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Extra;
import com.codyy.erpsportal.commons.utils.RxBus;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.repairs.controllers.adapters.CategoriesTabsAdapter;
import com.codyy.erpsportal.repairs.models.entities.CategoriesPageInfo;
import com.codyy.erpsportal.repairs.models.entities.MalfuncCategory;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 故障类别选择
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

    private CompositeDisposable mDisposables;

    private UserInfo mUserInfo;

    private OnCategorySelectedListener mOnCategorySelectedListener;

    /**
     * 默认数据，上次筛选分类已经选好的，打开了重新定位到这边
     */
    private List<CategoriesPageInfo> mInitPageInfoList;

    public static SelectCategoriesDgFragment newInstance(UserInfo userInfo) {
        SelectCategoriesDgFragment selectCategoriesDgFragment = new SelectCategoriesDgFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extra.USER_INFO, userInfo);
        selectCategoriesDgFragment.setArguments(bundle);
        return selectCategoriesDgFragment;
    }

    public SelectCategoriesDgFragment() {
        super();
        mDisposables = new CompositeDisposable();
    }

    public void setInitPageInfoList(List<CategoriesPageInfo> initPageInfoList) {
        if (initPageInfoList != null) {
            mInitPageInfoList = new ArrayList<>(initPageInfoList.size());
            for (CategoriesPageInfo pageInfo : initPageInfoList) {
                mInitPageInfoList.add(new CategoriesPageInfo(pageInfo));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(Extra.USER_INFO);
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
        mViewPager.setOffscreenPageLimit(3);
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
        observeCategorySelecting();
        if (mInitPageInfoList != null && mInitPageInfoList.size() > 0) {
            mTabsAdapter.addTabs(mInitPageInfoList);
            setNewTabMargin();
            mViewPager.setCurrentItem(mTabsAdapter.getCount() - 1);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        Disposable disposable = mWebApi.post4Json(URLConfig.GET_MALFUNC_CATEGORIES, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if ("success".equals(jsonObject.optString("result"))) {
                            MalfuncCategory[] categories = new Gson()
                                    .fromJson(jsonObject.optString("data"), MalfuncCategory[].class);
                            mTabsAdapter.addTab(new CategoriesPageInfo(categories));
                            setNewTabMargin();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "throwable=", throwable.getMessage());
                    }
                });
        mDisposables.add(disposable);
    }

    /**
     * 观察分类被选中
     */
    private void observeCategorySelecting() {
        Disposable disposable1 = RxBus.getInstance().toObservable(MalfuncCategory.class)
                .subscribe(new Consumer<MalfuncCategory>() {
                    @Override
                    public void accept(MalfuncCategory malfuncCategory) throws Exception {
                        Cog.d(TAG, "receive onItemClick=", malfuncCategory);
                        int tabPosition = mTabLayout.getSelectedTabPosition();
                        Tab tab = mTabLayout.getTabAt(tabPosition);
                        if (tab != null) {
                            tab.setText(malfuncCategory.getName());
                        }
                        if (tabPosition < 2) {
                            tryToAddTab(malfuncCategory);
                        } else {
                            notifySelected();
                            dismiss();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.e(TAG, "toObservable throwable=", throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
        mDisposables.add(disposable1);
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

    /**
     * 抓取分类的子分类，有的话加载标签
     * @param malfuncCategory 当前故障分类
     */
    private void tryToAddTab(MalfuncCategory malfuncCategory) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("id", malfuncCategory.getId());
        mWebApi.post4Json(URLConfig.GET_MALFUNC_CATEGORIES, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if ("success".equals(jsonObject.optString("result"))) {
                            MalfuncCategory[] categories = new Gson()
                                    .fromJson(jsonObject.optString("data"), MalfuncCategory[].class);
                            int nextPosition = mViewPager.getCurrentItem() + 1;
                            if (categories == null || categories.length == 0) {
                                mTabsAdapter.remove(nextPosition);
                                notifySelected();
                                setNewTabMargin();
                                dismiss();
                            } else {
                                mTabsAdapter.remove(nextPosition + 1);
                                if (nextPosition <= mTabsAdapter.getCount() - 1) {//有下一页
                                    //获取下一页
                                    MalfuncCategoriesFragment mcf = (MalfuncCategoriesFragment) mTabsAdapter
                                            .getFragmentAt(nextPosition);
                                    CategoriesPageInfo categoriesPageInfo = new CategoriesPageInfo(categories);
                                    mcf.setInfo(categoriesPageInfo);
                                    mTabsAdapter.update(nextPosition, categoriesPageInfo);
                                    Tab tab = mTabLayout.getTabAt(nextPosition);
                                    if (tab != null) {
                                        tab.setText("未选择");
                                    }
                                } else {
                                    mTabsAdapter.addTab(new CategoriesPageInfo(categories));
                                }
                                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                                setNewTabMargin();
                            }
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

    public void setOnCategorySelectedListener(OnCategorySelectedListener  listener) {
        mOnCategorySelectedListener = listener;
    }

    private void notifySelected() {
        if (mOnCategorySelectedListener != null) {
            List<CategoriesPageInfo> pageInfos = new ArrayList<>(mTabsAdapter.getCount());
            for (int i = 0; i < mTabsAdapter.getCount(); i++) {
                MalfuncCategoriesFragment mcf = (MalfuncCategoriesFragment) mTabsAdapter
                        .getFragmentAt(i);
                CategoriesPageInfo pageInfo = mcf.getCategoriesPageInfo();
                pageInfos.add(mcf.getCategoriesPageInfo());
            }
            mOnCategorySelectedListener.onCategorySelected(pageInfos);
        }
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
        mDisposables.clear();
    }

    public interface OnCategorySelectedListener{
        void onCategorySelected(List<CategoriesPageInfo> pageInfos);
    }

}
