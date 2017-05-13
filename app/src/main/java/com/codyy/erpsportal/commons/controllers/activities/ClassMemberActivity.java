package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.UserClassAdapter;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserClassBase;
import com.codyy.erpsportal.commons.models.entities.UserClassGroups;
import com.codyy.erpsportal.commons.models.entities.UserClassStudent;
import com.codyy.erpsportal.commons.models.entities.UserClassTeacher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.my.TeacherClassParse;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.groups.controllers.viewholders.ClassMemberSelectViewHolder;
import com.codyy.erpsportal.groups.controllers.viewholders.ClassMemberViewHolder;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 班级成员列表
 * @modified by poe 2016-06-20
 */
public class ClassMemberActivity extends BaseHttpActivity implements UserClassAdapter.OnItemClickListener {
    private final String TAG = "ClassMemberActivity";
    /**     * 用户类别     */
    public static final String EXTRA_USER_TYPE = "userType";
    /**     * 用户id     */
    public static final String EXTRA_USER_ID = "userId";
    /**     * 课堂id     */
    public static final String EXTRA_CLASS_ID = "classId";
    /**     * 教室名字     */
    public static final String EXTRA_CLASS_NAME = "className";
    /** */
    private final static String EXTRA_CLASS_LIST = "classList";//
    /**     * 获取班级列表     */
    private final static int GET_CLASS_LIST = 0x001;
    /**     * 获取班级老师     */
    private final static int GET_CLASS_TEACHER = 0x002;
    /**     * 获取学生详情     */
    private final static int GET_STUDENT_DETAIL = 0x003;
    /**
     * 来自"应用"
     */
    public static final String TYPE_FROM_APPLICATION = "from.function";
    /**
     * 来自＂我的-班级空间＂
     */
    public static final String TYPE_FROM_MY = "from.my.classSpace";
    /**
     * 来源
     */
    public static final String EXTRA_FROM = "from";

    private String mFromType = TYPE_FROM_MY;//默认来自＂我的－班级空间"

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTextTitle;
    @Bind(R.id.recycler_view_teacher)RecyclerView mRecycleTeacher;
    @Bind(R.id.recycler_view_student)RecyclerView mRecycleStudent;
    @Bind(R.id.tv_class_room_group)TextView mTextView;
    @Bind(R.id.rlt_class_member_middle)RelativeLayout mMiddleRelativeLayout;

    private ArrayList<ClassCont> mClasses;
    private PopupWindow mClassMemberPopupWindow;
    private PopupWindow mClassPopupWindow ;
    private UserClassAdapter mStudentAdapter;
    private ArrayList<UserClassBase> mUserStudents;
    private ArrayList<UserClassBase> mUserStudentAll;
    /**
     * 学生详情
     */
    private Dialog mDialog;
    private View mStudentDetailView;
    private RecyclerView mRecyclerViewDetail;
    private DialogUtil mDialogUtil;
    private String mUserId;
    private String mUserType;
    private String mClassID;
    private List<ClassCont> mData ;
    private List<UserClassGroups> mStudentData;//班级成员数据集合

