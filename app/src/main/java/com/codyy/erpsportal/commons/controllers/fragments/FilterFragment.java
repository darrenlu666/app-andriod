package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.SelectLeftAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.SelectRightAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.SingleAdapter;
import com.codyy.erpsportal.commons.models.entities.AreaBase;
import com.codyy.erpsportal.commons.models.entities.SchoolInfo;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 2015/5/27.
 */
public class FilterFragment extends Fragment implements View.OnClickListener {
    /**
     * 获取年级
     */
    private final static int GET_GRADE = 0x001;
    /**
     * 获取学科
     */
    private final static int GET_SUBJECT = 0x002;
    /**
     * 课表筛选
     */
    public static final int SCREEN_TIMETABLEVIEW = 7;

    private int mSelect = -1;

    private String mSubjectId = "";

    private String mGradeId = "";
    /**
     * 发起的评课
     */
    public static final int SPONSOR = 0;
    /**
     * 管辖区的评课
     */
    public static final int AREA = 1;

    /**
     * 受邀的评课
     */
    public static final int INVITED = 2;
    /**
     * 本校教师的评课
     */
    public static final int SCHOOLTEACHER = 3;

    /**
     * 我主讲的评课
     */
    public static final int MASTER = 4;
    /**
     * 参与的评课
     */
    public static final int ATTEND = 5;
    /**
     * 本校主讲的
     */
    public static final int SCHOOLMASTER = 6;
    /**
     * 管辖区的评课
     */
    public static final int AREA_EVALUATION = 8;
    public int type;
    private final static int GET_AREA = 0x001;//获得地区
    private final static int GET_DIRECT = 0x002;//获取直属学校
    private UserInfo userInfo;

    private List<AreaBase> areaBases;
    private ListView leftListview;
    private ListView rightListview;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private SelectRightAdapter rightAdapter;
    /**
     * 当前选择的地区
     */
    private AreaBase selectArea;
    /**
     * 状态
     */
    private LinearLayout mRelativeLayout;
    private LinearLayout mGradeRelativeLayout;
    private LinearLayout mSubjectRelativeLayout;
    private String[] single;

    /**
     * 发起的
     */
    private String[][] status_SPONSOR = {{"全部", null}, {"未开始", "INIT"}, {"进行中", "PROGRESS"}, {"已结束", "END"}};
    /**
     * 受邀的
     */
    private String[][] status_INVITED = {{"全部", null}, {"未开始", "INIT"}, {"进行中", "PROGRESS"}, {"已结束", "END"}, {"待处理", "WAIT"}, {"已拒绝", "REJECT"}, {"已过期", "TIMEOUT"}};
    private List<Status> mSponsor;
    private List<Status> mInvited;
    /**
     * 状态adapter
     */
    private SingleAdapter singleAdapter;

    private View view;

