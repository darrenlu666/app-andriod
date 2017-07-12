package com.codyy.erpsportal.groups.controllers.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.groups.models.entities.GroupMember;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组成员
 * Created by poe on 15-7-21.
 */
public class GroupMemberViewHolder extends BaseRecyclerViewHolder<GroupMember> {

    @Bind(R.id.tv_title) TextView mTitleTextView;
    @Bind(R.id.sdv)SimpleDraweeView mHeadPicSdv;
    @Bind(R.id.iv_role)ImageView mRoleImageView;

    public GroupMemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_memeber;
    }

    @Override
    public void setData(int position, GroupMember data) {
        mData   =   data;
        mCurrentPosition    =   position;
        mTitleTextView.setText(data.getRealName());
        ImageFetcher.getInstance(mTitleTextView.getContext()).fetchSmall(mHeadPicSdv,data.getHeadPic());
        //判断状态/和身份角色
        switch (data.getUserType()){
            case GroupMember.ROLE_CREATOR://管理者
                mRoleImageView.setVisibility(View.VISIBLE);
                mRoleImageView.setImageResource(R.drawable.ic_group_creator);
                break;
            case GroupMember.ROLE_MANAGER://管理员
//                mRoleImageView.setVisibility(View.VISIBLE);
//                mRoleImageView.setImageResource(R.drawable.ic_group_leader);
//                break;
            case GroupMember.ROLE_MEMBER://普通成员
                mRoleImageView.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
