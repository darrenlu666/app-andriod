package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RecyclerBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewBaseHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.models.entities.ClassEntity;
import com.codyy.erpsportal.homework.models.entities.student.StudentPersonalInfo;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 作业批阅界面 学生头像
 * created by ldh 2015/12/24
 */
public class WorkStuReadActivity extends ToolbarActivity implements View.OnClickListener {

    private static final String TAG = WorkStuReadActivity.class.getSimpleName();
    public static final String TYPE_FROM_WORK = "type_from_work";//来自作业页面
    public static final String EXTRA_WORK_ID = "com.codyy.erpsprotal.WORK_ID";
    public static final String EXTRA_FROM = "com.codyy.erpsprotal.FROM";
    public static final String EXTRA_IS_TEACHER_READ = "com.codyy.erpsprotal.IS_TEACHER_READ";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_no_data_tip)
    TextView mTipTv;
    /**
     * 标题
     */
    @Bind(R.id.toolbar_title)
    protected TextView mTitle;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    /**
     * 班级弹出框
     */
    private PopupWindow mPopWindow = null;
    /**
     * 班级弹出框布局内容
     */
    private ListView mListView;
    private ClassAdapter mClassListAdapter;

    private List<StudentPersonalInfo> mStudentList;//学生数据集
    private List<ClassEntity> mClassList;//班级数据集
    private String mWorkId;//作业ID
    private String mCurrentClassId;//当前选中班级的ID
    private String mIsTeacherRead;//是否是老师批阅
    private String mFrom;//来源
    private String mClassListUrl;//班级请求地址
    private String mStuListUrl;//学生列表地址
    private Map<String, String> mClassParams = new HashMap<>();//班级请求参数
    private Map<String, String> mStuListParams = new HashMap<>();//学生请求参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWorkId = getIntent().getStringExtra(EXTRA_WORK_ID);
        mFrom = getIntent().getStringExtra(EXTRA_FROM);
        mIsTeacherRead = getIntent().getStringExtra(EXTRA_IS_TEACHER_READ);
        setRequestInfo();
        loadClassData();
        EventBus.getDefault().register(this);
    }

    /**
     * 根据测试/作业参数不同，设置不同的请求信息
     */
    private void setRequestInfo() {
        if (mFrom.equals(TYPE_FROM_WORK)) {
            mClassListUrl = URLConfig.GET_HOMEWORK_CLASSES;
            mClassParams.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
            mClassParams.put("workId", mWorkId);

            mStuListUrl = URLConfig.GET_HOMEWORK_STUDENT_LIST;
            mStuListParams.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
            mStuListParams.put("workId", mWorkId);
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_student_read_list;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取班级列表
     */
    private void loadClassData() {
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(mClassListUrl, mClassParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "classlist = " + response);
                if ("success".equals(response.optString("result")) && mToolbar != null) {
                    mClassList = ClassEntity.parseClassList(response);
                    //设置标题--班级
                    Drawable drawable = getResources().getDrawable(R.drawable.pull_down_arrow);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                    mTitle.setCompoundDrawables(null, null, drawable, null);
                    mTitle.setText(mClassList.get(0).getClassRoomName());
                    mCurrentClassId = mClassList.get(0).getClassRoomId();
                    mStuListParams.put("baseClassId", mCurrentClassId);
                    loadStudentData();
                    //点击班级，弹出班级列表PopWindow，并可选择并添加点击事件
                    View view = getLayoutInflater().inflate(R.layout.class_room_select_class, null);
                    mListView = (ListView) view.findViewById(R.id.class_room_listview);
                    mClassListAdapter = new ClassAdapter(getApplicationContext(), mClassList);
                    mListView.setAdapter(mClassListAdapter);
                    if (mClassList.size() >= 3) {
                        mPopWindow = new PopupWindow(view, UIUtils.dip2px(getApplicationContext(), 150), UIUtils.dip2px(getApplicationContext(), 200), true);
                    } else {
                        mPopWindow = new PopupWindow(view, UIUtils.dip2px(getApplicationContext(), 150), UIUtils.dip2px(getApplicationContext(), 55 * mClassList.size()), true);
                    }
                    mPopWindow.setTouchable(true);
                    mPopWindow.setOutsideTouchable(true);
                    mPopWindow.setBackgroundDrawable((new ColorDrawable(Color.parseColor("#00000000"))));
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mTitle.setText(mClassList.get(position).getClassRoomName());
                            mCurrentClassId = mClassList.get(position).getClassRoomId();
                            mStuListParams.put("baseClassId", mCurrentClassId);
                            loadStudentData();
                            mPopWindow.dismiss();
                        }
                    });
                    mTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopWindow.showAsDropDown(v, 0, 0);
                        }
                    });
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

    /**
     * 获取学生列表
     */
    private void loadStudentData() {
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(mStuListUrl, mStuListParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result")) && mToolbar != null) {
                    mStudentList = StudentPersonalInfo.parseStudentResponse(response);
                    initView();
                    if (mStudentList.size() > 0) {
                        mTipTv.setVisibility(View.GONE);
                    } else {
                        mTipTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    mTipTv.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
                mTipTv.setVisibility(View.VISIBLE);
            }
        }));
    }

    private void initView() {
        RecyclerBaseAdapter adapter = new RecyclerBaseAdapter<StudentPersonalInfo>(this, R.layout.item_student_read_list, mStudentList) {
            @Override
            public void onBindView(RecyclerViewBaseHolder holder, final int position) {
                ((TextView) holder.getView(R.id.tv_name)).setText(mStudentList.get(position).getStudentName());
                ((SimpleDraweeView) holder.getView(R.id.sdv_headpic)).setImageURI(Uri.parse(mStudentList.get(position).getHeadPicStr()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkItemDetailActivity.startActivity(WorkStuReadActivity.this, mWorkId, WorkItemDetailFragment.STATUS_TEACHER_READ,
                                mStudentList.get(position).getStudentId(), mIsTeacherRead, mStudentList.get(position).getStudentName());
                    }
                });
            }
        };
        //学生头像列表
        mRecyclerView.setAdapter(adapter);
        adapter.setData(mStudentList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    public void onEventMainThread(String action) {
        if ((WorkItemDetailActivity.class.getSimpleName() + " read Y").equals(action)) {
            ToastUtil.showToast(this, getResources().getString(R.string.work_answer_submit_success));
            loadStudentData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_title:
                mPopWindow.showAsDropDown(v, (-UIUtils.dip2px(this, 40)), 0);
                break;
        }
    }

    /**
     * 班级列表listview的适配器
     */
    class ClassAdapter extends BaseAdapter {
        private List<ClassEntity> mList;

        public ClassAdapter(Context context, List<ClassEntity> list) {
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
            TextView textView = new TextView(WorkStuReadActivity.this);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(WorkStuReadActivity.this, 50)));
            textView.setGravity(Gravity.CENTER);
            textView.setText(mList.get(position).getClassRoomName());
            textView.setTextSize(15);
            if (textView.getText().toString().equals(mTitle.getText().toString())) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                textView.setTextColor(Color.parseColor("#707070"));
            }
            return textView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        final MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextView textView = (TextView) linearLayout.findViewById(R.id.task_title);
        textView.setText(menuItem.toString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStudentList != null && mStudentList.size() > 0) {
                    WorkReadByItemActivity.startActivity(WorkStuReadActivity.this, mWorkId, mCurrentClassId);
                } else {
                    ToastUtil.showToast(WorkStuReadActivity.this, getResources().getString(R.string.work_stu_list_no_person));
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public static void startActivity(Context context, String workId, String from, String readType) {
        Intent intent = new Intent(context, WorkStuReadActivity.class);
        intent.putExtra(EXTRA_WORK_ID, workId);
        intent.putExtra(EXTRA_FROM, from);
        intent.putExtra(EXTRA_IS_TEACHER_READ, readType);
        context.startActivity(intent);
    }
}
