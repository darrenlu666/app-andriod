package com.codyy.erpsportal.commons.controllers.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.classroom.models.ClassRoomContants;
import com.codyy.erpsportal.commons.controllers.activities.BarCodeActivity;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.PermissionUtils;
import com.codyy.erpsportal.commons.utils.SharedPreferenceUtil;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.controllers.activities.CacheResourceActivity;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PersonActivity;
import com.codyy.erpsportal.commons.controllers.activities.SettingActivity;
import com.codyy.erpsportal.commons.controllers.fragments.channels.BaseHttpFragment;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.FileUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UserFragmentUtils;
import com.codyy.erpsportal.commons.widgets.MyBottomSheet;
import com.codyy.erpsportal.groups.controllers.activities.ClassSpaceActivity;
import com.codyy.erpsportal.groups.controllers.activities.GroupManagerActivity;
import com.codyy.erpsportal.groups.controllers.activities.MyBlogActivity;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.UserInfoDao;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.my.ClassCont;
import com.codyy.erpsportal.commons.models.entities.my.TeacherClassParse;
import com.codyy.erpsportal.commons.models.personal.Student;
import com.codyy.erpsportal.commons.models.personal.StudentParse;
import com.codyy.erpsportal.resource.controllers.activities.ParentsResourcesActivity;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * “我的”页
 * Created by gujiajia on 2015/7/15.
 * modify by poe 2016/2/23
 */
