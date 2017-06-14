package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.HomeWorkDetailAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.HomeWorkDetail;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.homenews.FamousClassBean;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.services.uploadServices.AbstractUploadServiceReceiver;
import com.codyy.erpsportal.commons.services.uploadServices.UploadRequest;
import com.codyy.erpsportal.commons.services.uploadServices.UploadService;
import com.codyy.erpsportal.commons.utils.NetworkUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.homework.widgets.MySubmitDialog;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoUpVideoDialogFragment;
import com.codyy.url.URLConfig;
import com.codyy.widgets.AlbumActivity;
import com.codyy.widgets.model.entities.PhotoInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;

/**
 * 课堂作业详情
 */
public class HomeWorkDetailActivity extends ToolbarActivity {

    private static final String TAG = "HomeWorkDetailActivity---";
    public static final int REQUEST_IMAGE = 1000;
    /**
     * 详情数据
     */
    private FamousClassBean mFamousClassBean;
    private UserInfo mUserInfo;
    private final static int IMAGE_SIZE = 1024 * 1024 * 5;
    private RequestSender mSender;
    /**
     * 标题
     */
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.homework_class_detail_upload)
    TextView mTvUpload;
    @Bind(R.id.homework_class_detail_emptyview)
    EmptyView mEmptyView;
    @Bind(R.id.homework_class_detail_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_grade_subject)
    TextView mGradeSubjectTv;
    @Bind(R.id.school_name)
    TextView mSchoolNameTv;
    @Bind(R.id.homework_class_detail)
    TextView mStartTimeTv;
    private String mClassType;
    private HomeWorkDetail mHomeWorkDetail;
    private HomeWorkDetailAdapter mHomeWorkDetailAdapter;
    private WeiBoUpVideoDialogFragment mWeiBoUpVideoDialogFragment;
    private int start = 0;
    private int end = 99;
    private ArrayList<PhotoInfo> mImages;
    private ArrayList<UpLoadMsg> mUpload;
    private Gson mGson;
    private final AbstractUploadServiceReceiver uploadReceiver = new AbstractUploadServiceReceiver() {

        @Override
        public void onProgress(String uploadId, int progress) {
            mWeiBoUpVideoDialogFragment.upProgress(progress);
        }

        @Override
        public void onError(String uploadId, Exception exception) {
            ToastUtil.showToast(HomeWorkDetailActivity.this, "上传失败!");
        }

        @Override
        public void onCompleted(String uploadId, int serverResponseCode, String serverResponseMessage) {
            System.out.println(TAG + serverResponseMessage);
            UpLoadMsg upLoadMsg = new UpLoadMsg();
            try {
                upLoadMsg = mGson.fromJson(serverResponseMessage, UpLoadMsg.class);
            } catch (Exception e) {
                ToastUtil.showToast(HomeWorkDetailActivity.this, "上传失败!");
                if (mWeiBoUpVideoDialogFragment != null) {
                    mWeiBoUpVideoDialogFragment.dismiss();
                }
                return;
            }
            if (upLoadMsg.result) {
                mUpload.add(upLoadMsg);
                mWeiBoUpVideoDialogFragment.setProgress(mUpload.size() + 1, mImages.size());
                if (mUpload.size() == mImages.size()) {
                    mWeiBoUpVideoDialogFragment.dismiss();
                    sendHomeWork();
                }
            } else {
                mWeiBoUpVideoDialogFragment.dismiss();
                ToastUtil.showToast(HomeWorkDetailActivity.this, "上传失败!");
            }
        }
    };

    /**
     * MobileInterface/classWork/classWorkUpload.do?uuid=MOBILE:5f2081ddd4734f8eb0839e6902ad7c84&classId=&classroomId=&baseResourceServerId=&serverAddress=&validateCode
     */
    private void sendHomeWork() {
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("classId", mFamousClassBean.getClassId());
        parm.put("clsClassroomId", mFamousClassBean.getClassroomId());
        parm.put("serverAddress", mHomeWorkDetail.getServerAddress());
        parm.put("baseResourceServerId", mHomeWorkDetail.getRecourceServerId());
        parm.put("validateCode", mUserInfo.getValidateCode());//
        JSONArray array = new JSONArray();
        for (int i = 0; i < mUpload.size(); i++) {
            Map<String, String> param = new HashMap<>();
            param.put("imagePic", mUpload.get(i).getMessage());
            param.put("workName", mUpload.get(i).getRealname());
            JSONObject object = new JSONObject(param);
            array.put(object);
        }
        parm.put("workList", array.toString());
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.HOMEWORK_UPLOAD, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result"))) {
                    ToastUtil.showToast(HomeWorkDetailActivity.this, "上传成功!");
                    getHomeWorkDetail();
                } else {
                    ToastUtil.showToast(HomeWorkDetailActivity.this, "上传失败!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

            }
        }));
    }

    public void onUploadHomeworkClick(View view) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.IMAGE_MAX_SIZE, IMAGE_SIZE);
        intent.putExtra(AlbumActivity.SET_MAX_SELECT, 50);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * 是否显示图片 默认显示
     */
    private boolean isShowImage = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFamousClassBean = getIntent().getExtras().getParcelable("data");
        initHead(mFamousClassBean);
        mTitle.setText(mFamousClassBean.getTeacherName());
        mClassType = getIntent().getStringExtra("classType");
        mSender = new RequestSender(this);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        UploadService.NAMESPACE = "com.codyy.erpsportal";
        mEmptyView.setLoading(true);

        if (!NetworkUtils.isNetWorkTypeWifi(this) && getWifiSetting()) {
            //当非WiFi状态不显示图片设置为真时，弹出用户提示框
            MySubmitDialog dialog = MySubmitDialog.newInstance(getString(R.string.tip_homework_list_show_image), "是", "否", MySubmitDialog.DIALOG_STYLE_TYPE_3, new MySubmitDialog.OnclickListener() {
                @Override
                public void leftClick(MySubmitDialog myDialog) {
                    isShowImage = false;
                    getHomeWorkDetail();
                    myDialog.dismiss();
                }

                @Override
                public void rightClick(MySubmitDialog myDialog) {
                    isShowImage = true;
                    getHomeWorkDetail();
                    myDialog.dismiss();
                }

                @Override
                public void dismiss() {

                }
            });
            dialog.show(getSupportFragmentManager(), "show");
        } else {
            getHomeWorkDetail();
        }
        init();
    }

    private boolean getWifiSetting() {
        SharedPreferences preferences = getSharedPreferences(SettingActivity.SHARE_PREFERENCE_SETTING, MODE_PRIVATE);
        return preferences.getBoolean(SettingActivity.KEY_IMAGE_WIFI_ONLY, false);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_homework_detail;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
    }

    private void init() {
        mGson = new Gson();
        mImages = new ArrayList<>();
        mUpload = new ArrayList<>();
        mWeiBoUpVideoDialogFragment = WeiBoUpVideoDialogFragment.newInstance();
        mWeiBoUpVideoDialogFragment.setOnCancel(new WeiBoUpVideoDialogFragment.OnCancel() {
            @Override
            public void cancel() {
                UploadService.stopCurrentUpload();
            }
        });
    }

    /**
     * 获取作业详情
     * ?uuid=MOBILE:1dde6218a68947f9b85b69830c670e10&classId=aee4675675824495b846e4a64700462a&start=0&end=29&type=LIVE
     */
    private void getHomeWorkDetail() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", mUserInfo.getUuid());
        data.put("classId", mFamousClassBean.getClassId());//"832885c27b1e46cdb90056213663ea93"
        data.put("type", mClassType);
        data.put("start", String.valueOf(start));
        data.put("end", String.valueOf(end));
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.HOMEWORK_LIST_FAMOUS, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optString("result").equals("success") && mToolbar != null) {
                    mEmptyView.setVisibility(View.GONE);
                    mHomeWorkDetail = HomeWorkDetail.getHomeWorkDetail(response);
                    if (mHomeWorkDetail != null) {
                        mHomeWorkDetailAdapter = new HomeWorkDetailAdapter(HomeWorkDetailActivity.this, mHomeWorkDetail.getClassRoomList(), isShowImage);
                        mRecyclerView.setAdapter(mHomeWorkDetailAdapter);
                        //initHead();//注掉了，这边的信息从前一个页面带过来
                        initUploadBar();
                    }
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    ToastUtil.showToast(HomeWorkDetailActivity.this, "获取数据失败");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setLoading(false);
                ToastUtil.showToast(HomeWorkDetailActivity.this, "获取数据失败");

            }
        }));
    }

    private void initHead(FamousClassBean famousClassBean) {
        mGradeSubjectTv.setText(famousClassBean.getGrade() + "/" + famousClassBean.getSubject());
        mSchoolNameTv.setText(famousClassBean.getSchoolName());
        mStartTimeTv.setText(famousClassBean.getDate());
    }

    private void initUploadBar() {
        if ("SCHOOL_USR".equals(mUserInfo.getUserType())) {
            mTvUpload.setVisibility(View.GONE);
        } else {
            mTvUpload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    private boolean userInputIsValid(final String serverUrlString, final String fileToUploadPath,
                                     final String paramNameString) {
        if (serverUrlString.length() == 0) {
            Toast.makeText(this, "服务器URL无效", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            new URL(serverUrlString.toString());
        } catch (Exception exc) {
            Toast.makeText(this, "服务器URL无效", Toast.LENGTH_LONG).show();
            return false;
        }

        if (fileToUploadPath.length() == 0) {
            Toast.makeText(this, "请提供需要上传的文件", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!new File(fileToUploadPath).exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            return false;
        }

        if (paramNameString.length() == 0) {
            Toast.makeText(this, "请提供参数名", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //jsonmap
    //ResourceServer/res/{areaCode}/imageUpload.do?validateCode=
    private void onUploadButtonClick(PhotoInfo filename, String url) {
        final UploadRequest request = new UploadRequest(this, UUID.randomUUID().toString(), url);
        request.addFileToUpload(filename.getPath(), filename.getName(), filename.getName(), "multipart/form-data");
        request.setNotificationConfig(R.mipmap.ic_launcher, getString(R.string.title_activity_main), "上传", "成功", "错误", true);
        request.setCustomUserAgent("UploadServiceDemo/1.0");
        request.setNotificationClickIntent(null);

        request.setMaxRetries(2);

        try {
            UploadService.startUpload(request);
        } catch (Exception exc) {
            Toast.makeText(this, "Malformed upload request. " + exc.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onCancelUploadButtonClick() {
        UploadService.stopCurrentUpload();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (resultCode == AlbumActivity.RESULT_SUCCESS) {
                    ArrayList<PhotoInfo> images = data.getExtras().getParcelableArrayList(AlbumActivity.RESULT_SELECT_INFO);
                    if (images != null && images.size() > 0) {
                        mImages.clear();
                        mImages.addAll(images);
                        mUpload.clear();
                        if (mHomeWorkDetail == null) {
                            ToastUtil.showToast(this, "上传失败！");
                            return;
                        }
                        String url = mHomeWorkDetail.getServerAddress() + "/res/" + mUserInfo.getAreaCode() + "/imageUpload.do?validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=5";//"343861a1a65143a5801e5e27b8e20095";
                        for (PhotoInfo photoInfo : mImages) {
                            onUploadButtonClick(photoInfo, url);
                        }
                    }
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWeiBoUpVideoDialogFragment.setProgress(1, mImages.size());
                            mWeiBoUpVideoDialogFragment.show(getSupportFragmentManager(), "mWeiBoUpVideoDialogFragment");
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mSender.stop();
        super.onDestroy();
    }

    class UpLoadMsg {
        /**
         * code : 0
         * message : 3ba2a4e4d6c24b46a26a935f51d77300_3ac04c908c334d6baabdb7dba48e5c5b.jpg
         * realname : IMG_20160125_170513.jpg
         * result : true
         */

        private int code;
        private String message;
        private String realname;
        private boolean result;

        public UpLoadMsg() {
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getRealname() {
            return realname;
        }

        public boolean isResult() {
            return result;
        }

        public JSONObject getJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", message);
                jsonObject.put("realname", realname);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        public String toString() {

            return "{" + "message:" + "\"" + message + "\"" + ",realname:" + "\"" + realname + "\"" + "}";
        }
    }
}
