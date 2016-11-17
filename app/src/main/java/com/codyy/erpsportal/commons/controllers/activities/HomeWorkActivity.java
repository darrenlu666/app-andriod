package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.HomeWorkAdapter;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.homenews.FamousClassBean;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 课堂作业
 */
// TODO: 2016/7/13 1.接口调试
@Deprecated
public class HomeWorkActivity extends Activity {
    /**
     * 上滑
     */
    private final static int UP = 0x001;
    /**
     * 下拉
     */
    private final static int DOWN = 0x002;
    /**
     * 开始
     */
    private final static int INIT = 0x003;
    private List<FamousClassBean> mData = null;
    @Bind(R.id.task_list)
    protected PullToRefreshListView mList;
    @Bind(R.id.homework_list_no_timetable)
    TextView mTextView;
    @Bind(R.id.homework_filter_btn)
    Button mFilterBtn;
    private Bundle mBundle;
    @Bind(R.id.activity_homework_emptyview)
    EmptyView mEmptyView;

    @OnClick(R.id.back)
    public void onBackClick() {
        this.finish();
    }

    private UserInfo mUserInfo;

    /**
     * 列表开始
     */
    private int start = 0;
    /**
     * 列表内容
     */
    private int cont = 9;
    /**
     * 列表结束
     */
    private int end = start + cont;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private String mClassType;
    private HomeWorkAdapter mHomeWorkAdapter;
    private ArrayList<FamousClassBean> mFamousClassBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        ButterKnife.bind(this);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mClassType = getIntent().getStringExtra("classType");
        init();
        mEmptyView.setLoading(true);
        mEmptyView.setVisibility(View.VISIBLE);
        getHomeWork(INIT);
    }

    private void init() {
        mSender = new RequestSender(this);
        mFamousClassBeans = new ArrayList<>();
        ((TextView) findViewById(R.id.activity_homework_text_title)).setText(Titles.sWorkspaceNetClassTask);
        mHomeWorkAdapter = new HomeWorkAdapter(HomeWorkActivity.this, mFamousClassBeans);
        mList.setAdapter(mHomeWorkAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent it = new Intent(HomeWorkActivity.this, HomeWorkDetailActivity.class);
                it.putExtra("data", mFamousClassBeans.get(i - 1));
                it.putExtra("classType", mClassType);
                HomeWorkActivity.this.startActivity(it);
            }
        });
        mList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                start = 0;
                end = start + cont;
                getHomeWork(DOWN);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mFamousClassBeans.size() < end) {
                    mList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mList.onRefreshComplete();
                            ToastUtil.showToast(HomeWorkActivity.this, "没有更多加载");
                        }
                    }, 1000);
                } else {
                    start = mFamousClassBeans.size();
                    end = start + cont;
                    getHomeWork(UP);
                }
            }
        });
//        if ("TEACHER".equals(mUserInfo.getUserType())) {
//            mFilterBtn.setVisibility(View.GONE);
//        } else {
//            mFilterBtn.setVisibility(View.VISIBLE);
//        }
//        switch (mClassType) {
//            case "ONLINE_CLASS":
//                mFilterBtn.setVisibility(View.GONE);
//                break;
//            case "LIVE":
//                mFilterBtn.setVisibility(View.VISIBLE);
//                break;
//        }
    }

    @OnClick(R.id.homework_filter_btn)
    void filter() {
        Intent intent = new Intent(this, ReservationClassFilterActivity.class);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        intent.putExtra(ReservationClassFilterActivity.TYPE_NAME, ReservationClassFilterActivity.TYPE_HOMEWORK);
        startActivityForResult(intent, ReservationClassFilterActivity.RESULT_CODE);
    }

    @OnClick(R.id.homework_list_no_timetable)
    void onRefersh() {
        mList.setRefreshing();
    }

    private void getHomeWork(final int type) {
        if (mUserInfo != null) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", mUserInfo.getUuid());
            data.put("type", mClassType);
            if ("SCHOOL_USR".endsWith(mUserInfo.getUserType())) {
                data.put("userType", "SCHOOL");
                data.put("baseUserId", mUserInfo.getSchoolId());
            } else if ("TEACHER".endsWith(mUserInfo.getUserType())) {
                data.put("userType", "TEACHER");
                data.put("baseUserId", mUserInfo.getBaseUserId());
            }
            if (mBundle != null) {
                data.put("startDate", mBundle.getString(ReservationClassFilterActivity.STATE_TIME_START, ""));
                data.put("endDate", mBundle.getString(ReservationClassFilterActivity.STATE_TIME_END, ""));
                data.put("classlevelId", mBundle.getString(ReservationClassFilterActivity.STATE_GRADE, ""));
                data.put("speakerUserId", mBundle.getString(ReservationClassFilterActivity.STATE_TEACHER, ""));
                data.put("subjectId", mBundle.getString(ReservationClassFilterActivity.STATE_SUBJECT, ""));
            }
            data.put("start", String.valueOf(start));
            data.put("end", String.valueOf(end));
            mSender.sendRequest(new RequestSender.RequestData(URLConfig.FAMOUS_SCHOOL_CLASS, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mEmptyView.setVisibility(View.GONE);
                    if (mList.isRefreshing()) {
                        mList.onRefreshComplete();
                    }
                    if ("success".equals(response.optString("result"))) {
                        switch (type) {
                            case INIT:
                                mFamousClassBeans.clear();
                                break;
                            case UP:
                                break;
                            case DOWN:
                                mFamousClassBeans.clear();
                                break;
                        }
                        JSONArray news = response.optJSONArray("list");
                        int len = news.length();
                        for (int i = 0; i < len; i++) {
                            FamousClassBean bean = new FamousClassBean();
                            JSONObject jsonObjcet = news.optJSONObject(i);
                            bean.setClassId(jsonObjcet.optString("classId"));
                            bean.setClassroomId(jsonObjcet.optString("classroomId"));
                            bean.setDate(jsonObjcet.optString("date"));
                            bean.setSubjectPic(jsonObjcet.optString("subjectPic"));
                            bean.setOrder(jsonObjcet.optString("order"));
                            bean.setSchoolName(jsonObjcet.optString("schoolName"));
                            bean.setSubject(jsonObjcet.optString("subject"));
                            bean.setTeacherName(jsonObjcet.optString("teacherName"));
                            bean.setBaseResourceServerId(jsonObjcet.optString("baseResourceServerId"));
                            bean.setServerAddress(jsonObjcet.optString("serverAddress"));
                            mFamousClassBeans.add(bean);
                        }
                        mHomeWorkAdapter.notifyDataSetChanged();
                    }
                    if (mFamousClassBeans.size() == 0) {
                        mTextView.setVisibility(View.VISIBLE);
                        ToastUtil.showToast(HomeWorkActivity.this, "抱歉，未查询到相关信息!");
                    } else {
                        mTextView.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mEmptyView.setVisibility(View.GONE);
                    if (mList.isRefreshing()) {
                        mList.onRefreshComplete();
                    }
                    if (mFamousClassBeans.size() == 0) {
                        mTextView.setVisibility(View.VISIBLE);
                    } else {
                        mTextView.setVisibility(View.GONE);
                    }
                    ToastUtil.showToast(HomeWorkActivity.this, getResources().getString(R.string.net_error));
                }
            }, this.toString()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ReservationClassFilterActivity.RESULT_CODE:
                mBundle = data.getBundleExtra("data");
                getHomeWork(INIT);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mSender.stop(this.toString());
        super.onDestroy();
    }
}
