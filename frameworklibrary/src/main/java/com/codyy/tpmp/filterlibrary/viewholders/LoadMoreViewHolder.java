package com.codyy.tpmp.filterlibrary.viewholders;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codyy.tpmp.filterlibrary.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;

/**
 * 加载更多...在recyclerView的底部工多使用 .
 * Created by poe on 16-1-19.
 */
public class LoadMoreViewHolder<VH> extends BaseRecyclerViewHolder<VH>{

    TextView mTitleTextView;
    ProgressBar mProgressBar;
    TextView mMoreBtnTextView;
    private BaseRecyclerAdapter.OnLoadMoreClickListener mOnLoadMoreClickListener;

    public LoadMoreViewHolder(View itemView , BaseRecyclerAdapter.OnLoadMoreClickListener listener) {
        super(itemView);
        bindViews(itemView, listener);
    }

    private void bindViews(View itemView, BaseRecyclerAdapter.OnLoadMoreClickListener listener) {
        mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.pro_loading);
        mMoreBtnTextView = (TextView) itemView.findViewById(R.id.tv_more_btn);
        this.mOnLoadMoreClickListener = listener ;
        mMoreBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
            }
        });
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_view_load_more;
    }

    void showProgress(){
        mMoreBtnTextView.setVisibility(View.GONE);
        show();
        if(null != mOnLoadMoreClickListener) mOnLoadMoreClickListener.onMoreData();
    }

    @Override
    public void setData(int position,VH data) {
        mCurrentPosition = position;
        mData = data;
    }

    public void show(){
        mTitleTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void dismiss(){
        mTitleTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 是否显示＂．．．加载更多＂　
     * @param show false: 显示更多　true: 显示进度
     */
    public void showMore(boolean show){
        if(!show){
            mMoreBtnTextView.setVisibility(View.VISIBLE);
        }else{
            mMoreBtnTextView.setVisibility(View.GONE);
        }
    }
}
