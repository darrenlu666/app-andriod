package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.utils.UserFragmentUtils;
import com.codyy.erpsportal.commons.widgets.MyBottomSheet;
import com.codyy.erpsportal.groups.controllers.activities.ClassSpaceActivity;
import com.codyy.erpsportal.groups.controllers.activities.MyBlogActivity;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.my.TeacherClassParse;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.commons.models.personal.StudentParse;
import com.codyy.erpsportal.resource.controllers.activities.ParentsResourcesActivity;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoActivity;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 访客模式下的个人信息
 * Created by poe on 16-4-1.
 */
public class PublicUserActivity extends BaseHttpActivity {
    private final static String TAG = "PublicUserActivity";
    private static final String EXTRA_ID = "user.baseUserId";
    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.toolbar_title)    TextView mTitleTextView;
    @Bind(R.id.empty_view)    EmptyView mEmptyView;
    @Bind(R.id.sdv_my_head_icon)    SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv__my_name)    TextView mNameTextView;
    @Bind(R.id.tv_WeiBo)    TextView mWeiboTv;
    @Bind(R.id.tv_blog_post)    TextView mBlogTv;
    @Bind(R.id.tv_good_resource) TextView mResourceTv;//优课资源
    @Bind(R.id.tv_class_namespace) TextView mClassSpaceTv;//班级空间
    @Bind(R.id.lin_class_namespace)    LinearLayout mClassSpaceLinearLayout ;//班级空间容器 .
    /**
     * 选择班级Dialog
     */
    private MyBottomSheet mClassBottomSheetDialog;

    private String mUserId;//baseUserId
    private UserInfo mNativeUserInfo ;
    private List<Student> mStudents;//孩子信息　
    private List<ClassCont> mClassList = new ArrayList<>();//老师下面的班级.
    private StudentParse mStudentParse;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_public_user;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_PERSON_INFO;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseUserId", mUserId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response != null ? response.toString() : " null ");
        if (null == mEmptyView) return;
        mEmptyView.setLoading(false);
        UserInfo userInfo = UserInfo.parseJson(response);
        if (null != userInfo) {
            mNativeUserInfo = userInfo;
            refreshUI();
            mEmptyView.setVisibility(View.GONE);
            if(mNativeUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)){
                loadChildren();
            }else if(UserInfo.USER_TYPE_TEACHER.equals(mNativeUserInfo.getUserType())){
                loadClasses();
            }else if(null != mNativeUserInfo.getBaseClassName()){
                mClassSpaceTv.setText(mNativeUserInfo.getClasslevelName()+mNativeUserInfo.getBaseClassName());
                enableClassSpace();
            }
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取老师的班级信息{#老师专用}
     */
    private void loadClasses(){
        HashMap<String, String> data = new HashMap<>();
        if (mNativeUserInfo != null) {
            data.put("userId",mNativeUserInfo.getBaseUserId());
        }

        requestData(URLConfig.GET_TEACHER_CLASS_LIST,data,false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null == mResourceTv || null == mWeiboTv) return;
                TeacherClassParse teacherClassParse = new Gson().fromJson(response.toString(),TeacherClassParse.class);
                if(null != teacherClassParse && teacherClassParse.getDataList()!=null){
                    mClassList = teacherClassParse.getDataList();
                    mNativeUserInfo.setClassList(teacherClassParse.getDataList());
                    //refresh UI -> classInfo .
                    if(TextUtils.isEmpty(mNativeUserInfo.getBaseClassName())){
                        if(null != mClassList && mClassList.size() >0 ){
                            mClassSpaceTv.setText(UserFragmentUtils.getTeacherClassName(mClassList));
                            enableClassSpace();
                        }
                    }
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }


    /**
     * 获取家长下的孩子
     */
    private void loadChildren(){
        HashMap<String, String> data = new HashMap<>();
        String api = URLConfig.GET_PARENT_CHILDREN;
        if (mNativeUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
            data.put("userId",mNativeUserInfo.getBaseUserId());
            //if comer is an public user .
            if(!mNativeUserInfo.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                api = URLConfig.GET_PUBLIC_PARENT_CHILDREN;
            }
        }

        requestData(api, data,false, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null == mResourceTv || null == mWeiboTv) return;
                mStudentParse = new Gson().fromJson(response.toString(), StudentParse.class);
                if (null != mStudentParse) {
                    mNativeUserInfo.setChildList(mStudentParse.getChildren());
                    if (null != mStudentParse.getChildren() && mStudentParse.getChildren().size() > 0) {
                        //if first get the result . set default first student as the selected student by parent .
                        if (mNativeUserInfo.getSelectedChild() == null) {
                            mNativeUserInfo.setSelectedChild(mStudentParse.getChildren().get(0));
                        }
                        mStudents = mStudentParse.getChildren();
                        setClass();
                    }
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }

    private void setClass() {
        if(UserInfo.USER_TYPE_PARENT.equals(mNativeUserInfo.getUserType())){
            mResourceTv.setVisibility(View.VISIBLE);
            //是否有优课资源
            if (null != mStudentParse && !"Y".equals(mStudentParse.getResourceFlag())) {
                mResourceTv.setVisibility(View.GONE);
            }
        }
        //班级空间的信息 .　
        mClassSpaceTv.setText(UserFragmentUtils.getStudentClassName(mStudents));
        enableClassSpace();
    }

    /**
     * 放开班级选择
     */
    public void enableClassSpace(){
        mClassSpaceLinearLayout.setEnabled(true);
    }

    @Override
    public void onFailure(Throwable error) {
        if (null == mEmptyView) return;
        if (null != mNativeUserInfo) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void init() {
        mUserId = getIntent().getStringExtra(EXTRA_ID);
        initToolbar(mToolBar);
        mTitleTextView.setText("详细信息");

        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData(true);
            }
        });
        mClassSpaceLinearLayout.setEnabled(false);
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
        requestData(true);
    }

    /**
     * 刷新UI
     */
    private void refreshUI() {
        mNameTextView.setText(mNativeUserInfo.getRealName());
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView, mNativeUserInfo.getHeadPic());
        if(UserInfo.USER_TYPE_PARENT.equals(mNativeUserInfo.getUserType())){
            mResourceTv.setVisibility(View.VISIBLE);
        }else{
            mResourceTv.setVisibility(View.GONE);
        }
    }

    /**
     * 选择不同的班级，进入班级空间
     */
    @OnClick(R.id.lin_class_namespace)
    void selectClassRoom() {
        if (UserInfo.USER_TYPE_STUDENT.equals(mNativeUserInfo.getUserType())) {
            List<ClassCont> contList = new ArrayList<>();
            contList.add(new ClassCont(mNativeUserInfo.getBaseClassId(), mNativeUserInfo.getBaseClassName(), mNativeUserInfo.getClasslevelName()));
            ClassSpaceActivity.start(PublicUserActivity.this, "班级空间", mNativeUserInfo.getBaseClassId(), contList);
        } else {
            showClassDialog();
        }
    }

    /**
     * 选择班级进入
     */
    private void showClassDialog() {
        if (mClassBottomSheetDialog == null) {
            mClassBottomSheetDialog = UserFragmentUtils.createBottomSheet(PublicUserActivity.this, mNativeUserInfo, mClassList, mStudents, new BaseRecyclerAdapter.OnItemClickListener<String>() {
                @Override
                public void onItemClicked(View v, int position, String data) {
                    if (mNativeUserInfo.isTeacher()) {
                        if (position < mClassList.size()) {
                            ClassSpaceActivity.start(PublicUserActivity.this, "班级空间", mClassList.get(position).getBaseClassId(), mClassList);
                        } else {
                            LogUtils.log(TAG + " :" + "班级index越界 {@link PersonActivity: line 148");
                        }
                    } else if (mNativeUserInfo.isParent()) {
                        if (position < mStudents.size()) {
                            ClassSpaceActivity.start(PublicUserActivity.this, "班级空间", mStudents.get(position).getClassId(), UserFragmentUtils.constructClassListInfo(mStudents));
                        } else {
                            LogUtils.log(TAG + " :" + "班级index越界 {@link PersonActivity: line 148");
                        }
                    }
                    mClassBottomSheetDialog.dismiss();
                }
            });
        }
        mClassBottomSheetDialog.show();
    }


    @OnClick(R.id.rlt_header)
    void showPerson() {
        //如果不是管理员-进入个人详情
        PersonActivity.start(this, mNativeUserInfo.getBaseUserId());
    }

    @OnClick({R.id.tv_WeiBo, R.id.tv_blog_post,R.id.tv_good_resource})
    void singleClick(TextView textView) {
        switch (textView.getId()) {
            case R.id.tv_WeiBo://我的微博
                WeiBoActivity.start(this, WeiBoActivity.TYPE_VISITOR, null, mUserId, mNativeUserInfo.getRealName());
                break;
            case R.id.tv_blog_post://我的博文
                MyBlogActivity.start(this, mNativeUserInfo);
                break;
            case R.id.tv_good_resource://家长－优课资源
                List<Student> studentList = mNativeUserInfo.getChildList();
                if (studentList == null) {
                    ToastUtil.showToast(this, "无法获取孩子信息！");
                    return;
                }
                Student student = studentList.get(0);
                if (student == null) {
                    ToastUtil.showToast(this, "无法获取孩子信息！");
                    return;
                }
                ParentsResourcesActivity.start(this, mUserInfo,mNativeUserInfo.getBaseUserId(), student.getSchoolId());
                break;
            default:
                ToastUtil.showToast("暂未实现");
                break;
        }
    }

    public static void start(Activity activity, String baseUserId) {
        Intent intent = new Intent(activity, PublicUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra(EXTRA_ID, baseUserId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }

}
