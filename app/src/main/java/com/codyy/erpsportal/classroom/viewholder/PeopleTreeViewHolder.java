package com.codyy.erpsportal.classroom.viewholder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.Watcher;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.facebook.drawee.view.SimpleDraweeView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 17-3-13.
 */

public class PeopleTreeViewHolder extends BaseRecyclerViewHolder<Watcher> {

    @Bind(R.id.sdv_group_pic)
    SimpleDraweeView mSdvGroupPic;
    @Bind(R.id.name_tv)
    TextView mNameTv;
    @Bind(R.id.role_tv)
    TextView mRoleTv;
    @Bind(R.id.tv_area)
    TextView mTvArea;
    @Bind(R.id.grade_class_tv)
    TextView mGradeClassTv;
    @Bind(R.id.rlt_container)
    RelativeLayout mRltContainer;
    @Bind(R.id.area_tv)
    TextView mAreaTv;

    public PeopleTreeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_custom_living_people_tree;
    }

    @Override
    public void setData(int position, Watcher data) throws Throwable {
        if(null != data){
            ImageFetcher.getInstance(mSdvGroupPic.getContext()).fetchSmall(mSdvGroupPic,data.getHeadPic());
            mNameTv.setText(data.getRealName());
            mRoleTv.setText(data.getUserTypeName());
            //area+school
            StringBuilder jpsb = new StringBuilder("");
            if(null != data.getAreaPath()){
                String path = data.getAreaPath();
                if(path.contains("-")){
                    path = path.substring(path.lastIndexOf("-")+1);
                }
                jpsb.append(path);
                if(!UserInfo.USER_TYPE_AREA_USER.equals(data.getUserTypeName())){
                    jpsb.append("-");
                }
            }
            if(null != data.getSchoolName()&&!UserInfo.USER_TYPE_AREA_USER.equals(data.getUserTypeName())){
                jpsb.append(data.getSchoolName());
            }
            mAreaTv.setText(jpsb.toString());
            mGradeClassTv.setText(data.getBaseClassName());

            switch (data.getUserTypeName()){
                case  UserInfo.USER_TYPE_AREA_USER:
                    mAreaTv.setVisibility(View.GONE);
                    mGradeClassTv.setVisibility(View.GONE);
                    break;
                case UserInfo.USER_TYPE_SCHOOL_USER:
                case UserInfo.USER_TYPE_TEACHER:
                    mAreaTv.setVisibility(View.VISIBLE);
                    mGradeClassTv.setVisibility(View.GONE);
                    break;
                case UserInfo.USER_TYPE_STUDENT:
                    mAreaTv.setVisibility(View.VISIBLE);
                    mGradeClassTv.setVisibility(View.VISIBLE);
                    break;
                case UserInfo.USER_TYPE_PARENT:
                    mAreaTv.setVisibility(View.VISIBLE);
                    mGradeClassTv.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
