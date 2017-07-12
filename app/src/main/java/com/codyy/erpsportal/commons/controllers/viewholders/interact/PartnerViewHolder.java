package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingDetailEntity;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 参会者（集体备课）/参与机构（视频会议）
 * Created by poe on 16-6-23.
 */
public class PartnerViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {//PrepareLessonsDetailEntity.ParticipatorItem

    public static final int ITEM_TYPE_JOIN_PARTNER  =   0x01;//参会者
    public static final int ITEM_TYPE_JOIN_GROUP    =   0x02;//参与机构－视频会议
    public static final int ITEM_TYPE_JOIN_GROUP_COLLECT    =   0x03;//参与机构-集体备课
    @Bind(R.id.tv_school_name) TextView mSchoolTextView;
    @Bind(R.id.tv_classroom_desc)TextView mClassroomDescTv;
    @Bind(R.id.tv_classroom)TextView mClassroomTv;
    @Bind(R.id.tv_attend_meeting_desc) TextView mAttendDescTv;
    @Bind(R.id.tv_attend_meeting_people) TextView mAttendTextView;

    public PartnerViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_partner_or_grouper;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {
        mCurrentPosition = position;
        mData   =   data;
        switch (getItemViewType()){
            case ITEM_TYPE_JOIN_PARTNER://参会者
                mClassroomDescTv.setText("备课室");
                PrepareLessonsDetailEntity.ParticipatorItem participatorItem = (PrepareLessonsDetailEntity.ParticipatorItem) data;
                if(participatorItem.isSelfSchool()){
                    mSchoolTextView.setText(participatorItem.getSchoolName() + "(本校)");
                }else{
                    mSchoolTextView.setText(participatorItem.getSchoolName());
                }
                //没有分会场 ：隐藏【分会场】字段
                if(null == participatorItem.getClassroom() || TextUtils.isEmpty(participatorItem.getClassroom().trim())){
                    mClassroomDescTv.setVisibility(View.GONE);
                    mClassroomTv.setVisibility(View.GONE);
                }else{
                    mClassroomTv.setText(participatorItem.getClassroom());
                }

                mAttendTextView.setText(participatorItem.getParticipator());
                break;
            case ITEM_TYPE_JOIN_GROUP_COLLECT://参与机构-集体备课
                mClassroomDescTv.setText("备课室");
                mAttendDescTv.setText("参与者");
                PrepareLessonsDetailEntity.ParticipatorItem participatorItem2 = (PrepareLessonsDetailEntity.ParticipatorItem) data;
                if(participatorItem2.isSelfSchool()){
                    mSchoolTextView.setText(participatorItem2.getSchoolName() + "(本校)");
                }else{
                    mSchoolTextView.setText(participatorItem2.getSchoolName());
                }
                //没有分会场 ：隐藏【分会场】字段
                if(null == participatorItem2.getClassroom() || TextUtils.isEmpty(participatorItem2.getClassroom().trim())){
                    mClassroomDescTv.setVisibility(View.GONE);
                    mClassroomTv.setVisibility(View.GONE);
                }else{
                    mClassroomTv.setText(participatorItem2.getClassroom());
                }

                mAttendTextView.setText(participatorItem2.getParticipator());
                break;
            case ITEM_TYPE_JOIN_GROUP://参与机构-视频会议
                mClassroomDescTv.setText("分会场");
                VideoMeetingDetailEntity.ParticipatorEntity participator = (VideoMeetingDetailEntity.ParticipatorEntity) data;
                //没有分会场 ：隐藏【分会场】字段
                if(null == participator.getClassroom() || TextUtils.isEmpty(participator.getClassroom().trim())){
                    mClassroomDescTv.setVisibility(View.GONE);
                    mClassroomTv.setVisibility(View.GONE);
                }else{
                    mClassroomTv.setText(participator.getClassroom());
                }

                if(participator.isSelfSchool()){
                    mSchoolTextView.setText(participator.getSchoolName() + "(本校)");
                }else{
                    mSchoolTextView.setText(participator.getSchoolName());
                }
                mAttendTextView.setText(participator.getParticipator());
                break;
        }
    }
}