public class UserFragment extends BaseHttpFragment implements Handler.Callback {
    private static final String TAG = UserFragment.class.getSimpleName();
    private static final int MSG_UPLOAD_IMAGE_SUCCESS = 1;
    private static final int REQUEST_CHOOSE_FROM_CAMERA = 2;
    private static final int REQUEST_CROP_PIC = 3;
    private static final int REQUEST_GET_IMAGE_FROM = 4;
    private static final int REQUEST_CHOOSE_FROM_GALLERY = 5;
    @Bind(R.id.sdv_my_head_icon)  SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv__my_name)   TextView mNameTextView;
    @Bind(R.id.lin_class_namespace)    LinearLayout mClassSpaceLinearLayout;//class space .
    @Bind(R.id.tv_class_namespace)    TextView mClassSpaceTv;
    @Bind(R.id.tv_offline_cache)    TextView mCacheTv;
    @Bind(R.id.tv_setting)    TextView mSettingTv;
    @Bind(R.id.tv_WeiBo)    TextView mWeiboTv;
    @Bind(R.id.tv_blog_post)    TextView mBlogTv;
    @Bind(R.id.tv_photos)    TextView mPhotoTv;
    @Bind(R.id.tv_topic)    TextView mTopicTv;
    @Bind(R.id.tv_group_space)    TextView mGroupTv;
    @Bind(R.id.tv_qa)    TextView mQATv;
    @Bind(R.id.tv_good_resource)    TextView mResourceTv;
    //admin
    @Bind(R.id.tv_user_name)    TextView mUserNameTv;
    @Bind(R.id.tv_telephone)    TextView mTelephoneTv;
    @Bind(R.id.tv_area)    TextView mAreaTv;//行政区
    @Bind(R.id.tv_position)    TextView mPositionTv;
    @Bind(R.id.lin_user_name)    LinearLayout mUserLineLayout;
    @Bind(R.id.lin_phone)    LinearLayout mPhoneLinearLayout;
    @Bind(R.id.lin_area)    LinearLayout mAreaLinearLayout;
    @Bind(R.id.lin_position)    LinearLayout mPositionLinearLayout;
    @Bind(R.id.tv_class_namespace_desc)TextView mClassSpaceDescTv;
    @Bind(R.id.tv_position_desc)TextView mPositionDescTv;//职位　ｏｒ　学校　描述语言

    private Handler mHandler = new Handler(this);
    private UserInfo mUserInfo;
    private String mImageHead = "";
    private String mImageUrl = "";
    //some variables .
    private List<Student> mStudents = new ArrayList<>();//家长名下孩子集合 .
    private List<ClassCont> mClassList = new ArrayList<>();//老师下面的班级.
    private StudentParse mStudentParse;

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public String obtainAPI() {
        //获得班级列表
        if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {
            return URLConfig.GET_TEACHER_CLASS_LIST;
        } else if (UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())) {
            return URLConfig.GET_PARENT_CHILDREN;
        }
        return URLConfig.GET_TEACHER_CLASS_LIST;
    }

    @Override
    public HashMap<String, String> getParam(boolean isRefreshing) {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
            data.put("userId", mUserInfo.getBaseUserId());
        }
        return data;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mClassSpaceLinearLayout.setEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cog.d(TAG, "onActivityCreated（）");

        if (null != mUserInfo) {
            if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) { //老师获取班级信息
                if (mUserInfo.getClassList() == null || mUserInfo.getClassList().size() == 0) {
                    requestData(true);
                }else{
                    //直接填充数据
                    mClassList = mUserInfo.getClassList();
                    setTeacherClassName();
                }
            }else  if (UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())) {//家长获取班级信息
                if (mUserInfo.getChildList() == null || mUserInfo.getChildList().size() == 0 || mStudentParse == null) {
                    requestData(true);
                }else{
                    mStudents = mUserInfo.getChildList();
                    setClassName();
                }
            } else if(UserInfo.USER_TYPE_STUDENT.equals(mUserInfo.getUserType())){//学生－直接取值　.
                mClassSpaceTv.setText(mUserInfo.getClasslevelName()+mUserInfo.getBaseClassName());
                enableClassSpace();
            }
        }
        //防止出现更换账号
        onViewLoadCompleted();
    }

    @Override
    public void onSuccess(JSONObject response,boolean isRefreshing) {
        Cog.d(TAG, response.toString());
        if (null == mSimpleDraweeView) return;
        if ("success".equals(response.optString("result"))) {
            if (UserInfo.USER_TYPE_TEACHER.equals(mUserInfo.getUserType())) {//1.教师
                TeacherClassParse teacherClassParse = new Gson().fromJson(response.toString(), TeacherClassParse.class);
                if (null != teacherClassParse && teacherClassParse.getDataList() != null) {
                    mUserInfo.setClassList(teacherClassParse.getDataList());
                    mClassList = teacherClassParse.getDataList();
                    setTeacherClassName();
                }
            } else if (UserInfo.USER_TYPE_PARENT.equals(mUserInfo.getUserType())) {//2.家长
                mStudentParse = new Gson().fromJson(response.toString(), StudentParse.class);
                if (null != mStudentParse) {
                    mUserInfo.setChildList(mStudentParse.getChildren());
                    if (null != mStudentParse.getChildren() && mStudentParse.getChildren().size() > 0) {
                        //if first get the result . set default first student as the selected student by parent .
                        if (mUserInfo.getSelectedChild() == null) {
                            mUserInfo.setSelectedChild(mStudentParse.getChildren().get(0));
                            SharedPreferenceUtil.putString(ClassRoomContants.SHARE_PREFERENCE_STUDENT_ID, mStudentParse.getChildren().get(0).getStudentId());
                        }
                        mStudents = mStudentParse.getChildren();
                        setClassName();
                    }
                }
            }
        }
    }

    /**
     * 放开班级选择
     */
    public void enableClassSpace(){
        mClassSpaceLinearLayout.setEnabled(true);
    }

    @Override
    public void onFailure(Throwable error) {
        LogUtils.log(error);
    }

    /**
     * 选择不同的班级，进入班级空间
     */
    @OnClick(R.id.lin_class_namespace)
    void selectClassRoom() {
        if (UserInfo.USER_TYPE_STUDENT.equals(mUserInfo.getUserType())) {
            List<ClassCont> contList = new ArrayList<>();
            contList.add(new ClassCont(mUserInfo.getBaseClassId(), mUserInfo.getBaseClassName(), mUserInfo.getClasslevelName()));
            ClassSpaceActivity.start(getActivity(), "班级空间", mUserInfo.getBaseClassId(), contList,mUserInfo);
        } else {
            showClassDialog();
        }
    }

    /**
     * 教师-设置班级显示
     */
    private void setTeacherClassName() {
        mClassSpaceTv.setText(UserFragmentUtils.getTeacherClassName(mClassList));
        enableClassSpace();
    }

    /**
     * 家长-设置班级显示
     */
    private void setClassName() {
        mClassSpaceTv.setText(UserFragmentUtils.getStudentClassName(mStudents));
        enableClassSpace();
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        //config titles .
        mWeiboTv.setText(Titles.sPagetitleGlobalSpaceMicroblog);
        mBlogTv.setText(Titles.sWorkspaceBlog);
        mGroupTv.setText(Titles.sWorkspaceGroup);
        mResourceTv.setText(Titles.sWorkspaceResource);
        mNameTextView.setText(mUserInfo.getRealName());
        ImageFetcher.getInstance(getActivity()).fetchSmall(mSimpleDraweeView, mUserInfo.getHeadPic());
        mUserNameTv.setText(mUserInfo.getUserName());
        mTelephoneTv.setText(mUserInfo.getContactPhone());
        mAreaTv.setText(mUserInfo.getAreaName());

        mCacheTv.setVisibility(View.VISIBLE);
        mSettingTv.setVisibility(View.VISIBLE);
        // hide or show
        switch (mUserInfo.getUserType()) {
            case UserInfo.USER_TYPE_AREA_USER://管理员

                mClassSpaceLinearLayout.setVisibility(View.GONE);
                mWeiboTv.setVisibility(View.GONE);
                mBlogTv.setVisibility(View.GONE);
                mGroupTv.setVisibility(View.GONE);
                mResourceTv.setVisibility(View.GONE);

                mUserLineLayout.setVisibility(View.VISIBLE);
                mPhoneLinearLayout.setVisibility(View.VISIBLE);
                mAreaLinearLayout.setVisibility(View.VISIBLE);
                mPositionLinearLayout.setVisibility(View.VISIBLE);
                mPositionDescTv.setText("职位");
                mPositionTv.setText(mUserInfo.getPosition());
                getAreaPath();
                break;
            case UserInfo.USER_TYPE_SCHOOL_USER://学校管理员
                mClassSpaceLinearLayout.setVisibility(View.GONE);
                mWeiboTv.setVisibility(View.GONE);
                mBlogTv.setVisibility(View.GONE);
                mGroupTv.setVisibility(View.GONE);
                mResourceTv.setVisibility(View.GONE);

                mUserLineLayout.setVisibility(View.VISIBLE);
                mPhoneLinearLayout.setVisibility(View.VISIBLE);
                mAreaLinearLayout.setVisibility(View.VISIBLE);
                mPositionLinearLayout.setVisibility(View.VISIBLE);
                mPositionDescTv.setText("学校");
                mPositionTv.setText(mUserInfo.getSchoolName());
                getAreaPath();
                break;
            case UserInfo.USER_TYPE_TEACHER://教师
            case UserInfo.USER_TYPE_STUDENT://学生
                mClassSpaceLinearLayout.setVisibility(View.VISIBLE);
                mWeiboTv.setVisibility(View.VISIBLE);
                mBlogTv.setVisibility(View.VISIBLE);
                mGroupTv.setVisibility(View.GONE);
                mResourceTv.setVisibility(View.GONE);
                mUserLineLayout.setVisibility(View.GONE);
                mPhoneLinearLayout.setVisibility(View.GONE);
                mAreaLinearLayout.setVisibility(View.GONE);
                mPositionLinearLayout.setVisibility(View.GONE);
                break;
            case UserInfo.USER_TYPE_PARENT://家长
                mWeiboTv.setVisibility(View.VISIBLE);
                mBlogTv.setVisibility(View.VISIBLE);
                mResourceTv.setVisibility(View.VISIBLE);
                // TODO: 16-6-17 判断家长是否用拥有兴趣组
                if (mUserInfo.isHasTeam(UserInfo.TEAM_TYPE_INTEREST)) {
                    mGroupTv.setVisibility(View.VISIBLE);
                } else {
                    mGroupTv.setVisibility(View.GONE);
                }
                mUserLineLayout.setVisibility(View.GONE);
                mPhoneLinearLayout.setVisibility(View.GONE);
                mAreaLinearLayout.setVisibility(View.GONE);
                mPositionLinearLayout.setVisibility(View.GONE);
                break;
        }

        if (null != mStudentParse) {
            //是否有优课资源
            if ("Y".equals(mStudentParse.getResourceFlag())) {
                mResourceTv.setVisibility(View.VISIBLE);
            } else {
                mResourceTv.setVisibility(View.GONE);
            }

            //是否有个人圈组
            if ("Y".equals(mStudentParse.getGroupFlag())) {
                mGroupTv.setVisibility(View.VISIBLE);
            } else {
                mGroupTv.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.rlt_header)
    void showPerson() {
        //如果不是管理员-进入个人详情
        if (!isAdmin()) {
            PersonActivity.start(getActivity(), mUserInfo.getBaseUserId());
        }
    }


    /**
     * 是否是管理员
     *
     * @return
     */
    private boolean isAdmin() {
        boolean result = false;
        //管理员
        if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_AREA_USER)) {
            result = true;
        }
        //学校管理员
        if (mUserInfo.getUserType().equals(UserInfo.USER_TYPE_SCHOOL_USER)) {
            result = true;
        }
        return result;
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
                        UserFragmentUtils.cropImageUri(UserFragment.this, mImageHead, Uri.parse("file://" + mImageUrl), 100, 100, REQUEST_CROP_PIC);
                }
                break;
            case REQUEST_CHOOSE_FROM_GALLERY:
                if (intent != null) {
                    try {
                        mSimpleDraweeView.setDrawingCacheEnabled(false);
                        Uri uri = intent.getData();
                        ContentResolver resolver = getActivity().getContentResolver();
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
                        UserFragmentUtils.cropImageUri(UserFragment.this, mImageHead, photoUri, 200, 200, REQUEST_CROP_PIC);
                    }
                }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @OnClick(R.id.sdv_my_head_icon)
    void userIconClick() {
        if (isAdmin()) {
            PermissionUtils.verifyStorageCameraPermissions(getActivity(),mSimpleDraweeView,mPermissionInterface);
        }
    }

    private PermissionUtils.PermissionInterface mPermissionInterface = new PermissionUtils.PermissionInterface() {
        @Override
        public void next() {
            mImageUrl = UserFragmentUtils.createDir().getAbsolutePath() + "/image.jpg";
            mImageHead = UserFragmentUtils.createDir().getAbsolutePath() + "/heard.jpg";
            showBSDialog();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode,getActivity(),mPermissionInterface);
    }

    private void showBSDialog() {
        if (mDialog == null) {
            mDialog = new MyBottomSheet(getActivity());
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bottom_sheet_choose_picture, null);
            TextView photoTakeTv = (TextView) contentView.findViewById(R.id.tv_take_photos);
            TextView pickPhotoTv = (TextView) contentView.findViewById(R.id.tv_select_photos);
            TextView exitTv = (TextView) contentView.findViewById(R.id.tv_cancel);
            photoTakeTv.setOnClickListener(mDialogListener);
            pickPhotoTv.setOnClickListener(mDialogListener);
            exitTv.setOnClickListener(mDialogListener);
            mDialog.setContentView(contentView);
            View parent = (View) contentView.getParent();
            int displayHeight = getResources().getDisplayMetrics().heightPixels;
            Cog.i(TAG, "displayHeight : " + displayHeight);
            final BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);

            contentView.measure(0, 0);
            behavior.setPeekHeight(contentView.getMeasuredHeight());
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
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
     * 选择班级进入
     */
    private void showClassDialog() {
        if (mClassDialog == null || null == mClassList || mClassList.size() == 0) {
            mClassDialog = UserFragmentUtils.createBottomSheet(getActivity(), mUserInfo, mClassList, mStudents, new BaseRecyclerAdapter.OnItemClickListener<String>() {
                @Override
                public void onItemClicked(View v, int position, String data) {
                    if (mUserInfo.isTeacher()) {
                        if (position < mClassList.size()) {
                            ClassSpaceActivity.start(getActivity(), "班级空间", mClassList.get(position).getBaseClassId(), mClassList,mUserInfo);
                        } else {
                            LogUtils.log(TAG + " :" + "班级index越界 {@link PersonActivity: line 148");
                        }
                    } else if (mUserInfo.isParent()) {
                        if (position < mStudents.size()) {
                            ClassSpaceActivity.start(getActivity(), "班级空间", mStudents.get(position).getClassId(), UserFragmentUtils.constructClassListInfo(mStudents),mUserInfo);
                        } else {
                            LogUtils.log(TAG + " :" + "班级index越界 {@link PersonActivity: line 148");
                        }
                    }
                    mClassDialog.dismiss();
                }
            });
        }
        mClassDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        Cog.d(TAG, "onPause () ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Cog.d(TAG, "onResume（）");
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog.onDetachedFromWindow();
        }
        if (null != mClassDialog) {
            mClassDialog.dismiss();
            mClassDialog.cancel();
            mClassDialog.onDetachedFromWindow();
        }
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        ImageFetcher.getInstance(getActivity()).fetchSmall(mSimpleDraweeView, mUserInfo.getHeadPic());
    }

    /**
     * 更换头像Dialog
     */
    private MyBottomSheet mDialog;
    /**
     * 选择班级Dialog
     */
    private MyBottomSheet mClassDialog;
    private View.OnClickListener mDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
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

    @OnClick({R.id.tv_WeiBo, R.id.tv_blog_post, R.id.tv_photos, R.id.tv_topic, R.id.tv_group_space
            , R.id.tv_good_resource, R.id.tv_qa, R.id.tv_offline_cache, R.id.tv_setting
            , R.id.tv_bar_code})
    void singleClick(TextView textView) {
        switch (textView.getId()) {
            case R.id.tv_WeiBo://我的微博
                WeiBoActivity.start(getActivity(), WeiBoActivity.TYPE_PERSONAL, null, mUserInfo.getBaseUserId(), mUserInfo.getUserName());
                break;
            case R.id.tv_blog_post://我的博文
                MyBlogActivity.start(getActivity(), mUserInfo);
                break;
            case R.id.tv_photos://我的相册
                break;
            case R.id.tv_topic://我的话题
                break;
            case R.id.tv_group_space://我的圈组空间
                GroupManagerActivity.start(getActivity());
                break;
            case R.id.tv_good_resource://我的优课资源
                List<Student> studentList = mUserInfo.getChildList();
                if (studentList == null) {
                    ToastUtil.showToast(getContext(), "无法获取孩子信息！");
                    return;
                }
                Student student = studentList.get(0);
                if (student == null) {
                    ToastUtil.showToast(getContext(), "无法获取孩子信息！");
                    return;
                }
                ParentsResourcesActivity.start(getContext(), mUserInfo, student.getSchoolId());
                break;
            case R.id.tv_qa://我的问答
                break;
            case R.id.tv_offline_cache://离线缓存
                CacheResourceActivity.start(getActivity());
                break;
            case R.id.tv_setting://设置，需要处理回调退出登录code
                getActivity().startActivityForResult(new Intent(getActivity(), SettingActivity.class), MainActivity.REQUEST_SETTING);
                UIUtils.addEnterAnim(getActivity());
                break;
            case R.id.tv_bar_code://二维码
                if(NetworkUtils.isConnected()){
                    BarCodeActivity.start(getActivity(),mUserInfo);
                }else{
                    ToastUtil.showSnake(getString(R.string.net_error),mNameTextView);
                }
                break;
            default:
                ToastUtil.showToast("暂未实现");
                break;
        }
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
                            mUserInfo.setHeadPic(heard);
                            UserInfoDao.save(getActivity(), mUserInfo);
                            String headPic = mUserInfo.getHeadPic();
                            if (headPic != null && headPic.trim().length() > 0) {
                                ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,headPic);
                            }
                            FileUtils.deleteFileFolder(UserFragmentUtils.createDir());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    private void getAreaPath() {
        HashMap<String, String> data = new HashMap<>();
        data.put("baseUserId", mUserInfo.getBaseUserId());
        requestData(URLConfig.GET_PERSON_INFO, data,true, new BaseHttpActivity.IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response,boolean isRefreshing) {
                Cog.d(TAG, response != null ? response.toString() : " null ");
                if (null == mUserNameTv || null == mAreaTv) return;
                UserInfo userInfo = UserInfo.parseJson(response);
                if (null != userInfo) {
                    mUserInfo.setAreaPath(mUserInfo.getAreaPath());
                    mAreaTv.setText(userInfo.getAreaName());
                }
            }

            @Override
            public void onRequestFailure(Throwable error) {

            }
        });
    }
}
