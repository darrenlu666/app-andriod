package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;

import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;

/**
 * 我的博文-全部-分类博文item
 * Created by poe on 16-1-15.
 */
@Deprecated
public class CategoryBlogViewHolder extends ClassBlogViewHolder{

    public CategoryBlogViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(int position,BlogPost data) {
        super.setData(position , data);
        //设置分类名称
        mTeacherTextView.setText(data.getCategoryName());
    }
}
