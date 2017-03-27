package com.codyy.erpsportal.classroom.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.models.Titles;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专递课堂 直录播课堂 详情 下半部分碎片
 * Created by ldh on 2016/6/29.
 */
public class ClassDetailFragment extends Fragment {

    public static final String ARG_AREA = "area";
    public static final String ARG_CLASS_PERIOD = "classPeriod";
    public static final String ARG_CLASS_TIME = "classTime";
    public static final String ARG_GRADE = "grade";
    public static final String ARG_SCHOOL_NAME = "schoolName";
    public static final String ARG_TEACHER = "teacher";
    public static final String ARG_SUBJECT = "subject";
    public static final String ARG_RECEIVE_SCHOOL_LIST = "receiveSchoolList";
    public static final String ARG_LENGTH = "length";
    public static final String ARG_PLAY_COUNT = "playCount";
    @Bind(R.id.tv_class_room_area_value)
    TextView mTvClassRoomAreaValue;
    @Bind(R.id.tv_class_room_main_info)
    TextView mTvClassRoomMainInfoTitle;
    @Bind(R.id.tv_class_room_receive_info)
    TextView mTvClassRoomReceiveInfoTitle;
    @Bind(R.id.tv_class_room_main_info_value)
    TextView mTvClassRoomMainInfoValue;
    @Bind(R.id.tv_class_room_receive_info_value)
    TextView mTvClassRoomReceiveInfoValue;
    @Bind(R.id.tv_class_room_time_value)
    TextView mTvClassRoomTimeValue;
    @Bind(R.id.tv_class_room_time)
    TextView mTvClassRoomTimesTitle;
    @Bind(R.id.tv_length_value)
    TextView mClassLengthTv;
    @Bind(R.id.tv_play_count_value)
    TextView mPlayCountTv;
    @Bind(R.id.rl_length)
    RelativeLayout mLengthLayout;
    @Bind(R.id.rl_play_count)
    RelativeLayout mPlayCountLayout;

    private String mFrom;
    private String mArea;
    private String mClassPeriod;//节次 直录播课堂中才有
    private String mClassTime;
    private String mGrade;
    private String mSchoolName;
    private String mTeacher;
    private String mSubject;
    private List<String> mReceiveSchoolList;
    private String mLength;
    private int mPlayCount;

