package com.codyy.erpsportal.homework.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.R;

/**
 * 习题列表抽象碎片
 * Created by ldh on 2016/1/12.
 */
public abstract class ItemIndexBaseFragment extends Fragment {
    private View mView;
    private RecyclerView mRecyclerView;
    private Context mContext;

    public ItemIndexBaseFragment() {

    }

    public ItemIndexBaseFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_item_index, null);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        RecyclerView.Adapter mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, getSpanCount());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getItemSpanSize(position, gridLayoutManager);
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        return mView;
    }

    protected RecyclerView getRecyclerView(){
        if(mRecyclerView == null)
            return (RecyclerView) mView.findViewById(R.id.recycler_view);
        else
            return mRecyclerView;
    }

    /**
     * 设置adapter
     *
     * @return
     */
    protected abstract RecyclerView.Adapter getAdapter();

    /**
     * 设置每项的所占宽度
     *
     * @param position
     * @param gridLayoutManager
     * @return
     */
    protected abstract int getItemSpanSize(int position, GridLayoutManager gridLayoutManager);

    /**
     * 设置每项所占比例
     * @return
     */
    protected abstract int getSpanCount();

}
