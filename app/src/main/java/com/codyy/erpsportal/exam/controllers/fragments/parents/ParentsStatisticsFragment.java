package com.codyy.erpsportal.exam.controllers.fragments.parents;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.controllers.activities.parent.ParentViewCheckedActivity;
import com.codyy.erpsportal.exam.widgets.chartview.ChartView;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 家长-成绩分析
 * Created by eachann on 2016/3/6.
 */
public class ParentsStatisticsFragment extends Fragment {
    private static final String TAG = ParentsStatisticsFragment.class.getSimpleName();
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.cb_acount)
    CheckBox mCheckBoxAcount;
    @Bind(R.id.cb_bcount)
    CheckBox mCheckBoxBcount;
    @Bind(R.id.cb_ccount)
    CheckBox mCheckBoxCcount;
    @Bind(R.id.cb_dcount)
    CheckBox mCheckBoxDcount;
    @Bind(R.id.cv_chart)
    ChartView mChartView;
    @Bind(R.id.gl_layout)
    GridLayout glLayout;
    @Bind(R.id.v_divider_line1)
    View vDividerLine1;
    private ViewStub mViewStub;
    private TextView mTvName;
    private TextView mTvCreateTime;
    private TextView mTvScore;
    private TextView mTvMaxScore;
    private TextView mTvMinScore;
    private TextView mTvAvgScore;
    private TextView mTvMyScore;
    private TextView mTvId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parent_analysis, container, false);
        ButterKnife.bind(this, rootView);
        mViewStub = (ViewStub) rootView.findViewById(R.id.vs_info);
        mViewStub.inflate();
        mViewStub.setVisibility(View.INVISIBLE);
        mTvId = (TextView) rootView.findViewById(R.id.tv_exam_id);
        RelativeLayout mRlExamInfo = (RelativeLayout) rootView.findViewById(R.id.rl_exam_info);
        mTvName = (TextView) rootView.findViewById(R.id.tv_exam_name);
        mTvCreateTime = (TextView) rootView.findViewById(R.id.tv_exam_create_time);
        mTvScore = (TextView) rootView.findViewById(R.id.tv_exam_score);
        mTvMaxScore = (TextView) rootView.findViewById(R.id.tv_exam_max_score);
        mTvMinScore = (TextView) rootView.findViewById(R.id.tv_exam_min_score);
        mTvAvgScore = (TextView) rootView.findViewById(R.id.tv_exam_avg_score);
        mTvMyScore = (TextView) rootView.findViewById(R.id.tv_exam_my_score);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mChartView.setOnPointClickListener(new ChartView.OnPointClickListener() {
            @Override
            public void onPointClickListener(ChartView.ChartBean bean) {
                getExamInfo(bean);
            }
        });
        mRlExamInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParentViewCheckedActivity.startActivity(getActivity(), mTvName.getText().toString(), mTvId.getText().toString(), "");
            }
        });
        return rootView;
    }

    @OnClick({R.id.cb_acount, R.id.cb_bcount, R.id.cb_ccount, R.id.cb_dcount})
    public void onCheckBox(View v) {
        mViewStub.setVisibility(View.INVISIBLE);
        switch (v.getId()) {
            case R.id.cb_acount:
                if (mCheckBoxAcount.isChecked()) {
                    mChartView.setDataTypeToView(mData, "A");
                    mCheckBoxBcount.setChecked(false);
                    mCheckBoxCcount.setChecked(false);
                    mCheckBoxDcount.setChecked(false);
                } else {
                    mChartView.setDataToView(mData);
                }
                break;
            case R.id.cb_bcount:
                if (mCheckBoxBcount.isChecked()) {
                    mChartView.setDataTypeToView(mData, "B");
                    mCheckBoxAcount.setChecked(false);
                    mCheckBoxCcount.setChecked(false);
                    mCheckBoxDcount.setChecked(false);
                } else {
                    mChartView.setDataToView(mData);
                }
                break;
            case R.id.cb_ccount:
                if (mCheckBoxCcount.isChecked()) {
                    mChartView.setDataTypeToView(mData, "C");
                    mCheckBoxAcount.setChecked(false);
                    mCheckBoxBcount.setChecked(false);
                    mCheckBoxDcount.setChecked(false);
                } else {
                    mChartView.setDataToView(mData);
                }
                break;
            case R.id.cb_dcount:
                if (mCheckBoxDcount.isChecked()) {
                    mChartView.setDataTypeToView(mData, "D");
                    mCheckBoxAcount.setChecked(false);
                    mCheckBoxBcount.setChecked(false);
                    mCheckBoxCcount.setChecked(false);
                } else {
                    mChartView.setDataToView(mData);
                }
                break;
        }
    }

    /**
     * 点击折线图点 获取考试信息
     *
     * @param bean
     */
    private void getExamInfo(ChartView.ChartBean bean) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        params.put("examTaskId", bean.getExamTaskId());
        params.put("examResultId", bean.getExamResultId());
        RequestSender sender = new RequestSender(getActivity());
        sender.sendRequest(new RequestSender.RequestData(URLConfig.GET_PARENT_INFO, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                bindExamData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

            }
        }));
    }

    private void bindExamData(JSONObject response) {
        try {
            if (!response.isNull("data") && mTvAvgScore != null) {
                JSONObject object = response.getJSONObject("data");
                mTvId.setText(object.optString("examTaskId"));
                mTvName.setText(object.optString("examName"));
                mTvCreateTime.setText(object.optString("createTime"));
                mTvScore.setText(getString(R.string.exam_parent_score, object.optString("score")));
                mTvMaxScore.setText(getString(R.string.exam_parent_max_score, object.optString("maxScore")));
                mTvMinScore.setText(getString(R.string.exam_parent_min_score, object.optString("minScore")));
                mTvAvgScore.setText(getString(R.string.exam_parent_avg_score, object.optString("avgScore")));
                mTvMyScore.setText(getString(R.string.exam_parent_my_score, object.optString("myScore")));
                mViewStub.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSubjectData();
    }

    /**
     * 获取学科
     */
    private void getSubjectData() {
        RequestSender requestSender = new RequestSender(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("schoolId", UserInfoKeeper.obtainUserInfo().getSchoolId());
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.ALL_SUBJECTS_LIST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result")) && mChartView != null) {
                    handleJsonItems(response.optJSONArray("list"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

            }
        }));
    }

    private ListAdapter mAdapter;

    private void handleJsonItems(JSONArray jsonArray) {
        List<Choice> choiceList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Choice choice = new Choice();
                choice.setId(object.isNull("id") ? "" : object.optString("id"));
                choice.setTitle(object.isNull("name") ? "" : object.optString("name"));
                choiceList.add(choice);
            }
            mAdapter = new ListAdapter(choiceList, getActivity());
            mAdapter.setOnInViewClickListener(R.id.tv_name, new MMBaseRecyclerViewAdapter.onInternalClickListener<Choice>() {
                @Override
                public void OnClickListener(View parentV, View v, Integer position, Choice values) {
                    mViewStub.setVisibility(View.INVISIBLE);
                    mCheckBoxAcount.setChecked(false);
                    mCheckBoxBcount.setChecked(false);
                    mCheckBoxCcount.setChecked(false);
                    mCheckBoxDcount.setChecked(false);
                    mAdapter.setPosition(position);
                    getChartData(values);
                }

                @Override
                public void OnLongClickListener(View parentV, View v, Integer position, Choice values) {

                }
            });
            mRecyclerView.setAdapter(mAdapter);
            if (choiceList != null && choiceList.size() > 0) {
                getChartData(choiceList.get(0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject mData;

    private void getChartData(Choice choice) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        params.put("userId", UserInfoKeeper.getInstance().getUserInfo().getSelectedChild().getStudentId());
        params.put("subjectId", choice.getId());
        RequestSender sender = new RequestSender(getActivity());
        sender.sendRequest(new RequestSender.RequestData(URLConfig.SOCRE_ANALYZE, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //                    response = new JSONObject("{\"dCount\":6,\"message\":\"成功\",\"result\":\"success\",\"list\":[{\"examTaskId\":\"f8eed104027a4163ab2f0b7e0205e973\",\"examResultId\":\"2CA4026DB11D3F69E0530A32010A2C2A\",\"score\":47.5,\"totalScore\":150,\"time\":1456433757581,\"type\":\"D\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":38,\"totalScore\":100,\"time\":1457295770859,\"type\":\"D\"},{\"examTaskId\":\"f8eed104027a4163ab2f0b7e0205e973\",\"examResultId\":\"2CA4026DB11D3F69E0530A32010A2C2A\",\"score\":67.5,\"totalScore\":150,\"time\":1456433757581,\"type\":\"C\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":88,\"totalScore\":100,\"time\":1457295770859,\"type\":\"B\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":98,\"totalScore\":100,\"time\":1457295770859,\"type\":\"A\"},{\"examTaskId\":\"f8eed104027a4163ab2f0b7e0205e973\",\"examResultId\":\"2CA4026DB11D3F69E0530A32010A2C2A\",\"score\":47.5,\"totalScore\":150,\"time\":1456433757581,\"type\":\"D\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":38,\"totalScore\":100,\"time\":1457295770859,\"type\":\"D\"},{\"examTaskId\":\"f8eed104027a4163ab2f0b7e0205e973\",\"examResultId\":\"2CA4026DB11D3F69E0530A32010A2C2A\",\"score\":67.5,\"totalScore\":150,\"time\":1456433757581,\"type\":\"C\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":88,\"totalScore\":100,\"time\":1457295770859,\"type\":\"B\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":98,\"totalScore\":100,\"time\":1457295770859,\"type\":\"A\"},{\"examTaskId\":\"f8eed104027a4163ab2f0b7e0205e973\",\"examResultId\":\"2CA4026DB11D3F69E0530A32010A2C2A\",\"score\":47.5,\"totalScore\":150,\"time\":1456433757581,\"type\":\"D\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":38,\"totalScore\":100,\"time\":1457295770859,\"type\":\"D\"},{\"examTaskId\":\"f8eed104027a4163ab2f0b7e0205e973\",\"examResultId\":\"2CA4026DB11D3F69E0530A32010A2C2A\",\"score\":67.5,\"totalScore\":150,\"time\":1456433757581,\"type\":\"C\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":88,\"totalScore\":100,\"time\":1457295770859,\"type\":\"B\"},{\"examTaskId\":\"70d28bf51c854c1795869760103d95d0\",\"examResultId\":\"2D6CF81414EA1069E0530A32010A0AD9\",\"score\":98,\"totalScore\":100,\"time\":1457295770859,\"type\":\"A\"}],\"aCount\":3,\"bCount\":3,\"cCount\":3}");
                try {
                    if ("success".equals(response.optString("result")) && response.getJSONArray("list").length() > 0 && mTvAvgScore != null) {
                        glLayout.setVisibility(View.VISIBLE);
                        mChartView.setVisibility(View.VISIBLE);
                        vDividerLine1.setVisibility(View.VISIBLE);
                        mData = response;
                        SpannableString spannableString = new SpannableString(getString(R.string.exam_a_count, response.optInt("aCount")));
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7ECB33")), 2, 3 + String.valueOf(response.optInt("aCount")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mCheckBoxAcount.setText(spannableString);
                        spannableString = new SpannableString(getString(R.string.exam_b_count, response.optInt("bCount")));
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7ECB33")), 2, 3 + String.valueOf(response.optInt("bCount")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mCheckBoxBcount.setText(spannableString);
                        spannableString = new SpannableString(getString(R.string.exam_c_count, response.optInt("cCount")));
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFD9801")), 2, 3 + String.valueOf(response.optInt("cCount")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mCheckBoxCcount.setText(spannableString);
                        spannableString = new SpannableString(getString(R.string.exam_d_count, response.optInt("dCount")));
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFCB3338")), 3, 4 + String.valueOf(response.optInt("dCount")).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mCheckBoxDcount.setText(spannableString);
                        mChartView.setDataToView(response);
                    } else {
                        glLayout.setVisibility(View.INVISIBLE);
                        vDividerLine1.setVisibility(View.INVISIBLE);
                        mChartView.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class ListAdapter extends MMBaseRecyclerViewAdapter<Choice> {
        private int mPosition = 0;

        public ListAdapter(List<Choice> list, Context context) {
            super(list, context);
        }

        public void setPosition(int position) {
            this.mPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            final View view = LayoutInflater.from(context).inflate(R.layout.item_text_view, parent, false);
            return new NormalRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (mPosition == position)
                ((NormalRecyclerViewHolder) holder).tvName.setTextColor(getResources().getColor(R.color.main_color));
            else
                ((NormalRecyclerViewHolder) holder).tvName.setTextColor(getResources().getColor(R.color.exam_normal_color));
            ((NormalRecyclerViewHolder) holder).tvName.setText(list.get(position).getTitle());
        }

        public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            public NormalRecyclerViewHolder(View view) {
                super(view);
                tvName = (TextView) view.findViewById(R.id.tv_name);
            }
        }
    }

}
