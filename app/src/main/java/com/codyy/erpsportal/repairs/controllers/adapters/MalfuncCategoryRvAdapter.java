package com.codyy.erpsportal.repairs.controllers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.controllers.fragments.MalfuncCategoriesFragment.OnItemClickListener;
import com.codyy.erpsportal.repairs.models.entities.MalfuncCategory;

import java.util.List;

import butterknife.Bind;

/**
 * 故障类型适配器
 */
public class MalfuncCategoryRvAdapter extends RecyclerView.Adapter<MalfuncCategoryRvAdapter.ViewHolder> {

    private List<MalfuncCategory> mCategoryList;

    private OnItemClickListener mListener;

    private int mPosition = -1;

    public MalfuncCategoryRvAdapter() { }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setCategoryList(List<MalfuncCategory> categoryList) {
        mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_malfunc_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MalfuncCategory category = mCategoryList.get(position);
        holder.setDataToView(category, position == mPosition);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (mPosition == pos) return;//点击的已选中的就什么都不用搞了
                if (mListener != null) {
                    mListener.onItemClick(mCategoryList.get(pos), pos);
                }
                int originalPos = mPosition;
                mPosition = pos;
                notifyItemChanged(originalPos);
                notifyItemChanged(mPosition);
            }
        });
    }

    /**
     * 设置选中项
     * @param position 选中项位置
     */
    public void setSelectedPosition(int position) {
        mPosition = position;
    }

    @Override
    public int getItemCount() {
        return mCategoryList == null? 0: mCategoryList.size();
    }

    @LayoutId(R.layout.item_malfunc_category)
    public static class ViewHolder extends BindingRvHolder<MalfuncCategory> {

        @Bind(R.id.tv_category_name)
        CheckedTextView mCategoryNameTv;

        @Bind(R.id.iv_mark_selected)
        ImageView mSelectedMarkIv;

        public ViewHolder(View view) {
            super(view);
        }

        public void setDataToView(MalfuncCategory data, boolean isSelected) {
            mCategoryNameTv.setChecked(isSelected);
            mCategoryNameTv.setText(data.getName());
            if (isSelected) {
                mSelectedMarkIv.setVisibility(View.VISIBLE);
            } else {
                mSelectedMarkIv.setVisibility(View.GONE);
            }
        }
    }
}
