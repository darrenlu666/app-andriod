package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 2015/8/12.
 */
public class FilterGradeSubject extends Fragment implements View.OnClickListener {

    public final static int HAS_STATUS = 0x01; //有状态筛选

    public final static int NO_STATUS = 0x02; //无状态筛选

    private final static String TAG = "FilterGradeSubject";
    /**
     * 获取年级
     */
    private final static int GET_GRADE = 0x001;
    /**
     * 获取学科
     */
    private final static int GET_SUBJECT = 0x002;
    private ListView mLeftListView;
    /**
     * 网络请求
     */
    private RequestSender mSender;

    private List<GradeMode> mGrade;
    private List<GradeMode> mSubject;
    private List<GradeMode> mStatus;

    private TextView mTextViewGrade;

    private TextView mTextViewSubject;

    private TextView mTextViewStatus;

    private GradeMode mGradeMode;

    private GradeMode mSubjectMode;

    private GradeMode mStatusMode;
    private LeftAdapter mGradeAdapter, mSubjectAdapter, mStatusAdapter;
    private View view;//缓存Fragment view
    private UserInfo mUserInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSender = new RequestSender(getActivity());
        mGrade = new ArrayList<>();
        mSubject = new ArrayList<>();
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mSubjectAdapter = new LeftAdapter(mSubject);
        mGradeAdapter = new LeftAdapter(mGrade);
        httpConnect(URLConfig.GET_GRADE_UNLOGIN, null, GET_GRADE);
        httpConnect(URLConfig.GET_SUBJECT_UNLOGIN, null, GET_SUBJECT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View view = inflater.inflate(R.layout.fragment_filter_grade_subject, container, false);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_filter_grade_subject, null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initStatusData();
        mLeftListView = (ListView) view.findViewById(R.id.fragment_filter_grade_subject_leftlistview);
        view.findViewById(R.id.fragment_filter_grade_subject_grade).setOnClickListener(this);
        view.findViewById(R.id.fragment_filter_grade_subject_subject).setOnClickListener(this);

        if (getArguments().getInt("filter_type") == HAS_STATUS) {
            view.findViewById(R.id.fragment_filter_grade_subject_status).setOnClickListener(this);
        } else {
            view.findViewById(R.id.fragment_filter_grade_subject_status).setVisibility(View.GONE);
        }
        mLeftListView.setAdapter(mGradeAdapter);
        mTextViewGrade = (TextView) view.findViewById(R.id.fragment_filter_text_grade);
        mTextViewSubject = (TextView) view.findViewById(R.id.fragment_filter_text_subject);
        mTextViewStatus = (TextView) view.findViewById(R.id.fragment_filter_text_status);
        mLeftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LeftAdapter adapter = (LeftAdapter) mLeftListView.getAdapter();
                GradeMode gradeMode = adapter.getItem(position);
                switch (gradeMode.getType()) {
                    case GradeMode.GRADE:
                        mTextViewGrade.setText(gradeMode.getClassName());
                        mGradeMode = gradeMode;
                        HashMap<String, String> data = new HashMap<>();
                        data.put("classlevelId", mGradeMode.getClassLevelId());
                        httpConnect(URLConfig.GET_SUBJECT_UNLOGIN, data, GET_SUBJECT);
                        if (mSubjectMode != null) {
                            mSubjectMode.setType(GradeMode.SUBJECT);
                            mSubjectMode.setClassName("全部");
                            mSubjectMode.setClassLevelId("");
                        }
                        mTextViewSubject.setText("全部");
                        break;
                    case GradeMode.SUBJECT:
                        mTextViewSubject.setText(gradeMode.getClassName());
                        mSubjectMode = gradeMode;
                        break;
                    case GradeMode.STATUS:
                        mTextViewStatus.setText(gradeMode.getClassName());
                        mStatusMode = gradeMode;
                        break;
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_filter_grade_subject_grade://点击年级
                if (mGrade.size() == 0) {
                    httpConnect(URLConfig.GET_GRADE_UNLOGIN, null, GET_GRADE);
                } else {
                    if (mGradeAdapter != null) {
                        mLeftListView.setAdapter(mGradeAdapter);
                    }
                }
                break;
            case R.id.fragment_filter_grade_subject_subject://点击学科
                if (mSubject.size() == 0) {

                    httpConnect(URLConfig.GET_SUBJECT_UNLOGIN, null, GET_SUBJECT);
                } else {
                    mLeftListView.setAdapter(mSubjectAdapter);
                }
                break;
            case R.id.fragment_filter_grade_subject_status://点击状态
                if (mStatusAdapter == null) {
                    mStatusAdapter = new LeftAdapter(mStatus);
                }
                mLeftListView.setAdapter(mStatusAdapter);
                break;
            default:
                break;
        }
    }

