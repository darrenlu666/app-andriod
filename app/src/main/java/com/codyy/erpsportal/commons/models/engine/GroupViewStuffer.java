package com.codyy.erpsportal.commons.models.engine;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.groups.controllers.activities.GroupSpaceActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResGroup;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;

/**
 * 首页圈组项填入者
 * Created by gujiajia on 2016/8/19.
 */
@ItemLayoutId(R.layout.item_channel_group)
public class GroupViewStuffer implements ViewStuffer<MainResGroup> {

    private WeakReference<Activity> mActivityRef;

    public GroupViewStuffer(Activity activity) {
        this.mActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void onStuffView(View view, final MainResGroup group) {
        SimpleDraweeView groupIconDv = (SimpleDraweeView) view.findViewById(R.id.sdv_group_pic);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_group_name);
        TextView creatorTv = (TextView) view.findViewById(R.id.tv_creator);
        TextView memberTv = (TextView) view.findViewById(R.id.tv_member);
        TextView subjectTv = (TextView) view.findViewById(R.id.tv_subject_or_category);
        ImageFetcher.getInstance(view).fetchSmall(groupIconDv, group.getPic());
        titleTv.setText(group.getGroupName());
        creatorTv.setText(group.getRealName());
        String limit = group.getLimited()==0?"不限":(group.getLimited()+" 人");
        String number = group.getMemberCount()+" / "+limit;
        memberTv.setText(number);
        if (!TextUtils.isEmpty(group.getCategoryName())) {
            subjectTv.setText(group.getCategoryName());
        } else {
            subjectTv.setText(group.getClasslevelName() + "/" + group.getSubjectName());
        }
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = mActivityRef.get();
                if (activity == null) return;
                GroupSpaceActivity.start( activity, Titles.sHomepageGroup, group.getGroupId(),
                        CategoryFilterFragment.CATEGORY_TYPE_DOOR);
            }
        });
    }
}
