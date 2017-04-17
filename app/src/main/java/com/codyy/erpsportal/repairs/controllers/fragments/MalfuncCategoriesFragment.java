package com.codyy.erpsportal.repairs.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.RxBus;
import com.codyy.erpsportal.repairs.controllers.adapters.MalfuncCategoryRvAdapter;
import com.codyy.erpsportal.repairs.models.entities.CategoriesPageInfo;
import com.codyy.erpsportal.repairs.models.entities.MalfuncCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 故障类别选择
 */
public class MalfuncCategoriesFragment extends Fragment {

    private MalfuncCategoryRvAdapter mRvAdapter;

    private CategoriesPageInfo mCategoriesPageInfo;

    public MalfuncCategoriesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRvAdapter = new MalfuncCategoryRvAdapter();
        mRvAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(MalfuncCategory item, int position) {
                mCategoriesPageInfo.setSelected(position);
                RxBus.getInstance().send(item);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_malfunccategories, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter( mRvAdapter);
        setInfo(mCategoriesPageInfo);
        return view;
    }

    public void setInfo(CategoriesPageInfo categoriesPageInfo) {
        if (mRvAdapter != null && categoriesPageInfo != null) {
            mRvAdapter.setCategoryList(new ArrayList<>(Arrays.asList(categoriesPageInfo.getCategories())));
            mRvAdapter.setSelectedPosition(categoriesPageInfo.getSelected());
            mRvAdapter.notifyDataSetChanged();
        }
        mCategoriesPageInfo = categoriesPageInfo;
    }

    public void setCategoryList(@NonNull List<MalfuncCategory> categoryList) {
        mRvAdapter.setCategoryList(categoryList);
        mRvAdapter.setSelectedPosition(-1);
        mRvAdapter.notifyDataSetChanged();
    }

    public CategoriesPageInfo getCategoriesPageInfo() {
        return mCategoriesPageInfo;
    }

    private void setListener(OnItemClickListener onItemClickListener) {
        mRvAdapter.setListener(onItemClickListener);
    }

    public interface OnItemClickListener {
        void onItemClick(MalfuncCategory item, int position);
    }
}
