package com.codyy.erpsportal.groups.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.entities.filter.AreaItem;

/**
 * Created by caixingming on 2015/7/3.
 */
public class ChoiceViewHolder extends AbsViewHolder<AreaItem> {

    private TextView mTextView;

    @Override
    public int obtainLayoutId() {
        return R.layout.item_live_left_filter;
    }

    @Override
    public void mapFromView(View view) {
        mTextView = (TextView) view;
    }

    @Override
    public void setDataToView(AreaItem data, Context context) {

        if(data != null ){
//            Cog.e("选中","AreaName:"+data.getAreaName() +" selectArea:"+data.getSelectArea());
            if(data.getAreaName().equals(data.getSelectArea())){
                mTextView.setTextColor(context.getResources().getColor(R.color.green));
            }else{
                mTextView.setTextColor(context.getResources().getColor(R.color.md_grey_700));
            }

            mTextView.setText(data.getAreaName());
        }
    }
}