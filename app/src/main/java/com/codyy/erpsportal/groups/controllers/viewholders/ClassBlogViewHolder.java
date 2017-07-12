package com.codyy.erpsportal.groups.controllers.viewholders;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.blog.BlogPost;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 班级博文-item
 * Created by poe on 16-1-15.
 */
public class ClassBlogViewHolder extends BaseRecyclerViewHolder<BlogPost> {

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.et_desc)TextView mDescTextView;
    @Bind(R.id.tv_teacher)TextView mTeacherTextView;
    @Bind(R.id.tv_create_time)TextView mCrateTimeTv;

    public ClassBlogViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_class_blog_post;
    }

    @Override
    public void setData(int position,BlogPost data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getBlogPicture());
        mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getBlogTitle())));
        //老师
        mTeacherTextView.setText(data.getRealName());
        mDescTextView.setText(data.getBlogDesc());
        //create time
        if(!TextUtils.isEmpty(data.getCreateTime())){
            mCrateTimeTv.setText(DateUtil.getDateStr(Long.parseLong(data.getCreateTime()),DateUtil.DEF_FORMAT));
        }
    }
}
