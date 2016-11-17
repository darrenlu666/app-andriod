package com.codyy.erpsportal.groups.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.SoftKeyboardStateHelper;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.BaseHttpActivity;
import com.codyy.erpsportal.commons.exception.LogUtils;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.groups.models.entities.Group;
import com.codyy.erpsportal.groups.models.entities.GroupDetail;
import com.codyy.erpsportal.groups.models.entities.GroupDetailParse;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 应用-圈组空间详情
 * Created by poe on 16-1-18.
 */
public class GroupSpaceDetailManagerActivity extends BaseHttpActivity implements Serializable ,Handler.Callback,SoftKeyboardStateHelper.SoftKeyboardStateListener {
    private final static String TAG = "GroupSpaceDetailManagerActivity";
    public final static String EXTRA_DATA = "com.group.data";//
    private final String STR_APPROVE = "通过";
    private final String STR_REJECT = "不通过";
    private final String STR_RECOMMEND_DOOR = "推荐到门户";
    private final String STR_CANCEL_RECOMMEND = "取消推荐";
    private final String STR_CLOSE = "关闭圈组";
    private final String STR_CANCEL_CLOSE = "取消关闭";
    /**
     * 通过成功
     */
    private final int MSG_APPROVE_SUCCESS = 0x001;
    /**
     * 不通过成功
     */
    private final int MSG_REJECT_SUCCESS = 0x010;
    /**
     * 推荐成功
     */
    private final int MSG_RECOMMEND_SUCCESS = 0x021 ;
    /**
     * 取消推荐成功
     */
    private final int MSG_CANCEL_RECOMMEND_SUCCESS = 0x022;
    /**
     * 关闭圈组成功
     */
    private final int MSG_CLOSE_GROUP_SUCCESS = 0x030;
    /**
     * 取消关闭圈组成功
     */
    protected final int MSG_CANCEL_CLOSE_GROUP_SUCCESS = 0x031;

