package com.codyy.erpsportal.county.controllers.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;
import com.codyy.erpsportal.county.controllers.activities.CountyClassDetailActivity;
import com.codyy.erpsportal.county.controllers.models.entities.ActualClassItem;
import com.codyy.erpsportal.county.controllers.models.entities.CountyListItemDetail;
import com.codyy.erpsportal.county.controllers.models.entities.StatisticsItem;
import com.codyy.erpsportal.databinding.DialogSemesterStatisticsBinding;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 16-10-19.
 */
public class DialogStatisticsFragment extends AppCompatDialogFragment {
    private final static Object REQUEST_TAG = new Object();
    /**
     * 主讲教室开课详情
     */
    private final static int MSG_RESULT_DETIAL = 0x001;
    /**
     * 教室开课详情统计
     */
    private final static int MSG_RESULT_STATISTICS = 0x002;
    /**
     * 本周听课详情
     */
    public final static int DIALOG_TYPE_DETIAL = 0x001;
    /**
     * 周听课统计
     */
    public final static int DIALOG_TYPE_STATISTICS = DIALOG_TYPE_DETIAL + 1;
    /**
     *
     */
    public final static String EXTRA_CLASSROOMID = "classRoomId";
    /**
     *
     */
    public final static String EXTRA_RECEIVE_CLASSROOMID = "receiveClassroomId";
    public final static String EXTRA_WEEKSEQ = "weekSeq";
    public final static String EXTRA_BASEUSERID = "baseUserId";
    public final static String EXTRA_PARAM = "param";
    /**
     * dateTime
     */
    public final static String EXTRA_DATATIME = "dateTime";

    public final static String EXTRA_URL_TYPE = "url_type";
    private final static String EXTRA_TYPE = "extra_dialog_type";
    private final static String EXTRA_CLASS_TYPE = "extra_class_type";
    public final static String URL_TYPE_WEEK = "WEEK";
    public final static String URL_TYPE_SEMESTER = "SEMESTER";
    private int mType;
    private RequestSender mRequestSender;
    private int mClassType;
    /**
     * 请求的url
     */
    private String mUrl;
    private String mUrlType;
    private int mStart;
    private final int mCount = 9;
    private int mEnd;
    private UserInfo mUserInfo;
    private RefreshRecycleView mRefreshRecycleView;
    private TextView mClassRoomName;
    private DialogSemesterStatisticsBinding mDialogSemesterStatisticsBinding;
    /**
     *
     */
    private LinearLayout mReasonLayout;

