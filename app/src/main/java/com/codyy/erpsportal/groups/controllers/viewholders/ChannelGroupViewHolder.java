package com.codyy.erpsportal.groups.controllers.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 频道页-圈组-item
 * Created by poe on 16-1-15.
 */
public class ChannelGroupViewHolder extends BaseRecyclerViewHolder<Group> {

    public static final int ITEM_TYPE_GROUP_RECOMMEND = 0x01 ;
    public static final int ITEM_TYPE_GROUP_TEACHING = 0x02 ;
    public static final int ITEM_TYPE_GROUP_INTEREST = 0x03;

    @Bind(R.id.sdv_group_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_group_name)TextView mGroupTitleTextView;
    @Bind(R.id.tv_creator)TextView mCreatorTextView;
    @Bind(R.id.tv_member)TextView mMemberLimitTextView;
    @Bind(R.id.tv_subject_or_category)TextView mSubjectTextView;

    public ChannelGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_channel_group;
    }

    @Override
    public void setData(int position,Group data) {
        mCurrentPosition    =   position;
        this.mData  =   data ;
        if(Group.TYPE_INTEREST.equals(data.getGroupType())){//兴趣组
            mSubjectTextView.setText(data.getCategoryName());
        }else if(Group.TYPE_TEACHING.equals(data.getGroupType())){//教研组
            mSubjectTextView.setText(UIUtils.filterNull((TextUtils.isEmpty(data.getGrade())?"":data.getGrade()+"/")+data.getSubjectName()));
        }
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getPic());
        mGroupTitleTextView.setText(data.getGroupName());
        mCreatorTextView.setText(data.getGroupCreator());
        String limit = data.getLimited().equals("0")?"不限":(data.getLimited()+" 人");
        String number = data.getMemberCount()+" / "+limit;
        mMemberLimitTextView.setText(number);
    }
}
