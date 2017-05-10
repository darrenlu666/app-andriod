package com.codyy.erpsportal.repairs.controllers.adapters;

import android.content.Context;
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
import com.codyy.erpsportal.repairs.models.entities.ClassroomSelectItem;

import java.util.List;

import butterknife.Bind;

/**
 * 供选择班级适配器
 */
public class ClassroomsSelectingRvAdapter extends RecyclerView.Adapter<ClassroomsSelectingRvAdapter.ViewHolder> {

    private List<ClassroomSelectItem> mClassroomSelectItems;

    private OnItemClickListener mListener;

    private int mPosition = -1;

    public ClassroomsSelectingRvAdapter() { }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setClassroomSelectItems(List<ClassroomSelectItem> classroomSelectItems) {
        mClassroomSelectItems = classroomSelectItems;
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
        ClassroomSelectItem category = mClassroomSelectItems.get(position);
        holder.setDataToView(category, position == mPosition);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (mPosition == pos) return;//点击的已选中的就什么都不用搞了
                if (mListener != null) {
                    mListener.onItemClick(mClassroomSelectItems.get(pos), pos);
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

    /**
     * 设置选中项
     * @param item 项
     * @return 数据项的index，-1为不在列表中
     */
    public int setSelectedItem(ClassroomSelectItem item) {
        mPosition = mClassroomSelectItems == null? -1: mClassroomSelectItems.indexOf(item);
        return mPosition;
    }

    @Override
    public int getItemCount() {
        return mClassroomSelectItems == null? 0: mClassroomSelectItems.size();
    }

    @LayoutId(R.layout.item_malfunc_category)
    public static class ViewHolder extends BindingRvHolder<ClassroomSelectItem> {

        @Bind(R.id.tv_category_name)
        CheckedTextView mCategoryNameTv;

        @Bind(R.id.iv_mark_selected)
        ImageView mSelectedMarkIv;

        public ViewHolder(View view) {
            super(view);
        }

        public void setDataToView(ClassroomSelectItem item, boolean isSelected) {
            Context context = itemView.getContext();
            mCategoryNameTv.setChecked(isSelected);
            mCategoryNameTv.setText(context.getString(R.string.classroom_role_format,
                    item.getSkey(), item.getRoomName()));
            if (isSelected) {
                mSelectedMarkIv.setVisibility(View.VISIBLE);
            } else {
                mSelectedMarkIv.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ClassroomSelectItem item, int position);
    }
}
