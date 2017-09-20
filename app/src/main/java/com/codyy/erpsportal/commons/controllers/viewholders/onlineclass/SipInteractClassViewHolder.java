package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.onlineclass.SipNetResearch;
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
public class SipInteractClassViewHolder extends BaseRecyclerViewHolder<SipNetResearch> {
    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mTitleTextView;
    @Bind(R.id.tv_desc)TextView mDescTextView;
    @Bind(R.id.tv_view_count)TextView mViewCountTv;
    @Bind(R.id.tv_create_time)TextView mCrateTimeTv;

    public SipInteractClassViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_interact_class_sip;
    }


    @Override
    public void setData(int position,SipNetResearch data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        //head pic
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getSubjectPic());

        mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getTitle())));
        mDescTextView.setText(data.getDescription());
        mViewCountTv.setText(String.valueOf(data.getViewCount()));
        //create time
        mCrateTimeTv.setText(DateUtil.getDateStr(data.getStartTime(),DateUtil.DEF_FORMAT));
    }
}