    /**
     * 实时直播时使用的
     *
     * @param area
     * @param classPeriod
     * @param classTime
     * @param grade
     * @param schoolName
     * @param teacher
     * @param subject
     * @param receiveSchoolList
     * @return
     */
    public static ClassDetailFragment newInstance(String from, String area, String classPeriod, String classTime, String grade,
                                                  String schoolName, String teacher, String subject, ArrayList<String> receiveSchoolList) {
        Bundle args = new Bundle();
        args.putString(ClassRoomContants.FROM_WHERE_MODEL, from);
        args.putString(ARG_AREA, area);
        args.putString(ARG_CLASS_PERIOD, classPeriod);
        args.putString(ARG_CLASS_TIME, classTime);
        args.putString(ARG_GRADE, grade);
        args.putString(ARG_SCHOOL_NAME, schoolName);
        args.putString(ARG_TEACHER, teacher);
        args.putString(ARG_SUBJECT, subject);
        args.putStringArrayList(ARG_RECEIVE_SCHOOL_LIST, receiveSchoolList);
        ClassDetailFragment fragment = new ClassDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 往期录播时使用的
     *
     * @param area
     * @param classPeriod
     * @param classTime
     * @param grade
     * @param schoolName
     * @param teacher
     * @param subject
     * @param receiveSchoolList
     * @param length
     * @param playCount
     * @return
     */
    public static ClassDetailFragment newInstance(String from, String area, String classPeriod, String classTime, String grade,
                                                  String schoolName, String teacher, String subject,
                                                  ArrayList<String> receiveSchoolList, String length, int playCount) {

        Bundle args = new Bundle();
        args.putString(ClassRoomContants.FROM_WHERE_MODEL, from);
        args.putString(ARG_AREA, area);
        args.putString(ARG_CLASS_PERIOD, classPeriod);
        args.putString(ARG_CLASS_TIME, classTime);
        args.putString(ARG_GRADE, grade);
        args.putString(ARG_SCHOOL_NAME, schoolName);
        args.putString(ARG_TEACHER, teacher);
        args.putString(ARG_SUBJECT, subject);
        args.putStringArrayList(ARG_RECEIVE_SCHOOL_LIST, receiveSchoolList);
        args.putString(ARG_LENGTH, length);
        args.putInt(ARG_PLAY_COUNT, playCount);
        ClassDetailFragment fragment = new ClassDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFrom = getArguments().getString(ClassRoomContants.FROM_WHERE_MODEL);
            mArea = getArguments().getString(ARG_AREA);
            mClassPeriod = getArguments().getString(ARG_CLASS_PERIOD);
            mClassTime = getArguments().getString(ARG_CLASS_TIME);
            mGrade = getArguments().getString(ARG_GRADE);
            mSchoolName = getArguments().getString(ARG_SCHOOL_NAME);
            mTeacher = getArguments().getString(ARG_TEACHER);
            mSubject = getArguments().getString(ARG_SUBJECT);
            mReceiveSchoolList = getArguments().getStringArrayList(ARG_RECEIVE_SCHOOL_LIST);
            mLength = getArguments().getString(ARG_LENGTH);
            mPlayCount = getArguments().getInt(ARG_PLAY_COUNT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_detail, null);
        ButterKnife.bind(this, view);
        addViewData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void addViewData() {
        mTvClassRoomAreaValue.setText(mArea);
        mTvClassRoomMainInfoTitle.setText(Titles.sMaster + ":");
        mTvClassRoomReceiveInfoTitle.setText(Titles.sReceiver + ":");
        mTvClassRoomMainInfoValue.setText(getString(R.string.main_info, mSchoolName, mGrade + "\\", mSubject + "\\", mTeacher));
        mTvClassRoomReceiveInfoValue.setText(mReceiveSchoolList.size() == 0 ? "" : getReceiveSchoolName(mReceiveSchoolList));
        if (mFrom.equals(ClassRoomContants.TYPE_CUSTOM_RECORD) || mFrom.equals(ClassRoomContants.TYPE_LIVE_RECORD)) {
            mLengthLayout.setVisibility(View.VISIBLE);
            mPlayCountLayout.setVisibility(View.VISIBLE);
            mClassLengthTv.setText(getTimeMinite(mLength));
            mPlayCountTv.setText(mPlayCount + "");
        }
        if (ClassRoomContants.TYPE_LIVE_LIVE.equals(mFrom)) {
            mTvClassRoomTimesTitle.setText(getString(R.string.class_period));
            String[] numArr = getResources().getStringArray(R.array.numbers);
            mTvClassRoomTimeValue.setText(numArr[Integer.valueOf(mClassPeriod)]);
        } else {
            mTvClassRoomTimesTitle.setText(getString(R.string.class_room_time));
            mTvClassRoomTimeValue.setText(TextUtils.isEmpty(mClassTime)?"未开始":mClassTime);
        }
    }

    private String getTimeMinite(String time) {
        int timeInt = Integer.parseInt(time);
        if (timeInt >= 60 * 1000) {
            return timeInt / 1000 / 60 + "分" + timeInt / 1000 % 60 + "秒";
        } else {
            return timeInt / 1000 + "秒";
        }
    }

    private String getReceiveSchoolName(List<String> receiveSchoolList) {
        String str = "";
        for (int i = 0; i < receiveSchoolList.size(); i++) {
            str = str + receiveSchoolList.get(i) + "\n";
        }
        return str;
    }
}
