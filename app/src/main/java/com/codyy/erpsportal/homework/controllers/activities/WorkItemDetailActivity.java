package com.codyy.erpsportal.homework.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TaskActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskAnswerDao;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.homework.controllers.fragments.AddOverallCommentFragment;
import com.codyy.erpsportal.homework.controllers.fragments.GetOverallCommentFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemIndexDialog;
import com.codyy.erpsportal.homework.interfaces.AudioRecordClickListener;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.models.entities.student.StudentAnswersByPerson;
import com.codyy.erpsportal.homework.models.entities.task.TaskAnswer;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.homework.widgets.MySubmitDialog;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 作业题目内容
 * Created by ldh on 2016/1/15.
 */
public class WorkItemDetailActivity extends TaskActivity implements View.OnClickListener, AudioRecordClickListener {
    private static final String TAG = WorkItemDetailActivity.class.getSimpleName();
    public static final String ARG_ITEM_INFO = "itemInfo";
    public static final String ARG_STU_ANSWER_INFO = "stuAnswerInfo";
    public static final String ARG_STU_ANSWER_FILE_INFO = "stuAnswerFileInfo";
    public static final String ARG_STU_ANSWER_COMMENT = "stuAnswerComment";
    public static final String ARG_STU_COMMENT_RATING = "stuCommentRating";
    public static final String EXTRA_WORK_ID = "com.codyy.erpsportal.WORK_ID";
    public static final String EXTRA_CURRENT_STATUS = "com.codyy.erpsportal.CURRENT_STATUS";
    public static final String EXTRA_STUDENT_ID = "com.codyy.erpsportal.EXTRA_STUDENT_ID";
    public static final String EXTRA_IS_TEACHER_READ = "com.codyy.erpsportal.EXTRA_IS_TEACHER_READ";
    public static final String EXTRA_TITLE = "com.codyy.erpsportal.ARG_EXTRA_TITLE";

    private View mBottomView;//底部状态栏
    private View mBottomAudioView;//底部录制语音状态栏
    private TextView mCurrentIndexTv;//当前索引 如 1/24
    private TextView mSubmitTv;//提交
    private TextView mWorkInfoTv;//作业信息
    private DialogUtil mProgressDialog;//上传资源时的等待进度条

    private TaskReadDao mTaskReadDao = null;
    private TaskAnswerDao mTaskAnswerDao = null;
    private AddOverallCommentFragment mAddOverallCommentFragment = null;
    private ViewPager.OnPageChangeListener mListener;

