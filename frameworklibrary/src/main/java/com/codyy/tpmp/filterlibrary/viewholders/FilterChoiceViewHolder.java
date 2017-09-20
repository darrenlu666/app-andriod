package com.codyy.tpmp.filterlibrary.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import com.codyy.tpmp.filterlibrary.R;
import com.codyy.tpmp.filterlibrary.entities.FilterCell;


/**
 * 筛选-左边子item
 * Created by poe on 16-1-25.
 */
public class FilterChoiceViewHolder extends BaseRecyclerViewHolder<FilterCell> {

    TextView mTextView;

    public FilterChoiceViewHolder(View itemView ) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.title);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_filter_left;
    }

    @Override
    public void setData(int position , FilterCell data) {
        mCurrentPosition    =   position;
        mData = data ;
        if(data != null ){
            if(data.isCheck()){
                mTextView.setTextColor(ContextCompat.getColor(mTextView.getContext(),R.color.main_color));
            }else{
                mTextView.setTextColor(ContextCompat.getColor(mTextView.getContext(),R.color.grey_666));
            }
            mTextView.setText(data.getName());
        }
    }
}
