package com.codyy.erpsportal.county.widgets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.databinding.ContyClassdetailPopuBinding;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.timetable.models.entities.TimetableDetail;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kmdai on 16-8-4.
 */
public class ClassDetailDialog extends DialogFragment {
    private Integer mHashTag = this.hashCode();
    /**
     * 类型-区县总表
     */
    public final static int TYPE_CONTY_DETAIL = 0x001;
    public final static int TYPE = 0x002;
    public final static String EXTRA_DATA = "extra_data";
    public final static String EXTRA_TYPE = "extra_type";
    public final static String EXTRA_ID = "extra_id";
    public ContyClassdetailPopuBinding mContyClassdetailPopuBinding;
    private TimetableDetail.ScheduleListBean mScheduleListBean;
    private LinearLayout mReceiveLayout;
    private LinearLayout mDirectLayout;
    private LayoutInflater mLayoutInflater;
    private RequestSender mRequestSender;
    private int mType;
    private String mID;

    public static ClassDetailDialog newInstance(TimetableDetail.ScheduleListBean scheduleListBean, int type, String id) {

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_DATA, scheduleListBean);
        args.putInt(EXTRA_TYPE, type);
        args.putString(EXTRA_ID, id);
        ClassDetailDialog fragment = new ClassDetailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleListBean = getArguments().getParcelable(EXTRA_DATA);
        mType = getArguments().getInt(EXTRA_TYPE, TYPE_CONTY_DETAIL);
        mID = getArguments().getString(EXTRA_ID);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        mRequestSender = new RequestSender(getContext());

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.dialog_filter_animation);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContyClassdetailPopuBinding == null) {
            mContyClassdetailPopuBinding = ContyClassdetailPopuBinding.inflate(inflater, container, false);
        }
        return mContyClassdetailPopuBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLayoutInflater = LayoutInflater.from(getContext());
        mReceiveLayout = (LinearLayout) view.findViewById(R.id.receive_layout);
        mDirectLayout = (LinearLayout) view.findViewById(R.id.direct_layout);
        if (mScheduleListBean == null) {
            getData();
        } else {
            setValue();
        }
        view.findViewById(R.id.linearlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setValue() {
        if (mScheduleListBean != null && mReceiveLayout != null) {
            mContyClassdetailPopuBinding.setEntity(mScheduleListBean);
            mContyClassdetailPopuBinding.executePendingBindings();
            if (mScheduleListBean.getReceiveList() != null) {
                for (final TimetableDetail.ScheduleListBean.ReceiveListBean bean : mScheduleListBean.getReceiveList()) {
                    View item = mLayoutInflater.inflate(R.layout.receiveclass_item, mReceiveLayout, false);
                    TextView textView = (TextView) item.findViewById(R.id.receive_item_text_classroom);
                    textView.setText("学校：" + bean.getSchoolName() + "(学生数" + bean.getStudentNum() + ")");
                    TextView teacherName = (TextView) item.findViewById(R.id.receive_item_text_teacher_name);
                    TextView phone = (TextView) item.findViewById(R.id.receive_item_text_phone);
                    if (!TextUtils.isEmpty(bean.getTeacherName())) {
                        teacherName.setText("教师：" + bean.getTeacherName());
                        if (!TextUtils.isEmpty(bean.getTeacherPhone())) {
                            phone.setText(bean.getTeacherPhone());
                            phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mScheduleListBean.onPhoneClick(v, bean.getTeacherPhone());
                                }
                            });
                        } else {
                            teacherName.setText(teacherName.getText() + "(未填写联系电话)");
                            phone.setVisibility(View.GONE);
                        }
                    } else {
                        teacherName.setText("未填写教师信息");
                        phone.setVisibility(View.GONE);
                    }
                    mReceiveLayout.addView(item);
                }
            }
            if (mScheduleListBean.getDirectorList() != null) {
                for (final TimetableDetail.ScheduleListBean.DirectorListBean bean : mScheduleListBean.getDirectorList()) {
                    View item = mLayoutInflater.inflate(R.layout.item_direct_teacher, mDirectLayout, false);
                    TextView teacherName = (TextView) item.findViewById(R.id.directed_teacher);
                    teacherName.setText(bean.getRealname());
                    TextView teacherPhone = (TextView) item.findViewById(R.id.directed_teacher_phone);
                    if (!TextUtils.isEmpty(bean.getPhoneNum())) {
                        teacherPhone.setText(bean.getPhoneNum());
                        teacherPhone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mScheduleListBean.onPhoneClick(v, bean.getPhoneNum());
                            }
                        });
                    } else {
                        teacherName.setText(teacherName.getText() + "(未填写联系电话)");
                        teacherPhone.setVisibility(View.GONE);
                    }
                    mDirectLayout.addView(item);
                }
            }
        }
    }

    private void getData() {
        String url = "";
        Map<String, String> param = new HashMap<>();
        param.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        switch (mType) {
            case TYPE_CONTY_DETAIL:
                param.put("scheduleDetailId", mID);
                url = URLConfig.CONTY_GET_SCHEDULE_DETAIL;
                break;
        }
        mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (getView() == null) {
                    return;
                }
                switch (mType) {
                    case TYPE_CONTY_DETAIL:
                        TimetableDetail.ScheduleListBean scheduleListBean = getClassBen(response);
                        if (scheduleListBean != null) {
                            mScheduleListBean = scheduleListBean;
                            setData(scheduleListBean);
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, mHashTag));
    }

    public void setData(TimetableDetail.ScheduleListBean scheduleListBean) {
        mScheduleListBean = scheduleListBean;
        setValue();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mRequestSender.stop(mHashTag);
        super.onDismiss(dialog);
    }

    /**
     * 课表详细信息解析
     *
     * @param object
     * @return
     */
    public TimetableDetail.ScheduleListBean getClassBen(JSONObject object) {
        if ("success".equals(object.optString("result"))) {
            TimetableDetail.ScheduleListBean bean = new TimetableDetail.ScheduleListBean();
            bean.setCourseEndTime(object.optString("endTime"));
            bean.setCourseStartTime(object.optString("beginTime"));
            bean.setStatus(object.optString("status"));
            bean.setSubjectName(object.optString("courseName"));
            bean.setClassLevelName(object.optString("classLevelName"));
            bean.setTeacherName(object.optString("masterTeaName"));
            bean.setTeacherPhone(object.optString("masterTeaPhone"));
            bean.setMainClassroomName(object.optString("schoolName"));
            bean.setMainSchoolName(object.optString("roomName"));
            JSONArray array = object.optJSONArray("assitanceTeacherList");
            if (array != null) {
                ArrayList<TimetableDetail.ScheduleListBean.ReceiveListBean> receiveListBeens = new ArrayList<>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.optJSONObject(i);
                    if (object1 != null) {
                        TimetableDetail.ScheduleListBean.ReceiveListBean receiveListBean = new TimetableDetail.ScheduleListBean.ReceiveListBean();
                        receiveListBean.setTeacherName(object1.optString("assitanceTeaName"));
                        receiveListBean.setStudentNum(object1.optInt("studentNum"));
                        receiveListBean.setTeacherPhone(object1.optString("assitancePhone"));
                        receiveListBean.setSchoolName(object1.optString("assitanceSchoolName"));
                        receiveListBeens.add(receiveListBean);
                    }
                }
                bean.setReceiveList(receiveListBeens);
            }
            JSONArray array1 = object.optJSONArray("directorViewList");
            if (array1 != null) {
                ArrayList<TimetableDetail.ScheduleListBean.DirectorListBean> directorListBeanArrayList = new ArrayList<>(array1.length());
                for (int i = 0; i < array1.length(); i++) {
                    JSONObject object1 = array1.optJSONObject(i);
                    if (object1 != null) {
                        TimetableDetail.ScheduleListBean.DirectorListBean directorListBean = new TimetableDetail.ScheduleListBean.DirectorListBean();
                        directorListBean.setPhoneNum(object1.optString("phoneNum"));
                        directorListBean.setRealname(object1.optString("directorName"));
                        directorListBeanArrayList.add(directorListBean);
                    }
                }
                bean.setDirectorList(directorListBeanArrayList);
            }
            return bean;
        }
        return null;
    }
}