    private int mCurrentIndex = 0;//当前题号
    private int mPreIndex = -1;//前一个浏览的题号
    private int mTotalItemCount;//题目总数
    private List<ItemInfoClass> mItemInfoList;//题目信息
    private StudentAnswersByPerson mStudentAnswersByPerson;//学生答案信息
    private List<TaskReadDao.TaskItemReadInfo> mWorkItemReadInfoList = null;//本地获取批阅信息
    private String mWorkId;//作业id
    private String mCurrentStatus;//当前的状态
    private String mStudentId;//学生id
    private String mIsTeacherRead;//是否老师批阅
    private String mTitle;//标题
    private boolean mIsCommentPage;//是否有总体评论页面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Cog.d(TAG, "onCreate()~");
        //获取workId和当前状态参数
        mWorkId = getIntent().getStringExtra(EXTRA_WORK_ID);
        mCurrentStatus = getIntent().getStringExtra(EXTRA_CURRENT_STATUS);
        mStudentId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        mIsTeacherRead = getIntent().getStringExtra(EXTRA_IS_TEACHER_READ);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        if (mStudentId != null && WorkItemDetailFragment.isShowStuAnswer(mCurrentStatus)) {
            mStudentAnswersByPerson = loadStuAnswerData(mWorkId, mStudentId);//学生答案信息
        }
        super.onCreate(savedInstanceState);
        mTaskReadDao = TaskReadDao.newInstance(this);
        mTaskAnswerDao = TaskAnswerDao.getInstance(this);
    }

    @Override
    protected void onViewBound() {
        Cog.d(TAG, "onViewBound()~");
        super.onViewBound();
        mListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                mCurrentIndexTv.setText(WorkUtils.switchStr(getResources().getColor(R.color.main_color), mCurrentIndex > mTotalItemCount - 1 ? mTotalItemCount - 1 : mCurrentIndex, mTotalItemCount));
                mBottomAudioView.setVisibility(View.INVISIBLE);
                mBottomView.setVisibility(View.VISIBLE);
                if (position == mTotalItemCount && mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ)
                        || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ)) {//当最后一个fragment为AddOverallCommentFragment时
                    getOverallCommentFragment();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        //设置title
        setCustomTitle(mTitle);
        mPager.addOnPageChangeListener(mListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUnreadTip();
            }
        });
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    @Override
    protected String getUrl() {
        return URLConfig.GET_HOMEWORK_QUESTION;
    }

    /**
     * 获取请求参数
     *
     * @param params
     */
    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("workId", mWorkId);
    }

    @Override
    protected void onDestroy() {
        Cog.d(TAG, "onDestroy()~");
        if (mListener != null) {
            mPager.removeOnPageChangeListener(mListener);
        }
        super.onDestroy();
    }

    /**
     * 点击返回按钮，若当前在批阅状态，则弹出提示
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            openUnreadTip();
        }
        return false;
    }

    /**
     * 提示“未批阅完成”
     */
    private void openUnreadTip() {
        if (WorkItemDetailFragment.STATUS_STU_READ.equals(mCurrentStatus) || WorkItemDetailFragment.STATUS_TEACHER_READ.equals(mCurrentStatus)) {
            MySubmitDialog unReadBackDialog = MySubmitDialog.newInstance("您还未批阅完，确定退出吗？", "退出", "取消", MySubmitDialog.DIALOG_STYLE_TYPE_3, new MySubmitDialog.OnclickListener() {
                @Override
                public void leftClick(MySubmitDialog myDialog) {
                    myDialog.dismiss();
                }

                @Override
                public void rightClick(MySubmitDialog myDialog) {
                    myDialog.dismiss();
                    WorkItemDetailActivity.this.finish();
                }

                @Override
                public void dismiss() {

                }
            });
            unReadBackDialog.show(getSupportFragmentManager(), "show");
        }else{
            finish();
            UIUtils.addExitTranAnim(this);
        }
    }

    @Deprecated
    private List<ItemInfoClass> filterReadItemByAnswer(List<ItemInfoClass> itemList, StudentAnswersByPerson studentAnswersByPerson) {
        List<ItemInfoClass> newList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getWorkItemId().equals("")) {
                if (!getIsReadByWorkItemType(itemList.get(i).getWorkItemType(), studentAnswersByPerson)) {
                    newList.add(itemList.get(i));
                }
            } else if (!getIsReadByWorkItemId(itemList.get(i).getWorkItemId(), studentAnswersByPerson)) {
                newList.add(itemList.get(i));
            }
        }
        return newList;
    }

    private boolean getIsReadByWorkItemId(String workItemId, StudentAnswersByPerson studentAnswersByPerson) {
        boolean isRead = false;
        if (studentAnswersByPerson == null) return false;
        for (int i = 0; i < studentAnswersByPerson.getNomalAnswerList().size(); i++) {
            if (workItemId.equals(studentAnswersByPerson.getNomalAnswerList().get(i).getWorkQuestionId())) {
                isRead = studentAnswersByPerson.getNomalAnswerList().get(i).getReadOverFlag().equals("Y");
                break;
            }
        }
        return isRead;
    }

    private boolean getIsReadByWorkItemType(String workItemType, StudentAnswersByPerson studentAnswersByPerson) {
        boolean isRead = false;
        if (workItemType.equals(TaskFragment.TYPE_TEXT)) {
            isRead = studentAnswersByPerson != null && studentAnswersByPerson.getTextReadOverFlag().equals("Y");
        } else if (workItemType.equals(TaskFragment.TYPE_FILE)) {
            isRead = studentAnswersByPerson != null && studentAnswersByPerson.getDocReadOverFlag().equals("Y");
        }
        return isRead;
    }

    @Override
    protected void addFragments(JSONObject response) {
        Cog.d(TAG, response);
        mItemInfoList = ItemInfoClass.parseResponse(response);
        /*if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ)) {//当为批阅状态时，要去除已经批阅的题目
            mItemInfoList = filterReadItemByAnswer(mItemInfoList, mStudentAnswersByPerson);
        }*/
        boolean isReadHomeWork = mTaskReadDao.query(mStudentId, mWorkId).size() != 0;
        boolean isDoHomeWork = mTaskAnswerDao.query(mStudentId, mWorkId).size() != 0;
        for (int i = 0; i < mItemInfoList.size(); i++) {
            mTotalItemCount = mItemInfoList.size();
            ItemInfoClass itemInfoClass = mItemInfoList.get(i);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_ITEM_INFO, itemInfoClass);
            if (mStudentAnswersByPerson != null) {
                bundle.putParcelableArrayList(ARG_STU_ANSWER_FILE_INFO, (ArrayList) mStudentAnswersByPerson.getDocAnswerList());
                bundle.putParcelable(ARG_STU_ANSWER_INFO, mStudentAnswersByPerson);
            }
            bundle.putString(TaskFragment.ARG_STUDENT_ID, mStudentId);
            bundle.putString(TaskFragment.ARG_TASK_STATUS, mCurrentStatus);//传递当前状态
            bundle.putString(TaskFragment.ARG_WORK_ID, mWorkId);
            bundle.putString(TaskFragment.ARG_IS_TEACHER_READ, mIsTeacherRead);
            bundle.putString(TaskFragment.ARG_COMMENT, queryReadInfo(mStudentId, mWorkId, itemInfoClass.getWorkItemId()));
            if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_PROGRESS)) {//当学生做题时，首先将该套题目的所有习题的信息存储到本地
                TaskAnswerDao mTaskAnswerDao = TaskAnswerDao.getInstance(this);
                if (!isDoHomeWork) {//判断是否已经插入空的答题记录，无记录，则批量插入
                    mTaskAnswerDao.insert(mStudentId, mWorkId, itemInfoClass.getWorkItemType().equals(TaskFragment.TYPE_TEXT) ? mWorkId : itemInfoClass.getWorkItemId(), itemInfoClass.getWorkItemType(), "", "", "", "", "", "");
                }
            }
            if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ)) {
                TaskReadDao mTaskReadDao = TaskReadDao.newInstance(this);
                if (!isReadHomeWork) {//判断是否已经插入空的批阅记录，无记录，则批量插入
                    mTaskReadDao.insert(itemInfoClass.getWorkItemType(), mStudentId, mWorkId, itemInfoClass.getWorkItemId(), "", "");
                }
            }
            addFragment("", WorkItemDetailFragment.class, bundle);
            WorkItemDetailFragment.setAudioRecordClickListener(this);
        }
        if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ)) {
            Bundle bd = new Bundle();
            bd.putString(TaskFragment.ARG_STUDENT_ID, mStudentId);
            bd.putString(TaskFragment.ARG_WORK_ID, mWorkId);
            bd.putString(AddOverallCommentFragment.ARG_ROLE, mIsTeacherRead);
            addFragment("", AddOverallCommentFragment.class, bd);//添加总体评价页面（可编辑状态）
            AddOverallCommentFragment.setAudioRecordClickListener(this);
            mIsCommentPage = true;
        }

        if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_STATISTIC) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ_BROWSER)
                || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_PAR_CHECKED) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_CHECKED)) {
            Bundle bundle = new Bundle();
            if (mStudentAnswersByPerson != null) {
                bundle.putString(ARG_STU_ANSWER_COMMENT, mStudentAnswersByPerson.getTotalComment());
                bundle.putInt(ARG_STU_COMMENT_RATING, 4);
            }
            addFragment("", GetOverallCommentFragment.class, bundle);//添加总体评价（非编辑状态）
            mIsCommentPage = true;
        }
        if ((mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_VIEW) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ)) && mTotalItemCount == 0) {
            mBottomView = getLayoutInflater().inflate(R.layout.activity_work_item_detail_bottom_2, null);
            mBottomView.setVisibility(View.GONE);
            ToastUtil.showToast(this, "无题目信息");
        } else if (mTotalItemCount == 0) {
            mBottomView = getLayoutInflater().inflate(R.layout.activity_work_item_detail_bottom_3, null);
            mSubmitTv = (TextView) mBottomView.findViewById(R.id.btn_submit);
            mSubmitTv.setText(getResources().getString(R.string.work_read_submit));
            mSubmitTv.setOnClickListener(this);
            //添加载入动画
            setViewAnim(true, mSubmitTv);
        } else if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_PROGRESS) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ)//添加底部控件（1/24 | 提交）.当老师批阅、学生批阅以及学生答题时，有“提交”按钮
                || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ) && mTotalItemCount > 0) {
            mBottomView = getLayoutInflater().inflate(R.layout.activity_work_item_detail_bottom, null);
            mSubmitTv = (TextView) mBottomView.findViewById(R.id.btn_submit);
            mSubmitTv.setOnClickListener(this);
            mSubmitTv.setText(getResources().getString(R.string.work_read_submit));
            mCurrentIndexTv = (TextView) mBottomView.findViewById(R.id.btn_item_index);
            mCurrentIndexTv.setOnClickListener(this);
            //添加载入动画
            setViewAnim(true, mCurrentIndexTv, mSubmitTv);
            //设置进度的style
            mCurrentIndexTv.setText(WorkUtils.switchStr(getResources().getColor(R.color.main_color), mCurrentIndex, mTotalItemCount));
        } else {
            mBottomView = getLayoutInflater().inflate(R.layout.activity_work_item_detail_bottom_2, null);
            mCurrentIndexTv = (TextView) mBottomView.findViewById(R.id.btn_item_index);
            //mWorkInfoTv = (TextView) mBottomView.findViewById(R.id.btn_work_info);
            //mWorkInfoTv.setOnClickListener(this);
            mCurrentIndexTv.setOnClickListener(this);
            //添加载入动画
            setViewAnim(true, mCurrentIndexTv);
            //设置进度的style
            mCurrentIndexTv.setText(WorkUtils.switchStr(getResources().getColor(R.color.main_color), mCurrentIndex, mTotalItemCount));
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 48));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRelativeLayout.addView(mBottomView, params);
        //添加“按住说话”栏
        mBottomAudioView = getLayoutInflater().inflate(R.layout.activity_work_item_detail_bottom_4, null);
        mBottomAudioView.setVisibility(View.INVISIBLE);
        mRelativeLayout.addView(mBottomAudioView, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_item_index://跳转到习题列表界面
                if (!mCurrentIndexTv.getText().equals(getResources().getString(R.string.work_read_go_on))) {//点击“1/24”
                    Bundle bundle = new Bundle();
                    ArrayList<ItemInfoClass> mItemInfoNewList;
                    if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ) || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ)) {//批阅，显示批过的题目颜色
                        mWorkItemReadInfoList = mTaskReadDao.query(mStudentId, mWorkId);
                        mItemInfoNewList = switchList(switchListToReadList(mWorkItemReadInfoList, mItemInfoList), 0, 0, false);
                    } else if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_PROGRESS)) {//做题，显示已做题目的颜色
                        List<TaskAnswer> mTaskAnswerList = mTaskAnswerDao.query(mStudentId, mWorkId);
                        mItemInfoNewList = switchList(switchListToDoneList(mTaskAnswerList, mItemInfoList), 0, 0, false);
                    } else {//查看，显示当前题目颜色
                        mItemInfoNewList = switchList((ArrayList<ItemInfoClass>) mItemInfoList, mCurrentIndex, mPreIndex, true);
                    }
                    mPreIndex = mCurrentIndex;
                    bundle.putParcelableArrayList(WorkItemIndexDialog.ARG_DATA, mItemInfoNewList);
                    bundle.putBoolean(WorkItemIndexDialog.ARG_HAS_TOTAL_COMMENT, mIsCommentPage);
                    bundle.putInt(WorkItemIndexDialog.ARG_ITEM_COUNT, mTotalItemCount);
                    bundle.putBoolean(WorkItemIndexDialog.ARG_SHOW_FIRST_PAGE, true);
                    final WorkItemIndexDialog dialog = new WorkItemIndexDialog();
                    dialog.setArguments(bundle);
                    dialog.setOnItemIndexClickListener(new WorkItemIndexDialog.onItemIndexClickListener() {
                        @Override
                        public void onBtnItemIndexClick(int itemIndex) {
                            mCurrentIndexTv.setText(WorkUtils.switchStr(getResources().getColor(R.color.main_color), itemIndex, mTotalItemCount));
                            mPager.setCurrentItem(itemIndex, true);
                            dialog.dismiss();
                        }
                    });
                    dialog.show(getSupportFragmentManager(), WorkItemIndexDialog.TAG);
                }
                break;
            case R.id.btn_submit://提交作业/批阅
                if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_READ)
                        || mCurrentStatus.equals(WorkItemDetailFragment.STATUS_TEACHER_READ)) {//老师或学生批阅状态
                    submitReadInfos();
                } else if (mCurrentStatus.equals(WorkItemDetailFragment.STATUS_STU_PROGRESS)) {//学生做题状态
                    submitAnswerInfo();
                }
                break;
        }
    }

    private void getOverallCommentFragment() {
        int fragmentCount = getSupportFragmentManager().getFragments().size();
        for (int j = 0; j < fragmentCount; j++) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(j);
            if (mAddOverallCommentFragment == null && fragment instanceof AddOverallCommentFragment) {
                mAddOverallCommentFragment = (AddOverallCommentFragment) fragment;
                break;
            }
        }
    }

    private void submitReadInfos() {
        getOverallCommentFragment();
        if (mAddOverallCommentFragment != null) {
            if (mAddOverallCommentFragment.getCommentEditTextView().getText().length() <= 0) {
                ToastUtil.showToast(this, getResources().getString(R.string.work_read_no_overall_comment));
            } else {
                mOverallComment = mAddOverallCommentFragment.getCommentEditTextView().getText().toString();
                submitAllComment();//提交批阅信息
            }
        } else {
            ToastUtil.showToast(this, getResources().getString(R.string.work_read_no_overall_comment));
        }
    }

    /**
     * 批量提交批阅信息
     */
    private String mOverallComment = "";//总评

    private void submitAllComment() {
        mWorkItemReadInfoList = mTaskReadDao.query(mStudentId, mWorkId);//获取本地批阅信息
        int mReadNum = getAlreadyReadNum(mWorkItemReadInfoList);
        int needReadNum = getSubjectItemNum(mItemInfoList, mStudentAnswersByPerson);//haveAttachement(mItemInfoList) ? mItemInfoList.size() - 1 : mItemInfoList.size();
        String dialogTitle = mReadNum < needReadNum ?
                getResources().getString(R.string.work_read_submit_tip) : getResources().getString(R.string.work_answer_submit_tip_2);
        MySubmitDialog unReadDialog = MySubmitDialog.newInstance(dialogTitle, MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
            @Override
            public void leftClick(MySubmitDialog myDialog) {
                myDialog.dismiss();
            }

            @Override
            public void rightClick(MySubmitDialog myDialog) {
                requestSubmitRead();
                myDialog.dismiss();
            }

            @Override
            public void dismiss() {

            }
        });
        unReadDialog.show(getSupportFragmentManager(), "submitReadInfos");
    }

    /**
     * 获取未批阅主观题个数
     *
     * @param itemInfoList
     * @return
     */
    private int getSubjectItemNum(List<ItemInfoClass> itemInfoList, StudentAnswersByPerson studentAnswersByPerson) {
        int num = 0;
        if (studentAnswersByPerson == null) return 0;
        for (int i = 0; i < itemInfoList.size(); i++) {
            if (WorkUtils.isSubjective(itemInfoList.get(i).getWorkItemType())) {
                if (itemInfoList.get(i).getWorkItemType().equals(TaskFragment.TYPE_TEXT)) {
                    if ("".equals(studentAnswersByPerson.getTextAnswerComment()))
                        num++;
                } else if (studentAnswersByPerson.getNomalAnswerList() != null) {
                    for (int j = 0; j < studentAnswersByPerson.getNomalAnswerList().size(); j++) {
                        if (itemInfoList.get(i).getWorkItemId().equals(studentAnswersByPerson.getNomalAnswerList().get(j).getWorkQuestionId())) {
                            if ("".equals(studentAnswersByPerson.getNomalAnswerList().get(j).getComment()))
                                num++;
                        }
                    }
                }
            }
        }
        return num;
    }

    private void requestSubmitRead() {
        if (mProgressDialog == null) {
            mProgressDialog = new DialogUtil(this);
            mProgressDialog.showDialog();
        } else {
            mProgressDialog.showDialog();
        }
        RequestSender requestSender = new RequestSender(this);
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        params.put("workId", mWorkId);
        params.put("studentId", mStudentId);
        JSONArray readArray = new JSONArray();
        for (int i = 0; i < mWorkItemReadInfoList.size(); i++) {
            Map<String, String> readMap = new HashMap<>();
            readMap.put("workItemId", mWorkItemReadInfoList.get(i).getTaskItemId());
            readMap.put("readContent", StringUtils.replaceHtml2(mWorkItemReadInfoList.get(i).getTaskItemReadComment()));
            readMap.put("isTextType", mWorkItemReadInfoList.get(i).getTaskItemType().equals(TaskFragment.TYPE_TEXT) ? "true" : "false");
            JSONObject readObject = new JSONObject(readMap);
            readArray.put(readObject);
        }
        params.put("readList", readArray.toString());
        params.put("totalContent", StringUtils.replaceHtml2(mOverallComment));
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.SUBMIT_READ_INFOS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optString("result").equals("success") && mToolbar != null) {
                    if (mProgressDialog != null) {
                        mProgressDialog.cancel();
                    }
                    mTaskReadDao.delete(mStudentId, mWorkId);
                    EventBus.getDefault().post(TAG + " read" + (mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? " Y" : " N"));  //提交成功后返回列表界面
                    finish();
                } else {
                    if (mProgressDialog != null) {
                        mProgressDialog.cancel();
                    }
                    ToastUtil.showToast(WorkItemDetailActivity.this, getResources().getString(R.string.work_answer_submit_failure));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                ToastUtil.showToast(WorkItemDetailActivity.this, getResources().getString(R.string.work_answer_submit_failure));
            }
        }));
    }

    private int getAlreadyReadNum(List<TaskReadDao.TaskItemReadInfo> mWorkItemReadInfoList) {
        int readNum = 0;
        for (int i = 0; i < mWorkItemReadInfoList.size(); i++) {
            TaskReadDao.TaskItemReadInfo taskItemReadInfo = mWorkItemReadInfoList.get(i);
            if (!taskItemReadInfo.getTaskItemReadComment().equals("")) {
                readNum++;
            }
        }
        return readNum;
    }

    private Map<String, String> mAnswerParams;
    private int mAnswerNum;//已做题目数量

    private Map<String, String> getParams() {
        mAnswerNum = 0;
        List<TaskAnswer> mTaskAnswers = mTaskAnswerDao.query(mStudentId, mWorkId);
        List<TaskPicInfo> textPicInfos = mTaskAnswerDao.queryPicInfo(mStudentId, mWorkId, "", TaskFragment.TYPE_TEXT);
        mAnswerParams = new HashMap<>();
        mAnswerParams.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        mAnswerParams.put("workId", mWorkId);
        JSONArray textImageArray = new JSONArray();//文本题图片信息
        for (int i = 0; i < textPicInfos.size(); i++) {
            Map<String, String> imgInfo = new HashMap<>();
            imgInfo.put("imgName", textPicInfos.get(i).getImageName());
            imgInfo.put("imgUrl", textPicInfos.get(i).getImageUrl());
            JSONObject textImageObject = new JSONObject(imgInfo);
            textImageArray.put(textImageObject);
        }
        mAnswerParams.put("imgList", textImageArray.toString());

        JSONArray questionAnswerArray = new JSONArray();//普通题信息
        for (int i = 0; i < mTaskAnswers.size(); i++) {
            TaskAnswer taskAnswer = mTaskAnswers.get(i);
            if (taskAnswer.getTaskItemType().equals(WorkItemDetailFragment.TYPE_TEXT)) {
                mAnswerParams.put("textAnswer", StringUtils.replaceHtml2(taskAnswer.getStudentTextAnswer()));
                if (!taskAnswer.getStudentTextAnswer().equals("") || textPicInfos.size() > 0) {
                    mAnswerNum = mAnswerNum + 1;
                }
            } else {
                List<TaskPicInfo> picInfos = mTaskAnswerDao.queryPicInfo(mStudentId, mWorkId, taskAnswer.getTaskItemId(), taskAnswer.getTaskItemType());
                JSONArray questionPicArray = new JSONArray();
                for (int j = 0; j < picInfos.size(); j++) {
                    Map<String, String> imgInfo = new HashMap<>();
                    imgInfo.put("imgName", picInfos.get(j).getImageName());
                    imgInfo.put("imgUrl", picInfos.get(j).getImageUrl());
                    JSONObject questionImageObject = new JSONObject(imgInfo);
                    questionPicArray.put(questionImageObject);
                }

                Map<String, String> questionAnswerInfo = new HashMap<>();
                questionAnswerInfo.put("workItemId", taskAnswer.getTaskItemId());
                questionAnswerInfo.put("workItemType", taskAnswer.getTaskItemType());
                questionAnswerInfo.put("studentAnswer", StringUtils.replaceHtml2(taskAnswer.getStudentAnswer()));
                questionAnswerInfo.put("answerVideo", taskAnswer.getResourceName());
                questionAnswerInfo.put("answerVideoResId", taskAnswer.getResourceId());
                questionAnswerInfo.put("imgList", questionPicArray.toString());
                if (!taskAnswer.getStudentAnswer().equals("") || picInfos.size() > 0 || !taskAnswer.getResourceId().equals("")) {
                    mAnswerNum = mAnswerNum + 1;
                }
                JSONObject questionAnswerObject = new JSONObject(questionAnswerInfo);
                questionAnswerArray.put(questionAnswerObject);
            }
        }
        mAnswerParams.put("answerCount", String.valueOf(mAnswerNum));
        mAnswerParams.put("questionAnswer", questionAnswerArray.toString());
        return mAnswerParams;
    }

    //提交学生答题答案
    private void submitAnswerInfo() {
        mAnswerParams = getParams();
        MySubmitDialog unFinishDialog = MySubmitDialog.newInstance(mAnswerNum < mItemInfoList.size() ?
                        getResources().getString(R.string.work_answer_submit_tip) : getResources().getString(R.string.work_answer_submit_tip_2),
                MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
                    @Override
                    public void leftClick(MySubmitDialog myDialog) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void rightClick(MySubmitDialog myDialog) {
                        requestSubmitAnswer();
                        myDialog.dismiss();
                    }

                    @Override
                    public void dismiss() {

                    }
                });
        unFinishDialog.show(getSupportFragmentManager(), "submitAnswers");
    }

    private void requestSubmitAnswer() {
        if (mProgressDialog == null) {
            mProgressDialog = new DialogUtil(this);
            mProgressDialog.showDialog();
        } else {
            mProgressDialog.showDialog();
        }
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.SUBMIT_STUDENT_ANSWER, mAnswerParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optString("result").equals("success") && mToolbar != null) {
                    if (mProgressDialog != null) {
                        mProgressDialog.cancel();
                    }
                    mTaskAnswerDao.deleteTaskInfo(mStudentId, mWorkId);  //删除本地答案
                    mTaskAnswerDao.deleteAllPicInfo(mStudentId, mWorkId);//删除本地图片记录
                    EventBus.getDefault().post(TAG + " do");  //提交成功后返回列表界面
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                if (mProgressDialog != null) {
                    mProgressDialog.cancel();
                }
                ToastUtil.showToast(WorkItemDetailActivity.this, getResources().getString(R.string.work_answer_submit_failure));
            }
        }, false));
    }

    public static void startActivity(Context context, String workId, String status, @Nullable String studentId, String readType, String mTitle) {
        Intent intent = new Intent(context, WorkItemDetailActivity.class);
        intent.putExtra(EXTRA_WORK_ID, workId);
        intent.putExtra(EXTRA_CURRENT_STATUS, status);
        intent.putExtra(EXTRA_STUDENT_ID, studentId);
        intent.putExtra(EXTRA_IS_TEACHER_READ, readType);
        intent.putExtra(EXTRA_TITLE, mTitle);
        context.startActivity(intent);
    }


    /**
     * 提取出已批阅的题目
     *
     * @param mWorkItemReadInfoList
     * @param itemInfoList
     * @return
     */
    private ArrayList<ItemInfoClass> switchListToReadList(List<TaskReadDao.TaskItemReadInfo> mWorkItemReadInfoList, List<ItemInfoClass> itemInfoList) {
        for (int i = 0; i < mWorkItemReadInfoList.size(); i++) {
            TaskReadDao.TaskItemReadInfo taskItemReadInfo = mWorkItemReadInfoList.get(i);
            for (int j = 0; j < itemInfoList.size(); j++) {
                if (itemInfoList.get(j).getWorkItemId().equals(taskItemReadInfo.getTaskItemId())) {
                    if (!("").equals(taskItemReadInfo.getTaskItemReadComment())) {
                        itemInfoList.get(j).setColor(getResources().getColor(R.color.work_read_index_color));
                        break;
                    } else {
                        itemInfoList.get(j).setColor(getResources().getColor(R.color.work_read_item_stu_list_back_color));
                    }
                }
            }
        }
        return (ArrayList<ItemInfoClass>) itemInfoList;
    }

    /**
     * 提取出已答题目
     *
     * @param taskAnswerList
     * @param itemInfoList
     * @return
     */
    private ArrayList<ItemInfoClass> switchListToDoneList(List<TaskAnswer> taskAnswerList, List<ItemInfoClass> itemInfoList) {
        for (int i = 0; i < itemInfoList.size(); i++) {
            ItemInfoClass itemInfoClass = itemInfoList.get(i);
            itemInfoClass.setColor(getResources().getColor(R.color.work_item_index_color));
            if (itemInfoClass.getWorkItemType().equals(WorkItemDetailFragment.TYPE_TEXT)) {
                for (int j = 0; j < taskAnswerList.size(); j++) {
                    if (taskAnswerList.get(j).getTaskItemType().equals(WorkItemDetailFragment.TYPE_TEXT)) {
                        if (!taskAnswerList.get(j).getStudentTextAnswer().equals("")) {
                            itemInfoClass.setColor(getResources().getColor(R.color.work_read_index_color));
                            break;
                        } else {
                            List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mWorkId, taskAnswerList.get(j).getTaskItemId(), WorkItemDetailFragment.TYPE_TEXT);
                            if (picInfoList.size() > 0)
                                itemInfoClass.setColor(getResources().getColor(R.color.work_read_index_color));
                            break;
                        }
                    }
                }
            } else {
                for (int j = 0; j < taskAnswerList.size(); j++) {
                    if (taskAnswerList.get(j).getTaskItemId().equals(itemInfoClass.getWorkItemId())) {
                        if (!taskAnswerList.get(j).getStudentAnswer().equals("") || !taskAnswerList.get(j).getResourceLocalPath().equals("")) {
                            itemInfoClass.setColor(getResources().getColor(R.color.work_read_index_color));
                            break;
                        } else {
                            List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mWorkId, taskAnswerList.get(j).getTaskItemId(), taskAnswerList.get(j).getTaskItemType());
                            if (picInfoList.size() > 0)
                                itemInfoClass.setColor(getResources().getColor(R.color.work_read_index_color));
                            break;
                        }
                    }
                }
            }
        }
        return (ArrayList<ItemInfoClass>) itemInfoList;
    }

    /**
     * 重新封装题目信息，以便题目索引界面使用
     *
     * @param list
     * @param currentIndex
     * @param preIndex
     * @param isView
     * @return
     */
    @Nullable
    private ArrayList<ItemInfoClass> switchList(ArrayList<ItemInfoClass> list, int currentIndex, int preIndex, boolean isView) {
        ArrayList<ItemInfoClass> newList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ItemInfoClass itemInfoClass = list.get(i);
                if (i == 0) {
                    ItemInfoClass titleItemInfo = changeItemType(itemInfoClass.getWorkItemType());
                    newList.add(titleItemInfo);
                    newList.add(itemInfoClass);
                } else {
                    if (!itemInfoClass.getWorkItemType().equals(list.get(i - 1).getWorkItemType())) {
                        ItemInfoClass titleItemInfo = changeItemType(itemInfoClass.getWorkItemType());
                        newList.add(titleItemInfo);
                        newList.add(itemInfoClass);
                    } else
                        newList.add(itemInfoClass);
                }
                if (isView) {
                    if (currentIndex == i) {//找出被选中的题目，将其置红
                        itemInfoClass.setColor(getResources().getColor(R.color.work_item_index_color_selected));
                    }
                    if (preIndex == i && preIndex != currentIndex) {//找出前一个被选中的题目，将其置灰
                        itemInfoClass.setColor(getResources().getColor(R.color.work_item_index_color));
                    }
                }
            }
            return newList;
        }
        return null;
    }


    /**
     * 设置题目类型
     *
     * @param workItemType
     * @return
     */
    private ItemInfoClass changeItemType(String workItemType) {
        ItemInfoClass titleItemInfo = new ItemInfoClass();
        switch (workItemType) {
            case TaskFragment.TYPE_SINGLE_CHOICE:
                titleItemInfo.setWorkItemType("单选题");
                break;
            case TaskFragment.TYPE_MULTI_CHOICE:
                titleItemInfo.setWorkItemType("多选题");
                break;
            case TaskFragment.TYPE_JUDGEMENT:
                titleItemInfo.setWorkItemType("判断题");
                break;
            case TaskFragment.TYPE_FILL_IN_BLANK:
                titleItemInfo.setWorkItemType("填空题");
                break;
            case TaskFragment.TYPE_ASK_ANSWER:
                titleItemInfo.setWorkItemType("问答题");
                break;
            case TaskFragment.TYPE_COMPUTING:
                titleItemInfo.setWorkItemType("计算题");
                break;
            case TaskFragment.TYPE_TEXT:
                titleItemInfo.setWorkItemType("文本题");
                break;
            case TaskFragment.TYPE_FILE:
                titleItemInfo.setWorkItemType("附件题");
                break;
        }
        return titleItemInfo;
    }

    @Deprecated
    private boolean haveAttachement(List<ItemInfoClass> itemInfoList) {
        for (int i = 0; i < itemInfoList.size(); i++) {
            if (itemInfoList.get(i).getWorkItemType().equals(WorkItemDetailFragment.TYPE_TEXT))
                return true;
        }
        return false;
    }

    /**
     * 获取按人批阅时学生答案列表
     *
     * @param workId
     * @param studentId
     * @return
     */
    private StudentAnswersByPerson loadStuAnswerData(String workId, String studentId) {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        params.put("workId", workId);
        params.put("studentId", studentId);
        RequestSender requestSender = new RequestSender(this);
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_STUDENT_ANSWER, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optString("result").equals("success") && mToolbar != null) {
                    mStudentAnswersByPerson = StudentAnswersByPerson.parseResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
        return mStudentAnswersByPerson;
    }

    /**
     * 切换底部状态栏
     */
    private void toggleBottomBar() {
        mBottomAudioView.setVisibility(mBottomAudioView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        mBottomView.setVisibility(mBottomView.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void OnImageClick() {
        toggleBottomBar();
    }

    /**
     * 在本地查询已经做过的点评
     *
     * @return
     */
    private String queryReadInfo(String studentId, String taskId, String taskItemId) {
        List<TaskReadDao.TaskItemReadInfo> list = mTaskReadDao.query(studentId, taskId);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTaskItemId().equals(taskItemId)) {
                return list.get(i).getTaskItemReadComment();
            }
        }
        return "";
    }
}
