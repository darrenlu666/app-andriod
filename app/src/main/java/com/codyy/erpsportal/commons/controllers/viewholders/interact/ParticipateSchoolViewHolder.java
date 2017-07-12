package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.PrepareLessonsDetailEntity;
import com.codyy.erpsportal.commons.models.entities.SchoolTeacher;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 互动听课-听课端/评课议课-参与范围
 * Created by poe on 16-6-23.
 */
public class ParticipateSchoolViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {

    public static final int ITEM_TYPE_ATTEND_TEACHER     =   0x01;//参与教师
    public static final int ITEM_TYPE_LISTEN_TERMINAL    =   0x02;//听课段

    @Bind(R.id.assessment_detail_item_school) TextView mSchoolTextView;
    @Bind(R.id.assessment_detail_item_teacher) TextView mTeacherTextView;
    @Bind(R.id.tv_teacher_desc)TextView mTeacherDescTv;//参与教师/听课端

    public ParticipateSchoolViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_assessment_detail_participate;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {

        mCurrentPosition = position;
        mData   =   data;

       switch (getItemViewType()){
           case ITEM_TYPE_ATTEND_TEACHER://参与范围
               mTeacherDescTv.setText("参与教师");
               SchoolTeacher st = (SchoolTeacher) data;
               if(st.getIsSelf().equals("Y")){
                   mSchoolTextView.setText(st.getSchoolName() + "(本校)");
               }else{
                   mSchoolTextView.setText(st.getSchoolName());
               }
               mTeacherTextView.setText(st.getTeachers());
               break;
           case ITEM_TYPE_LISTEN_TERMINAL://听课端
               mTeacherDescTv.setText("听课教师");
               PrepareLessonsDetailEntity.ParticipatorItem pi = (PrepareLessonsDetailEntity.ParticipatorItem) data;

               mSchoolTextView.setText(pi.getSchoolName());
               mTeacherTextView.setText(pi.getParticipator());
               break;
       }

    }
}
