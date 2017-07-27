package com.codyy.erpsportal.commons.controllers.viewholders.onlineclass;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.onlineclass.SipNetResearch;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组-博文-下一页-热门博文item
 * Created by poe on 16-1-15.
 */
public class SipPersonalClassViewHolder extends BaseRecyclerViewHolder<SipNetResearch> {
    @Bind(R.id.tv_name)TextView mTitleTextView;

    public SipPersonalClassViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_personal_class_sip;
    }

    @Override
    public void setData(int position,SipNetResearch data) {
        mCurrentPosition    =   position ;
        mData   =   data ;
        mTitleTextView.setText(Html.fromHtml(UIUtils.filterCharacter(data.getTitle())));
    }
}
