package com.codyy.erpsportal.reservation.controllers.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
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
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.databinding.DialogReservationPopuBinding;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.reservation.models.entities.ReservationDetial;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kmdai on 16-8-9.
 */
public class ReservationDetialDialog extends AppCompatDialogFragment {
    private DialogReservationPopuBinding mDialogReservationPopuBinding;
    private RequestSender mRequestSender;
    private ReservationDetial.ListBean mReservationDetial;
    private String mSchoolID;
    private String mLiveID;
    private LinearLayout mReceiveLayout;
    private LayoutInflater mLayoutInflater;
    private LinearLayout mDirectLayout;
    private Integer mHashTag = this.hashCode();

    public static ReservationDetialDialog newInstance(String schoolID, String liveID) {

        Bundle args = new Bundle();
        args.putString("mSchoolID", schoolID);
        args.putString("mLiveID", liveID);
        ReservationDetialDialog fragment = new ReservationDetialDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(getContext());
        mSchoolID = getArguments().getString("mSchoolID");
        mLiveID = getArguments().getString("mLiveID");
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
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
        mDialogReservationPopuBinding = DialogReservationPopuBinding.inflate(inflater, container, false);
        return mDialogReservationPopuBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mReceiveLayout = (LinearLayout) view.findViewById(R.id.receive_layout);
        mDirectLayout = (LinearLayout) view.findViewById(R.id.direct_layout);
        mLayoutInflater = getLayoutInflater(savedInstanceState);
        getClassTableDetial();
        view.findViewById(R.id.linearlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setData() {
        if (mReservationDetial != null) {
            mDialogReservationPopuBinding.setEntity(mReservationDetial);
            mDialogReservationPopuBinding.executePendingBindings();
            if (mReservationDetial.getReceiveList() != null) {
                for (final ReservationDetial.ListBean.ReceiveListBean bean : mReservationDetial.getReceiveList()) {
                    View item = mLayoutInflater.inflate(R.layout.receiveclass_item, mReceiveLayout, false);
                    TextView textView = (TextView) item.findViewById(R.id.receive_item_text_classroom);
                    textView.setText("学校：" + bean.getSchoolName() + "(学生数 " + bean.getStuNum() + ")");
                    TextView teacherName = (TextView) item.findViewById(R.id.receive_item_text_teacher_name);
                    TextView phone = (TextView) item.findViewById(R.id.receive_item_text_phone);
                    if (!TextUtils.isEmpty(bean.getHelpUserName())) {
                        teacherName.setText("教师：" + bean.getHelpUserName());
                        if (TextUtils.isEmpty(bean.getContact())) {
                            teacherName.setText(teacherName.getText() + "(未填写联系电话)");
                            phone.setVisibility(View.GONE);
                        } else {
                            phone.setText(bean.getContact());
                            phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mReservationDetial.onPhoneClick(v, bean.getContact());
                                }
                            });
                        }
                    } else {
                        teacherName.setText("未填写教师信息");
                        phone.setVisibility(View.GONE);
                    }
                    mReceiveLayout.addView(item);
                }
            }
            if (mReservationDetial.getDirectorList() != null) {
                for (final ReservationDetial.ListBean.DirectorListBean bean : mReservationDetial.getDirectorList()) {
                    View item = mLayoutInflater.inflate(R.layout.item_direct_teacher, mDirectLayout, false);
                    TextView teacherName = (TextView) item.findViewById(R.id.directed_teacher);

                    TextView teacherPhone = (TextView) item.findViewById(R.id.directed_teacher_phone);
                    if (!TextUtils.isEmpty(bean.getDirectorUserName())) {
                        teacherName.setText(bean.getDirectorUserName());
                        if (TextUtils.isEmpty(bean.getDirectorPhone())) {
                            teacherName.setText(teacherName.getText() + "(未填写联系电话)");
                            teacherPhone.setVisibility(View.GONE);
                        } else {
                            teacherPhone.setText(bean.getDirectorPhone());
                            teacherPhone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mReservationDetial.onPhoneClick(v, bean.getDirectorPhone());
                                }
                            });
                        }
                    }
                    mDirectLayout.addView(item);
                }
            }
        }
    }

    /**
     * 获取课表详情
     */
    private void getClassTableDetial() {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        param.put("schoolId", mSchoolID);
        param.put("liveId", mLiveID);
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_LIVE_INFO, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (getDialog() != null && getDialog().isShowing()) {
                    getReservationDetial(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(getContext(), "获取数据失败！");
            }
        }, mHashTag));
    }

    /**
     * 获取课表详细信息
     *
     * @param object
     */
    private void getReservationDetial(JSONObject object) {
        if ("success".equals(object.optString("result"))) {
            ReservationDetial reservationDetial = null;
            try {
                reservationDetial = new Gson().fromJson(object.toString(), ReservationDetial.class);
                if (reservationDetial != null) {
                    mReservationDetial = reservationDetial.getList();
                    setData();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                ToastUtil.showToast(getContext(), "数据异常！");
            }

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mRequestSender.stop(mHashTag);
        super.onDismiss(dialog);
    }
}
