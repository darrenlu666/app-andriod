package com.codyy.erpsportal.groups.controllers.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.groups.models.entities.GroupSpace;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 圈组空间-详情-item
 * Created by poe on 16-1-15.
 */
public class GroupSpaceViewHolder extends BaseRecyclerViewHolder<GroupSpace> {

    @Bind(R.id.sdv_group_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_group_name)TextView mGroupTitleTextView;
    @Bind(R.id.tv_operator)TextView mOperatorTextView;
    @Bind(R.id.tv_member)TextView mMemberLimitTextView;
    @Bind(R.id.tv_subject_or_category)TextView mSubjectTextView;
    @Bind(R.id.tv_subject_desc)TextView mSubjectDescTextView;
    private int mOperatorType = GroupSpace.GROUP_OPERATE_TYPE_PROMPT;

    public GroupSpaceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_group_space;
    }

    @Override
    public void setData(int position,GroupSpace data) {
        mCurrentPosition    =   position ;
        this.mData  =   data ;
        //bug fix by poe start 47: 与UI不一致
        mSubjectDescTextView.setVisibility(View.GONE);
        if(Group.TYPE_INTEREST.equals(data.getGroupType())){//兴趣组
            mSubjectDescTextView.setText(mSubjectDescTextView.getResources().getString(R.string.group_category));
            mSubjectTextView.setText(data.getCategoryName());
        }else if(Group.TYPE_TEACHING.equals(data.getGroupType())){//教研组
            mSubjectDescTextView.setText(mSubjectDescTextView.getResources().getString(R.string.group_subject));
//            mSubjectTextView.setText(UIUtils.filterNull((TextUtils.isEmpty(data.getGrade())?"":data.getGrade()+"/")+data.getSubjectName()));
            StringBuilder tvSubject = new StringBuilder();
            if(!TextUtils.isEmpty(data.getSemesterName())){
                tvSubject.append(data.getSemesterName());
                if(!TextUtils.isEmpty(data.getGrade())){
                    tvSubject.append("/");
                }
            }
            if(!TextUtils.isEmpty(data.getGrade())){
                tvSubject.append(data.getGrade());
                if(!TextUtils.isEmpty(data.getSubjectName())){
                    tvSubject.append("/");
                }
            }
            if(!TextUtils.isEmpty(data.getSubjectName()))tvSubject.append(data.getSubjectName());
            mSubjectTextView.setText(UIUtils.filterNull(tvSubject.toString()));
        }
        //bug fix by poe start end 71: 与UI不一致
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,data.getPic());
        mGroupTitleTextView.setText(data.getGroupName());

        String limit = data.getLimited().equals("0")?"不限":(data.getLimited()+" 人");
        String number = data.getMemberCount()+" / "+limit;
        mMemberLimitTextView.setText(number);

        //operator ,稍后判断显示的造作及背景图片...
        mOperatorType = data.getOperateType();
        updateOperate();
    }

    private void updateOperate() {
        switch (mOperatorType){
            case GroupSpace.GROUP_OPERATE_TYPE_PROMPT:
                mOperatorTextView.setEnabled(true);
                mOperatorTextView.setBackgroundResource(R.drawable.bg_green_prompt);
                mOperatorTextView.setText("申请加入");
                break;
            case GroupSpace.GROUP_OPERATE_TYPE_WAIT:
                mOperatorTextView.setBackgroundResource(R.drawable.bg_green_prompt);
                mOperatorTextView.setText("审核中");
                mOperatorTextView.setEnabled(false);
                break;
            case GroupSpace.GROUP_OPERATE_TYPE_EXIT:
                if("CREATOR".equals(mData.getUserType())){
                    mOperatorTextView.setEnabled(false);
                    mOperatorTextView.setVisibility(View.INVISIBLE);
                }else{
                    mOperatorTextView.setEnabled(true);
                }
                mOperatorTextView.setBackgroundResource(R.drawable.bg_red_exit_group);
                mOperatorTextView.setText("退出圈组");
                break;
        }

        //过滤教研组
        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
        if(mData.getGroupType().equals(Group.TYPE_TEACHING) ){
            if(userInfo.getUserType().equals(UserInfo.USER_TYPE_STUDENT)||userInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)){
                mOperatorTextView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
