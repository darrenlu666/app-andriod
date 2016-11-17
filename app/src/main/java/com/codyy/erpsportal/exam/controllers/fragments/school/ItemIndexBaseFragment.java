package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * 习题列表抽象碎片
 * Created by eachann on 2016/1/12.
 */
public abstract class ItemIndexBaseFragment extends Fragment {
    private View view;
    private RecyclerView mRecyclerView;
    protected TextView mUpOrDownButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_recycle_topic_statistics, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mUpOrDownButton = (TextView) view.findViewById(R.id.ud_class);
        RecyclerView.Adapter mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getSpanCount());
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getItemSpanSize(position, gridLayoutManager);
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        loadData();
        return view;
    }

    protected RecyclerView getRecyclerView() {
        if (mRecyclerView == null)
            return (RecyclerView) view.findViewById(R.id.recycler_view);
        else
            return mRecyclerView;
    }

    /**
     * 设置adapter
     *
     * @return
     */
    protected abstract RecyclerView.Adapter getAdapter();

    protected abstract void loadData();

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
     *
     * @return
     */
    protected abstract int getSpanCount();

}
