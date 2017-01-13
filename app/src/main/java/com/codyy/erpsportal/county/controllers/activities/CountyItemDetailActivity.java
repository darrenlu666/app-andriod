package com.codyy.erpsportal.county.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourNewActivity;
import com.codyy.erpsportal.commons.controllers.activities.ClassTourPagerActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.TourClassroom;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.county.controllers.fragments.ContyListFragment;
import com.codyy.erpsportal.county.controllers.models.entities.CountyListItem;
import com.codyy.erpsportal.county.controllers.models.entities.CountyListItemDetail;
import com.codyy.erpsportal.databinding.ActivityContyItemDetailBinding;
import com.codyy.url.URLConfig;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CountyItemDetailActivity extends AppCompatActivity {
    private Integer mHashTag = this.hashCode();
    public final static String EXTRA_DATA = "extra_data";
    private final static int CONTY_GET_PLAN_DETAIL = 0x001;
    private final static int CONTY_GET_SELF_DETAIL = 0x002;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.item_list)
    ListView mSchoolLV;
    private int mType;
    private CountyListItem.ScheduleItem mScheduleItem;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    private ActivityContyItemDetailBinding mActivityContyItemDetailBinding;
    private CountyListItemDetail mContyListItemDetial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityContyItemDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_conty_item_detail);
        ButterKnife.bind(this);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mRequestSender = new RequestSender(this);
        mScheduleItem = getIntent().getParcelableExtra(EXTRA_DATA);
//        mSchoolLV.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(CountyItemDetailActivity.this, R.anim.list_item_show));
        if (mScheduleItem != null) {
            mType = mScheduleItem.getContyType();
        }
        initToolbar(mToolbar);
        switch (mType) {
            case ContyListFragment.TYPE_LIBERTY:
                mTextView.setText("自主开课");
                findViewById(R.id.plan_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.libetry_layout).setVisibility(View.GONE);
                findViewById(R.id.textView6).setVisibility(View.GONE);
                break;
            case ContyListFragment.TYPE_PLAN:
                mTextView.setText("计划开课");
                findViewById(R.id.plan_layout).setVisibility(View.GONE);
                findViewById(R.id.libetry_layout).setVisibility(View.VISIBLE);
                break;
        }
        getDetial();
    }

    private void getDetial() {
        if (mUserInfo != null && mScheduleItem != null) {
            Map<String, String> param = new HashMap<>();
            param.put("uuid", mUserInfo.getUuid());
            if (mType == ContyListFragment.TYPE_PLAN) {
                param.put("baseClasslevelId", mScheduleItem.getClasslevelId());
                param.put("baseSubjectId", mScheduleItem.getSubjectId());
                param.put("baseUserId", mScheduleItem.getBaseUserId());
                param.put("classRoomId", mScheduleItem.getClassroomId());
                param.put("scheduleId", mScheduleItem.getScheduleId());
                httpConnect(URLConfig.CONTY_GET_PLAN_DETAIL, param, CONTY_GET_PLAN_DETAIL);
            } else {
                param.put("scheduleDetailId", mScheduleItem.getScheduleDetailId());
                httpConnect(URLConfig.CONTY_GET_SELF_DETAIL, param, CONTY_GET_SELF_DETAIL);
            }
        }

    }

    /**
     * 初始化toolbar
     *
     * @param toolbar
     */
    protected void initToolbar(Toolbar toolbar) {
        if (toolbar == null)
            return;
        toolbar.setTitle("");
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn_bg);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * 设置overflowIcon
     *
     * @param toolbar
     * @param resId
     */
    public void setOverFlowIcon(Toolbar toolbar, int resId) {
        toolbar.setOverflowIcon(getResources().getDrawable(resId));
    }

    /**
     * 增加了默认的返回finish事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                UIUtils.addExitTranAnim(this);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * 头部点击
     *
     * @param view
     */
    public void titleClick(View view) {
        if (mContyListItemDetial != null) {
            CountyClassDetailActivity.start(this,
                    mContyListItemDetial.getClassroomId(),
                    mContyListItemDetial.getBaseUserId(),
                    mContyListItemDetial.getTeacherName(),
                    CountyClassDetailActivity.TYPE_TEACHER);
        }
    }

    /**
     * 进入课堂
     *
     * @param view
     */
    public void enterClass(View view) {
        TourClassroom tourClassroom = new TourClassroom();
        tourClassroom.setClassRoomId(mContyListItemDetial.getClassroomId());
        tourClassroom.setId(mContyListItemDetial.getScheduleDetailId());
        ClassTourPagerActivity.start(this, tourClassroom, mUserInfo, ClassTourNewActivity.TYPE_SPECIAL_DELIVERY_CLASSROOM);
    }

    /**
     * 网络请求
     *
     * @param url
     * @param param
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> param, final int msg) {
        mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isFinishing()) {
                    return;
                }
                switch (msg) {
                    //主讲详情
                    case CONTY_GET_PLAN_DETAIL:
                    case CONTY_GET_SELF_DETAIL:
                        if ("success".equals(response.optString("result"))) {
                            Gson gson = new Gson();
                            mContyListItemDetial = gson.fromJson(response.toString(), CountyListItemDetail.class);
                            mContyListItemDetial.setDayStr(CountyListItemDetail.getDayStr(mContyListItemDetial.getDaySeq()));
                            mContyListItemDetial.setClassStr(CountyListItemDetail.getNumberStr(mContyListItemDetial.getClassSeq()));
                            if (mContyListItemDetial != null) {
                                mActivityContyItemDetailBinding.setEntity(mContyListItemDetial);
                                mActivityContyItemDetailBinding.executePendingBindings();
                                mSchoolLV.setAdapter(new SchoolAdapter(mContyListItemDetial.getReceiveClassRoomList()));
                            }
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

    public static void start(Context context, int type) {
        Intent intent = new Intent(context, CountyItemDetailActivity.class);
        intent.putExtra(ContyListFragment.EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }

    class SchoolAdapter extends BaseAdapter {
        List<CountyListItemDetail.ReceiveClassRoomListBean> mReceiveClassRoomListBeen;

        SchoolAdapter(List<CountyListItemDetail.ReceiveClassRoomListBean> receiveClassRoomListBeen) {
            mReceiveClassRoomListBeen = receiveClassRoomListBeen;
        }

        @Override
        public int getCount() {
            return mReceiveClassRoomListBeen.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CountyListItemDetail.ReceiveClassRoomListBean receiveClassRoomListBean = mReceiveClassRoomListBeen.get(position);
            convertView = getLayoutInflater().inflate(R.layout.item_conty_itemdetail_classitem, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.school_name);
            textView.setText(receiveClassRoomListBean.getSchoolName() + "(" + receiveClassRoomListBean.getClassRoom() + ")");
            TextView student = (TextView) convertView.findViewById(R.id.studnet_number);
            student.setText("受益学生:" + receiveClassRoomListBean.getStudentCount());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CountyClassDetailActivity.start(CountyItemDetailActivity.this,
                            receiveClassRoomListBean.getClsClassroomId(),
                            mContyListItemDetial.getBaseUserId(),
                            receiveClassRoomListBean.getSchoolName() + "(" + receiveClassRoomListBean.getClassRoom() + ")",
                            CountyClassDetailActivity.TYPE_RECEIVEROOM);
                }
            });
            return convertView;
        }
    }
}
