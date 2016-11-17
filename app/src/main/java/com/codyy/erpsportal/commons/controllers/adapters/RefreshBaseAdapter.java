package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;

import java.util.List;

/**
 * Created by kmdai on 15-12-11.
 */
public abstract class RefreshBaseAdapter<T extends RefreshEntity> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 数据
     */
    private List<T> mDatas;

    private int mState = RefreshRecycleView.STATE_UP_LOADEMORE;
    protected Context mContext;
    private LayoutInflater mInflater;
    private int mVisibility;
    /**
     * 最后显示更多加载字体的颜色
     */
    protected int mTextColor;

    public RefreshBaseAdapter(Context mContext) {
        this.mContext = mContext;
        init(mContext);
    }

    public RefreshBaseAdapter(Context mContext, List<T> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mVisibility = View.VISIBLE;
        init(mContext);
    }

    private void init(Context context) {
        mInflater = LayoutInflater.from(mContext);
        mTextColor = context.getResources().getColor(R.color.primary_text);
    }

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RefreshEntity.REFRESH_TYPE_LASTVIEW) {
            return new LastViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_viewholder_last, parent, false));
        }
        return getHolderView(mInflater, parent, viewType);
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == mDatas.size()) {
            LastViewHolder lastViewHolder = (LastViewHolder) holder;
            lastViewHolder.setState(mState);
            lastViewHolder.setVisibility(mVisibility);
        } else {
            if (mDatas != null) {
                onBindView(holder, position, mDatas.get(position));
            }
        }
    }

    public List<T> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    final public int getItemCount() {
        if (mDatas != null && mDatas.size() > 0) {
            return mDatas.size() + 1;
        }
        return 0;
    }

    public T getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas != null) {
            return position == mDatas.size() ? RefreshEntity.REFRESH_TYPE_LASTVIEW : mDatas.get(position).getmHolderType();
        }
        return super.getItemViewType(position);
    }

    public int getState() {
        return mState;
    }

    public void setState(int mState) {
        this.mState = mState;
    }

    /**
     * 返回viewholder
     *
     * @param inflater
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    public abstract RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     * @param entity
     */
    public abstract void onBindView(RecyclerView.ViewHolder holder, int position, T entity);

    /**
     * 最后显示的viewholder
     */
    class LastViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        TextView mTextView;
        View mItemView;

        public LastViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.adapter_holder_process);
            mTextView = (TextView) itemView.findViewById(R.id.adapter_holder_textview);
            mTextView.setTextColor(mTextColor);
        }

        public void setState(int state) {
            switch (state) {
                case RefreshRecycleView.STATE_NO_MORE:
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText(mContext.getString(R.string.refresh_state_no_more));
                    break;
                case RefreshRecycleView.STATE_LOADING:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTextView.setText(mContext.getString(R.string.refresh_state_loading));
                    break;
                case RefreshRecycleView.STATE_UP_LOADEMORE:
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText(mContext.getString(R.string.refresh_state_up_loadmore));
                    break;
                case RefreshRecycleView.STATE_LOADE_ERROR:
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText(mContext.getString(R.string.refresh_state_loade_error));
                    break;
            }
        }

        public void setVisibility(int v) {
            mItemView.setVisibility(v);
        }
    }

    public void clearDatas() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加list数据，并刷新view
     *
     * @param datas
     */
    public void addListDatas(List<T> datas) {
        if (datas != null && datas.size() > 0) {
            int start = mDatas.size();
            if (mDatas != null) {
                mDatas.addAll(datas);
            }
            if (start > 0) {
                notifyItemRangeChanged(start, getItemCount() - start);
            } else {
                notifyDataSetChanged();
            }
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * 添加单个数据，并刷新view
     *
     * @param object
     */
    public void addData(T object) {
        if (object != null) {
            int start = mDatas.size() == 0 ? 0 : mDatas.size() - 1;
            if (mDatas != null) {
                mDatas.add(object);
            }
            notifyItemRangeChanged(start, getItemCount() - start - 1);
        }
    }

    /**
     * 在相应位置添加数据，并刷新view
     *
     * @param object
     * @param position
     */
    public void addData(T object, int position) {
        if (object != null) {
            if (mDatas != null) {
                mDatas.add(position, object);
            }
            notifyItemInserted(position);
        }
    }

    public void ramoveData(T object) {
        if (object != null) {
            if (mDatas != null) {
                int index = mDatas.indexOf(object);
                if (mDatas.size() == 1) {
                    mDatas.remove(object);
                    notifyDataSetChanged();
                } else if (index > 0) {
                    notifyItemRemoved(index);
                    mDatas.remove(object);
                }
            }
        }
    }

    public void setLastVisibility(int v) {
        mVisibility = v;
        if (getItemCount() > 0) {
            notifyItemChanged(getItemCount() - 1);
        }
    }
}