    private Context mContext;
    private SelectLeftAdapter mSelectLeftAdapter;
    private List<AreaBase> leftAreaBases;
    /**
     * 状态
     */
    private TextView mTextView;
    private TextView mTvGrade;
    private TextView mTvSubject;
    private Status mStatus;
    private String classID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view == null) {
            mContext = getActivity();
            mSender = new RequestSender(getActivity());
            Bundle bundle = getArguments();
            type = bundle.getInt("type");
            userInfo = bundle.getParcelable("userInfo");
            view = getActivity().getLayoutInflater().inflate(R.layout.select_layout, null);
            init(view);
            AreaBase areaInfo = new AreaBase();
            areaInfo.setAreaName("全部");
            areaInfo.setAreaId(userInfo.getBaseAreaId());
            areaBases.add(areaInfo);
            selectArea = areaInfo;
            setStatus();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void init(View view) {
        areaBases = new ArrayList<>();
        leftAreaBases = new ArrayList<>();
        mSponsor = new ArrayList<>();
        mInvited = new ArrayList<>();
        for (int i = 0; i < status_SPONSOR.length; i++) {
            Status status = new Status();
            status.setName(status_SPONSOR[i][0]);
            status.setId(status_SPONSOR[i][1]);
            mSponsor.add(status);
        }
        for (int i = 0; i < status_INVITED.length; i++) {
            Status status = new Status();
            status.setName(status_INVITED[i][0]);
            status.setId(status_INVITED[i][1]);
            mInvited.add(status);
        }
        rightListview = (ListView) view.findViewById(R.id.select_layout_listview_right);
        leftListview = (ListView) view.findViewById(R.id.select_layout_listview_left);
        mRelativeLayout = (LinearLayout) view.findViewById(R.id.select_layout_all_state);
        mRelativeLayout.setOnClickListener(this);
        mSubjectRelativeLayout = (LinearLayout) view.findViewById(R.id.select_layout_subject_state);
        mSubjectRelativeLayout.setOnClickListener(this);
        mGradeRelativeLayout = (LinearLayout) view.findViewById(R.id.select_layout_grade_state);
        mGradeRelativeLayout.setOnClickListener(this);
        leftListview.setOnItemClickListener(leftListener);
        rightListview.setOnItemClickListener(rightListener);
        rightAdapter = new SelectRightAdapter(mContext, areaBases);
        rightListview.setAdapter(rightAdapter);
        mSelectLeftAdapter = new SelectLeftAdapter(mContext, leftAreaBases);
        leftListview.setAdapter(mSelectLeftAdapter);
        mTextView = (TextView) view.findViewById(R.id.select_province_text_name);
        mTvGrade = (TextView) view.findViewById(R.id.select_grade_text_name);
        mTvSubject = (TextView) view.findViewById(R.id.select_subject_text_name);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * 获取地区信息
     */
    private void getAreaInof() {
        Map<String, String> data1 = new HashMap<>();
        data1.put("uuid", userInfo.getUuid());
        data1.put("areaId", selectArea.getAreaId());
        httpConnect(URLConfig.GET_AREA, data1, GET_AREA);
    }

    /**
     * 左侧列表监听
     */
    private AdapterView.OnItemClickListener leftListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isNetworkConnected()) {
                switch (type) {
                    case SPONSOR:
                    case INVITED:
                    case ATTEND:
                    case MASTER:
                    case SCHOOLMASTER:
                    case SCHOOLTEACHER:
                        if (leftListview.getAdapter() instanceof SingleAdapter) {
                            SingleAdapter ms = (SingleAdapter) leftListview.getAdapter();
                            mStatus = ms.getItem(position);
                            selectState(mStatus.getName());
                        }
                        break;
                    case AREA:
                    case SCREEN_TIMETABLEVIEW:
                    case AREA_EVALUATION:
                        if (leftListview.getAdapter() instanceof SelectLeftAdapter) {

                            if (mSelect == GET_GRADE) {
                                AreaBase areaBase = (AreaBase) leftListview.getAdapter().getItem(position);
                                selectGradeState(areaBase);
                                classID = areaBase.getAreaId();

                                return;
                            } else if (mSelect == GET_SUBJECT) {
                                AreaBase areaBase = (AreaBase) leftListview.getAdapter().getItem(position);
                                selectSubjectState(areaBase);
                                return;
                            }
                            AreaBase areaBase = (AreaBase) leftListview.getAdapter().getItem(position);
                            selectArea.setSelectName(areaBase.getAreaName());
                            mSelectLeftAdapter.setSelectName(areaBase.getAreaName());
                            if ("school".equals(areaBase.getType())) {
                                selectArea.setType("school");
                                selectArea.setSelectName(areaBase.getAreaName());
                                selectArea.setSchoolID(areaBase.getSchoolID());
                                selectArea.setAreaId(areaBase.getAreaId());
                                selectArea.setLevel(areaBase.getLevel());
                                selectArea.setAreaName(areaBase.getAreaName());
                                mSelectLeftAdapter.setSelectName(selectArea.getSelectName());
                            } else if ("area_all".equals(areaBase.getType())) {
                                removeAreas();
                                selectArea.setType("area");
                                selectArea.setLevel(areaBase.getLevel());
                                selectArea.setAreaName(areaBase.getAreaName());
                                selectArea.setAreaId(areaBase.getAreaId());
                                mSelectLeftAdapter.setSelectName("全部");
                            } else if ("school_all".equals(areaBase.getType())) {
                                selectArea.setType("area");
                                selectArea.setAreaName("全部");
                                selectArea.setLevel("学校");
                                selectArea.setAreaId(areaBase.getAreaId());
                                mSelectLeftAdapter.setSelectName("全部");
                            } else {
                                if ("直属校".equals(areaBase.getAreaName())) {
                                    rightListview.setEnabled(false);
                                    leftListview.setEnabled(false);
                                    removeAreas();
                                    selectArea.setAreaName("直属校");
                                    if (!"学校".equals(selectArea.getLevel())) {
                                        selectArea.setLevel(areaBase.getLevel());
                                        AreaBase areaBase1 = new AreaBase();
                                        areaBase1.setLevel("学校");
                                        areaBase1.setAreaName("全部");
                                        areaBase1.setDirect(true);
                                        areaBase1.setType(areaBase.getType());
                                        areaBase1.setAreaId(areaBase.getAreaId());
                                        areaBases.add(areaBase1);
                                        selectArea = areaBase1;
                                    }
                                    getDirectSchool(selectArea);
                                } else {
                                    leftListview.setEnabled(false);
                                    rightListview.setEnabled(false);
                                    removeAreas();
                                    selectArea.setLevel(areaBase.getLevel());
                                    selectArea.setAreaName(areaBase.getAreaName());
                                    selectArea.setAreaId(areaBase.getAreaId());
                                    selectArea.setType(areaBase.getType());
                                    rightAdapter.notifyDataSetChanged();
                                    AreaBase areaBase1 = new AreaBase();
                                    areaBase1.setAreaId(areaBase.getAreaId());
                                    areaBase1.setType("area");
                                    areaBase1.setAreaName("全部");
                                    areaBases.add(areaBase1);
                                    selectArea = areaBase1;
                                    getAreaInof();
                                }
                                //                            leftAreaBases.clear();
                            }
                            mSelectLeftAdapter.notifyDataSetChanged();
                            rightAdapter.setmSlectID(areaBases.indexOf(selectArea));
                            rightAdapter.notifyDataSetChanged();
                        } else if (leftListview.getAdapter() instanceof SingleAdapter) {
                            SingleAdapter ms = (SingleAdapter) leftListview.getAdapter();
                            mStatus = ms.getItem(position);
                            selectState(mStatus.getName());
                        }
                        break;
                }
            } else {
                ToastUtil.showToast(getActivity(), "无网络！");
            }
        }
    };

    private void selectState(String state) {
        singleAdapter.setSelect(state);
        mTextView.setText(state);
        singleAdapter.notifyDataSetChanged();
    }

    private void selectGradeState(AreaBase areaBase) {
        if (mTvGrade.getText().toString().equals(areaBase.getAreaName())) {
            mTvGrade.setText(areaBase.getAreaName());
            mGradeId = areaBase.getAreaId();
        } else {
            mTvSubject.setText("全部");
            mSubjectId = "";
            mTvGrade.setText(areaBase.getAreaName());
            mGradeId = areaBase.getAreaId();
        }

    }

    private void selectSubjectState(AreaBase areaBase) {
        mTvSubject.setText(areaBase.getAreaName());
        mSubjectId = areaBase.getAreaId();
    }


    /**
     * 右侧列表监听
     */
    private AdapterView.OnItemClickListener rightListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelect = -1;
            switch (type) {
                case ATTEND:
                case MASTER:
                case SCHOOLTEACHER:
                case SPONSOR:
                case INVITED:
                case SCHOOLMASTER:
                    break;
                case AREA:
                case SCREEN_TIMETABLEVIEW:
                case AREA_EVALUATION:
                    if (isNetworkConnected()) {
                        if (leftListview.getVisibility() == View.GONE) {
                            leftListview.setVisibility(View.VISIBLE);
                        }
                        AreaBase areaBase = areaBases.get(position);
                        selectArea = areaBase;
                        leftAreaBases.clear();
                        leftAreaBases.addAll(selectArea.getAreaBases());
                        mSelectLeftAdapter.setSelectName(areaBase.getSelectName());
                        rightAdapter.setmSlectID(position);
                        rightAdapter.notifyDataSetChanged();
                        if (!(leftListview.getAdapter() instanceof SelectLeftAdapter)) {
                            leftListview.setAdapter(mSelectLeftAdapter);
                        } else {
                            mSelectLeftAdapter.notifyDataSetChanged();
                        }
                        if (mRelativeLayout.getVisibility() == View.VISIBLE) {
                            mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                            mGradeRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                            mSubjectRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                        }
                    } else {
                        ToastUtil.showToast(getActivity(), "无网络！");
                    }
                    break;
            }
        }
    };

    /**
     * 设置筛选状态
     */
    private void setStatus() {
        switch (type) {
            case AREA_EVALUATION:
//                leftListview.setVisibility(View.GONE);
                getAreaInof();
                rightListview.setVisibility(View.VISIBLE);
                mRelativeLayout.setVisibility(View.VISIBLE);
                mGradeRelativeLayout.setVisibility(View.GONE);
                mSubjectRelativeLayout.setVisibility(View.GONE);
                singleAdapter = new SingleAdapter(mContext, mSponsor);
                break;
            case AREA:
//                leftListview.setVisibility(View.GONE);
                getAreaInof();
                rightListview.setVisibility(View.VISIBLE);
                mRelativeLayout.setVisibility(View.VISIBLE);
                mGradeRelativeLayout.setVisibility(View.VISIBLE);
                mSubjectRelativeLayout.setVisibility(View.VISIBLE);
                singleAdapter = new SingleAdapter(mContext, mSponsor);
                break;
            case ATTEND:
            case MASTER:
            case SCHOOLTEACHER:
            case SCHOOLMASTER:
            case SPONSOR:
                rightListview.setVisibility(View.GONE);
                mRelativeLayout.setVisibility(View.GONE);
                mGradeRelativeLayout.setVisibility(View.GONE);
                mSubjectRelativeLayout.setVisibility(View.GONE);
                singleAdapter = new SingleAdapter(mContext, mSponsor);
                leftListview.setAdapter(singleAdapter);
                leftListview.setVisibility(View.VISIBLE);
                break;
            case SCREEN_TIMETABLEVIEW:
//                leftListview.setVisibility(View.GONE);
                getAreaInof();
                mRelativeLayout.setVisibility(View.GONE);
                mGradeRelativeLayout.setVisibility(View.GONE);
                mSubjectRelativeLayout.setVisibility(View.GONE);
                break;
            case INVITED:
                leftListview.setVisibility(View.VISIBLE);
                rightListview.setVisibility(View.GONE);
                mRelativeLayout.setVisibility(View.GONE);
                mGradeRelativeLayout.setVisibility(View.GONE);
                mSubjectRelativeLayout.setVisibility(View.GONE);
                singleAdapter = new SingleAdapter(mContext, mInvited);
                leftListview.setAdapter(singleAdapter);
                break;
        }
    }


    /**
     * 设置userInfo
     *
     * @param mUserInfo
     */
    public void setUserInfo(UserInfo mUserInfo) {
        this.userInfo = mUserInfo;
    }

    /**
     * 网络连接
     * 成功 message.atg1=0，失败=1
     * message.obg是json数据
     *
     * @param data
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {

        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                leftListview.setEnabled(true);
                switch (msg) {
                    case GET_AREA:
                        AreaBase.getArea(response, selectArea);
                        if (!selectArea.hasChild) {
                            getDirectSchool(selectArea);
                        } else {
                            rightListview.setEnabled(true);
                        }
                        leftAreaBases.clear();
                        leftAreaBases.addAll(selectArea.getAreaBases());
                        mSelectLeftAdapter.setSelectName("全部");
                        mSelectLeftAdapter.notifyDataSetChanged();
                        rightAdapter.notifyDataSetChanged();
                        break;
                    case GET_DIRECT:
                        rightListview.setEnabled(true);
                        SchoolInfo.getSchoolInfo(response, selectArea);
                        leftAreaBases.clear();
                        leftAreaBases.addAll(selectArea.getAreaBases());
                        mSelectLeftAdapter.setSelectName("全部");
                        mSelectLeftAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(Throwable error) {
                rightListview.setEnabled(true);
                leftListview.setEnabled(true);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
            }
        }, this.toString()));
    }

    /**
     * 删除地区
     */
    private void removeAreas() {
        int a = areaBases.size();
        int b = areaBases.indexOf(selectArea);
        for (int i = b + 1; i < a; i++) {
            areaBases.remove(b + 1);
        }
    }

    /**
     * 获得直属学校
     *
     * @param areaBase
     */
    private void getDirectSchool(AreaBase areaBase) {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("areaId", areaBase.getAreaId());
        httpConnect(URLConfig.GET_DIRECT_SCHOOL, data, GET_DIRECT);
    }

    /**
     * 返回最后筛选
     *
     * @return
     */
    public AreaBase getLastArea() {
        if (areaBases.size() != 0) {
            return areaBases.get(areaBases.size() - 1);
        }
        return null;
    }

    public void setSingle(String[] single) {
        this.single = single;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_layout_all_state://全部状态
                mSelect = -1;
                if (leftListview.getVisibility() == View.GONE) {
                    leftListview.setVisibility(View.VISIBLE);
                }
                mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.personal_divide_line));
                mGradeRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                mSubjectRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                rightAdapter.setmSlectID(-1);
                rightAdapter.notifyDataSetChanged();
                leftListview.setAdapter(singleAdapter);
                break;

            case R.id.select_layout_grade_state://点击年级
            {
                mSelect = GET_GRADE;
                Map<String, String> param = new HashMap<>();
                if (UserInfo.USER_TYPE_SCHOOL_USER.equals(userInfo.getUserType())) {
                    param.put("schoolId", userInfo.getBaseAreaId());
                } else if (UserInfo.USER_TYPE_AREA_USER.equals(userInfo.getUserType())) {
                    param.put("areaId", userInfo.getBaseAreaId());
                }
                httpConnect1(URLConfig.GET_GRADE_UNLOGIN, param, GET_GRADE);
                if (leftListview.getVisibility() == View.GONE) {
                    leftListview.setVisibility(View.VISIBLE);
                }
                mGradeRelativeLayout.setBackgroundColor(getResources().getColor(R.color.personal_divide_line));
                mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                mSubjectRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                rightAdapter.setmSlectID(-1);
                rightAdapter.notifyDataSetChanged();
                leftListview.setAdapter(mSelectLeftAdapter);
            }
            break;

            case R.id.select_layout_subject_state://点击学科
                mSelect = GET_SUBJECT;
