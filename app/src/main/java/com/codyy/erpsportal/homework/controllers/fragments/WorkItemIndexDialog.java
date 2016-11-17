package com.codyy.erpsportal.homework.controllers.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ItemIndexListRecyBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.commons.utils.CharUtils;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目索引界面
 * Created by ldh on 2016/3/7.
 */
public class WorkItemIndexDialog extends DialogFragment implements View.OnClickListener {
    public static String TAG = WorkItemIndexDialog.class.getSimpleName();
    public static final String ARG_DATA = "arg_data";
    public static final String ARG_HAS_TOTAL_COMMENT = "arg_has_total_comment";
    public static final String ARG_SHOW_FIRST_PAGE = "arg_show_first_page";
    public static final String ARG_ITEM_COUNT = "arg_item_count";
    /**
     * 是否有评论页
     */
    private boolean mIsCommentPage;
    /**
     * 是否有首页
     */
    private boolean mIsFirstPage;
    /**
     * 题目总数
     */
    private int mItemCount;
    private ArrayList<ItemInfoClass> mData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelableArrayList(ARG_DATA);
            mIsCommentPage = getArguments().getBoolean(ARG_HAS_TOTAL_COMMENT);
            mItemCount = getArguments().getInt(ARG_ITEM_COUNT);
            mIsFirstPage = getArguments().getBoolean(ARG_SHOW_FIRST_PAGE);
        }
    }

    private int getActivityWindowHeight() {
        return getActivity().getWindow().getDecorView().getHeight();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_item_index, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        TextView mCloseTv = (TextView) view.findViewById(R.id.tv_close);
       /* TextView mFirstPageTv = (TextView) view.findViewById(R.id.tv_first_page);
        mFirstPageTv.setVisibility(mIsFirstPage ? View.VISIBLE : View.INVISIBLE);
        mFirstPageTv.setOnClickListener(this);*/
        /*TextView mTotalComment = (TextView) view.findViewById(R.id.tv_total_comment);
        mTotalComment.setOnClickListener(this);
        mTotalComment.setVisibility(mIsCommentPage ? View.VISIBLE : View.INVISIBLE);*/
        Dialog dialog = new Dialog(getActivity(), R.style.work_item_index_dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setGravity(Gravity.BOTTOM);
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = getActivityWindowHeight() * 2 / 3;
            window.setAttributes(lp);
        }
        RecyclerView.Adapter mAdapter = new ItemIndexListRecyAdapter(getContext(), mData);
        mRecyclerView.setAdapter(mAdapter);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return CharUtils.strIsEnglish(mData.get(position).getWorkItemType().replace("_", "")) ? 1 : gridLayoutManager.getSpanCount();
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mCloseTv.setOnClickListener(this);
        setCancelable(true);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_first_page:
                mItemIndexClickListener.onBtnItemIndexClick(0);
                break;
            case R.id.tv_total_comment:
                mItemIndexClickListener.onBtnItemIndexClick(mItemCount);
                break;
            case R.id.tv_close:
                dismiss();
                break;
        }
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
            button.setText(data.getWorkItemIndex());
            if (data.getColor() != 0) {
                button.setBackgroundColor(data.getColor());
            } else {
                button.setBackgroundColor(Color.rgb(236, 236, 236));
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, data.getWorkItemIndex() + "");
                    mItemIndexClickListener.onBtnItemIndexClick(data.getWorkItemIndex() - 1);
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