    public static DialogStatisticsFragment newInstance(int dialogType, int classType, Bundle param) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_TYPE, dialogType);
        args.putInt(EXTRA_CLASS_TYPE, classType);
        args.putBundle(EXTRA_PARAM, param);
        DialogStatisticsFragment fragment = new DialogStatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mType = getArguments().getInt(EXTRA_TYPE, DIALOG_TYPE_DETIAL);
        mClassType = getArguments().getInt(EXTRA_CLASS_TYPE, CountyClassDetailActivity.TYPE_MASTERCLASSROOM);
        mRequestSender = new RequestSender(getContext());
        switch (mType) {
            case DIALOG_TYPE_DETIAL:
                if (mClassType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                    mUrl = URLConfig.GET_CLASS_WEEKSCHEDULE_DETAIL;
                } else if (mClassType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                    mUrl = URLConfig.GET_RECEIVE_WEEKSCHEDULE_DETAIL;
                } else {//主讲教师
                    mUrl = URLConfig.GET_TEACHER_WEEKSCHEDULE_DETAIL;
                }
                mStart = 0;
                mEnd = mStart + mCount;
                httpConnect(mUrl, getParam(), MSG_RESULT_DETIAL, true);
                break;
            case DIALOG_TYPE_STATISTICS:
                if (mClassType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                    mUrl = URLConfig.GET_MAIN_CLASS_COUNT;
                } else if (mClassType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                    mUrl = URLConfig.GET_RECEIVE_COUNT_DETAIL;
                } else {//主讲教师
                    mUrl = URLConfig.GET_MAIN_TEACHER_COUNT;
                }
                httpConnect(mUrl, getParam(), MSG_RESULT_STATISTICS, true);
                break;
        }
    }

    private Map<String, String> getParam() {
        Map<String, String> param = new HashMap<>();
        Bundle bundle = getArguments().getBundle(EXTRA_PARAM);
        mUrlType = bundle.getString(EXTRA_URL_TYPE);
        param.put("type", mUrlType);
        param.put("uuid", mUserInfo.getUuid());
        String classRoomId = bundle.getString(EXTRA_CLASSROOMID);
        if (!TextUtils.isEmpty(classRoomId)) {
            param.put(EXTRA_CLASSROOMID, classRoomId);
        }
        String receiveClassroomId = bundle.getString(EXTRA_RECEIVE_CLASSROOMID);
        if (!TextUtils.isEmpty(receiveClassroomId)) {
            param.put(EXTRA_RECEIVE_CLASSROOMID, receiveClassroomId);
        }
        if (mClassType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
            param.put(EXTRA_WEEKSEQ, String.valueOf(bundle.getInt(EXTRA_WEEKSEQ)));
        } else if (mClassType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
            param.put(EXTRA_DATATIME, bundle.getString(EXTRA_DATATIME));
        } else {//主讲教师
            param.put(EXTRA_BASEUSERID, bundle.getString(EXTRA_BASEUSERID));
            param.put(EXTRA_DATATIME, bundle.getString(EXTRA_DATATIME));
        }
        switch (mType) {
            case DIALOG_TYPE_DETIAL:
                param.put("start", String.valueOf(mStart));
                param.put("end", String.valueOf(mEnd));
                break;
            case DIALOG_TYPE_STATISTICS:
                break;
        }
        return param;
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
        switch (mType) {
            case DIALOG_TYPE_DETIAL:
                return inflater.inflate(R.layout.dialog_week_class_detial, container, false);
            case DIALOG_TYPE_STATISTICS:
                mDialogSemesterStatisticsBinding = DialogSemesterStatisticsBinding.inflate(inflater, container, false);
                return mDialogSemesterStatisticsBinding.getRoot();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            view.findViewById(R.id.linearlayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        switch (mType) {
            case DIALOG_TYPE_DETIAL: {
                TextView title = (TextView) view.findViewById(R.id.title_text);
                mClassRoomName = (TextView) view.findViewById(R.id.classroom_name);
                if (URL_TYPE_WEEK.equals(mUrlType)) {
                    title.setText("本周开课详情");
                } else {
                    title.setText("本学期开课详情");
                }
                mRefreshRecycleView = (RefreshRecycleView) view.findViewById(R.id.refresh_recyclerview);
                mRefreshRecycleView.setRecycleOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                mRefreshRecycleView.setOnStateChangeLstener(new RefreshRecycleView.OnStateChangeLstener() {
                    @Override
                    public void onRefresh() {
                        mStart = 0;
                        mEnd = mStart + mCount;
                        httpConnect(mUrl, getParam(), MSG_RESULT_DETIAL, true);
                    }

                    @Override
                    public boolean onBottom() {
                        int size = mRefreshRecycleView.getAdapter().getmDatas().size();
                        if (size >= mEnd) {
                            mStart = size;
                            mEnd = mStart + mCount;
                            httpConnect(mUrl, getParam(), MSG_RESULT_DETIAL, false);
                            return true;
                        } else {
                            mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                        }
                        return false;
                    }
                });
            }
            break;
            case DIALOG_TYPE_STATISTICS:
                TextView title = (TextView) view.findViewById(R.id.title_text);
                mClassRoomName = (TextView) view.findViewById(R.id.classroom_name);
                if (URL_TYPE_WEEK.equals(mUrlType)) {
                    title.setText("本周开课统计");
                } else {
                    title.setText("本学期开课统计");
                }
                mReasonLayout = (LinearLayout) view.findViewById(R.id.reason_class_layout);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * @param url
     * @param param
     * @param msg
     */
    private void httpConnect(String url, final Map<String, String> param, final int msg, final boolean isClear) {
        mRequestSender.sendGetRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (getDialog() == null || !getDialog().isShowing()) {
                    return;
                }
                switch (msg) {
                    case MSG_RESULT_DETIAL:
                        if (mRefreshRecycleView != null) {
                            mRefreshRecycleView.setRefreshing(false);
                            if ("success".equals(response.optString("result"))) {
                                String name = response.optString("classroomName");
                                if (mClassType == CountyClassDetailActivity.TYPE_MASTERCLASSROOM) {//主讲教室
                                    String week = response.optString("weekSeq");
                                    if (URL_TYPE_WEEK.equals(mUrlType)) {
                                        mClassRoomName.setText(name + "/第" + week + "周");
                                    } else {
                                        mClassRoomName.setText(name);
                                    }
                                } else if (mClassType == CountyClassDetailActivity.TYPE_RECEIVEROOM) {//接收教室
                                    String school = response.optString("schoolName");
                                    String date = response.optString("weekDate");
                                    if (URL_TYPE_WEEK.equals(mUrlType)) {
                                        mClassRoomName.setText(school + "(" + name + ")/" + date);
                                    } else {
                                        mClassRoomName.setText(school + "(" + name + ")");
                                    }
                                } else {//主讲教师
                                    String classroomName = response.optString("classroomName");
                                    String realname = response.optString("realname");
                                    if (URL_TYPE_WEEK.equals(mUrlType)) {
                                        String week = response.optString("weekSeq");
                                        mClassRoomName.setText(classroomName + "/" + realname + "/第" + week + "周");
                                    } else {
                                        mClassRoomName.setText(classroomName + "/" + realname);
                                    }
                                }
                                List<ActualClassItem> actualClassItems = ActualClassItem.getActualClassItemList(response.optJSONArray("dataList"));
                                if (isClear) {
                                    if (actualClassItems == null || actualClassItems.size() == 0) {
                                        ToastUtil.showToast(getContext(), getContext().getString(R.string.tips_no_data_to_show));
                                    }
                                    mRefreshRecycleView.setAdapter(new DetialAdapter(getContext(), actualClassItems));
                                    if (actualClassItems.size() < mEnd) {
                                        mRefreshRecycleView.setLastVisibility(false);
                                    } else {
                                        mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_UP_LOADEMORE);
                                    }
                                } else {
                                    if (mRefreshRecycleView.getAdapter() != null) {
                                        DetialAdapter detialAdapter = (DetialAdapter) mRefreshRecycleView.getAdapter();
                                        detialAdapter.addListDatas(actualClassItems);
                                        if (detialAdapter.getmDatas().size() < mEnd) {
                                            mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_NO_MORE);
                                        } else {
                                            mRefreshRecycleView.setAdapterLastState(RefreshRecycleView.STATE_UP_LOADEMORE);
                                        }
                                    }
                                }
                            } else {
                                ToastUtil.showToast(getContext(), getString(R.string.net_connect_error));
                            }
                        }
                        break;
                    case MSG_RESULT_STATISTICS:
                        StatisticsItem statisticsItem = null;
                        try {
                            statisticsItem = StatisticsItem.getStatisticsItem(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getContext(), "数据异常！");
                        }
                        if (statisticsItem != null && "success".equals(statisticsItem.getResult())) {
                            statisticsItem.setType(mClassType);
                            mDialogSemesterStatisticsBinding.setEntity(statisticsItem);
                            mDialogSemesterStatisticsBinding.executePendingBindings();
                            for (StatisticsItem.ReasonListBean reasonListBean : statisticsItem.getReasonList()) {
                                TextView textView = new TextView(getContext());
                                textView.setTextAppearance(getContext(), R.style.semester_statistics_textstyle);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, UIUtils.dip2px(getContext(), 10), 0, 0);
                                textView.setLayoutParams(params);
                                textView.setText(reasonListBean.getReasonName() + "：" + reasonListBean.getCount());
                                mReasonLayout.addView(textView);
                            }
                        } else {
                            ToastUtil.showToast(getContext(), getString(R.string.net_connect_error));
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (getDialog() == null || !getDialog().isShowing()) {
                    return;
                }
                if (mRefreshRecycleView != null) {
                    mRefreshRecycleView.setRefreshing(false);
                }
                ToastUtil.showToast(getContext(), getString(R.string.net_error));
            }
        }, REQUEST_TAG));
    }

    @Override
    public void onDestroyView() {
        mRequestSender.stop(REQUEST_TAG);
        super.onDestroyView();
    }

    class DetialAdapter extends RefreshBaseAdapter<ActualClassItem> {

        public DetialAdapter(Context mContext) {
            super(mContext);
        }

        public DetialAdapter(Context mContext, List<ActualClassItem> mDatas) {
            super(mContext, mDatas);
            mTextColor = Color.parseColor("#ffffff");
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return new Item(inflater.inflate(R.layout.item_dialog_week_class_detial, parent, false));
        }

        @Override
        public void onBindView(RecyclerView.ViewHolder holder, int position, ActualClassItem entity) {
            ((Item) holder).setData(getmDatas().get(position));
        }

        class Item extends RecyclerView.ViewHolder {
            TextView mName;
            TextView mClassDate;
            TextView mReason;
            TextView mState;

            public Item(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.teacher_name);
                mClassDate = (TextView) itemView.findViewById(R.id.class_date);
                mState = (TextView) itemView.findViewById(R.id.class_state);
                mReason = (TextView) itemView.findViewById(R.id.class_state_reason);
            }

            public void setData(ActualClassItem actualClassItem) {
                mName.setText(actualClassItem.getRealName() + " " + actualClassItem.getClasslevelName() + actualClassItem.getSubjectName());
                mState.setText(actualClassItem.getClassStatus());
                mState.setTextColor(getColor(actualClassItem.getClassStatus()));
                if (URL_TYPE_WEEK.equals(mUrlType)) {
                    mClassDate.setText("星期" + CountyListItemDetail.getDayStr(actualClassItem.getDaySeq()) + "/第" + CountyListItemDetail.getNumberStr(actualClassItem.getClassSeq()) + "节");
                } else {
                    mClassDate.setText("第" + actualClassItem.getWeekSeq() + "周/星期" + CountyListItemDetail.getDayStr(actualClassItem.getDaySeq()) + "/第" + CountyListItemDetail.getNumberStr(actualClassItem.getClassSeq()) + "节");
                }
                mReason.setText(actualClassItem.getCancelReason());
                if ("因故停课".equals(actualClassItem.getClassStatus()) || "因故未听课".equals(actualClassItem.getClassStatus())) {
                    mReason.setVisibility(View.VISIBLE);
                } else {
                    mReason.setVisibility(View.GONE);
                }
            }
        }

        /**
         * VALID:有效授课 INVALID:无效授课 REASONABLE_MISSED:因故停课 UNREASONABLE_MISSED:无故停课
         *
         * @return
         */
        private String getState(String state) {
            if ("VALID".equals(state)) {
                return "有效授课";
            } else if ("INVALID".equals(state)) {
                return "无效授课";
            } else if ("REASONABLE_MISSED".equals(state)) {
                return "因故停课";
            } else if ("UNREASONABLE_MISSED".equals(state)) {
                return "无故停课";
            }
            return state;
        }

        private int getColor(String state) {
            if ("有效授课".equals(state) || "有效听课".equals(state)) {
                return Color.parseColor("#8ec35f");
            } else if ("无效授课".equals(state) || "无效听课".equals(state)) {
                return Color.parseColor("#ca77cb");
            } else if ("因故停课".equals(state) || "因故未听课".equals(state)) {
                return Color.parseColor("#c18c75");
            } else if ("无故停课".equals(state) || "无故未听课".equals(state)) {
                return Color.parseColor("#fa6c6e");
            }
            return Color.parseColor("#8ec35f");
        }

        /**
         * SYSTEM:在线课堂系统故障 MACHINE:班班通设备故障 ELECTRIC:电力故障 TEACH_LEAVE:教师请假 NETWORK:网络故障
         * MASTERREASON:主讲教室无故停课 CLASSROOM_LOCKED:教室关闭 OTHER:其它原因 (若是"其他原因"时，只返回"OTHER", 不需返回详情)
         */
        private String getReason(String status) {
            if ("SYSTEM".equals(status)) {
                return "在线课堂系统故障";
            } else if ("MACHINE".equals(status)) {
                return "班班通设备故障";
            } else if ("ELECTRIC".equals(status)) {
                return "电力故障";
            } else if ("TEACH_LEAVE".equals(status)) {
                return "教师请假";
            } else if ("NETWORK".equals(status)) {
                return "网络故障";
            } else if ("MASTERREASON".equals(status)) {
                return Titles.sMasterRoom + "无故停课";
            } else if ("CLASSROOM_LOCKED".equals(status)) {
                return "教室关闭";
            } else if ("OTHER".equals(status)) {
                return "其他原因";
            }
            return "";
        }
    }
}
