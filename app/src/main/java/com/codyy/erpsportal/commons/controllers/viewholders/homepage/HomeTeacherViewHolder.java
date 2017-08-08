package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.mainpage.GreatTeacher;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 17-8-7.
 */

public class HomeTeacherViewHolder extends BaseRecyclerViewHolder<GreatTeacher> {


    @Bind(R.id.famousteacher_image)
    SimpleDraweeView mFamousteacherImage;
    @Bind(R.id.famousteacher_name)
    TextView mFamousteacherName;
    @Bind(R.id.famousteacher_grade)
    TextView mFamousteacherGrade;

    public HomeTeacherViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.famousteacher_layout_item;
    }

    @Override
    public void setData(int position, GreatTeacher data) throws Throwable {
        ImageFetcher.getInstance(mFamousteacherImage.getContext()).fetchSmall(mFamousteacherImage, data.getHeadPic());
        mFamousteacherName.setText(data.getRealName());
        mFamousteacherGrade.setText(data.getSubjectName());
    }

}
