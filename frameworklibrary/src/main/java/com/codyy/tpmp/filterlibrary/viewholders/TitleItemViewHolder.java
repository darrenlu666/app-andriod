package com.codyy.tpmp.filterlibrary.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.tpmp.filterlibrary.R;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

/**
 * 通用绿色箭头+更多>> 列表 title viewHold
 * Created by poe on 16-1-15.
 */
public class TitleItemViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar>{
    /**
     * 仅显示标题
     * 0x100 - 0x200为不希望绘制divider的值
     */
    public static final int ITEM_TYPE_TITLE_SIMPLE = 0x100101 ;
    /**
     * 仅显示标题 /并且没有更多数据
     * 0x100 - 0x200为不希望绘制divider的值
     */
    public static final int ITEM_TYPE_TITLE_SIMPLE_NO_DATA = 0x100102 ;
    /**
     * 显示更多>>
     * 0x100 - 0x200为不希望绘制divider的值
     */
    public static final int ITEM_TYPE_TITLE_MORE = 0x100103;
    /**
     * 显示更多>> /并且没有更多数据
     * 0x100 - 0x200为不希望绘制divider的值
     */
    public static final int ITEM_TYPE_TITLE_MORE_NO_DATA = 0x100104;

    TextView mTitleTextView;
    Button mMoreButton;
    RelativeLayout mNoMoreRelativeLayout;

    public TitleItemViewHolder(View view) {
        super(view);
        bindViews(view);
    }

    private void bindViews(View itemView) {
        mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
        mMoreButton = (Button) itemView.findViewById(R.id.btn_more);
        mNoMoreRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rlt_no_data);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_recycler_title_bar;
    }

    @Override
    public void setData(int position,BaseTitleItemBar data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        mTitleTextView.setText(data.getBaseTitle());
        switch (data.getBaseViewHoldType()){
            case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE:
                mMoreButton.setVisibility(View.GONE);
                mNoMoreRelativeLayout.setVisibility(View.GONE);
                break;
            case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE:
                mMoreButton.setVisibility(View.VISIBLE);
                mNoMoreRelativeLayout.setVisibility(View.GONE);
                break;
            case TitleItemViewHolder.ITEM_TYPE_TITLE_SIMPLE_NO_DATA:
                mMoreButton.setVisibility(View.GONE);
                mNoMoreRelativeLayout.setVisibility(View.VISIBLE);
                break;
            case TitleItemViewHolder.ITEM_TYPE_TITLE_MORE_NO_DATA:
                mMoreButton.setVisibility(View.VISIBLE);
                mNoMoreRelativeLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public interface OnClickListener{
        /**
         * item click
         */
        void OnClick(String title);
    }
}
