package com.codyy.erpsportal.tutorship.controllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.tutorship.controllers.activities.TutorialTestActivity;
import com.codyy.erpsportal.tutorship.controllers.fragments.TutorialsTestsFragment.TutorialTestViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.tutorship.models.entities.TutorialTest;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 随堂测验列表
 * Created by gujiajia on 2015/12/24.
 */
public class TutorialsTestsFragment extends LoadMoreFragment<TutorialTest, TutorialTestViewHolder> implements OnFilterObserver {

    private final static String TAG = "TutorialsTestsFragment";

    private UserInfo mUserInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
        }
        if (mUserInfo == null) mUserInfo = UserInfoKeeper.obtainUserInfo();
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected ViewHolderCreator<TutorialTestViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<TutorialTestViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_tutorial_test;
            }

            @Override
            protected TutorialTestViewHolder doCreate(View view) {
                return new TutorialTestViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_TUTORIAL_TEST_LIST;
    }

    @Override
    protected void addParams(Map<String, String> map) {
        map.put("uuid", mUserInfo.getUuid());
    }

    @Override
    protected List<TutorialTest> getList(JSONObject response) {
        JsonParser<TutorialTest> jsonParser = new JsonParser<TutorialTest>() {
            @Override
            public TutorialTest parse(JSONObject jsonObject) {
                TutorialTest tutorialTest = new TutorialTest();
                tutorialTest.setId(jsonObject.optString("meetExamId"));
                tutorialTest.setTitle(jsonObject.optString("examName"));
                tutorialTest.setDuration(jsonObject.optInt("duration"));
                tutorialTest.setGradeName(jsonObject.optString("baseClasslevelName"));
                tutorialTest.setSubjectIcon(optStrOrNull(jsonObject, "subjectPic"));
                tutorialTest.setCreateTime(jsonObject.optString("createTimeStr"));
                return tutorialTest;
            }
        };
        return jsonParser.parseArray(response.optJSONArray("list"));
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        Cog.d(TAG, "onFilterConfirmed params=", params);
        if (params != null) {
            updateParamsBaseOnMap(params, "subjectId");
            updateParamsBaseOnMap(params, "classLevelId", "classlevelId");
            loadData(true);
        }
    }

    public static class TutorialTestViewHolder extends RecyclerViewHolder<TutorialTest> {

        private TextView titleTv;

        private SimpleDraweeView subjectDv;

        private TextView gradeNameTv;

        private TextView durationTv;

        private TextView createTimeTv;

        public TutorialTestViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            subjectDv = (SimpleDraweeView) view.findViewById(R.id.dv_subject);
            gradeNameTv = (TextView) view.findViewById(R.id.tv_grade_name);
            durationTv = (TextView) view.findViewById(R.id.tv_duration);
            createTimeTv = (TextView) view.findViewById(R.id.tv_create_time);
        }

        @Override
        public void setDataToView(final TutorialTest data) {
            Context context = itemView.getContext();
            titleTv.setText(data.getTitle());
            if (!TextUtils.isEmpty(data.getSubjectIcon())) {
                ImageFetcher.getInstance(context).fetchImage(subjectDv, URLConfig.IMAGE_URL + data.getSubjectIcon());
            }
            gradeNameTv.setText(context.getString(R.string.grade_lb, data.getGradeName()));
            durationTv.setText(context.getString(R.string.duration_lb, data.getDuration()));
            createTimeTv.setText(context.getString(R.string.create_time_lb, data.getCreateTime()));
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TutorialTestActivity.start((Activity)v.getContext(), data);
                }
            });
        }
    }
}
