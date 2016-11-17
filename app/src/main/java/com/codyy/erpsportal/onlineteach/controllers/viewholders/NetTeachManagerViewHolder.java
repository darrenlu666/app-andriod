package com.codyy.erpsportal.onlineteach.controllers.viewholders;

import android.view.View;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.onlineteach.models.entities.NetTeach;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.Date;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 频道页-圈组-item
 * Created by poe on 16-1-15.
 */
public class NetTeachManagerViewHolder extends BaseRecyclerViewHolder<NetTeach> {
    private static final String TAG = "NetTeachManagerViewHolder";
    /**
     * 区内课程管理
     */
    public static final int ITEM_TYPE_MANAGER = 0x01 ;
    /**
     * 本校课程原理
     */
    public static final int ITEM_TYPE_SCHOOL = 0x02 ;
    /**
     * 我的课程
     */
    public static final int ITEM_TYPE_MY = 0x03;
    /**
     * 我创建的
     */
    public static final int ITEM_TYPE_MY_CREATE = 0x04;

    @Bind(R.id.sdv_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_title)TextView mTitleTextView;
    @Bind(R.id.tv_teacher)TextView mTeacherTextView;
    @Bind(R.id.tv_subject)TextView mSubjectTextView;
    @Bind(R.id.tv_create_time)TextView mCreateTimeTv;
    @Bind(R.id.tv_teacher_desc)TextView mMasterTeacherTitleTv;//标题－主讲教师

    public NetTeachManagerViewHolder(View itemView ) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        mMasterTeacherTitleTv.setText(Titles.sMasterTeacher);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_net_teach;
    }

    @Override
    public void setData(int position , NetTeach data) {
        Cog.i(TAG , "set Data position : " +data.getTitle()+"::"+ position);
        mCurrentPosition    =   position;
        mData   =   data;

        switch (data.getStatus()){
            case "INIT"://未开始
                mSimpleDraweeView.setImageResource(R.drawable.unstart);
                break;
            case "PROGRESS"://进行中
                mSimpleDraweeView.setImageResource(R.drawable.xxhdpi_assessmenting_);
                break;
            case "END"://已结束
                mSimpleDraweeView.setImageResource(R.drawable.xxhdpi_end);
                break;
            default:
                mSimpleDraweeView.setImageResource(R.drawable.unstart);
                break;
        }

        mTitleTextView.setText(data.getTitle());
        mTeacherTextView.setText(data.getTeacher());
        mSubjectTextView.setText(data.getSubject());
        mCreateTimeTv.setText(DateUtil.dateToString(new Date(data.getStartTime()),DateUtil.DEF_FORMAT));
    }
}