    @Bind(R.id.toolbar)Toolbar mToolBar;
    @Bind(R.id.toolbar_title)TextView mTitleTextView;
    @Bind(R.id.lin_container)LinearLayout mContainerLineLayout;//scroll view container view .
    @Bind(R.id.sdv_group_pic)SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.tv_group_name)TextView mGroupTitleTv;
    @Bind(R.id.tv_operator_left)TextView mOperatorLeftTv;//通过/推荐/取消推荐
    @Bind(R.id.tv_operator_right)TextView mOperatorRightTv;//不通过/关闭
    @Bind(R.id.tv_subject_desc)TextView mSubjectDescTv;
    @Bind(R.id.tv_subject_or_category)TextView mSubjectTv;
    @Bind(R.id.tv_detail_creator)TextView mCreatorTv;
    @Bind(R.id.tv_detail_create_time)TextView mCreateTimeTv;
    @Bind(R.id.tv_team)TextView mTeamTv;
    @Bind(R.id.tv_school)TextView mSchoolTv;
    @Bind(R.id.tv_group_desc)TextView mDescTv;
    @Bind(R.id.tv_module_desc)TextView mModuleTv;//子模块介绍 .
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    //new prompt input layout .
    @Bind(R.id.rlt_prompt_input)RelativeLayout mPromptRelativeLayout;//申请加入视图
    @Bind(R.id.iv_prompt_close)ImageView mClosePromptIv;//关闭申请输入框图标
    @Bind(R.id.tv_send_prompt)TextView mSendPromptTv;//发送按钮
    @Bind(R.id.edt_prompt_reason)EditText mInputEdt;

    private String mGroupId ;// 圈组id .
    private GroupDetail mGroupSpace ;// 圈组详情
    private Handler mHandler = new Handler(this);
    private boolean mIsKeyBoardShow = false;
    private InputMethodManager mInputManager;

    @Override
    public int obtainLayoutId() {
        return R.layout.activity_group_space_detail_manager;
    }

    @Override
    public String obtainAPI() {
        return URLConfig.GET_APPLICATION_GROUP_DETAIL;
    }

    @Override
    public HashMap<String, String> getParam() {
        HashMap<String, String> data = new HashMap<>();
        if (mUserInfo != null) {
            data.put("uuid", mUserInfo.getUuid());
        }
        if (null != mGroupId) data.put("groupId", mGroupId);
        return data;
    }

    @Override
    public void onSuccess(JSONObject response) {
        Cog.d(TAG , response.toString());
        if(null == mEmptyView) return;
        mEmptyView.setLoading(false);
        //parse data
        GroupDetailParse parse = new Gson().fromJson(response.toString(),GroupDetailParse.class);
        if(parse != null && parse.getGroupDetail() != null){
            mGroupSpace = parse.getGroupDetail();
        }

        if(null != mGroupSpace){
            refreshUI();
            mEmptyView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(VolleyError error) {
        if(null == mEmptyView) return;
        if (mGroupSpace == null) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //沉浸模式 兼容4.4
        UiMainUtils.setNavigationTintColor(this,R.color.main_green);
        mGroupId = getIntent().getStringExtra(EXTRA_DATA);
        initToolbar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsKeyBoardShow) {
                    UiMainUtils.hideKeyBoard(mInputManager);
                }
                finish();
                UIUtils.addExitTranAnim(GroupSpaceDetailManagerActivity.this);
            }
        });
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                mEmptyView.setLoading(true);
                requestData();
            }
        });
        mTitleTextView.setText(getString(R.string.group_detail));
        //申请加入输入框 .
        mInputEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        approveOrReject(Group.TYPE_OPERATE_REJECT);
                    }
                }
                return false;
            }
        });
        mContainerLineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInput();
            }
        });
        mOperatorLeftTv.setEnabled(false);
        mOperatorRightTv.setEnabled(false);
        //load data .
        requestData();
    }

    private void refreshUI() {
        ImageFetcher.getInstance(EApplication.instance()).fetchSmall(mSimpleDraweeView,mGroupSpace.getPic());
        mGroupTitleTv.setText(UIUtils.filterNull(mGroupSpace.getGroupName()));
        mCreatorTv.setText(UIUtils.filterNull(mGroupSpace.getGroupCreator()));
        mCreateTimeTv.setText(DateUtil.getDateStr(Long.valueOf(mGroupSpace.getCreateTime()),DateUtil.DEF_FORMAT));
//        mSubjectTv.setText(UIUtils.filterNull(mGroupSpace.getSubjectName()));
        String teamName = "教研组";
        if(Group.TYPE_INTEREST.equals(mGroupSpace.getGroupType())){
            teamName = "兴趣组";
        }
        mTeamTv.setText(UIUtils.filterNull(teamName));
        //bug fix by poe start 222: 与UI不一致
        mSubjectDescTv.setVisibility(View.GONE);
        //小学一年级语文
        switch (mGroupSpace.getGroupType()){
            case Group.TYPE_INTEREST:
                mSubjectDescTv.setText("分类");
                if(!TextUtils.isEmpty(mGroupSpace.getCategoryName()))mSubjectTv.append(mGroupSpace.getCategoryName());
                break;
            case Group.TYPE_TEACHING:
                mSubjectDescTv.setText("学科");
                StringBuilder tvSubject = new StringBuilder();
                if(!TextUtils.isEmpty(mGroupSpace.getSemesterName())){
                    tvSubject.append(mGroupSpace.getSemesterName());
                    if(!TextUtils.isEmpty(mGroupSpace.getGrade())){
                        tvSubject.append("/");
                    }
                }
                if(!TextUtils.isEmpty(mGroupSpace.getGrade())){
                    tvSubject.append(mGroupSpace.getGrade());
                    if(!TextUtils.isEmpty(mGroupSpace.getSubjectName())){
                        tvSubject.append("/");
                    }
                }
                if(!TextUtils.isEmpty(mGroupSpace.getSubjectName()))tvSubject.append(mGroupSpace.getSubjectName());
                mSubjectTv.setText(UIUtils.filterNull(tvSubject.toString()));
                break;
        }
        //bug fix by poe start 249: 与UI不一致
        //学校 xxx省-xxx市-xxx县-xxx校
        StringBuilder tvSB = new StringBuilder();
        if(!TextUtils.isEmpty(mGroupSpace.getAreaPath())) tvSB.append(mGroupSpace.getAreaPath());
        if(!TextUtils.isEmpty(mGroupSpace.getSchoolName())){
            tvSB.append("-");
            tvSB.append(mGroupSpace.getSchoolName());
        }
        mSchoolTv.setText(UIUtils.filterNull(tvSB.toString()));

        mDescTv.setText(UIUtils.filterNull(mGroupSpace.getGroupDesc()));
        mModuleTv.setText(UIUtils.filterNull(mGroupSpace.getModules()));

        //角色、状态判断
        setRoleAndState();
        mOperatorLeftTv.setEnabled(true);
        mOperatorRightTv.setEnabled(true);
    }

    private void setRoleAndState() {
        switch (mUserInfo.getUserType()){
            case UserInfo.USER_TYPE_AREA_USER://区域管理员
                //推荐到门户/取消推荐
                mOperatorLeftTv.setText(STR_RECOMMEND_DOOR);
                mOperatorLeftTv.setVisibility(View.VISIBLE);
                mOperatorRightTv.setVisibility(View.INVISIBLE);
                setJoinStatus();
                break;
            case UserInfo.USER_TYPE_SCHOOL_USER://学校用户
                if("Y".equals(mGroupSpace.getCloseFlag())){
                    mOperatorLeftTv.setVisibility(View.VISIBLE);
                    mOperatorLeftTv.setText(STR_CANCEL_CLOSE);
                    mOperatorRightTv.setVisibility(View.INVISIBLE);
                }else{
                    mOperatorLeftTv.setVisibility(View.VISIBLE);
                    mOperatorRightTv.setVisibility(View.VISIBLE);
                    setJoinStatus();
                }
                break;
        }
    }

    @OnClick(R.id.rlt_container)
    void goGroupSpace(){
        //判断是否通过了
        if(Group.TYPE_OPERATE_APPROVE .equals( mGroupSpace.getApproveStatus())){
            GroupSpaceActivity.start(this,"圈组",mGroupSpace.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_GROUP);
            hideInput();
        }
    }

    /**
     * 校内圈组-设置操作状态
     */
    private void setJoinStatus() {
        switch (mGroupSpace.getApproveStatus()){
            case Group.TYPE_OPERATE_APPROVE://已经通过
                if(Integer.valueOf(mGroupSpace.getRecommendCount())<1){//未推荐过
                    mOperatorLeftTv.setText(STR_RECOMMEND_DOOR);
                }else{//已经推荐过了 ->“取消推荐”
                    mOperatorLeftTv.setText(STR_CANCEL_RECOMMEND);
                }
                mOperatorRightTv.setText(STR_CLOSE);
                break;
            case Group.TYPE_OPERATE_PENDING://带审核
                mOperatorLeftTv.setText(STR_APPROVE);
                mOperatorRightTv.setText(STR_REJECT);
                break;
            case Group.TYPE_OPERATE_REJECT://不通过(只显示不通过）
                mOperatorLeftTv.setVisibility(View.INVISIBLE);
                mOperatorRightTv.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @OnClick(R.id.tv_operator_left)
    void passOrProvider(TextView tvLeft){
        switch (tvLeft.getText().toString()){
            case STR_APPROVE://通过
                approveOrReject(Group.TYPE_OPERATE_APPROVE);
                break;
            case STR_RECOMMEND_DOOR://推荐到圈组
                recommendOrCancel("true");
                break;
            case STR_CANCEL_RECOMMEND://取消推荐
                recommendOrCancel("false");
                break;
            case STR_CANCEL_CLOSE://取消关闭
                closeGroup(false);
                break;
        }
    }

    @OnClick(R.id.tv_operator_right)
    void rejectOrClose(TextView tvRight){
        switch (tvRight.getText().toString()){
            case STR_REJECT://不通过
                //弹出输入框
               /* mPromptRelativeLayout.setVisibility(View.VISIBLE);
                mPromptRelativeLayout.requestFocus();
                if (!mIsKeyBoardShow) {
                    UiMainUtils.showKeyBoard(mInputManager);
                }*/
                approveOrReject(Group.TYPE_OPERATE_REJECT);
                break;
            case STR_CLOSE://关闭圈组
                closeGroup(true);
                break;
        }
    }

    @OnClick(R.id.tv_send_prompt)
    void sendPromptReason(){
        approveOrReject(Group.TYPE_OPERATE_REJECT);
    }


    @OnClick(R.id.iv_prompt_close)
    void closePromptView(){
        hideInput();
    }

    private void hideInput() {
        mPromptRelativeLayout.setVisibility(View.GONE);
        if (mIsKeyBoardShow) {
            UiMainUtils.hideKeyBoard(mInputManager);
        }
    }
    /**
     * 通过 or 不通过
     * @param action {@link Group#TYPE_OPERATE_APPROVE,Group#TYPE_OPERATE_REJECT}
     */
    private void approveOrReject(final String action){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("uuid",mUserInfo.getUuid());
        hashMap.put("groupId",mGroupSpace.getGroupId());
        hashMap.put("approveStatus",action);
        if(null !=mInputEdt &&!TextUtils.isEmpty(mInputEdt.getText().toString())){
            hashMap.put("rejectReason",mInputEdt.getText().toString());
        }
        requestData(URLConfig.UPDATE_GROUP_CHECK, hashMap, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                if(null == mOperatorLeftTv || null == response ) return;
                String message = response.optString("message");
                if("success".equals(response.optString("result"))){
                    if(Group.TYPE_OPERATE_APPROVE.equals(action)){
                        //toast null 》
                        if(TextUtils.isEmpty(UIUtils.filterNull(message))){
                            message = "通过成功！";
                        }
                        ToastUtil.showToast(message);
                        mHandler.sendEmptyMessage(MSG_APPROVE_SUCCESS);
                    }else if(Group.TYPE_OPERATE_REJECT.equals(action)){
                        if(TextUtils.isEmpty(UIUtils.filterNull(message))){
                            message = "不通过成功！";
                        }
                        ToastUtil.showToast(message);
                        mInputEdt.setText("");
                        hideInput();
                        mHandler.sendEmptyMessage(MSG_REJECT_SUCCESS);
                    }
                }else {
                    if(TextUtils.isEmpty(UIUtils.filterNull(message))){
                            message = "审核失败！";
                    }
                    ToastUtil.showToast(message);
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                LogUtils.log(error);
            }
        });
    }

    /**
     * 推荐 or 取消推荐
     * @param action true :推荐 false :取消
     */
    private void recommendOrCancel(final String action){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("uuid",mUserInfo.getUuid());
        hashMap.put("groupId",mGroupSpace.getGroupId());
        hashMap.put("type",action);
        requestData(URLConfig.UPDATE_GROUP_RECOMMEND, hashMap, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                if(null == mOperatorLeftTv || null == response) return;
                Cog.d(TAG,response.toString());
                if("success".equals(response.optString("result"))){

                    if("true".equals(action)){
                        if(TextUtils.isEmpty(UIUtils.filterNull(response.optString("message")))){
                            ToastUtil.showToast("推荐成功！");
                        }else{
                            ToastUtil.showToast(response.optString("message"));
                        }
                        mHandler.sendEmptyMessage(MSG_RECOMMEND_SUCCESS);
                    }else if("false".equals(action)){
                        if(TextUtils.isEmpty(UIUtils.filterNull(response.optString("message")))){
                            ToastUtil.showToast("取消推荐成功！");
                        }else{
                            ToastUtil.showToast(response.optString("message"));
                        }
                        mHandler.sendEmptyMessage(MSG_CANCEL_RECOMMEND_SUCCESS);
                    }
                }else {
                    if(TextUtils.isEmpty(UIUtils.filterNull(response.optString("message")))){
                        if("true".equals(action)){
                            ToastUtil.showToast("推荐失败！");
                        }else{
                            ToastUtil.showToast("取消推荐失败！");
                        }
                    }else{
                        ToastUtil.showToast(response.optString("message"));
                    }
                    ToastUtil.showToast(response.optString("message"));
                }
            }

            @Override
            public void onRequestFailure(VolleyError error) {
                LogUtils.log(error);
            }
        });

    }

    private  void closeGroupSuccess() {
        setResult(Activity.RESULT_OK);
        finish();
        UIUtils.addExitTranAnim(GroupSpaceDetailManagerActivity.this);
    }

    /**
     *关闭圈组
     * @param close true :关闭  false :取消关闭
     */
    private void closeGroup(final boolean close){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("uuid",mUserInfo.getUuid());
        hashMap.put("groupId",mGroupSpace.getGroupId());
        hashMap.put("type",(close?"true":"false"));

        requestData(URLConfig.UPDATE_GROUP_CLOSE, hashMap, new IRequest() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                if(null == mOperatorLeftTv || null == response) return;
                Cog.d(TAG,response.toString());
                String message = response.optString("message");
                if("success".equals(response.optString("result"))){

                    if(TextUtils.isEmpty(UIUtils.filterNull(message))){
                        if(close){
                            message = "关闭成功！";
                        }else{
                            message = "取消关闭成功！";
                        }
                    }
                    ToastUtil.showToast(message);
                    mHandler.sendEmptyMessage(MSG_CLOSE_GROUP_SUCCESS);
                }else{
                    if(TextUtils.isEmpty(UIUtils.filterNull(message))){
                        if(close){
                            message = "关闭失败！";
                        }else{
                            message = "取消关闭失败！";
                        }
                    }
                    ToastUtil.showToast(message);
                }
            }
            @Override
            public void onRequestFailure(VolleyError error) {
                LogUtils.log(error);
            }
        });
    }
    /**
     * @param context context
     * @param groupId groupId
     */
    public static void start(Fragment context , String groupId ,int requestCode){
        Intent intent = new Intent(context.getActivity() , GroupSpaceDetailManagerActivity.class);
        intent.putExtra(EXTRA_DATA , groupId);
        context.startActivityForResult(intent , requestCode);
        UIUtils.addEnterAnim(context.getActivity());
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(null == mOperatorLeftTv) return false;
        switch (msg.what){
            case MSG_APPROVE_SUCCESS://通过
                mOperatorLeftTv.setText(STR_RECOMMEND_DOOR);
                mOperatorRightTv.setText(STR_CLOSE);
                break;
            case MSG_REJECT_SUCCESS://不通过
                mOperatorLeftTv.setVisibility(View.INVISIBLE);
                mOperatorRightTv.setVisibility(View.INVISIBLE);
                break;
            case MSG_RECOMMEND_SUCCESS://推荐到门户
                mOperatorLeftTv.setText(STR_CANCEL_RECOMMEND);
                break;
            case MSG_CANCEL_RECOMMEND_SUCCESS://取消推荐
                mOperatorLeftTv.setText(STR_RECOMMEND_DOOR);
                break;
            case MSG_CLOSE_GROUP_SUCCESS://关闭圈组
            case MSG_CANCEL_CLOSE_GROUP_SUCCESS://取消关闭圈组
                closeGroupSuccess();
                break;
        }
        return false;
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        mInputEdt.requestFocus();
        mIsKeyBoardShow = true;
    }

    @Override
    public void onSoftKeyboardClosed() {
        mIsKeyBoardShow = false;
        if (mInputEdt.getVisibility() == View.VISIBLE) {
            mPromptRelativeLayout.setVisibility(View.GONE);
        }
    }
}
