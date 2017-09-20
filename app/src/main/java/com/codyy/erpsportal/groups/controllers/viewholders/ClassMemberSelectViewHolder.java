package com.codyy.erpsportal.groups.controllers.viewholders;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单个TextView (班级成员头部ｉｔｅｍ).
 * Created by poe on 16-1-25.
 */
public  class ClassMemberSelectViewHolder extends BaseRecyclerViewHolder<ClassCont> {

    @Bind(R.id.tv_content) TextView mContentTextView;

    public ClassMemberSelectViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_class_selector;
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
        //处理选中颜色需要在外面设置 viewHold#setAdapter(BaseRecyclerViewAdapter adapter)
        if(getAdapter()!=null && getAdapter().getSelectedPosition() == mCurrentPosition ){
            mContentTextView.setTextColor(ContextCompat.getColor(mContentTextView.getContext(),R.color.main_color));
        }else{
            mContentTextView.setTextColor(ContextCompat.getColor(mContentTextView.getContext(),R.color.black_666));
        }
    }

}
