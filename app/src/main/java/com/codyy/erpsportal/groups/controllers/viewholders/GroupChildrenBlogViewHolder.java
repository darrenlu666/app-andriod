package com.codyy.erpsportal.groups.controllers.viewholders;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
 * 圈组-博文-下一页-热门博文item
 * Created by poe on 16-1-15.
 */
public class GroupChildrenBlogViewHolder extends BaseRecyclerViewHolder<BlogPost> {
    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.et_desc)TextView mDescTextView;
    @Bind(R.id.tv_creator)TextView mCreatorTextView;
    @Bind(R.id.tv_create_time)TextView mCrateTimeTv;

    public GroupChildrenBlogViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_child_blog_post;
    }


    @Override
    public void setData(int position,BlogPost data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getBlogPicture());
        //是否为置顶
        if(data.getBaseViewHoldType() == ChannelBlogViewHolder.ITEM_TYPE_TOP){
            String text = "[置顶]"+Html.fromHtml(UIUtils.filterCharacter(data.getBlogTitle()));
            Spannable spannable = new SpannableString(text);
            spannable.setSpan(new StyleSpan(Typeface.BOLD),0,4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#EE9A00")),0,4,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            mTitleTextView.setText(spannable);
        }else{
            mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getBlogTitle())));
        }
        mDescTextView.setText(data.getBlogDesc());
        mCreatorTextView.setText(data.getRealName());
        //create time
        if(!TextUtils.isEmpty(data.getCreateTime())){
            mCrateTimeTv.setText(DateUtil.getDateStr(Long.parseLong(data.getCreateTime()),DateUtil.DEF_FORMAT));
        }
    }
}
