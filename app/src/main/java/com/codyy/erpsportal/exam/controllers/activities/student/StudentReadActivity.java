package com.codyy.erpsportal.exam.controllers.activities.student;

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
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.activities.teacher.TeacherDoReadActivity;
import com.codyy.erpsportal.exam.controllers.activities.teacher.TeacherDoReadByTopicActivity;
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
 * 学生-按学生批阅列表
 * created by eachann 2016-02-15
 */
public class StudentReadActivity extends ToolbarActivity implements View.OnClickListener {

    private static final String TAG = StudentReadActivity.class.getSimpleName();
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
    private String mCurrentClassId;//当前选中班级的ID
    private String mTaskTitle;//界面标题
    private static String mFrom;//来源
    private String mClassListUrl;//班级请求地址
    private String mStuListUrl;//学生列表地址
    private Map<String, String> mClassParams = new HashMap<>();//班级请求参数
    private Map<String, String> mStuListParams = new HashMap<>();//学生请求参数
    private static int mAllSubjectiveNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setRequestInfo();
        loadClassData();
    }

    public void onEventMainThread(String action) {
        if (TeacherDoReadActivity.class.getSimpleName().equals(action))
            loadStudentData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 根据测试/作业参数不同，设置不同的请求信息
     */
    private void setRequestInfo() {
        mClassListUrl = URLConfig.GET_EXAM_CLASS_LIST;
        mClassParams.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        mClassParams.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));

        mStuListUrl = URLConfig.GET_EXAM_SHOW_STU_LIST;
        mStuListParams.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        mStuListParams.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_student_read_list;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
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
                    mClassList = ClassEntity.parseExamClassList(response);
                    //设置标题--班级
                    Drawable drawable = getResources().getDrawable(R.drawable.pull_down_arrow);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                    mTitle.setCompoundDrawables(null, null, drawable, null);
                    mTitle.setText(mClassList.get(0).getClassRoomName());
                    mCurrentClassId = mClassList.get(0).getClassRoomId();
                    mStuListParams.put("classId", mCurrentClassId);
                    mStuListParams.put("classlevelId", mClassList.get(0).getClasslevelId());
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
                            mStuListParams.put("classId", mCurrentClassId);
                            mStuListParams.put("classlevelId", mClassList.get(position).getClasslevelId());
                            loadStudentData();
                            mPopWindow.dismiss();
                        }
                    });
                    mTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopWindow.showAsDropDown(v, (-UIUtils.dip2px(getApplicationContext(), 40)), 0);
                        }
                    });
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

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
                    mStudentList = StudentPersonalInfo.parseExamStudentResponse(response);
                    mAllSubjectiveNum = response.isNull("allSubjectiveNum") ? 0 : response.optInt("allSubjectiveNum");
                    initView();
                    if(mStudentList.size()>0){
                        mTipTv.setVisibility(View.GONE);
                    }else{
                        mTipTv.setVisibility(View.VISIBLE);
                    }
                }else{
                    mTipTv.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                mTipTv.setVisibility(View.VISIBLE);
            }
        }));
    }

    private void initView() {
        //学生头像列表
        mRecyclerView.setAdapter(new RecyclerBaseAdapter<StudentPersonalInfo>(this, R.layout.item_student_read_list, mStudentList) {
            @Override
            public void onBindView(RecyclerViewBaseHolder holder, final int position) {
                ((TextView) holder.getView(R.id.tv_name)).setText(mStudentList.get(position).getStudentName());
                ((SimpleDraweeView) holder.getView(R.id.sdv_headpic)).setImageURI(Uri.parse(mStudentList.get(position).getHeadPicStr()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TeacherDoReadActivity.startActivity(StudentReadActivity.this, getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), mStudentList.get(position), mStudentList.get(position).getExamResultId());
                    }
                });
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
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
        private Context mContext;
        private List<ClassEntity> mList;

        public ClassAdapter(Context context, List<ClassEntity> list) {
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
            TextView textView = new TextView(StudentReadActivity.this);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(StudentReadActivity.this, 50)));
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
        textView.setText(getString(R.string.work_title_read_by_item));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStudentList != null && mStudentList.size() > 0) {
                    TeacherDoReadByTopicActivity.start(StudentReadActivity.this, getString(R.string.work_title_read_by_item), getIntent().getStringExtra(EXTRA_EXAM_TASK_ID), mStuListParams.get("classId"), mStuListParams.get("classlevelId"));
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public static void startActivity(Context context, String examTaskId, String classlevelId) {
        Intent intent = new Intent(context, StudentReadActivity.class);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra("classlevelId", classlevelId);
        context.startActivity(intent);
    }

}
