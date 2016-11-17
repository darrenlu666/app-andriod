package com.codyy.erpsportal.homework.controllers.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ItemIndexListRecyBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.commons.utils.CharUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 习题列表fragment
 * Created by ldh on 2016/1/12.
 */
@Deprecated
public class WorkItemIndexFragment extends ItemIndexBaseFragment {

    private static String TAG = WorkItemIndexFragment.class.getSimpleName();
    public static final String ARG_DATA = "arg_data";
    private ArrayList<ItemInfoClass> mData;

    public static WorkItemIndexFragment newInstance(Context context, List<ItemInfoClass> data) {

        Bundle args = new Bundle();
        ArrayList<ItemInfoClass> list = (ArrayList<ItemInfoClass>) data;
        args.putParcelableArrayList(ARG_DATA, list);
        WorkItemIndexFragment fragment = new WorkItemIndexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WorkItemIndexFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelableArrayList(ARG_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setPadding(0, 0, 0, UIUtils.dip2px(getActivity(), 58));
        return recyclerView;
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new ItemIndexListRecyAdapter(getContext(), mData);
    }

    @Override
    public int getItemSpanSize(int position, GridLayoutManager gridLayoutManager) {
        return CharUtils.strIsEnglish(mData.get(position).getWorkItemType().replace("_", "")) ? 1 : gridLayoutManager.getSpanCount();
    }

    @Override
    protected int getSpanCount() {
        return 6;
    }

    public class ItemIndexListRecyAdapter extends ItemIndexListRecyBaseAdapter<ItemInfoClass> {
        private List<ItemInfoClass> mList;

        public ItemIndexListRecyAdapter(Context context, List<ItemInfoClass> list) {
            super(context, list);
            mList = list;
        }

        @Override
        public int getItemType(int position) {
            return CharUtils.strIsEnglish(mData.get(position).getWorkItemType().replace("_", "")) ? TYPE_CONTENT : TYPE_TITLE;
        }

        @Override
        protected int getLayoutId(int viewType) {
            return viewType == TYPE_TITLE ? R.layout.item_work_item_index_title : R.layout.item_work_item_index_content;
        }

        @Override
        protected RecyclerView.ViewHolder createViewHolder(View view, int viewType) {
            return viewType == TYPE_TITLE ? new ItemTitleViewHolder(view) : new ItemIndexViewHolder(view);
        }
    }

    public static class ItemTitleViewHolder extends RecyclerViewHolder<ItemInfoClass> {
        private TextView textView;

        public ItemTitleViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            textView = (TextView) view.findViewById(R.id.tv_title_item_list);
        }

        @Override
        public void setDataToView(ItemInfoClass data) {
            textView.setText(data.getWorkItemType());
        }
    }

    public static class ItemIndexViewHolder extends RecyclerViewHolder<ItemInfoClass> {
        private Button button;

        public ItemIndexViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            button = (Button) view.findViewById(R.id.btn_item_content);
        }

        @Override
        public void setDataToView(final ItemInfoClass data) {
            button.setText(data.getWorkItemIndex() + "");
            if (data.getColor() == 0) {
                button.setBackgroundColor(Color.rgb(236, 236, 236));
            } else {
                button.setBackgroundColor(data.getColor());
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, data.getWorkItemIndex() + "");
                    mItemIndexClickListener.onBtnItemIndexClick(Integer.valueOf(data.getWorkItemIndex()) - 1);
                }
            });
        }
    }

    public interface onItemIndexClickListener {
        void onBtnItemIndexClick(int itemIndex);
    }

    private static onItemIndexClickListener mItemIndexClickListener = null;

    public void setOnItemIndexClickListener(onItemIndexClickListener listener) {
        mItemIndexClickListener = listener;
    }
}
