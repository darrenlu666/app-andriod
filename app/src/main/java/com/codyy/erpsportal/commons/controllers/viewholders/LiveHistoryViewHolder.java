package com.codyy.erpsportal.commons.controllers.viewholders;

import android.content.Context;
import android.net.Uri;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.models.entities.LiveClassListModel;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.StringUtils;

/**
 * Created by caixingming on 2015/5/25.
 * 修改 节次为开课时间
 */
public class LiveHistoryViewHolder extends LiveViewHolder {

    @Override
    public int obtainLayoutId() {
        return R.layout.item_live_lesson_history;
    }

    @Override
    public void setDataToView(LiveClassListModel data, Context context) {

        mSchoolTextView.setText(data.getSchool());
        mSubjectTextView.setText(data.getSubject());
        mGradeTextView.setText(data.getGrade());
        mTeacherTextView.setText(data.getTeacher());
        mIndexTextView.setText(data.getRealBeginTime());
        //set image
        String icon = URLConfig.IMAGE_URL +  StringUtils.replaceSmallPic(data.getIcon());//UiUtils.getSmallImage(data.getIcon());
        Cog.d("icon:", icon);
        Uri uri = Uri.parse(icon);
        mImageView.setImageURI(uri);
    }
}