package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;
import com.codyy.erpsportal.onlineteach.models.entities.NetParticipator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 互动听课-主讲学校/接收学校
 * 网络授课－听课学校.
 * Created by poe on 16-6-23.
 */
public class MainOrReceiveSchoolViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar>{

    public static final int ITEM_TYPE_MAIN_SCHOOL       =   0x01;//主讲学校
    public static final int ITEM_TYPE_RECEIVE_SCHOOL    =   0x02;//接收学校

    @Bind(R.id.tv_school_name) TextView mSchoolTextView;
    @Bind(R.id.tv_attend_metting_people) TextView mAttendTextView;
    @Bind(R.id.tv_lesson_room) TextView mLessonRoomTextView;
    @Bind(R.id.tv_lesson_room_text)TextView mRoomDescTv;//教室描述
    @Bind(R.id.tv_attend_metting_text)TextView mTeacherDescTv;//教师描述

    public MainOrReceiveSchoolViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_receive_school;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {

        mCurrentPosition = position;
        mData   =   data;
        switch (getItemViewType()){
            case ITEM_TYPE_MAIN_SCHOOL://主讲学校
                mRoomDescTv.setText(Titles.sMasterRoom);//"主讲教室");
                mTeacherDescTv.setText(Titles.sMasterTeacher);//"主讲教师");
                PrepareLessonsDetailEntity.MasterSchoolEntity masterSchoolEntity = (PrepareLessonsDetailEntity.MasterSchoolEntity) data;
                mSchoolTextView.setText(masterSchoolEntity.getSchoolName());
                mLessonRoomTextView.setText(masterSchoolEntity.getMasterclassroom());
                mAttendTextView.setText(masterSchoolEntity.getMasterteacher());
                break;
            case ITEM_TYPE_RECEIVE_SCHOOL://接收学校
                if(data instanceof PrepareLessonsDetailEntity.ReceiveSchoolItem){
                    mRoomDescTv.setText(Titles.sReceiveRoom);//"接收教室");
                    mTeacherDescTv.setText(Titles.sCoachTeacher);//"辅助教师");
                    PrepareLessonsDetailEntity.ReceiveSchoolItem receiveSchoolItem = (PrepareLessonsDetailEntity.ReceiveSchoolItem) data;
                    mSchoolTextView.setText(receiveSchoolItem.getSchoolName());
                    mLessonRoomTextView.setText(receiveSchoolItem.getReceiveclassroom());
                    mAttendTextView.setText(receiveSchoolItem.getReceiveteacher());
                }else if(data instanceof NetParticipator){//网络授课－听课学校
                    mRoomDescTv.setText("听课教师");
                    mTeacherDescTv.setText("听课学生");
                    NetParticipator receiveSchoolItem = (NetParticipator) data;
                    mSchoolTextView.setText(receiveSchoolItem.getSchoolName());

                    //听课教师
                    ArrayList<String> teachers = receiveSchoolItem.getTeacher();



                    if(null != teachers && teachers.size()>0){
                        StringBuilder sb1 = new StringBuilder();
                        for(String str : teachers){
                            if(sb1.length()>0){
                                sb1.append("、");
                            }
                            sb1.append(str);
                        }
                        mLessonRoomTextView.setText(sb1.toString());
                    }
                    //听课学生
                    ArrayList<String> students = receiveSchoolItem.getStudent();
                    if(null != students && students.size()>0){
                        StringBuilder sb2 = new StringBuilder();
                        for(String str : students){
                            if(sb2.length()>0){
                                sb2.append("、");
                            }
                            sb2.append(str);
                        }
                        mAttendTextView.setText(sb2.toString());
                    }
                }
                break;
        }
    }
}
