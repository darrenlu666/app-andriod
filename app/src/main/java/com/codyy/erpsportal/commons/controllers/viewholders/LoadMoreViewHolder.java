package com.codyy.erpsportal.commons.controllers.viewholders;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 加载更多...在recyclerView的底部工多使用 .
 * Created by poe on 16-1-19.
 */
public class LoadMoreViewHolder<VH> extends BaseRecyclerViewHolder<VH>{

    @Bind(R.id.tv_title)TextView mTitleTextView;
    @Bind(R.id.pro_loading)ProgressBar mProgressBar;
    @Bind(R.id.tv_more_btn)TextView mMoreBtnTextView;
    private BaseRecyclerAdapter.OnLoadMoreClickListener mOnLoadMoreClickListener;

    public LoadMoreViewHolder(View itemView ,BaseRecyclerAdapter.OnLoadMoreClickListener listener) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.mOnLoadMoreClickListener = listener ;
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_view_load_more;
    }

    @OnClick(R.id.tv_more_btn)
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
