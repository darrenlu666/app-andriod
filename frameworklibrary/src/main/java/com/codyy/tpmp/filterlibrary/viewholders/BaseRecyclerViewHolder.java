package com.codyy.tpmp.filterlibrary.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;


/**
 * 基本的ViewHolder
 * Created by poe on 15-8-10.
 */
public abstract class BaseRecyclerViewHolder<T>  extends RecyclerView.ViewHolder{
    /**
     * 缓存数据，跳转使用
     */
    protected T mData ;
    /**
     * 当前viewHolder在整个数据中的位置
     */
    protected int mCurrentPosition = -1;

    protected BaseRecyclerAdapter mAdapter;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }


    /**
     * 仅方便查看view
     * @return
     */
    public abstract int obtainLayoutId();
    /**
     * onBindViewHolder
     * 如果需要使用data应当缓存 mData = data
     * @param data
     */
    // TODO: 16-2-16 填充数据，
    public abstract void setData(int position ,T data)throws Throwable;

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    public BaseRecyclerAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseRecyclerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }
}
