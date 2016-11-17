package com.codyy.erpsportal.tutorship.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.OnFilterObserver;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.parsers.JsonParser;
import com.codyy.erpsportal.tutorship.controllers.activities.TutorshipActivity;
import com.codyy.erpsportal.tutorship.controllers.activities.TutorshipDetailsActivity;
import com.codyy.erpsportal.tutorship.controllers.fragments.TutorshipListFragment.TutorshipViewHolder;
import com.codyy.erpsportal.tutorship.models.entities.Tutorship;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 辅导列表碎片
 * Created by gujiajia on 2015/12/18.
 */
public class TutorshipListFragment extends LoadMoreFragment<Tutorship, TutorshipViewHolder> implements OnFilterObserver {

    private static final String TAG = "TutorshipListFragment";

    public static final String ARG_TYPE = "arg_type";

    public static final String TYPE_OPEN = "OPEN";

    public static final String TYPE_LISTEN = "LISTEN";

    private String mType;

    private UserInfo mUserInfo;

    private DateTimeFormatter mDateTimeFormatter;

    public TutorshipListFragment() {
        mDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    protected ViewHolderCreator<TutorshipViewHolder> newViewHolderCreator() {
        return new ViewHolderCreator<TutorshipViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_tutorship;
            }

            @Override
            protected TutorshipViewHolder doCreate(View view) {
                return new TutorshipViewHolder(view);
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_TUTORSHIP_LIST;
    }

    @Override
    protected List<Tutorship> getList(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("list");
        JsonParser<Tutorship> jsonParser = new JsonParser<Tutorship>() {
            @Override
            public Tutorship parse(JSONObject jsonObject) {
                Tutorship tutorship = new Tutorship();
                tutorship.setId(jsonObject.optString("meetingId"));
                tutorship.setSpeakerName(jsonObject.optString("mainSpeakerUserName"));
                tutorship.setTitle(jsonObject.optString("meetingTitle"));
                long time = jsonObject.optLong("beginTime");
                tutorship.setStartTime(mDateTimeFormatter.print(time));
                tutorship.setStatus(jsonObject.optString("status"));
                tutorship.setSubjectName(jsonObject.optString("baseSubjectName"));
                return tutorship;
            }
        };
        return jsonParser.parseArray(jsonArray);
    }

    @Override
    protected void addParams(Map<String, String> map) {
        addParam("uuid", mUserInfo.getUuid());
        if (!TextUtils.isEmpty(mType)) {
            addParam("listType", mType);
        }
    }

    public static TutorshipListFragment newInstance(String type) {
        TutorshipListFragment fragment = new TutorshipListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onFilterConfirmed(Map<String, String> params) {
        if (params != null) {
            updateParamsBaseOnMap(params, "classLevelId");
            updateParamsBaseOnMap(params, "subjectId");
            updateParamsBaseOnMap(params, "status");
            loadData(true);
        }
    }

    public static class TutorshipViewHolder extends RecyclerViewHolder<Tutorship> {

        private Context context;

        private TextView titleTv;

        private ImageView statusIv;

        private TextView speakerNameIv;

        private TextView subjectNameTv;

        private TextView startTimeTv;

        private View container;

        public TutorshipViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void mapFromView(View view) {
            context = view.getContext();
            container = view;
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            statusIv = (ImageView) view.findViewById(R.id.iv_status);
            speakerNameIv = (TextView) view.findViewById(R.id.tv_grade_name);
            subjectNameTv = (TextView) view.findViewById(R.id.tv_duration);
            startTimeTv = (TextView) view.findViewById(R.id.tv_create_time);
        }

        @Override
        public void setDataToView(final Tutorship data) {
            titleTv.setText(data.getTitle());
            if (Tutorship.STATUS_INIT.equals(data.getStatus())) {
                statusIv.setImageResource(R.drawable.tutorship_status_init);
            } else if (Tutorship.STATUS_PROGRESS.equals(data.getStatus())) {
                statusIv.setImageResource(R.drawable.tutorship_status_progress);
            } else {
                statusIv.setImageResource(R.drawable.tutorship_status_end);
            }
//            speakerNameIv.setText(context.getString(R.string.main_teacher_is, data.getSpeakerName()));
            speakerNameIv.setText(Titles.sMasterTeacher + " " + data.getSpeakerName());
            subjectNameTv.setText(data.getSubjectName());
            startTimeTv.setText(context.getString(R.string.open_lesson_time_is, data.getStartTime()));
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cog.d(TAG, "onTutorshipItemClick");
                    TutorshipActivity activity = (TutorshipActivity) v.getContext();
                    TutorshipDetailsActivity.start(activity, activity.getUserInfo(), data.getId());
                }
            });
        }
    }

}
