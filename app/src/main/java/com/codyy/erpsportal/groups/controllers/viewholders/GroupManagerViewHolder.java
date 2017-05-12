package com.codyy.erpsportal.groups.controllers.viewholders;

import butterknife.Bind;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.ButterKnife;

/**
 * 频道页-圈组-item
 * Created by poe on 16-1-15.
 */
public class GroupManagerViewHolder extends BaseRecyclerViewHolder<Group> {
    private static final String TAG = "GroupManagerViewHolder";
    /**
     * 辖区内圈组
     */
    public static final int ITEM_TYPE_GROUP_MANAGER = 0x01 ;
    /**
     * 校内圈组
     */
    public static final int ITEM_TYPE_GROUP_SCHOOL = 0x02 ;
    /**
     * 我的圈组
     */
    public static final int ITEM_TYPE_GROUP_MY = 0x03;

    @Bind(R.id.sdv_group_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_group_name)TextView mGroupTitleTextView;
    @Bind(R.id.tv_creator)TextView mCreatorTextView;
    @Bind(R.id.tv_member)TextView mMemberLimitTextView;
    @Bind(R.id.tv_subject_or_category)TextView mSubjectTextView;
    @Bind(R.id.iv_role)ImageView mRoleImageView;
    @Bind(R.id.tv_state)TextView mStateTextView;

    public GroupManagerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_manager;
    }

    @Override
    public void setData(int position , Group data) {
        Cog.i(TAG , "set Data position : " +data.getGroupName()+"::"+ position);
        mCurrentPosition    =   position;
        mData   =   data;
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
        //判断状态/和身份角色
        switch (data.getBaseViewHoldType()){
            case ITEM_TYPE_GROUP_MANAGER://辖区内
                mRoleImageView.setVisibility(View.INVISIBLE);
                mStateTextView.setVisibility(View.INVISIBLE);
                break;
            case ITEM_TYPE_GROUP_SCHOOL://校内的
                mRoleImageView.setVisibility(View.INVISIBLE);
                if("Y".equals(data.getClosedFlag())){
                    mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_close));
                    mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_grey_500));
                }else{
                    setApproveStatus(data,true);
                }
                break;
            case ITEM_TYPE_GROUP_MY://我的
                //default： visible CREATOR/MANAGER/MEMBER
                if("CREATOR".equals(data.getUserType())){
                    mRoleImageView.setImageResource(R.drawable.ic_group_manager);
                    mRoleImageView.setVisibility(View.VISIBLE);
                    if("Y".equals(data.getClosedFlag())){
                        mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_close));
                        mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_grey_500));
                        mStateTextView.setVisibility(View.VISIBLE);
                    }else {
                        setApproveStatus(data,false);
                    }
                }else if("MANAGER".equals(data.getUserType())){
                    mRoleImageView.setImageResource(R.drawable.ic_group_leader);
                    mRoleImageView.setVisibility(View.VISIBLE);
                    if("Y".equals(data.getClosedFlag())){
                        mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_close));
                        mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_grey_500));
                        mStateTextView.setVisibility(View.VISIBLE);
                    }else{
                        mStateTextView.setVisibility(View.INVISIBLE);
                    }
                }else{// 成员MEMBER
                    mRoleImageView.setVisibility(View.INVISIBLE);
                    if("Y".equals(data.getClosedFlag())){
                        mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_close));
                        mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_grey_500));
                        mStateTextView.setVisibility(View.VISIBLE);
                    }else{
                        setJoinStatus(data);
                    }
                }
                break;
        }
    }

    private void setJoinStatus(Group data) {
        Cog.i(TAG,"setJoinStatus data : "+data.getGroupName()+"::"+data.getJoinStatus() );
        switch (data.getJoinStatus()){
            case "APPROVED"://加入的圈组没有 “通过”状态
                mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_approved));
                mStateTextView.setVisibility(View.INVISIBLE);
                mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.main_green));
                break;
            case "WAIT":
                mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_wait));
                mStateTextView.setVisibility(View.VISIBLE);
                mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_orange_A400));
                break;
            case "REJECT":
                mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_reject));
                mStateTextView.setVisibility(View.VISIBLE);
                mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_red_800));
                break;
        }
    }

    /**
     * 校内圈组-设置操作状态
     * @param data
     * @param isSchool
     */
    private void setApproveStatus(Group data ,boolean isSchool) {
        switch (data.getApproveStatus()){
            case Group.TYPE_OPERATE_APPROVE://通过 不用显示
                mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_approved));
                if(isSchool){
                    mStateTextView.setVisibility(View.VISIBLE);
                    mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.main_green));
                }else{
                    mStateTextView.setVisibility(View.INVISIBLE);
                }
                break;
            case Group.TYPE_OPERATE_PENDING:
                mStateTextView.setVisibility(View.VISIBLE);
                mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_wait_check));
                mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_orange_A400));
                break;
            case Group.TYPE_OPERATE_REJECT:
                mStateTextView.setVisibility(View.VISIBLE);
                mStateTextView.setText(mStateTextView.getResources().getString(R.string.group_status_reject));
                mStateTextView.setTextColor(mStateTextView.getContext().getResources().getColor(R.color.md_red_800));
                break;
        }
    }
}
