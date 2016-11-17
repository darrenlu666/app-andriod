package com.codyy.erpsportal.groups.controllers.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个TextView (班级成员头部ｉｔｅｍ).
 * Created by poe on 16-1-25.
 */
public  class ClassMemberSelectViewHolder extends BaseRecyclerViewHolder<ClassCont> {

    @Bind(R.id.content) TextView mContentTextView;

    public ClassMemberSelectViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_filter_simple_text;
    }

    @Override
    public void setData(int position , ClassCont data) {
        mCurrentPosition    =   position ;
        mData  = data ;

        if(null != data){
            StringBuilder sb = new StringBuilder();
            if(!TextUtils.isEmpty(data.getClassLevelName())) sb.append(data.getClassLevelName());
            if(!TextUtils.isEmpty(data.getBaseClassName())) sb.append(data.getBaseClassName());
            mContentTextView.setText(sb.toString());
        }
    }

}
