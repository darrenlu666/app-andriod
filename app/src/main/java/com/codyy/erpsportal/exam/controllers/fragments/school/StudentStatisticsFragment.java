package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.codyy.ScreenUtils;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolStatisticsDetailActivity;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsVhrCreator;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.ExamClass;
import com.codyy.erpsportal.exam.models.entities.school.ExamStudentStatistics;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.commons.widgets.UpOrDownButton;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 班级测试学生统计列表碎片
 *
 * @author eachann
 * @date 2016-01-15
 */
public class StudentStatisticsFragment extends StudentStatisticsLoadFragment<ExamStudentStatistics, StudentStatisticsFragment.ExamStudentStatisticsViewHolder> {

    private static final String TAG = StudentStatisticsFragment.class.getSimpleName();
    private PopupWindow mPopWindow = null;
    private ClassAdapter mClassListAdapter;
    private ListView mListView;

    @Override
    protected void extraInitViewsStyles() {
        super.extraInitViewsStyles();
        mList = getArguments().getParcelableArrayList("ARG_LIST");
        mClassId = mList.get(0).getClassRoomId();
        mUpOrDwonClass.setText(mList.get(0).getClassRoomName());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mUpOrDwonClass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击班级，弹出班级列表PopWindow，并可选择并添加点击事件
                View view = getActivity().getLayoutInflater().inflate(R.layout.class_room_select_class, null);
                view.findViewById(R.id.iamge_1).setVisibility(View.GONE);
                mListView = (ListView) view.findViewById(R.id.class_room_listview);
                mClassListAdapter = new ClassAdapter(getActivity(), mList);
                mListView.setAdapter(mClassListAdapter);

                int num = mList.size() > 3 ? 3 : mList.size();
                mPopWindow = new PopupWindow(view, ScreenUtils.getScreenWidth(getActivity()), num * UIUtils.dip2px(getActivity(), 48), true);//mList.size()
                mPopWindow.setTouchable(true);
                mPopWindow.setOutsideTouchable(true);
                mPopWindow.setBackgroundDrawable((new ColorDrawable(Color.parseColor("#00000000"))));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mClassId = mList.get(position).getClassRoomId();
                        mUpOrDwonClass.setText(mList.get(position).getClassRoomName());
                        getData();
                        mPopWindow.dismiss();
                    }
                });
                mPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, UIUtils.dip2px(getActivity(), 48));
            }
        });
        mUdName.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdName.setChecked(true);
                    mUdName.setUp(true);
                    mUdScore.setInitView();
                    mUdRate.setInitView();
                    mUdCnt.setInitView();
                    mNameSort = "1";
                    mScoreSort = "";
                    mNumSort = "";
                    mRightSort = "";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdName.setChecked(true);
                mUdScore.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mNameSort = "1";
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "";
                getData();
            }

            @Override
            public void onDown() {
                mUdName.setChecked(true);
                mUdScore.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "";
                mNameSort = "0";
                getData();
            }
        });
        mUdScore.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdScore.setChecked(true);
                    mUdScore.setUp(true);
                    mUdName.setInitView();
                    mUdRate.setInitView();
                    mUdCnt.setInitView();
                    mNameSort = "";
                    mScoreSort = "1";
                    mNumSort = "";
                    mRightSort = "";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdScore.setChecked(true);
                mUdName.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "1";
                mNumSort = "";
                mRightSort = "";
                getData();
            }

            @Override
            public void onDown() {
                mUdScore.setChecked(true);
                mUdName.setInitView();
                mUdRate.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "0";
                mNumSort = "";
                mRightSort = "";
                getData();
            }
        });
        mUdRate.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdRate.setChecked(true);
                    mUdRate.setUp(true);
                    mUdName.setInitView();
                    mUdScore.setInitView();
                    mUdCnt.setInitView();
                    mNameSort = "";
                    mScoreSort = "";
                    mNumSort = "";
                    mRightSort = "1";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdRate.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "1";
                getData();
            }

            @Override
            public void onDown() {
                mUdRate.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdCnt.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "";
                mRightSort = "0";
                getData();
            }
        });
        mUdCnt.setStateChangedListener(new UpOrDownButton.OnStateChangedListener() {
            @Override
            public void onSelected(boolean isUp) {
                if (!isUp) {
                    mUdCnt.setChecked(true);
                    mUdCnt.setUp(true);
                    mUdName.setInitView();
                    mUdScore.setInitView();
                    mUdRate.setInitView();
                    mNameSort = "";
                    mScoreSort = "";
                    mNumSort = "1";
                    mRightSort = "";
                    getData();
                }
            }

            @Override
            public void onUp() {
                mUdCnt.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdRate.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "1";
                mRightSort = "";
                getData();
            }

            @Override
            public void onDown() {
                mUdCnt.setChecked(true);
                mUdName.setInitView();
                mUdScore.setInitView();
                mUdRate.setInitView();
                mNameSort = "";
                mScoreSort = "";
                mNumSort = "0";
                mRightSort = "";
                getData();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AbsVhrCreator<ExamStudentStatisticsViewHolder> newViewHolderCreator() {
        return new AbsVhrCreator<ExamStudentStatisticsViewHolder>() {
            @Override
            protected int obtainLayoutId() {
                return R.layout.item_exam_student_statistics;
            }

            @Override
            protected ExamStudentStatisticsViewHolder doCreate(View view) {
                return new ExamStudentStatisticsViewHolder(view, getArguments().getString("examTaskId"));
            }
        };
    }

    @Override
    protected String getUrl() {
        return URLConfig.GET_STUDENT_STATISTIC;
    }

    private List<ExamClass> mList;
    private String mClassId;

    @Override
    protected List<ExamStudentStatistics> getList(JSONObject response) {
        List<ExamStudentStatistics> list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ExamStudentStatistics statistics = new ExamStudentStatistics();
                statistics.setExamResultId(object.isNull("examResultId") ? "" : object.getString("examResultId"));
                statistics.setBaseUserId(object.isNull("baseUserId") ? "" : object.getString("baseUserId"));
                statistics.setBaseUserName(object.isNull("baseUserName") ? "" : object.getString("baseUserName"));
                statistics.setHeadUrl(object.isNull("headUrl") ? "" : object.getString("headUrl"));
                statistics.setScore(object.isNull("score") ? "0" : object.getString("score"));
                statistics.setAnswerCount(object.isNull("answerCount") ? "0" : object.getString("answerCount"));
                statistics.setTotalCount(object.isNull("totalCount") ? "0" : object.getString("totalCount"));
                statistics.setRightRate(object.isNull("rightRate") ? "0" : object.getString("rightRate"));
                list.add(statistics);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private String mNameSort = "";
    private String mScoreSort = "";
    private String mNumSort = "";
    private String mRightSort = "";

    @Override
    protected void addParams(Map<String, String> map) {
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        map.put("examTaskId", getArguments().getString("examTaskId"));
        map.put("classId", mClassId);
    }

    private void getData() {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("examTaskId", getArguments().getString("examTaskId"));
        addParam("classId", mClassId);
        addParam("nameSort", mNameSort);
        addParam("scoreSort", mScoreSort);
        addParam("numSort", mNumSort);
        addParam("rightSort", mRightSort);
        loadData(true);
    }

    public static class ExamStudentStatisticsViewHolder extends RecyclerViewHolder<ExamStudentStatistics> {

        private SimpleDraweeView headIconSDV;

        private TextView nameTV;

        private TextView answerCountsTv;
        private TextView scoreTv;
        private TextView rateTv;
        private View container;
        private Context mContext;
        private String mExamTaskId;

        public ExamStudentStatisticsViewHolder(View itemView, String examTaskId) {
            super(itemView);
            this.mContext = itemView.getContext();
            this.mExamTaskId = examTaskId;
        }

        @Override
        public void mapFromView(View view) {
            container = view;
            headIconSDV = (SimpleDraweeView) view.findViewById(R.id.sdv_student_head_icon);
            nameTV = (TextView) view.findViewById(R.id.tv_exam_student_name);
            answerCountsTv = (TextView) view.findViewById(R.id.tv_exam_student_answer_counts);
            scoreTv = (TextView) view.findViewById(R.id.tv_exam_student_score);
            rateTv = (TextView) view.findViewById(R.id.tv_exam_student_rate);
        }

        @Override
        public void setDataToView(final ExamStudentStatistics data) {
            headIconSDV.setImageURI(Uri.parse(data.getHeadUrl()));
            nameTV.setText(data.getBaseUserName());
            SpannableStringBuilder builder = new SpannableStringBuilder(mContext.getString(R.string.exam_answer_cnt, data.getAnswerCount(), data.getTotalCount()));
            builder.setSpan(new ForegroundColorSpan(Color.RED), 4, 4 + data.getAnswerCount().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            answerCountsTv.setText(builder);
            scoreTv.setText(mContext.getString(R.string.exam_answer_score, data.getScore()));
            rateTv.setText(mContext.getString(R.string.exam_answer_rate, data.getRightRate()));
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = mContext.getString(R.string.exam_statistics_detail_title, data.getBaseUserName(), data.getScore());
                    SchoolStatisticsDetailActivity.startActivity(mContext, title, mExamTaskId, data.getBaseUserId());
                }
            });
        }
    }

    /**
     * 班级列表listview的适配器
     */
    class ClassAdapter extends BaseAdapter {
        private Context mContext;
        private List<ExamClass> mList;

        public ClassAdapter(Context context, List<ExamClass> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(mContext, 50)));
            textView.setGravity(Gravity.CENTER);
            textView.setText(mList.get(position).getClassRoomName());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            if (textView.getText().toString().equals(mUpOrDwonClass.getText().toString())) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                textView.setTextColor(Color.parseColor("#707070"));
            }
            return textView;
        }
    }
}
