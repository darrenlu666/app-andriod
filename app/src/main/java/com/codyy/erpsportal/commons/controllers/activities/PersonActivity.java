package com.codyy.erpsportal.commons.controllers.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.PermissionUtils;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.FileUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UserFragmentUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.MyBottomSheet;
import com.codyy.erpsportal.groups.controllers.activities.ClassSpaceActivity;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.my.TeacherClassParse;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.commons.models.personal.StudentParse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 个人信息页面
 * Created by poe on 16-2-24.
 */
public class PersonActivity extends BaseHttpActivity implements Handler.Callback{
    private final static String TAG = "PersonActivity";
    private static final String EXTRA_ID = "user.baseUserId";
    private static final int MSG_UPLOAD_IMAGE_SUCCESS = 1;
    private static final int REQUEST_CHOOSE_FROM_CAMERA = 2;
    private static final int REQUEST_CROP_PIC = 3;
    private static final int REQUEST_GET_IMAGE_FROM = 4;
    private static final int REQUEST_CHOOSE_FROM_GALLERY = 5;

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.sdv_my_head_icon)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_name)TextView mNameTv;
    @Bind(R.id.tv_user_name)TextView mUserNameTv;
    @Bind(R.id.tv_telephone)TextView mTelephoneTv;
    @Bind(R.id.tv_area)TextView mAreaTv;//行政区
    @Bind(R.id.tv_position)TextView mPositionTv;
    @Bind(R.id.tv_school)TextView mSchoolTv;
    @Bind(R.id.tv_class)TextView mClassTv;
    @Bind(R.id.tv_subject)TextView mSubjectTv;
    @Bind(R.id.lin_position)LinearLayout mPositionLinear;
    @Bind(R.id.lin_school)LinearLayout mSchoolLinear;
    @Bind(R.id.lin_class)LinearLayout mClassLinear;
    @Bind(R.id.lin_subject)LinearLayout mSubjectLinear;
    @Bind(R.id.empty_view)EmptyView mEmptyView ;
    @Bind(R.id.tv_school_desc)TextView mSchoolDescTv;
    @Bind(R.id.tv_class_desc)TextView mClassDescTv;
    @Bind(R.id.forbidden_frame_layout)FrameLayout mForbiddenFrameLayout;//禁止访问提示TextView .

    //some variables .
    private List<Student> mStudents = new ArrayList<>();//家长名下孩子集合 .
    private List<ClassCont> mClassList = new ArrayList<>();//老师下面的班级.
    private String mImageHead = "";
    private String mImageUrl = "";
    private Handler mHandler = new Handler(this);
    private String mUserId ;//baseUserId
    private UserInfo mNativeUserInfo ;
    private StudentParse mStudentParse;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_person_info;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_PERSON_INFO;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseUserId",mUserId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG,response != null ?response.toString():" null ");
        if(null == mSchoolTv || null == mClassTv) return;
        mEmptyView.setLoading(false);
        UserInfo userInfo = UserInfo.parseJson(response);
        if(null != userInfo){
            mNativeUserInfo = userInfo ;
            //更新本人的ｕｕｉｄ防止过期
            if(mNativeUserInfo.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                mNativeUserInfo.setUuid(mUserInfo.getUuid());
                mNativeUserInfo.setChildList(mUserInfo.getChildList());
                mNativeUserInfo.setClassList(mUserInfo.getClassList());
            }
            refreshUI();
            switch (mNativeUserInfo.getUserType()){
                case UserInfo.USER_TYPE_TEACHER://教师
                    //设置教师的班级空间
                    if (mNativeUserInfo.getClassList() == null || mNativeUserInfo.getClassList().size() == 0) {
                        loadClasses();
                    }
                    break;
                case UserInfo.USER_TYPE_PARENT://家长
                    //班级学校
                    if(mNativeUserInfo.getChildList() == null || mNativeUserInfo.getChildList().size()==0){
                        loadChildren();
                    }
                    break;
            }
            mEmptyView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(Throwable error) {
        if(null == mEmptyView ) return;
        if(null != mNativeUserInfo){
            mEmptyView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void init() {
        mUserId   =   getIntent().getStringExtra(EXTRA_ID);
        mImageUrl = UserFragmentUtils.createDir().getAbsolutePath() + "/image.jpg";
        mImageHead = UserFragmentUtils.createDir().getAbsolutePath() + "/heard.jpg";
        mTitleTextView.setText(getString(R.string.person_info));
        initToolbar(mToolBar);
        //用户默认没有被禁用.
        mForbiddenFrameLayout.setVisibility(View.GONE);

        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        mClassLinear.setEnabled(false);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData(true);
            }
        });

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
        requestData(true);
    }

    private void refreshUI() {
        if(null == mNativeUserInfo) return;
        ImageFetcher.getInstance(this).fetchSmall(mSimpleDraweeView,mNativeUserInfo.getHeadPic());
        mNameTv.setText(mNativeUserInfo.getRealName());
        mUserNameTv.setText(mNativeUserInfo.getUserName());
        mTelephoneTv.setText(mNativeUserInfo.getContactPhone());
        //区域位置
        mAreaTv.setText(UIUtils.filterNull(mNativeUserInfo.getAreaPath()));
        mPositionTv.setText(mNativeUserInfo.getPosition());
        mSchoolTv.setText(mNativeUserInfo.getSchoolName());
        mSubjectTv.setText(mNativeUserInfo.getSubjectNames());
        mSchoolDescTv.setText(getString(R.string.school));
        mClassDescTv.setText(getString(R.string.my_class));

        switch (mNativeUserInfo.getUserType()){
            case UserInfo.USER_TYPE_AREA_USER://管理员
                mPositionLinear.setVisibility(View.VISIBLE);
                mSchoolLinear.setVisibility(View.GONE);
                mClassLinear.setVisibility(View.GONE);
                mSubjectLinear.setVisibility(View.GONE);
                break;
            case UserInfo.USER_TYPE_SCHOOL_USER://学校管理员
                mPositionLinear.setVisibility(View.GONE);
                mSchoolLinear.setVisibility(View.VISIBLE);
                mClassLinear.setVisibility(View.GONE);
                mSubjectLinear.setVisibility(View.GONE);
                break;
            case UserInfo.USER_TYPE_TEACHER://教师
                mPositionLinear.setVisibility(View.GONE);
                mSchoolLinear.setVisibility(View.VISIBLE);
                mClassLinear.setVisibility(View.VISIBLE);
                mSubjectLinear.setVisibility(View.VISIBLE);
                //设置教师的班级空间
                if(mNativeUserInfo.getClassList() != null && mNativeUserInfo.getClassList().size()>0){
                    mClassList = mNativeUserInfo.getClassList() ;
                    if(mClassList != null && mClassList.size()>0){
                        setTeacherClassName();//产品说要显示老师的所有任课班级信息...修bug阶段提出的需求变更!
                    }
                }
                break;
            case UserInfo.USER_TYPE_STUDENT://学生
                mPositionLinear.setVisibility(View.GONE);
                mSchoolLinear.setVisibility(View.VISIBLE);
                mClassLinear.setVisibility(View.VISIBLE);
                mSubjectLinear.setVisibility(View.GONE);
                mClassTv.setText(UIUtils.filterNull(mNativeUserInfo.getClasslevelName()+mNativeUserInfo.getBaseClassName()));
                enableClass();
                break;
            case UserInfo.USER_TYPE_PARENT://家长
                mPositionLinear.setVisibility(View.GONE);
                mSchoolLinear.setVisibility(View.VISIBLE);
                mClassLinear.setVisibility(View.VISIBLE);
                mSubjectLinear.setVisibility(View.GONE);
                mSchoolDescTv.setText(getString(R.string.school_child));
                mClassDescTv.setText(getString(R.string.my_child_class));
                //班级学校
                if(mNativeUserInfo.getChildList() != null ){
                    mStudents   =   mNativeUserInfo.getChildList() ;
                    setSchoolAndClassName();
                }
                break;
        }
    }

    /**
     * 允许点击
     */
    private void enableClass() {
        mClassLinear.setEnabled(true);
    }

    /**
     * 选择不同的班级，进入班级空间
     */
    @OnClick(R.id.lin_class)
    void selectClassRoom(){
        //过滤访客
        if(UserInfo.USER_TYPE_STUDENT.equals(mNativeUserInfo.getUserType())){
            List<ClassCont> contList = new ArrayList<>();
            contList.add(new ClassCont(mNativeUserInfo.getBaseClassId() ,mNativeUserInfo.getBaseClassName(),mNativeUserInfo.getClasslevelName()));
            ClassSpaceActivity.start(PersonActivity.this,"班级空间",mUserInfo.getBaseClassId(),contList);
        }else{
            showClassDialog(mNativeUserInfo);
        }
    }

     /**
     * 选择不同的学校，进入班级空间
     */
    @OnClick(R.id.lin_school)
    void selectSchool(){
        //过滤访客
        if(!mUserId.equals(mUserInfo.getBaseUserId())) {
            if(mNativeUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)){
                UserFragmentUtils.selectSchool(getSupportFragmentManager(),mStudents);
            }
            return;
        }
        if(mUserInfo.getUserType().equals(UserInfo.USER_TYPE_PARENT)){
            UserFragmentUtils.selectSchool(getSupportFragmentManager(),mStudents);
        }
    }


    /**
     * 获取家长下的孩子
     */
    private void loadChildren(){
        HashMap<String, String> data = new HashMap<>();
        if (mNativeUserInfo != null) {
            data.put("uuid", mNativeUserInfo.getUuid());
            data.put("userId",mNativeUserInfo.getBaseUserId());
        }

        String api = URLConfig.GET_PARENT_CHILDREN;
        if(!mNativeUserInfo.getBaseUserId().equals(mUserInfo.getBaseUserId())){
            api = URLConfig.GET_PUBLIC_PARENT_CHILDREN;
        }

        requestData(api, data, false,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null == mSchoolTv || null == mClassTv) return;
                mStudentParse = new Gson().fromJson(response.toString(), StudentParse.class);
                if (null != mStudentParse) {
                    mNativeUserInfo.setChildList(mStudentParse.getChildren());
                    if (null != mStudentParse.getChildren() && mStudentParse.getChildren().size() > 0) {
                        //if first get the result . set default first student as the selected student by parent .
                        if (mNativeUserInfo.getSelectedChild() == null) {
                            mNativeUserInfo.setSelectedChild(mStudentParse.getChildren().get(0));
                        }
                        mStudents = mStudentParse.getChildren();
                        setSchoolAndClassName();
                    }
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }

    /**
     * 获取老师的班级信息{#老师专用}
     */
    private void loadClasses(){
        HashMap<String, String> data = new HashMap<>();
        if (mNativeUserInfo != null) {
            data.put("userId",mNativeUserInfo.getBaseUserId());
        }

        requestData(URLConfig.GET_TEACHER_CLASS_LIST, data, false ,new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                if(null == mSchoolTv || null == mClassTv) return;
                TeacherClassParse teacherClassParse = new Gson().fromJson(response.toString(),TeacherClassParse.class);
                if(null != teacherClassParse && teacherClassParse.getDataList()!=null){
                    mClassList = teacherClassParse.getDataList();
                    mNativeUserInfo.setClassList(teacherClassParse.getDataList());
                    //refresh UI -> classInfo .
                    if(TextUtils.isEmpty(mNativeUserInfo.getBaseClassName())){
                        //设置班级信息 - default ： 第一个班级
                        if(null != mClassList && mClassList.size() >0 ){
                            mClassTv.setText(UserFragmentUtils.getTeacherClassName(mClassList));
                            enableClass();
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
     * 家长-设置班级显示
     */
    private void setSchoolAndClassName() {
        mSchoolTv.setText(UserFragmentUtils.getStudentSchoolName(mStudents));
        mClassTv.setText(UserFragmentUtils.getStudentClassName(mStudents));
        enableClass();
    }

    /**
     * 家长-设置班级显示
     */
    private void setTeacherClassName() {
        mClassTv.setText(UserFragmentUtils.getTeacherClassName(mClassList));
        enableClass();
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openAlbumIntent, REQUEST_GET_IMAGE_FROM);
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + mImageUrl));
        startActivityForResult(intent, REQUEST_CHOOSE_FROM_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CHOOSE_FROM_CAMERA:
                if (intent == null) {
                    if ((new File(mImageUrl)).exists())
                        UserFragmentUtils.cropImageUri2(PersonActivity.this,mImageHead,Uri.parse("file://" + mImageUrl), 100, 100, REQUEST_CROP_PIC);
                }
                break;
            case REQUEST_CHOOSE_FROM_GALLERY:
                if (intent != null) {
                    try {
                        mSimpleDraweeView.setDrawingCacheEnabled(false);
                        Uri uri = intent.getData();
                        ContentResolver resolver = getContentResolver();
                        Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, uri);
                        mSimpleDraweeView.setImageBitmap(bm);
                        //上传头像
                        uploadImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogUtils.log(e);
                    }
                }
                break;
            case REQUEST_CROP_PIC://from crop_big_picture
                mSimpleDraweeView.setDrawingCacheEnabled(false);
                mSimpleDraweeView.setImageURI(Uri.parse("file://" + mImageHead));
                uploadImage();
                break;
            case REQUEST_GET_IMAGE_FROM:
                if (intent != null) {
                    Uri photoUri = intent.getData();
                    if (photoUri != null) {
                        UserFragmentUtils.cropImageUri2(PersonActivity.this,mImageHead,photoUri, 200, 200, REQUEST_CROP_PIC);
                    }
                }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @OnClick(R.id.sdv_my_head_icon)
    void userIconClick() {
        //过滤访客
        if(!mUserId.equals(mUserInfo.getBaseUserId())) return;
        PermissionUtils.verifyStorageCameraPermissions(PersonActivity.this , mSimpleDraweeView,mPermissionInterface);
    }

    private PermissionUtils.PermissionInterface mPermissionInterface = new PermissionUtils.PermissionInterface() {
        @Override
        public void next() {
            showBSDialog();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       PermissionUtils.onRequestPermissionsResult(requestCode ,PersonActivity.this,mPermissionInterface);
    }

    private void showBSDialog() {
        if(mDialog == null){
            mDialog = new MyBottomSheet(this);
            View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet_choose_picture, null);
            TextView photoTakeTv = (TextView) contentView.findViewById(R.id.tv_take_photos);
            TextView pickPhotoTv = (TextView) contentView.findViewById(R.id.tv_select_photos);
            TextView exitTv = (TextView) contentView.findViewById(R.id.tv_cancel);
            photoTakeTv.setOnClickListener(mDialogListener);
            pickPhotoTv.setOnClickListener(mDialogListener);
            exitTv.setOnClickListener(mDialogListener);
            mDialog.setContentView(contentView);
            View parent = (View) contentView.getParent();
            int displayHeight = getResources().getDisplayMetrics().heightPixels;
            Cog.i(TAG , "displayHeight : "+ displayHeight);
            final BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);

            contentView.measure(0,0);
            behavior.setPeekHeight(contentView.getMeasuredHeight());
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
            layoutParams.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
            parent.setLayoutParams(layoutParams);
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }

        mDialog.show();
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cog.i(TAG , "img path : "+mImageHead);
                File file = new File(mImageHead);
                String js = UserFragmentUtils.uploadFile(file, URLConfig.UPLOAD_IMAGE + "?uuid=" + mUserInfo.getUuid());
                Message message = new Message();
                message.what = MSG_UPLOAD_IMAGE_SUCCESS;
                message.obj = js;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    //    private BottomSheetDialog mDialog ;
    private MyBottomSheet mDialog ;
    /** 选择班级Dialog */
    private MyBottomSheet mClassDialog;
    private View.OnClickListener mDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_select_photos:
                    choseHeadImageFromGallery();
                    mDialog.dismiss();
                    break;
                case R.id.tv_take_photos:
                    choseHeadImageFromCameraCapture();
                    mDialog.dismiss();
                    break;
                case R.id.tv_cancel:
                    mDialog.dismiss();
                    break;
            }
        }
    };

    /**
     * 选择班级进入
     */
    private void showClassDialog(final UserInfo userinfo) {
        if(mClassDialog == null){
            mClassDialog = UserFragmentUtils.createBottomSheet(PersonActivity.this,userinfo , mClassList , mStudents,new BaseRecyclerAdapter.OnItemClickListener<String>() {
                @Override
                public void onItemClicked(View v, int position, String data) {
                    if(userinfo.isTeacher()){
                        if(position < mClassList.size()){
                            ClassSpaceActivity.start(PersonActivity.this,"班级空间",mClassList.get(position).getBaseClassId(),mClassList);
                        }else{
                            LogUtils.log(TAG+" :"+"班级index越界 {@link PersonActivity: line 148");
                        }
                    }else if(userinfo.isParent()){
                        if(position < mStudents.size()){
                            ClassSpaceActivity.start(PersonActivity.this,"班级空间",mStudents.get(position).getClassId() ,
                                    UserFragmentUtils.constructClassListInfo(mStudents));
                        }else{
                            LogUtils.log(TAG+" :"+"班级index越界 {@link PersonActivity: line 148");
                        }
                    }
                    mClassDialog.dismiss();
                }
            });
        }
        mClassDialog.show();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPLOAD_IMAGE_SUCCESS:
                try {
                    String a = (String) msg.obj;
                    if (a != null) {
                        JSONObject object = new JSONObject(a);
                        if ("success".equals(object.opt("result"))) {
                            String heard = object.optString("headPic");

                            FileUtils.deleteFileFolder(UserFragmentUtils.createDir());
                            mNativeUserInfo.setHeadPic(heard);

                            if (heard != null && heard.trim().length() > 0) {
                                ImageFetcher.getInstance(this).fetchSmall(mSimpleDraweeView,heard);
                            }

                            if(mNativeUserInfo.getBaseUserId().equals(mUserInfo.getBaseUserId())){
                                mUserInfo.setHeadPic(heard);
                                UserInfoKeeper.getInstance().setUserInfo(mNativeUserInfo);
                                UserInfoDao.save(this, mNativeUserInfo);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    public static void start(Activity activity , String baseUserId) {
        Intent intent = new Intent(activity, PersonActivity.class);
        intent.putExtra(EXTRA_ID,baseUserId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }
}