    /**
     * 网络请求
     *
     * @param url
     * @param data
     * @param msg
     */
    private void httpConnect(String url, HashMap<String, String> data, final int msg) {
        Map<String, String> map = new HashMap<>(1);
        if (mUserInfo.isSchool()||mUserInfo.isTeacher()||mUserInfo.isParent()||mUserInfo.isStudent()) {
            map.put("schoolId", mUserInfo.getSchoolId());
        } else if (mUserInfo.isArea()) {
            map.put("areaId", mUserInfo.getBaseAreaId());
        }
        /*if (TextUtils.isEmpty(UserInfoKeeper.obtainUserInfo().getSchoolId())) {
            map.put("areaId", UserInfoKeeper.obtainUserInfo().getBaseAreaId());
        } else {
            map.put("schoolId", UserInfoKeeper.obtainUserInfo().getSchoolId());
        }*/
        mSender.sendRequest(new RequestSender.RequestData(url, map, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, response + "");
                switch (msg) {
                    case GET_GRADE:
                        if ("success".equals(response.optString("result"))) {
                            mGrade.clear();
                            GradeMode gradeAll = new GradeMode();
                            gradeAll.setClassName("全部");
                            gradeAll.setType(GradeMode.GRADE);
                            mGrade.add(gradeAll);
                            JSONArray array = response.optJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                GradeMode gradeMode = new GradeMode();
                                JSONObject object = array.optJSONObject(i);
                                gradeMode.setClassName(object.optString("name"));
                                gradeMode.setClassLevelId(object.optString("id"));
                                gradeMode.setType(GradeMode.GRADE);
                                mGrade.add(gradeMode);
                            }
                            if (mGradeAdapter != null) {
                                mLeftListView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mGradeAdapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        }
                        break;
                    case GET_SUBJECT:
                        if ("success".equals(response.optString("result"))) {
                            mSubject.clear();
                            GradeMode subjectAll = new GradeMode();
                            subjectAll.setClassName("全部");
                            subjectAll.setType(GradeMode.SUBJECT);
                            mSubject.add(subjectAll);
                            JSONArray array = response.optJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                GradeMode gradeMode = new GradeMode();
                                JSONObject object = array.optJSONObject(i);
                                gradeMode.setType(GradeMode.SUBJECT);
                                gradeMode.setClassName(object.optString("name"));
                                gradeMode.setClassLevelId(object.optString("id"));
                                mSubject.add(gradeMode);
                            }
                            if (mSubjectAdapter != null) {
                                mLeftListView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSubjectAdapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(Throwable error) {
                Cog.e(TAG, "onErrorResponse error=", error);
            }
        }));
    }

    /**
     * 获取学科
     *
     * @return
     */
    public String getmGradeID() {
        if (mGradeMode != null) {
            return mGradeMode.getClassLevelId();
        }
        return null;
    }


    public String getmSubjectID() {
        if (mSubjectMode != null) {
            return mSubjectMode.getClassLevelId();
        }
        return null;
    }

    public String getmStatusID() {
        if (mStatusMode != null) {
            return mStatusMode.getClassLevelId();
        }
        return null;
    }


    class GradeMode {

        final static int GRADE = 0x01;
        final static int SUBJECT = 0x002;
        final static int STATUS = 0x003;
        /**
         * className : 一年级
         * classLevelId : 4defd613c12c4c26b21e79a3c6178fcc
         */
        private String className;
        private String classLevelId;

        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public void setClassLevelId(String classLevelId) {
            this.classLevelId = classLevelId;
        }

        public String getClassName() {
            return className;
        }

        public String getClassLevelId() {
            return classLevelId;
        }
    }


    class LeftAdapter extends BaseAdapter {

        List<GradeMode> gradeModes;

        LeftAdapter(List<GradeMode> gradeModes) {
            this.gradeModes = gradeModes;
        }

        @Override
        public int getCount() {
            return gradeModes.size();
        }

        @Override
        public GradeMode getItem(int position) {
            return gradeModes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return gradeModes.get(position).getType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GradeMode gradeMode = gradeModes.get(position);
            TextView textView = new TextView(getActivity());
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setMinHeight(UIUtils.dip2px(getActivity(), 48));
            textView.setGravity(Gravity.CENTER);
            if (gradeMode.getType() == GradeMode.SUBJECT && gradeMode.getClassName().equals(mTextViewSubject.getText().toString()) || gradeMode.getType() == GradeMode.GRADE && gradeMode.getClassName().equals(mTextViewGrade.getText().toString())) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                textView.setTextColor(getResources().getColor(R.color.personal_text_color));
            }
            textView.setTextSize(15);
            textView.setText(gradeMode.getClassName());
            return textView;
        }
    }

    private void initStatusData() {
        mStatus = new ArrayList<>();
        GradeMode status0 = new GradeMode();
        status0.setClassName("全部");
        status0.setType(GradeMode.STATUS);
        mStatus.add(status0);
        GradeMode status1 = new GradeMode();
        status1.setClassName("未开始");
        status1.setType(GradeMode.STATUS);
        status1.setClassLevelId("INIT");
        mStatus.add(status1);
        GradeMode status2 = new GradeMode();
        status2.setClassName("进行中");
        status2.setType(GradeMode.STATUS);
        status2.setClassLevelId("PROGRESS");
        mStatus.add(status2);
        GradeMode status3 = new GradeMode();
        status3.setClassName("已结束");
        status3.setType(GradeMode.STATUS);
        status3.setClassLevelId("END");
        mStatus.add(status3);
    }

    @Override
    public void onDestroy() {
        mSender.stop();
        super.onDestroy();
    }
}