//                HashMap<String, String> data = new HashMap<>();
//                if (!"".equals(classID))
//                    data.put("classlevelId", classID);
                Map<String, String> param = new HashMap<>();
                if (UserInfo.USER_TYPE_SCHOOL_USER.equals(userInfo.getUserType())) {
                    param.put("schoolId", userInfo.getBaseAreaId());
                } else if (UserInfo.USER_TYPE_AREA_USER.equals(userInfo.getUserType())) {
                    param.put("areaId", userInfo.getBaseAreaId());
                }
                httpConnect1(URLConfig.GET_SUBJECT_UNLOGIN, param, GET_SUBJECT);
                if (leftListview.getVisibility() == View.GONE) {
                    leftListview.setVisibility(View.VISIBLE);
                }
                mSubjectRelativeLayout.setBackgroundColor(getResources().getColor(R.color.personal_divide_line));
                mGradeRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                rightAdapter.setmSlectID(-1);
                rightAdapter.notifyDataSetChanged();
                leftListview.setAdapter(mSelectLeftAdapter);
                break;
        }
    }

    /**
     * 返回状态
     *
     * @return
     */
    public Status getState() {
        return mStatus;
    }

    /**
     * 返回学科ID
     *
     * @return
     */
    public String getFilterSubjectId() {
        return mSubjectId;
    }

    /**
     * 返回学科年级ID
     *
     * @return
     */
    public String getFilterGradeId() {
        return mGradeId;
    }

    /**
     * 返回状态ID
     *
     * @return
     */
    public String getFilterStateId() {
        return mStatus != null ? mStatus.getId() : "";
    }

    public class Status {
        String name;
        String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * 网络请求
     *
     * @param url
     * @param data
     * @param msg
     */
    private void httpConnect1(String url, Map<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Cog.d("FilterFragment", response + "");
                switch (msg) {
                    case GET_GRADE:
                        leftAreaBases.clear();
                        if ("success".equals(response.optString("result"))) {
                            AreaBase areaBase = new AreaBase();
                            areaBase.setAreaName("全部");
                            areaBase.setAreaId("");
                            leftAreaBases.add(areaBase);
                            JSONArray array = response.optJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                AreaBase areaBase1 = new AreaBase();
                                JSONObject object = array.optJSONObject(i);
                                areaBase1.setAreaName(object.optString("name"));
                                areaBase1.setAreaId(object.optString("id"));
                                leftAreaBases.add(areaBase1);
                            }
                        }
                        mSelectLeftAdapter.notifyDataSetChanged();
                        break;
                    case GET_SUBJECT:
                        leftAreaBases.clear();
                        if ("success".equals(response.optString("result"))) {
                            AreaBase areaBase = new AreaBase();
                            areaBase.setAreaName("全部");
                            areaBase.setAreaId("");
                            leftAreaBases.add(areaBase);
                            JSONArray array = response.optJSONArray("list");
                            for (int i = 0; i < array.length(); i++) {
                                AreaBase areaBase1 = new AreaBase();
                                JSONObject object = array.optJSONObject(i);
                                areaBase1.setAreaName(object.optString("name"));
                                areaBase1.setAreaId(object.optString("id"));
                                leftAreaBases.add(areaBase1);
                            }
                        }
                        mSelectLeftAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(Throwable error) {
                leftAreaBases.clear();
                mSelectLeftAdapter.notifyDataSetChanged();
            }
        }));
    }

    public boolean isNetworkConnected() {
        if (getActivity() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mSender.stop(this.toString());
        super.onDestroy();
    }
}