    private BaseRecyclerAdapter<ClassCont,ClassMemberSelectViewHolder> mClassAdapter = null;//班级选择
    private BaseRecyclerAdapter<UserClassGroups,ClassMemberViewHolder> mClassMemberAdapter = null;//班级成员选择

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_class_layout;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_TEACHER_CLASS_MEMBERDETAIL;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> map = new HashMap<>();
        map.put("baseClassId", mClassID);
        map.put("userId", mUserId);
        return map;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        if(null ==  mRecycleTeacher) return;
//        if(isFinishing())
        JSONArray groups = response.optJSONArray("classGroups");
        JSONArray students = response.optJSONArray("students");
        JSONArray teachers = response.optJSONArray("teachers");
        ArrayList<UserClassBase> teacher = new ArrayList<>();
        //老师
        UserClassTeacher.getClassTeacher(teachers, teacher);
        mRecycleTeacher.setAdapter(new UserClassAdapter(ClassMemberActivity.this, teacher));
        //学生
        mUserStudentAll.clear();
        UserClassStudent.getClassStudent(students, mUserStudentAll);
        mUserStudents.clear();
        mUserStudents.addAll(mUserStudentAll);
        mStudentAdapter.notifyDataSetChanged();
        //学生筛选条件
        mStudentData = new ArrayList<>();
        UserClassGroups.getClassGroup(groups, mStudentData);
        if (mStudentData.size() > 0) {
            mTextView.setText(mStudentData.get(0).getGroupName());
        }
        if (students.length() <= 0) {
            ToastUtil.showToast(ClassMemberActivity.this, "暂无学生信息！");
        }
    }

    @Override
    public void onFailure(Throwable error) {

    }

    /**
     * 初始化
     */
    public void init() {
        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        mUserType = getIntent().getStringExtra(EXTRA_USER_TYPE);
        mClassID = getIntent().getStringExtra(EXTRA_CLASS_ID);
        mData   =   getIntent().getParcelableArrayListExtra(EXTRA_CLASS_LIST);
        mFromType   =   getIntent().getStringExtra(EXTRA_FROM);
        initToolbar(mToolBar);
        if(mData != null && mData.size()>0 && mClassID !=null){
            ClassCont cc = mData.get(0);
            for(int i=0 ;i<mData.size();i++){
                if(mClassID.equals(mData.get(i).getBaseClassId())){
                    cc = mData.get(i);
                    break;
                }
            }
            setTitle(cc);
        }else if(UserInfo.USER_TYPE_STUDENT.equals(mUserType)){
            setTitle(new ClassCont(mUserInfo.getBaseClassId() ,mUserInfo.getBaseClassName(),mUserInfo.getClasslevelName()));
        }else{
            mTextTitle.setText(Titles.sWorkspaceMember);
        }
        if(UserInfo.USER_TYPE_TEACHER.equals(mUserType)&&TYPE_FROM_APPLICATION.equals(mFromType)){
            //set the drop down icon .
            setIconDown();
        }
        if(UserInfo.USER_TYPE_PARENT.equals(mUserType)||UserInfo.USER_TYPE_STUDENT.equals(mUserType)){
            mTextView.setVisibility(View.GONE);
        }else{
            //向下箭头
            mTextView.setVisibility(View.VISIBLE);
            arrowDownMember();
        }

        mClasses = new ArrayList<>();
        mUserStudents = new ArrayList<>();
        mUserStudentAll = new ArrayList<>();
        mDialogUtil = new DialogUtil(this);
        mStudentAdapter = new UserClassAdapter(ClassMemberActivity.this, mUserStudents);
        mStudentAdapter.setmOnItemClickListener(this);
        mRecycleStudent.setAdapter(mStudentAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecycleTeacher.setLayoutManager(linearLayoutManager);
        mRecycleStudent.setLayoutManager(new GridLayoutManager(this, 4));
        mDialog = new Dialog(this, R.style.input_dialog);
        mStudentDetailView = getLayoutInflater().inflate(R.layout.class_room_studentdetail, null);
        mDialog.setContentView(mStudentDetailView);
        mRecyclerViewDetail = (RecyclerView) mDialog.findViewById(R.id.class_studentdetail_recycle_view);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewDetail.setLayoutManager(linearLayoutManager1);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = UIUtils.dip2px(this, 250);
        lp.height = UIUtils.dip2px(this, 350);
        window.setAttributes(lp);
        //get data .
        requestData(true);
    }

    //班级成员箭头－>下
    private void arrowDownMember() {
        Drawable drawable = getTintedDrawable(this, R.drawable.ic_black_down_arrow, UiMainUtils.getColor(R.color.black_666), false);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        mTextView.setCompoundDrawables(null, null, drawable, null);
    }

    //班级成员箭头－＞上
    private void arrowUpMember() {
        Drawable drawable = getTintedDrawable(this, R.drawable.img_up, UiMainUtils.getColor(R.color.black_666), false);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        mTextView.setCompoundDrawables(null, null, drawable, null);
    }

    private void setIconDown() {
        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.ic_white_down_arrow);
        arrow.setBounds(0, 0, arrow.getMinimumWidth(), arrow.getMinimumHeight()); //设置边界
        mTextTitle.setCompoundDrawables(null,null,arrow,null);
        mTextTitle.setCompoundDrawablePadding(10);
    }

    /**
     * bug fix :8519 android，ios应用-班级成员-点击上方页签，那个展出箭头需要有向上向下的变化
     */
    private void setIconUp() {
        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.ic_white_up_arrow);
        arrow.setBounds(0, 0, arrow.getMinimumWidth(), arrow.getMinimumHeight()); //设置边界
        mTextTitle.setCompoundDrawables(null,null,arrow,null);
        mTextTitle.setCompoundDrawablePadding(10);
    }

    private void setTitle(ClassCont cc) {
        StringBuilder sb = new StringBuilder();
        if(!TextUtils.isEmpty(cc.getClassLevelName())) sb.append(cc.getClassLevelName());
        if(!TextUtils.isEmpty(cc.getBaseClassName())) sb.append(cc.getBaseClassName());
        mTextTitle.setText(sb.toString());
    }

   @OnClick(R.id.toolbar_title)
   public void showClassList(){
        if(UserInfo.USER_TYPE_TEACHER.equals(mUserType)&&TYPE_FROM_APPLICATION.equals(mFromType)){
            if(null == mClassPopupWindow){
                View contentView = LayoutInflater.from(this).inflate(R.layout.recycleview_single,null);
                RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);

                recyclerView.addItemDecoration(new SimpleHorizonDivider(divider,true));
                mClassAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<ClassMemberSelectViewHolder>() {
                    @Override
                    public ClassMemberSelectViewHolder createViewHolder(ViewGroup parent, int viewType) {
                        ClassMemberSelectViewHolder viewHolder = new ClassMemberSelectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_selector,null));
                        viewHolder.setAdapter(mClassAdapter);
                        return viewHolder;
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return 0;
                    }
                });
                recyclerView.setAdapter(mClassAdapter);
                mClassAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ClassCont>() {
                    @Override
                    public void onItemClicked(View v, int position, ClassCont data) throws Exception {
                        ClassCont cc = mData.get(position);
                        mClassID = cc.getBaseClassId();
                        setTitle(cc);
                        mClassAdapter.notifyDataSetChanged();
                        requestData(true);
                        mClassPopupWindow.dismiss();
                    }
                });
                mClassPopupWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mClassPopupWindow.setOutsideTouchable(true);
                mClassPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mClassPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setIconDown();
                    }
                });
                mClassAdapter.setData(mData);
            }
            setIconUp();
            mClassPopupWindow.showAsDropDown(mToolBar);
        }
    }

    /**
     * 获取老师
     */
    private void getTeachers() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseClassId", mClassID);
        data.put("userId", mUserId);
        httpConnect(URLConfig.GET_TEACHER_CLASS_MEMBERDETAIL, data, GET_CLASS_TEACHER);
    }

    /**
     * 获取学生详情
     * mobile/myHome/getTeacherClassMemberDetail.do
     * @param student 学生类
     */
    private void getStudentDetail(UserClassStudent student) {
        HashMap<String, String> data = new HashMap<>();
        data.put("userId", student.getStudentId());
        httpConnect(URLConfig.GET_STUDENT_DETAIL, data, GET_STUDENT_DETAIL);
    }

    @OnClick(R.id.tv_class_room_group)
    void onGroupClick() {
        if (mStudentData == null || mStudentData.size() <= 0) {
            ToastUtil.showToast(this, "暂无数据！");
        } else {
            if(null == mClassMemberPopupWindow){
                View contentView = LayoutInflater.from(this).inflate(R.layout.recycleview_single,null);
                RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
                recyclerView.addItemDecoration(new SimpleHorizonDivider(divider,true));
                mClassMemberAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<ClassMemberViewHolder>() {
                    @Override
                    public ClassMemberViewHolder createViewHolder(ViewGroup parent, int viewType) {
                        ClassMemberViewHolder viewHolder = new ClassMemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_selector,null));
                        viewHolder.setAdapter(mClassMemberAdapter);
                        return viewHolder;
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return 0;
                    }
                });
                recyclerView.setAdapter(mClassMemberAdapter);
                mClassMemberAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<UserClassGroups>() {
                    @Override
                    public void onItemClicked(View v, int position, UserClassGroups data) throws Exception {
                        mTextView.setText(data.getGroupName());
//                        arrowDownMember();
                        studentFliterout(data.getGroupId());
                        mClassMemberAdapter.notifyDataSetChanged();
                        mClassMemberPopupWindow.dismiss();
                    }
                });
                mClassMemberPopupWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mClassMemberPopupWindow.setOutsideTouchable(true);
                mClassMemberPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mClassMemberPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        arrowDownMember();
                    }
                });
                mClassMemberAdapter.setData(mStudentData);
            }
            arrowUpMember();
            mClassMemberPopupWindow.showAsDropDown(mMiddleRelativeLayout);
        }
    }

    /**
     * 着色
     * @param context      上下文
     * @param resId        资源
     * @param tint         颜色
     * @param shouldMutate 是否需要改变
     * @return 图片
     */
    public static Drawable getTintedDrawable(Context context, int resId, int tint, boolean shouldMutate) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        drawable = DrawableCompat.wrap(drawable);
        if (shouldMutate) drawable = drawable.mutate();
        DrawableCompat.setTint(drawable, tint);
        return drawable;
    }

    /**
     * 网络连接
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mDialogUtil.cancel();
                switch (msg) {
                    case GET_CLASS_LIST://获取班级信息
                        TeacherClassParse teacherClassParse = new Gson().fromJson(response.toString(),TeacherClassParse.class);
                        if(null != teacherClassParse && teacherClassParse.getDataList()!=null){
                            mClasses = (ArrayList<ClassCont>) teacherClassParse.getDataList();
                        }
                        if (mClasses.size() > 0) {
                            mTextTitle.setText(mClasses.get(0).getBaseClassName());
                            getTeachers();
                        }
                        break;
                    case GET_CLASS_TEACHER:
                        break;
                    case GET_STUDENT_DETAIL:
                        showStudentDetail(response);
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable response) {
                mDialogUtil.cancel();
            }
        }));
    }

    /**
     * 显示学生详情
     */
    private void showStudentDetail(JSONObject response) {
        if ("success".equals(response.optString("result"))) {
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) mDialog.findViewById(R.id.class_studentdetail_simpledraweeview);
            final JSONObject studentObject = response.optJSONObject("student");
            ImageFetcher.getInstance(EApplication.instance()).fetchSmall(simpleDraweeView,studentObject.optString("studentHeadPic") );
            simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.placeholderimage_head);
            TextView name = (TextView) mDialog.findViewById(R.id.class_studentdetail_text_name);
            name.setText(studentObject.optString("studentName"));
            JSONArray parent = response.optJSONArray("parents");
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                    if (studentObject.optString("studentId").equals(userInfo.getBaseUserId())) {
                        MainActivity.start(ClassMemberActivity.this, userInfo, 2);
                    } else {//2.访客
                        PublicUserActivity.start(ClassMemberActivity.this, studentObject.optString("studentId"));
                    }
                }
            });
            ArrayList<UserClassBase> bases = new ArrayList<>();
            for (int i = 0; i < parent.length(); i++) {
                JSONObject object = parent.optJSONObject(i);
                UserClassTeacher teacher = new UserClassTeacher();
                teacher.setType(UserClassBase.TYPE_TEACHER);
                teacher.setTeacherName(object.optString("parentName"));
                teacher.setTeacherHeadpic(object.optString("parentHeadpic"));
                teacher.setTeacherId(object.optString("parentId"));
                bases.add(teacher);
            }
            mRecyclerViewDetail.setAdapter(new UserClassAdapter(this, bases));
            int[] a = new int[2];
            mStudentDetailView.getLocationOnScreen(a);
            mDialog.show();
        }
    }

    /**
     * 学生筛选
     */
    private void studentFliterout(String groupId) {
        mUserStudents.clear();
        if ("all".equals(groupId)) {
            mUserStudents.addAll(mUserStudentAll);
        } else {
            for (int i = 0; i < mUserStudentAll.size(); i++) {
                UserClassStudent userClassStudent = (UserClassStudent) mUserStudentAll.get(i);
                if (groupId.equals(userClassStudent.getGroupId())) {
                    mUserStudents.add(userClassStudent);
                }
            }
        }
        mStudentAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnStudentItemClick(int position, View view) {
        int a[] = new int[2];
        view.getLocationInWindow(a);
        mDialogUtil.showDialog();
        getStudentDetail((UserClassStudent) mUserStudents.get(position));
    }

    public static void start(Context from, UserInfo userInfo, String classId, String className , List<ClassCont> classCont , String type) {
        if (userInfo == null) return;//用户数据未加载好，点击班级无效
        Intent intent = new Intent(from, ClassMemberActivity.class);
        intent.putExtra(ClassMemberActivity.EXTRA_USER_ID, userInfo.getBaseUserId());
        intent.putExtra(ClassMemberActivity.EXTRA_USER_TYPE, userInfo.getUserType());
        intent.putExtra(ClassMemberActivity.EXTRA_CLASS_ID, classId);
        intent.putExtra(ClassMemberActivity.EXTRA_CLASS_NAME, className);
        intent.putParcelableArrayListExtra(EXTRA_CLASS_LIST , (ArrayList<? extends Parcelable>) classCont);
        intent.putExtra(EXTRA_FROM , type);
        from.startActivity(intent);
        UIUtils.addEnterAnim((Activity) from);
    }
}
