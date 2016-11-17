package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主持机构/主备学校.
 * Created by poe on 16-6-23.
 */
public class MainOrganizationViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar>{

    private static final int ITEM_TYPE_MAIN_SCHOOL           =   0x01;//主备学校
    private static final int ITEM_TYPE_MAIN_ORGANIZATION     =   0x02;//主持机构

    @Bind(R.id.tv_school_name) TextView mSchoolTextView;
    @Bind(R.id.tv_lesson_room_text)TextView mRoomDescTv;//
    @Bind(R.id.tv_lesson_room) TextView mRoomTextView;//主会场
    @Bind(R.id.tv_speaker_desc)TextView mSpeakerDescTv;//
    @Bind(R.id.tv_speaker)TextView mSpeakerTv;//主讲人/主持人
    @Bind(R.id.tv_attend_metting_people) TextView mAttendTextView;//参会者

    public MainOrganizationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_main_organization;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {
        mCurrentPosition = position;
        mData   =   data;
        switch (getItemViewType()){
            case ITEM_TYPE_MAIN_SCHOOL://主备学校
                mRoomDescTv.setText("备课室");
                mSpeakerDescTv.setText("主讲人");
                PrepareLessonsDetailEntity.MasterSchoolEntity masterSchoolEntity = (PrepareLessonsDetailEntity.MasterSchoolEntity) data;
                mSchoolTextView.setText(masterSchoolEntity.getSchoolName());
                mRoomTextView.setText(masterSchoolEntity.getMasterclassroom());
                mSpeakerTv.setText(masterSchoolEntity.getMasterteacher());
                mAttendTextView.setText(masterSchoolEntity.getParticipator());
                break;
            case ITEM_TYPE_MAIN_ORGANIZATION://主持机构
                mRoomDescTv.setText("主会场");
                mSpeakerDescTv.setText("主持人");
                PrepareLessonsDetailEntity.MasterSchoolEntity masterSchool = (PrepareLessonsDetailEntity.MasterSchoolEntity) data;
                mSchoolTextView.setText(masterSchool.getSchoolName());
                mRoomTextView.setText(masterSchool.getMasterclassroom());
                mSpeakerTv.setText(masterSchool.getMasterteacher());
                mAttendTextView.setText(masterSchool.getParticipator());
                break;
        }
    }
}
