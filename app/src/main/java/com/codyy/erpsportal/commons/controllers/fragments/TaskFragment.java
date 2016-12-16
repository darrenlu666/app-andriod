package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.ScreenUtils;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.rethink.controllers.activities.SubjectMaterialPicturesActivity;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.utils.HtmlUtils;
import com.codyy.erpsportal.exam.controllers.activities.media.MMSelectorActivity;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.controllers.activities.media.audio.MMAudioAlbumFragment;
import com.codyy.erpsportal.exam.controllers.activities.media.image.MMImageAlbumFragment;
import com.codyy.erpsportal.exam.controllers.activities.media.image.MMImageBean;
import com.codyy.erpsportal.exam.controllers.activities.media.video.MMVideoAlbumFragment;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.exam.utils.MediaCheck;
import com.codyy.erpsportal.exam.widgets.richtext.RichText;
import com.codyy.erpsportal.homework.interfaces.AudioRecordClickListener;
import com.codyy.erpsportal.homework.controllers.activities.PreviewImageActivity;
import com.codyy.erpsportal.homework.controllers.activities.VideoViewActivity;
import com.codyy.erpsportal.homework.controllers.fragments.AddOverallCommentFragment;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.models.entities.student.ImageDetail;
import com.codyy.erpsportal.homework.models.entities.student.StudentAnswersByPerson;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.homework.widgets.AudioBar;
import com.codyy.erpsportal.homework.widgets.DialogUtils;
import com.codyy.erpsportal.homework.widgets.MySubmitDialog;
import com.codyy.erpsportal.homework.widgets.ProgressCircle;
import com.codyy.erpsportal.homework.widgets.UploadAsyncTask;
import com.codyy.erpsportal.homework.widgets.WorkAnswerMediaPlayer;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskAnswerDao;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.codyy.erpsportal.homework.models.entities.task.TaskAnswer;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.codyy.erpsportal.commons.widgets.MyRadioGroup;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 习题碎片
 * Created by eachann on 2015/12/30.
 */
public class TaskFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = TaskFragment.class.getSimpleName();
    /**
     * 单选题
     */
    public static final String TYPE_SINGLE_CHOICE = "SINGLE_CHOICE";
    /**
     * 多选题
     */
    public static final String TYPE_MULTI_CHOICE = "MULTI_CHOICE";
    /**
     * 判断题
     */
    public static final String TYPE_JUDGEMENT = "JUDEMENT";
    /**
     * 填空题
     */
    public static final String TYPE_FILL_IN_BLANK = "FILL_IN_BLANK";
    /**
     * 问答题
     */
    public static final String TYPE_ASK_ANSWER = "ASK_ANSWER";
    /**
     * 计算题
     */
    public static final String TYPE_COMPUTING = "COMPUTING";
    /**
     * 文本题
     */
    public static final String TYPE_TEXT = "TEXT";
    /**
     * 附件题
     */
    public static final String TYPE_FILE = "FILE";
    /**
     * 作业内容
     */
    public static final String TYPE_WORK_CONTENT = "WORK_CONTENT";
    /**
     * 容易
     */
    public static final String DIFFICULT_EASY = "EASY";
    /**
     * 较容易
     */
    public static final String DIFFICULT_LITTLE_EASY = "LITTLE_EASY";
    /**
     * 一般
     */
    public static final String DIFFICULT_NORMAL = "NORMAL";
    /**
     * 较难
     */
    public static final String DIFFICULT_LITTLE_DIFFICULT = "LITTLE_DIFFICULT";
    /**
     * 难
     */
    public static final String DIFFICULT_DIFFICULT = "DIFFICULT";

    /**
     * 来源：共享题库
     */
    public static final String SOURCE_PUBLIC = "PUBLIC";

    /**
     * 来源：我的题库
     */
    public static final String SOURCE_PRIVATE = "PRIVATE";

    /**
     * 来源：收藏习题
     */
    public static final String SOURCE_FAVORITE = "FAVORITE";

    /**
     * 测试类型
     */
    public static final String TYPE_EXAM = "TYPE_EXAM";
    /**
     * 作业类型
     */
    public static final String TYPE_WORK = "TYPE_WORK";

    public static final String ARG_TASK_TYPE = "ARG_TASK_TYPE";
    public static final String ARG_TASK_STATUS = "ARG_TASK_STATUS";
    public static final String ARG_WORK_ID = "ARG_WORK_ID";
    public static final String ARG_STUDENT_ID = "ARG_STUDENT_ID";
    public static final String ARG_TASK_ITEM_TYPE = "ARG_TASK_ITEM_TYPE";
    public static final String ARG_IS_TEACHER_READ = "ARG_IS_TEACHER_READ";
    public static final String ARG_COMMENT = "ARG_COMMENT";
    public static final String ARG_TASK_DATA = "ARG_TASK_DATA";
    public static final String STATUS_VIEW = "STATUS_VIEW";//查看状态
    public static final String STATUS_READ = "STATUS_READ";//查看已批阅
    public static final String STATUS_READ_SELF = "STATUS_READ_SELF";//查看自主测试已完成
    public static final String STATUS_WAIT = "STATUS_WAIT";//查看待批阅
    public static final String STATUS_DO = "STATUS_DO";//做题状态
    public static final String STATUS_DO_READ = "STATUS_DO_READ";//按学生批阅
    public static final String STATUS_DO_READ_BY = "STATUS_DO_READ_BY";//按习题批阅
    public static final String TITLE_COMMENT_TEACHER = "老师点评";
    public static final String TITLE_COMMENT_STUDENT = "学生点评";
    private static final int FILE_NAME_LENGTH_SHOW = 20;//附件题文件名显示长度
    /**
     * 音频类型
     */
    public static final String TYPE_RESOURCE_AUDIO = "TYPE_RESOURCE_AUDIO";
    /**
     * 视频类型
     */
    public static final String TYPE_RESOURCE_VIDEO = "TYPE_RESOURCE_VIDEO";
    /**
     * 图片类型
     */
    public static final String TYPE_RESOURCE_IMAGE = "TYPE_RESOURCE_IMAGE";

    /**
     * 视频地址包含字段
     */
    public static final String TYPE_VIDEO_STRING = "viewVideo";
    /**
     * 音频地址包含字段
     */
    public static final String TYPE_AUDIO_STRING = "viewAudio";

    /**
     * 音频类型
     */
    public static final String TYPE_AUDIO = "type_audio";
    /**
     * 视频类型
     */
    public static final String TYPE_VIDEO = "type_video";

    protected View mRootView;
    protected LinearLayout mLinearLayout;//习题界面最外层布局
    private static int mSpaceing;

    protected String mTaskType;//类型：测试/作业
    private String mTaskStatus;//状态：查看/批阅/做题/待批阅
    private QuestionInfo mQuestionInfo;//测试题目信息
    private UserInfo mUserInfo;
    protected TaskReadDao mTaskReadDao = null;//批阅内容存储类
    protected TaskAnswerDao mTaskAnswerDao = null;//学生答案存储类
    protected ItemInfoClass mItemInfoClass;//传递过来的题目信息
    protected StudentAnswersByPerson mStudentAnswersByPerson;//传递过来的学生答案信息
    protected StudentAnswersByPerson.NomalAnswerListEntity mNomalAnswerListEntity = null;//学生普通题答案
    protected List<StudentAnswersByPerson.DocAnswerListEntity> mDocAnswerList = null;//学生附件题答案列表

    protected TaskAnswer mTaskAnswer;
    protected String mTaskItemType = "";//题目类型
    protected String mStudentId = "";//学生Id;
    protected String mTaskId = "";//测试/作业Id
    protected String mIsTeacherRead;//是否老师批阅
    protected String mStudentAnswer = "";//主观题答案
    protected String mStudentTextAnswer = "";//文本题答案
    private String mCurrentResourceId = "";//音视频资源id
    private String mCurrentResourceName = "";//音视频资源名称
    protected String mCurrentResourceLocalPath = "";//资源本地地址
    protected String mCurrentResourceType = "";//资源类型：音频/视频

    /**
     * 题目id
     */
    protected String mTaskItemId;

    /**
     * 用于上传批阅信息的id（测试模块用到）
     */
    private String mTaskItemResultId;
    /**
     * 测试打分分值
     */
    private String mScore = "";
    /**
     * 测试/作业批阅点评
     */
    private String mComment = "";

    /**
     * 视频上传进度圈
     */
    private ProgressCircle mVideoProgress;
    /**
     * 视频上传完成后显示的“播放”按钮
     */
    private ImageView mVideoStartIv;

    /**
     * 图片适配器
     */
    private ImagePreviewAdapter mImagePreviewAdapter;

    /**
     * 图片点击放大浏览的图片地址集合，从富文本中获取得来
     */
    private List<String> mImageList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.d(TAG, "onCreate");
        mTaskReadDao = TaskReadDao.newInstance(getActivity());
        mTaskAnswerDao = TaskAnswerDao.getInstance(getActivity());
        mUploadImageCount = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType).size();//获取已上传成功的图片
        if (mUserInfo == null) {
            mUserInfo = UserInfoKeeper.obtainUserInfo();
        }
        mSpaceing = UIUtils.dip2px(getActivity(), 8f);
        ScrollView scrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(params);
        scrollView.setBackgroundColor(getResources().getColor(R.color.exam_bg_color));
        mRootView = scrollView;
        scrollView.setVerticalScrollBarEnabled(false);
        mLinearLayout = new LinearLayout(getActivity());//根布局
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setLayoutParams(params);
        scrollView.addView(mLinearLayout);

        mImagePreviewAdapter = new ImagePreviewAdapter(new ArrayList<ImageDetail>(), getContext());
        mImagePreviewAdapter.setOnInViewClickListener(R.id.sdv_headpic, new MMBaseRecyclerViewAdapter.onInternalClickListener<ImageDetail>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, ImageDetail values) {
                Intent intent = new Intent(getActivity(), PreviewImageActivity.class);
                PreviewImageActivity.setOnDeleteListener(new PreviewImageActivity.OnDeleteSuccess() {
                    @Override
                    public void onPreviewResult(ArrayList<ImageDetail> result, String deleteUrl) {
                        if (result.size() > 0)
                            addImageLayout(result);
                        else
                            deleteImageLayout();

                        mTaskAnswerDao.deleteOnePicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType, deleteUrl);
                    }
                });
                intent.putParcelableArrayListExtra(EXTRA_DATA, mSelectedImageUrlList);
                startActivity(intent);
            }

            @Override
            public void OnLongClickListener(View parentV, View v, final Integer position, ImageDetail values) {
                DialogUtils dialogUtil = new DialogUtils(getString(R.string.tip_delete_image), getFragmentManager());
                dialogUtil.setOnRightClickListener(new DialogUtils.OnRightClickListener() {
                    @Override
                    public void onRightClick() {
                        mImagePreviewAdapter.setPosition(position);
                        mTaskAnswerDao.deleteOnePicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType, mSelectedImageUrlList.get(mImagePreviewAdapter.getPosition()).getPicUrl());
                        mUploadImageCount = mUploadImageCount - 1;
                        mSelectedImageUrlList.remove(mImagePreviewAdapter.getPosition());
                        mImagePreviewAdapter.remove(mImagePreviewAdapter.getPosition());
                        mImagePreviewAdapter.setList(mSelectedImageUrlList);
                        if (mSelectedImageUrlList.size() == 0) {//当无图片时，删除recyclerview
                            deleteImageLayout();
                        }
                    }
                });
            }
        });

        mTaskType = getArguments().getString(ARG_TASK_TYPE);//获取习题类型
        if (mTaskType != null && !mTaskType.equals(TYPE_WORK)) {//只有当测试那边过来时，才执行下面代码。作业的参数已经有啦
            mTaskStatus = getArguments().getString(ARG_TASK_STATUS);//测试的获取习题状态
        }

        if (mTaskType == null || mTaskType.equals(TYPE_WORK)) {//习题类型为作业时

        } else if (mTaskType.equals(TYPE_EXAM)) {
            //习题类型为测试时
            if (getArguments().getParcelable(ARG_TASK_DATA) != null) {
                mQuestionInfo = getArguments().getParcelable(ARG_TASK_DATA);
                switch (mTaskStatus) {
                    case STATUS_VIEW:
                        setExamStatusView(mQuestionInfo);
                        break;
                    case STATUS_READ:
                        setExamStatusRead(mQuestionInfo);
                        break;
                    case STATUS_READ_SELF:
                        setExamStatusReadSelf(mQuestionInfo);
                        break;
                    case STATUS_WAIT:
                        setExamStatusWait(mQuestionInfo);
                        break;
                    case STATUS_DO:
                        mStudentId = mQuestionInfo.getStudentId();
                        mTaskId = mQuestionInfo.getExamTaskId();
                        mTaskItemId = mQuestionInfo.getQuestionId();
                        mTaskItemType = mQuestionInfo.getQuestionType();
                        setExamStatusDo(mQuestionInfo);
                        break;
                    case STATUS_DO_READ:
                        mStudentId = mQuestionInfo.getStudentId();
                        mTaskId = mQuestionInfo.getExamTaskId();
                        mTaskItemId = mQuestionInfo.getQuestionResultId();
                        mTaskItemResultId = mQuestionInfo.getQuestionResultId();
                        mTaskItemType = mQuestionInfo.getQuestionType();
                        setExamStatusDoRead(mQuestionInfo);
                        break;
                    case STATUS_DO_READ_BY:
                        mStudentId = mQuestionInfo.getStudentId();
                        mTaskId = mQuestionInfo.getExamTaskId();
                        mTaskItemId = mQuestionInfo.getQuestionResultId();
                        mTaskItemResultId = mQuestionInfo.getQuestionResultId();
                        mTaskItemType = mQuestionInfo.getQuestionType();
                        setExamStatusDoReadBy(mQuestionInfo);
                        break;
                }
            }
        }
    }

    /**
     * added by eachann 2016-02-04
     * 当类型为测试；状态为待批阅时，调用此方法初始化布局
     *
     * @param info
     */
    private void setExamStatusWait(QuestionInfo info) {
        addType(info.getQuestionType());//习题类型
        addContent(info.getQuestionContent());//题干
        addExamMedia(info.getQuestionMediaUrl(), false, false);
        switch (info.getQuestionType()) {
            case TYPE_SINGLE_CHOICE:
                addOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//单选题选项
                break;
            case TYPE_MULTI_CHOICE:
                addMultiOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//多选题选项
                break;
            case TYPE_JUDGEMENT:
                addJudgeOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//判断题选项
                break;
            case TYPE_FILL_IN_BLANK:
                break;
            case TYPE_ASK_ANSWER:
                break;
            case TYPE_COMPUTING:
                break;
        }
        addMineAnswer(info.getQuestionStudentAnswer());//我的答案
        addExamMedia(info.getQuestionStudentAnswerMediaUrl(), true, false);
        addDifficultyFactor(info.getQuestionDifficultyFactor(), true);//难易度
        addExamReadScore(info.getQuestionScores(), String.valueOf(info.getQuestionScore()));//分值
        addBottomView();
    }

    /**
     * added by eachann 2016-02-04
     * 当类型为测试；状态为已批阅时，调用此方法初始化布局
     *
     * @param info
     */
    private void setExamStatusRead(QuestionInfo info) {
        addType(info.getQuestionType());//题目类型
        addContent(info.getQuestionContent());//题干
        addExamMedia(info.getQuestionMediaUrl(), false, false);
        switch (info.getQuestionType()) {
            case TYPE_SINGLE_CHOICE:
                addOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//单选选项
                break;
            case TYPE_MULTI_CHOICE:
                addMultiOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//多选选项
                break;
            case TYPE_JUDGEMENT:
                addJudgeOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//判断题选项
                break;
            case TYPE_FILL_IN_BLANK:
                break;
            case TYPE_ASK_ANSWER:
                break;
            case TYPE_COMPUTING:
                break;
        }
        addCorrectAnswer(info.getQuestionCorrectAnswer());//正确答案
        addStudentAnswer(info.getQuestionStudentAnswer());//学生答案
        addExamMedia(info.getQuestionStudentAnswerMediaUrl(), true, false);//学生答案音视频
        addDifficultyFactor(info.getQuestionDifficultyFactor(), true);//难易度
        addExamReadScore(info.getQuestionScores(), String.valueOf(info.getQuestionScore()));//得分;分值
        addReviews(info.getQuestionTeacherReviews());//老师点评
        addResolve(info.getQuestionResolve(), info.getQuestionResolveVideo());//习题解析
        addExamMedia(info.getQuestionResolveVideo(), false, false);//习题解析音视频
        addKnowledgePoints(info.getQuestionKnowledgePoint());//知识点
        addBottomView();
    }

    /**
     * added by eachann 2016-02-04
     * 当类型为测试；状态为按学生批阅时，调用此方法初始化布局
     *
     * @param info
     */
    private void setExamStatusDoRead(QuestionInfo info) {

        addType(info.getQuestionType());//题目类型
        addContent(info.getQuestionContent());//题干
        addExamMedia(info.getQuestionMediaUrl(), false, false);
        switch (info.getQuestionType()) {
            case TYPE_SINGLE_CHOICE:
                addOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//单选选项
                break;
            case TYPE_MULTI_CHOICE:
                addMultiOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//多选选项
                break;
            case TYPE_JUDGEMENT:
                addJudgeOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//判断题选项
                break;
            case TYPE_FILL_IN_BLANK:
                break;
            case TYPE_ASK_ANSWER:
                break;
            case TYPE_COMPUTING:
                break;
        }
        addCorrectAnswer(info.getQuestionCorrectAnswer());//正确答案
        addStudentAnswer(info.getQuestionStudentAnswer());//学生答案
        addExamMedia(info.getQuestionStudentAnswerMediaUrl(), true, false);
        addDifficultyFactor(info.getQuestionDifficultyFactor(), true);//难易度
        addExamReadScore(info.getQuestionScores(), String.valueOf(info.getQuestionScore()));//得分;分值
        switch (info.getQuestionType()) {
            case TYPE_ASK_ANSWER:
                addScore(getScore(info));
                break;
            case TYPE_COMPUTING:
                addScore(getScore(info));
                break;
        }
        addCommentEditText("老师点评:", "");
//        addReviews(mQuestionInfo.getQuestionTeacherReviews());//老师点评
//        addResolve(mQuestionInfo.getQuestionResolve());//习题解析
//        addKnowledgePoints(mQuestionInfo.getQuestionKnowledgePoint());//知识点
        addBottomView();
    }

    /**
     * 获取测试-打分数值（已存入本地的）
     *
     * @param info
     * @return
     */
    private String getScore(QuestionInfo info) {
        String score = "";
        List<TaskReadDao.TaskItemReadInfo> readInfos = mTaskReadDao.queryExam(mStudentId, info.getExamTaskId());
        for (TaskReadDao.TaskItemReadInfo readInfo : readInfos) {
            if (info.getQuestionResultId().equals(readInfo.getTaskItemId())) {
                score = readInfo.getTaskItemReadScore();
                break;
            }
        }
        if (score.contains("∷")) {
            score = score.split("∷")[0];
        }
        return score;
    }

    /**
     * added by eachann 2016-02-04
     * 当类型为测试；状态为按学生批阅时，调用此方法初始化布局
     *
     * @param info
     */
    private void setExamStatusDoReadBy(QuestionInfo info) {
        addType(info.getQuestionType());//题目类型
        addContent(info.getQuestionContent());//题干
        addExamMedia(info.getQuestionMediaUrl(), false, false);
        addCorrectAnswer(info.getQuestionCorrectAnswer());//正确答案
    }

    /**
     * added by eachann 2016-02-26
     * 当类型为测试；状态为自主测试已完成时，调用此方法初始化布局
     *
     * @param info
     */
    private void setExamStatusReadSelf(QuestionInfo info) {
        addType(info.getQuestionType());//题目类型
        addContent(info.getQuestionContent());//题干
        addExamMedia(info.getQuestionMediaUrl(), false, false);
        switch (info.getQuestionType()) {
            case TYPE_SINGLE_CHOICE:
                addOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//单选选项
                break;
            case TYPE_MULTI_CHOICE:
                addMultiOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//多选选项
                break;
            case TYPE_JUDGEMENT:
                addJudgeOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());//判断题选项
                break;
            case TYPE_FILL_IN_BLANK:
                break;
            case TYPE_ASK_ANSWER:
                break;
            case TYPE_COMPUTING:
                break;
        }
        addCorrectAnswer(info.getQuestionCorrectAnswer());//正确答案
        addMineAnswer(info.getQuestionStudentAnswer());//我的答案
        addExamMedia(info.getQuestionStudentAnswerMediaUrl(), true, false);
        addDifficultyFactor(info.getQuestionDifficultyFactor(), true);//难易度
        addResolve(info.getQuestionResolve(), info.getQuestionResolveVideo());//习题解析
        addExamMedia(info.getQuestionResolveVideo(), false, false);
        addKnowledgePoints(info.getQuestionKnowledgePoint());//知识点
        //addComment(TaskFragment.TITLE_COMMENT_TEACHER, info.getQuestionTeacherReviews());
        addBottomView();
    }

    /**
     * added by eachann 2016-01-29
     * 当类型为测试，状态为do 时，调用此方法初始化布局
     *
     * @param info
     */
    private void setExamStatusDo(QuestionInfo info) {
        addType(info.getQuestionType());
        addExamScore(info.getQuestionScore(), info.getQuestionDifficultyFactor(), true);
        addContent(info.getQuestionContent());
        switch (info.getQuestionType()) {
            case TYPE_SINGLE_CHOICE:
                addExamMedia(info.getQuestionMediaUrl(), false, false);
                addOptions(info.getQuestionOptions(), true, info.getQuestionCorrectAnswer());
                break;
            case TYPE_MULTI_CHOICE:
                addExamMedia(info.getQuestionMediaUrl(), false, false);
                addMultiOptions(info.getQuestionOptions(), true, info.getQuestionCorrectAnswer());
                break;
            case TYPE_JUDGEMENT:
                addExamMedia(info.getQuestionMediaUrl(), false, false);
                addJudgeOptions(info.getQuestionOptions(), true, info.getQuestionCorrectAnswer());
                break;
            case TYPE_FILL_IN_BLANK:
                addExamMedia(info.getQuestionMediaUrl(), false, false);
                if (info.getQuestionCorrectAnswer() != null) {
                    addAnswerFillInBlank(info.getQuestionBlankCounts(), info.getQuestionCorrectAnswer());
                } else {
                    addAnswerFillInBlank(info.getQuestionBlankCounts());
                }
                break;
            case TYPE_ASK_ANSWER:
                addExamMedia(info.getQuestionMediaUrl(), false, false);
                addAnswerLayout(WorkItemDetailFragment.STATUS_STU_PROGRESS, info.getQuestionCorrectAnswer());
                addMediaToAnswer(info);
                break;
            case TYPE_COMPUTING:
                addExamMedia(info.getQuestionMediaUrl(), false, false);
                addAnswerLayout(WorkItemDetailFragment.STATUS_STU_PROGRESS, info.getQuestionCorrectAnswer());
                addMediaToAnswer(info);
                break;
        }
    }

    private void addMediaToAnswer(QuestionInfo info) {
        List<TaskPicInfo> taskPicInfos = TaskAnswerDao.getInstance(getContext()).queryPicInfo(info.getStudentId(), info.getExamTaskId(), info.getQuestionId(), info.getQuestionType());
        if (taskPicInfos.size() > 0) {
            addImageLayout(getImageUrlList(taskPicInfos));
        }
        TaskAnswer answer = TaskAnswerDao.getInstance(getContext()).queryItemInfo(info.getStudentId(), info.getExamTaskId(), info.getQuestionId(), info.getQuestionType());
        if (answer != null && !TextUtils.isEmpty(answer.getResourceLocalPath())) {
            if (TYPE_AUDIO.equals(answer.getResourceType())) {
                addServiceAnswerAudioBar(answer.getResourceLocalPath(), true, true);
            } else if (TYPE_VIDEO.equals(answer.getResourceType())) {
                addVideoThumbnail(answer.getResourceLocalPath(), FROM_LOACAL, "", true);
            }
        }
    }

    /**
     * picInfoList的来源是数据库中，所以该信息已上传成功，状态为Loaded
     *
     * @param picInfoList
     * @return
     */
    private List<ImageDetail> getImageUrlList(List<TaskPicInfo> picInfoList) {
        List<ImageDetail> imageUrlList = new ArrayList<>();
        for (int i = 0; i < picInfoList.size(); i++) {
            ImageDetail imageDetail = new ImageDetail();
            imageDetail.setLoadInfo(ImageDetail.TYPE_LOADED);
            imageDetail.setPicUrl(picInfoList.get(i).getImageLocalPath());
            imageUrlList.add(imageDetail);
        }
        return imageUrlList;
    }

    private List<String> getImageLocalPath(ArrayList<ImageDetail> picInfoList) {
        List<String> imageUrlList = new ArrayList<>();
        for (int i = 0; i < picInfoList.size(); i++) {
            imageUrlList.add(picInfoList.get(i).getPicUrl());
        }
        return imageUrlList;
    }

    /**
     * 增加视频
     *
     * @param url
     * @param isStuAnswer 是否是答题
     * @param isDel       前置条件 @isStuAnswer 为true; 是否可删除，只有答题的时候可以删除，其他不能删除
     */
    private void addExamMedia(String url, boolean isStuAnswer, boolean isDel) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(TYPE_VIDEO_STRING)) {//加载视频
                addVideoThumbnail(url, FROM_NET, "", false);
            } else if (url.contains(TYPE_AUDIO_STRING)) {//加载音频
                if (isStuAnswer)
                    addServiceAnswerAudioBar(url, isDel, true);
                else
                    addServiceItemAudioBar(url);
            }
        }
    }

    /**
     * added by eachann 2016-01-28
     * 当类型为测试，状态为view 时，调用此方法初始化布局
     *
     * @param info
     */

    private void setExamStatusView(QuestionInfo info) {
        addType(info.getQuestionType());
        addContent(info.getQuestionContent());
        addExamMedia(info.getQuestionMediaUrl(), false, false);
        switch (info.getQuestionType()) {
            case TYPE_SINGLE_CHOICE:
                addOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());
                break;
            case TYPE_MULTI_CHOICE:
                addMultiOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());
                break;
            case TYPE_JUDGEMENT:
                addJudgeOptions(info.getQuestionOptions(), false, info.getQuestionCorrectAnswer());
                break;
            case TYPE_FILL_IN_BLANK:
                break;
            case TYPE_ASK_ANSWER:
                break;
            case TYPE_COMPUTING:
                break;
        }
        addCorrectAnswer(info.getQuestionCorrectAnswer());
        addResolve(info.getQuestionResolve(), info.getQuestionResolveVideo());
        addExamMedia(info.getQuestionResolveVideo(), false, false);//习题解析音视频
        addKnowledgePoints(info.getQuestionKnowledgePoint());
        addDifficultyFactor(info.getQuestionDifficultyFactor(), true);
        addAssembleCounts(info.getQuestionAssembleCounts());
        addUpdateDate(info.getQuestionUpdateDate());
        addComeFrom(info.getQuestionComeFrom());
        addBottomView();
    }

    /**
     * added by eachann 2016-02-04
     * 老师点评
     *
     * @param reviews
     */
    protected void addReviews(String reviews) {
        if (TextUtils.isEmpty(reviews)) return;
        SpannableString spannableString = new SpannableString("老师点评");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(textView);
        WebView webView = new WebView(getActivity());
        LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(mWebViewLP);
        setContentToWebView(webView, reviews);
        mLinearLayout.addView(webView);
        addDividerLine();
    }

    /**
     * added by eachann 2016-02-04
     * 查看已批阅 得分和分值
     *
     * @param scores 分值
     * @param score  得分
     */
    private void addExamReadScore(String scores, String score) {
        if (TextUtils.isEmpty(scores)) return;
        TextView tvScores = new TextView(getActivity());
        SpannableStringBuilder builderScores = new SpannableStringBuilder("分值 ");
        builderScores.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builderScores.setSpan(new AbsoluteSizeSpan(16, true), 0, builderScores.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builderScores.append(scores);
        builderScores.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 3, 3 + scores.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builderScores.append("分");
        tvScores.setText(builderScores);
        tvScores.setPadding(mSpaceing, mSpaceing, 0, mSpaceing);
        tvScores.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        tvScores.setBackgroundColor(getResources().getColor(R.color.white));
        mLinearLayout.addView(tvScores);
        QuestionInfo info = getArguments().getParcelable(ARG_TASK_DATA);
        TextView tvScore;
        SpannableStringBuilder builderScore;
        switch (mTaskStatus) {

            case STATUS_READ:
                tvScore = new TextView(getActivity());
                builderScore = new SpannableStringBuilder("得分 ");
                builderScore.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builderScore.setSpan(new AbsoluteSizeSpan(16, true), 0, builderScore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builderScore.append(score);
                builderScore.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 3, 3 + score.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builderScore.append("分");
                tvScore.setText(builderScore);
                tvScore.setPadding(mSpaceing, mSpaceing, 0, mSpaceing);
                tvScore.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        tvScore.setBackgroundColor(getResources().getColor(R.color.white));
                mLinearLayout.addView(tvScore);
                break;
            case STATUS_DO_READ:
                switch (info.getQuestionType()) {
                    case TYPE_ASK_ANSWER:
                        return;
                    case TYPE_COMPUTING:
                        return;
                }
                tvScore = new TextView(getActivity());
                builderScore = new SpannableStringBuilder("得分 ");
                builderScore.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builderScore.setSpan(new AbsoluteSizeSpan(16, true), 0, builderScore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builderScore.append(score);
                builderScore.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 3, 3 + score.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builderScore.append("分");
                tvScore.setText(builderScore);
                tvScore.setPadding(mSpaceing, mSpaceing, 0, mSpaceing);
                tvScore.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        tvScore.setBackgroundColor(getResources().getColor(R.color.white));
                mLinearLayout.addView(tvScore);
                break;
        }

        addDividerLine();
    }

    /**
     * 得分 和难易度
     *
     * @param score     分值
     * @param difficult 难度
     * @param clickable 是否指示器：true 为指示器，不可点击；false为可点击
     */
    private void addExamScore(int score, String difficult, boolean clickable) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        SpannableString spannableString = new SpannableString("难易度 ");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tvDiff = new TextView(getActivity());
        tvDiff.setText(spannableString);
        linearLayout.addView(tvDiff);
        RatingBar bar = (RatingBar) getActivity().getLayoutInflater().inflate(R.layout.rating_bar_layout, null);
        bar.setNumStars(5);//5颗星
        bar.setIsIndicator(clickable);
        bar.setStepSize(1f);
        bar.setRating(getRarRating(difficult));
        linearLayout.addView(bar);
        linearLayout.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(linearLayout);
        if (score >= 0) {
            TextView textView = new TextView(getActivity());
            SpannableStringBuilder builder = new SpannableStringBuilder("分值 ");
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new AbsoluteSizeSpan(16, true), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(String.valueOf(score));
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 3, 3 + String.valueOf(score).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("分");
            textView.setText(builder);
            textView.setPadding(mSpaceing, mSpaceing, 0, mSpaceing);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setBackgroundColor(getResources().getColor(R.color.white));
            mLinearLayout.addView(textView);
        }
    }

    public static float getRarRating(String difficult) {
        switch (difficult) {
            case DIFFICULT_EASY:
                return 1f;
            case DIFFICULT_LITTLE_EASY:
                return 2f;
            case DIFFICULT_NORMAL:
                return 3f;
            case DIFFICULT_LITTLE_DIFFICULT:
                return 4f;
            case DIFFICULT_DIFFICULT:
                return 5f;
            default:
                return 0f;
        }
    }

    /**
     * 答题-填空题
     *
     * @param blankCounts 填空题数量
     */
    protected void addAnswerFillInBlank(int blankCounts) {
        addDividerLine();
        final Map<Integer, String> result = new HashMap<>();
        for (int i = 0; i < blankCounts; i++) {
            result.put(i, "");//先存“”，以占位
            LinearLayout blankLL = new LinearLayout(getActivity());
            blankLL.setOrientation(LinearLayout.HORIZONTAL);
            blankLL.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
            blankLL.setBackgroundColor(getResources().getColor(R.color.white));
            TextView seq = new TextView(getActivity());
            seq.setBackgroundColor(getResources().getColor(R.color.white));
            seq.setText(getString(R.string.work_fill_answer_index, i + 1 + ""));
            seq.setPadding(mSpaceing, 0, mSpaceing, 0);
            blankLL.addView(seq);
            final EditText editText = new EditText(getActivity());
            if (mQuestionInfo != null && !TextUtils.isEmpty(mQuestionInfo.getHint())) {
                editText.setHint(mQuestionInfo.getHint());
            } else {
                editText.setHint(getString(R.string.input_solution));
            }
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            editText.setSingleLine();
            editText.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
            editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(getActivity(), 38f)));
            editText.setBackgroundResource(R.drawable.drawable_fill_in_blank_edittext);
            editText.setTag(i);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});//最多500个字符
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    result.put(Integer.valueOf(editText.getTag().toString()), editText.getText().toString());
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, TYPE_FILL_IN_BLANK, WorkUtils.getBlankAnswer(result), "", "", "", "", "");
                }
            });
            blankLL.addView(editText);
            mLinearLayout.addView(blankLL);
        }
    }

    /**
     * 答题-填空题
     *
     * @param blankCounts 填空题数量
     */
    protected void addAnswerFillInBlank(int blankCounts, String studentAnswer) {
        addDividerLine();
        List<String> studentAnswerList = WorkUtils.splitFillAnswer(studentAnswer);
        final Map<Integer, String> result = new HashMap<>();
        for (int i = 0; i < blankCounts; i++) {
            result.put(i, "");//先存“”，以占位
            LinearLayout blankLL = new LinearLayout(getActivity());
            blankLL.setOrientation(LinearLayout.HORIZONTAL);
            blankLL.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
            blankLL.setBackgroundColor(getResources().getColor(R.color.white));
            TextView seq = new TextView(getActivity());
            seq.setBackgroundColor(getResources().getColor(R.color.white));
            seq.setText("(" + (i + 1) + ")");
            seq.setPadding(mSpaceing, 0, mSpaceing, 0);
            blankLL.addView(seq);
            final EditText editText = new EditText(getActivity());
            editText.setHint(getString(R.string.input_solution));
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            editText.setSingleLine();
            editText.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
            editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(getActivity(), 38f)));
            editText.setBackgroundResource(R.drawable.drawable_fill_in_blank_edittext);
            editText.setText(studentAnswerList.get(i));
            editText.setTag(i);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    result.put(Integer.valueOf(editText.getTag().toString()), editText.getText().toString());
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, TYPE_FILL_IN_BLANK, WorkUtils.getBlankAnswer(result), "", "", "", "", "");
                }
            });
            blankLL.addView(editText);
            mLinearLayout.addView(blankLL);
        }
    }

    private LinearLayout mBtnPicContainer;
    private LinearLayout mBtnAudioContainer;
    private LinearLayout mBtnVideoContainer;

    /**
     * 答题-主观题
     */
    @Deprecated
    protected void addAnswerSubjective() {
        addDividerLine();
        LinearLayout subjective = new LinearLayout(getActivity());
        subjective.setOrientation(LinearLayout.VERTICAL);
        subjective.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        subjective.setBackgroundColor(getResources().getColor(R.color.white));
        TextView textView = new TextView(getActivity());
        textView.setText(getString(R.string.answer_solution));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setPadding(0, mSpaceing, 0, mSpaceing);
        subjective.addView(textView);
        EditText editText = new EditText(getActivity());
        editText.setHint(getString(R.string.input_solution));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        editText.setMaxLines(5);
        editText.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundResource(R.drawable.drawable_fill_in_blank_edittext);
        subjective.addView(editText);
        mLinearLayout.addView(subjective);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_layout_element, null);
        mBtnPicContainer = (LinearLayout) view.findViewById(R.id.line_select_pic);
        mBtnAudioContainer = (LinearLayout) view.findViewById(R.id.line_select_audio);
        mBtnVideoContainer = (LinearLayout) view.findViewById(R.id.line_select_video);
        //选择图片
        mBtnPicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MMSelectorActivity.class);
                intent.putExtra("EXTRA_TYPE", "IMAGE");
                startActivityForResult(intent, REQUEST_CODE);
                UIUtils.addEnterAnim(getActivity());
            }
        });
        //选择音频
        mBtnAudioContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MMSelectorActivity.class);
                intent.putExtra("EXTRA_TYPE", "AUDIO");
                startActivityForResult(intent, REQUEST_CODE);
                UIUtils.addEnterAnim(getActivity());
            }
        });
        //选择视频
        mBtnVideoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MMSelectorActivity.class);
                intent.putExtra("EXTRA_TYPE", "VIDEO");
                startActivityForResult(intent, REQUEST_CODE);
                UIUtils.addEnterAnim(getActivity());
            }
        });
        mLinearLayout.addView(view);

    }

    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";
    private ArrayList<MMImageBean> mImageBeans;//选择的图片
    private String mVideoOrAudioUrl;//选择的音视频地址
    private int mUpLoadingImageIndex = 0;//正在上传的图片索引，从0开始
    private boolean mIsUploadingResource;//正在上传音频或视频

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == getActivity().RESULT_OK && data != null) {
                    pullToDown();
                    ArrayList<MMImageBean> mImageList = data.getParcelableArrayListExtra(EXTRA_DATA);
                    String resourceType = data.getExtras().getString(MMVideoAlbumFragment.EXTRA_TYPE);
                    mImageBeans = new ArrayList<>();
                    for (MMImageBean imageBean : mImageList) {
                        if (imageBean.isSeleted()) {
                            mImageBeans.add(imageBean);
                        }
                    }
                    if (MMImageAlbumFragment.TYPE_IMAGE.equals(resourceType)) {
                        /**
                         * 重置上传图片标识mUpLoadingImageIndex；返回的图片地址集合清空；清空本地图片数据
                         */
                        mUpLoadingImageIndex = 0;
                        mUploadImageCount = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType).size();//获取已上传成功的图片
                        mResourceType = TYPE_RESOURCE_IMAGE;
                        mVideoOrAudioUrl = mImageBeans.get(0).getPath();
                        mSelectedImageUrlList = (ArrayList<ImageDetail>) getSelectedImagesPath(mImageBeans);
                        upLoadResource();
                    } else if (MMAudioAlbumFragment.TYPE_AUDIO.equals(resourceType)) {
                        Cog.e(TAG, "选择的是音频");
                        mVideoOrAudioUrl = mImageBeans.get(0).getPath();
                        addServiceAnswerAudioBar(mVideoOrAudioUrl, true, false);
                    } else if (MMVideoAlbumFragment.TYPE_VIDEO.equals(resourceType)) {
                        Cog.e(TAG, "选择的是视频");
                        mVideoOrAudioUrl = mImageBeans.get(0).getPath();
                        mResourceType = TYPE_RESOURCE_VIDEO;
                        upLoadResource();
                    }
                    Cog.e(TAG, mImageBeans.toString());

                }
                break;
        }
    }


    /**
     * 将界面滚动到底部
     */
    protected void pullToDown() {
        mRootView.post(new Runnable() {//添加音视频图片时将界面滑动到最下方
            @Override
            public void run() {
                ((ScrollView) mRootView).fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    protected void pullToUp() {
        mRootView.post(new Runnable() {//添加音视频图片时将界面滑动到最下方
            @Override
            public void run() {
                ((ScrollView) mRootView).fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private UploadAsyncTask mMyTask;
    /**
     * 是否取消上传
     */
    private boolean isCancel = false;
    /**
     * 当前上传的资源类型（图片 音频 视频）
     */
    private String mResourceType;

    private void startUploadProgress() {
        isCancel = false;
        switch (mResourceType) {
            case TYPE_RESOURCE_VIDEO:
                addVideoThumbnail(mVideoOrAudioUrl, FROM_LOACAL, "", true);
                break;
            case TYPE_RESOURCE_IMAGE:
                mUploadedPicInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
                if (mLinearLayout.findViewWithTag("imageRecyclerView") != null) {
                    addImageLayout(mSelectedImageUrlList = getTotalSelectedImages(mUploadedPicInfoList));
                } else {
                    addImageLayout(mSelectedImageUrlList);
                }
                break;
        }
        upLoadTask();
    }

    /**
     * 已经上传过的图片集合
     */
    private List<TaskPicInfo> mUploadedPicInfoList;

    private ArrayList<ImageDetail> getTotalSelectedImages(List<TaskPicInfo> picInfo) {
        ArrayList<ImageDetail> imageDetailArrayList = new ArrayList<>();
        for (int i = 0; i < picInfo.size(); i++) {
            ImageDetail imageDetail = new ImageDetail();
            imageDetail.setLoadInfo(ImageDetail.TYPE_LOADED);
            imageDetail.setPicUrl(picInfo.get(i).getImageLocalPath());
            imageDetailArrayList.add(imageDetail);
        }
        for (int i = 0; i < mSelectedImageUrlList.size(); i++) {
            ImageDetail imageDetail = new ImageDetail();
            imageDetail.setLoadInfo(ImageDetail.TYPE_LOADING);
            imageDetail.setPicUrl(mSelectedImageUrlList.get(i).getPicUrl());
            imageDetailArrayList.add(imageDetail);
        }
        return imageDetailArrayList;
    }

    private void upLoadTask() {
        mMyTask = new UploadAsyncTask() {
            @Override
            public void onMyPreExecute() {
                switch (mResourceType) {
                    case TYPE_RESOURCE_IMAGE:
                        //mImagePreviewAdapter.refreshImageStatus(mUpLoadingImageIndex);
                        break;
                    case TYPE_RESOURCE_VIDEO:
                        mVideoProgress.setVisibility(View.VISIBLE);
                        mVideoStartIv.setVisibility(View.GONE);
                        mVideoUploadFailureTv.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onMyProgressUpdate(Integer... values) {
                if (TYPE_RESOURCE_VIDEO.equals(mResourceType)) {
                    if (values[0] == 100) {
                        mVideoProgress.setVisibility(View.INVISIBLE);
                    } else {
                        mVideoProgress.setProgress(values[0]);
                    }
                }
            }

            @Override
            public void onMyPostExecute(String result) {
                if (!isCancel) {
                    setResult(result, mResourceType);
                }
            }
        };
        mMyTask.execute(mVideoOrAudioUrl, mUploadRespurceUrl);
    }

    /**
     * 已经上传的图片数量，包括上传成功和失败的
     */
    private int mUploadImageCount = 0;

    /**
     * 音视频、图片上传失败时，显示……
     */
    private void setUploadFailure() {
        switch (mResourceType) {
            case TYPE_RESOURCE_IMAGE:
                refreshImageStatus(mUploadImageCount, ImageDetail.TYPE_LOAD_FAILED);
                break;
            case TYPE_RESOURCE_VIDEO:
                mVideoProgress.setVisibility(View.INVISIBLE);
                mVideoUploadFailureTv.setVisibility(View.VISIBLE);
                break;
        }
        mMyTask.cancel(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_upload_failure:
                reUploadResource();
                break;
            case R.id.iv_video_upload_failure:
                reUploadResource();
                break;
        }
    }

    /**
     * 前一次上传失败后，重新上传
     */
    private void reUploadResource() {
        MySubmitDialog reUploadDialog = MySubmitDialog.newInstance("是否重发该资源", "重发", "取消", MySubmitDialog.DIALOG_STYLE_TYPE_3,
                new MySubmitDialog.OnclickListener() {
                    @Override
                    public void leftClick(MySubmitDialog myDialog) {
                        myDialog.dismiss();
                    }

                    @Override
                    public void rightClick(MySubmitDialog myDialog) {
                        upLoadTask();
                        myDialog.dismiss();
                    }

                    @Override
                    public void dismiss() {

                    }
                });
        reUploadDialog.show(getFragmentManager(), "reupload");
    }

    private void setResult(String result, String resourceType) {
        if (result == null) {
            //ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_answer_submit_failure));
            setUploadFailure();
            if (mResourceType.equals(TYPE_RESOURCE_IMAGE))
                uploadImage();
            return;
        }
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mTaskAnswerDao != null) {
            queryDBAnswer();
        }
        switch (resourceType) {
            case TYPE_RESOURCE_VIDEO:
                if ("true".equals(resultObject.optString("result"))) {
                    mCurrentResourceLocalPath = mVideoOrAudioUrl;
                    mCurrentResourceType = TYPE_VIDEO;
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, mTaskItemType,
                            mTaskAnswer != null ? mTaskAnswer.getStudentAnswer() : mStudentAnswer,
                            mTaskAnswer != null ? mTaskAnswer.getStudentTextAnswer() : mStudentTextAnswer,
                            resultObject.optString("originalName"), resultObject.optString("message"), mVideoOrAudioUrl, TYPE_VIDEO);
                    mCurrentResourceId = resultObject.optString("message");
                    mCurrentResourceName = resultObject.optString("originalName");
                    mVideoStartIv.setVisibility(View.VISIBLE);
                    ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_video_submit_success));
                } else {
                    ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_answer_submit_failure));
                    setUploadFailure();
                }
                break;
            case TYPE_RESOURCE_IMAGE:
                if ("true".equals(resultObject.optString("result"))) {
                    mTaskAnswerDao.insertPic(mStudentId, mTaskId, mTaskItemType.equals(TYPE_TEXT) ? mTaskId : mTaskItemId, mTaskItemType, resultObject.optString("realname"), resultObject.optString("message"),
                            mSelectedImageUrlList.get((mUploadedPicInfoList == null ? 0 : mUploadedPicInfoList.size()) + mUpLoadingImageIndex).getPicUrl());
                    refreshImageStatus(mUploadImageCount, ImageDetail.TYPE_LOADED);
                    uploadImage();
                } else {
                    setUploadFailure();
                    uploadImage();
                }
                break;
        }
    }

    /**
     * 继续上传图片
     */
    private void uploadImage() {
        mUpLoadingImageIndex = mUpLoadingImageIndex + 1;
        if (mUpLoadingImageIndex < mImageBeans.size()) {
            mVideoOrAudioUrl = mImageBeans.get(mUpLoadingImageIndex).getPath();
            upLoadTask();
        }
        if (mUpLoadingImageIndex == mImageBeans.size() && getActivity() != null) {
            //ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_pic_submit_success));
        }
    }

    /**
     * 改变图片上传状态
     *
     * @param position
     * @param status
     */
    private void refreshImageStatus(int position, String status) {
        updateSelectedImageInfo(position, status);
        Log.d(TAG, "当前图片position:" + position);
        mImagePreviewAdapter.setList(mSelectedImageUrlList);
        mUploadImageCount = mUploadImageCount + 1;//选择上传后，将该值+1，即为当前显示的图片数量
    }

    /**
     * 刷新图片的上传状态
     *
     * @param position
     * @param status
     */
    private void updateSelectedImageInfo(int position, String status) {
        for (int i = 0; i < mSelectedImageUrlList.size(); i++) {
            if (i == position) {
                mSelectedImageUrlList.get(position).setLoadInfo(status);
                break;
            }
        }
    }

    /**
     * 音视频图片资源上传地址
     */
    private String mUploadRespurceUrl = "";
    private Handler mHander;

    private void upLoadResource() {
        switch (mResourceType) {
            case TYPE_RESOURCE_IMAGE:
                mUploadRespurceUrl = mUserInfo.getServerAddress() + "/res/" + mUserInfo.getAreaCode() + "/imageUpload.do?validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=5";
                break;
            case TYPE_RESOURCE_VIDEO:
                mUploadRespurceUrl = mUserInfo.getServerAddress() + "/res/mix/" + mUserInfo.getAreaCode() +
                        "/upload.do?printscreen=Y&printscreenType=auto&sourceType=work_rec_stu_que_answer&validateCode=" + mUserInfo.getValidateCode() + "&sizeLimit=500";//
                break;
        }
        startUploadProgress();
    }

    private ArrayList<ImageDetail> mSelectedImageUrlList = new ArrayList<>();//选择图片上传成功后返回的message集合
    private static final int REQUEST_CODE = 1 << 4;

    /**
     * 增加底部占位view
     */
    protected void addBottomView() {
        View view = new TextView(getActivity());
//        view.setBackgroundColor(getResources().getColor(R.color.white));
        view.setPadding(mSpaceing, mSpaceing * 5, mSpaceing, mSpaceing);
        mLinearLayout.addView(view);
    }

    /**
     * @param from 来源
     */
    protected void addComeFrom(String from) {
        if (TextUtils.isEmpty(from)) return;
        String sourceStr;
        if (SOURCE_PUBLIC.equals(from)) {
            sourceStr = "共享题库";
        } else if (SOURCE_PRIVATE.equals(from)) {
            sourceStr = "我的题库";
        } else if (SOURCE_FAVORITE.equals(from)) {
            sourceStr = "收藏习题";
        } else {
            sourceStr = from;
        }
        SpannableString spannableString = new SpannableString("来源    " + sourceStr);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(textView);
        addDividerLine();
    }

    /**
     * @param date 更新日期
     */
    protected void addUpdateDate(String date) {
        SpannableString spannableString = new SpannableString("更新日期  " + date);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(textView);
        addDividerLine();
    }

    /**
     * @param counts 组卷次数
     */
    protected void addAssembleCounts(String counts) {
        if (TextUtils.isEmpty(counts)) return;
        SpannableString spannableString = new SpannableString("组卷次数  " + counts);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(textView);
        addDividerLine();
    }

    /**
     * @param difficult 难易度
     * @param clickable true :不可点击；false：可点击
     */
    protected void addDifficultyFactor(String difficult, boolean clickable) {
        if (TextUtils.isEmpty(difficult)) return;
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        SpannableString spannableString = new SpannableString("难易度 ");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        linearLayout.addView(textView);
//        RatingBar bar = new RatingBar(getActivity());
        RatingBar bar = (RatingBar) getActivity().getLayoutInflater().inflate(R.layout.rating_bar_layout, null);
//        bar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, UIUtils.dip2px(getActivity(), 38f)));
        bar.setNumStars(5);//5颗星
        bar.setIsIndicator(clickable);
        bar.setStepSize(1f);
        switch (difficult) {
            case DIFFICULT_EASY:
                bar.setRating(1f);
                break;
            case DIFFICULT_LITTLE_EASY:
                bar.setRating(2f);
                break;
            case DIFFICULT_NORMAL:
                bar.setRating(3f);
                break;
            case DIFFICULT_LITTLE_DIFFICULT:
                bar.setRating(4f);
                break;
            case DIFFICULT_DIFFICULT:
                bar.setRating(5f);
                break;
            default:
                bar.setRating(0f);
                break;
        }
        linearLayout.addView(bar);
        linearLayout.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(linearLayout);
        addDividerLine();
    }

    /**
     * @param points 知识点
     */
    protected void addKnowledgePoints(String points) {
        //if (TextUtils.isEmpty(points)) return;
        SpannableString spannableString = new SpannableString("知识点");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(textView);
        RichText richText = new RichText(getActivity());
        richText.setText(points);
        richText.setPadding(mSpaceing / 2, 0, mSpaceing, mSpaceing);
        mLinearLayout.addView(richText);
        addDividerLine();
    }

    /**
     * @param resolve 习题解析
     */
    protected void addResolve(String resolve, String url) {
        if (TextUtils.isEmpty(resolve) && TextUtils.isEmpty(url)) return;
        SpannableString spannableString = new SpannableString("习题解析");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(textView);
        if (!TextUtils.isEmpty(resolve)) {
            WebView webView = new WebView(getActivity());
            LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            webView.setLayoutParams(mWebViewLP);
            webView.setBackgroundColor(Color.TRANSPARENT);
            setContentToWebView(webView, resolve);
            mLinearLayout.addView(webView);
        }
        addDividerLine();
    }

    /**
     * @param correct 正确答案
     */
    protected void addCorrectAnswer(String correct) {
        if (TextUtils.isEmpty(correct)) return;
        if (options != null) {
            if (correct.equals("A") && options[0] != null) {
                correct = options[0].substring(2);
            } else if (correct.equals("B") && options[1] != null) {
                correct = options[1].substring(2);
            }
        }
        SpannableString spannableString = new SpannableString("正确答案 " + correct);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        WebView webView = new WebView(getActivity());
        LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(mWebViewLP);
        webView.setBackgroundColor(Color.TRANSPARENT);
        setContentToWebView(webView, spannableString.toString());
        mLinearLayout.addView(webView);
        addDividerLine();
    }

    /**
     * 添加学生答案
     *
     * @param answer
     */
    protected void addStudentAnswer(String answer) {
        if (answer.contains("∷")) {
            answer = WorkUtils.parserStuFillAnswer(answer).toString();
        }
        if (TYPE_MULTI_CHOICE.equals(mTaskItemType)) {
            answer = sortAnswer(answer);
        }
        if (TYPE_JUDGEMENT.equals(mTaskItemType) && options != null) {
            if (answer.equals("A") && options[0] != null) {
                answer = options[0].substring(2);
            } else if (answer.equals("B") && options[1] != null) {
                answer = options[1].substring(2);
            }
        }
        SpannableString spannableString = new SpannableString("学生答案 " + answer);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        WebView webView = new WebView(getActivity());
        LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(mWebViewLP);
        webView.setBackgroundColor(Color.TRANSPARENT);
        setContentToWebView(webView, spannableString.toString());
        mLinearLayout.addView(webView);
        addDividerLine();
    }

    /**
     * 对字母排序
     *
     * @param answer
     * @return
     */
    private String sortAnswer(String answer) {
        String str = "";
        char[] arrays = answer.toCharArray();
        Arrays.sort(arrays);
        for (int i = 0; i < arrays.length; i++) {
            str = str + arrays[i];
        }
        return str;
    }

    /**
     * 添加学生答案
     *
     * @param answer
     */
    protected void addMineAnswer(String answer) {
        answer = answer.replace("∷", ";");
        SpannableString spannableString = new SpannableString("我的答案 " + answer);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 4, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        WebView webView = new WebView(getActivity());
        LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(mWebViewLP);
        webView.setBackgroundColor(Color.TRANSPARENT);
        setContentToWebView(webView, spannableString.toString());
        mLinearLayout.addView(webView);
        addDividerLine();
    }


    /**
     * 增加分割线
     */
    private void addDividerLine() {
        View view = new View(getActivity());
        view.setBackgroundColor(getResources().getColor(R.color.exam_divider));
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(getActivity(), 0.5f)));
        mLinearLayout.addView(view);
    }

    /**
     * @param content 题干
     */
    protected void addContent(String content) {
        WebView webView = new WebView(getActivity());
        LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(mWebViewLP);
        setContentToWebView(webView, content);
        mLinearLayout.addView(webView);
    }

    private String[] options;

    /**
     * @param content   判断题选项
     * @param clickable 是否可点击(查看试卷：false;答题:true)
     * @param correct   正确答案(A)
     */
    protected void addJudgeOptions(String content, final boolean clickable, String correct) {
        /*拆分选项*/
        options = content.split("∷");
        final MyRadioGroup radioGroup = new MyRadioGroup(getActivity());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.setLayoutParams(params);
        radioGroup.setBackgroundColor(getResources().getColor(R.color.white));
        if (options.length > 0) {
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final RadioButton rb = new RadioButton(getActivity());
                //rb.setTag(i);
                rb.setLayoutParams(llp);
                rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                rb.setClickable(clickable);
                rb.setGravity(Gravity.CENTER);
                rb.setTextColor(getResources().getColor(R.color.white));
                rb.setTag(option.substring(0, 1));
                switch (option.substring(0, 1)) {
                    case "A":
                        if (correct != null && "A".equals(correct)) {
                            rb.setChecked(true);
                        }
                        rb.setText(option.substring(option.indexOf(".") + 1));
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "B":
                        if (correct != null && "B".equals(correct)) {
                            rb.setChecked(true);
                        }
                        rb.setText(option.substring(option.indexOf(".") + 1));
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                }
                radioGroup.addView(rb);
                if (clickable) {
                    rb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, TYPE_JUDGEMENT, rb.getTag().toString(), "", "", "", "", "");
                            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
                                if (rb.getTag().toString().equals(v.getTag().toString())) {
                                    rb.setChecked(true);
                                } else {
                                    rb.setChecked(false);
                                }
                                rb.setBackgroundResource(R.drawable.radio_btn_shap);
                            }
                        }
                    });
                }
            }
            mLinearLayout.addView(radioGroup);
            addDividerLine();
        }
    }

    private List<String> answerList = new ArrayList<>();//多选题本地保存的答案

    /**
     * added by eachann 2016-01-28
     *
     * @param content   选择题选项
     * @param clickable 是否可点击(查看试卷：false;答题:true)
     * @param corrects  查看习题 ：正确答案(可多个：ABC)；答题：null
     */
    protected void addMultiOptions(String content, final boolean clickable, final String corrects) {
        List<String> stringList = null;

        if (corrects != null) {
            stringList = new ArrayList<>();
        /*拆分答案*/
            for (int i = 0; i < corrects.length(); i++) {
                stringList.add(corrects.substring(i, i + 1));
            }
        }
        /*拆分选项*/
        String[] options = content.split("∷");
        if (options.length > 0) {
            for (String option : options) {
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
                ll.setBackgroundColor(getResources().getColor(R.color.white));
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final CheckBox rb = new CheckBox(getActivity());
                rb.setLayoutParams(llp);
                rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                rb.setClickable(clickable);
                rb.setGravity(Gravity.CENTER);
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                rb.setText(option.substring(0, 1));
                rb.setTextColor(getResources().getColor(R.color.white));
                rb.setTag(option.substring(0, 1));
                switch (option.substring(0, 1)) {
                    case "A":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "A":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "B":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "B":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "C":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "C":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "D":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "D":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "E":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "E":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "F":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "F":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "G":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "G":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "H":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "H":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                }
                ll.addView(rb);
                if (clickable) {
                    answerList = (stringList != null ? stringList : answerList);
                    rb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (rb.isChecked()) {
                                if (!answerList.contains(rb.getTag().toString())) {
                                    answerList.add(rb.getTag().toString());
                                }
                            } else {
                                answerList.remove(rb.getTag().toString());
                            }
                            mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, TYPE_MULTI_CHOICE, WorkUtils.splitString(answerList), "", "", "", "", "");
                        }
                    });
                }
                //移除RichText,修复选项中出现很长公式显示截断的问题
                WebView webView = new WebView(getActivity());
                LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                mWebViewLP.gravity = Gravity.CENTER_VERTICAL;
                webView.setLayoutParams(mWebViewLP);
                setContentToWebView(webView, option.substring(2));
                ll.addView(webView);
                mLinearLayout.addView(ll);
            }
        }
        addDividerLine();
    }

    /**
     * add by eachann
     *
     * @param content   选择题选项
     * @param clickable 是否可点击(查看试卷：false;答题:true)
     * @param corrects  查看习题 ：正确答案；答题：null
     */
    protected void addOptions(String content, final boolean clickable, final String corrects) {
        List<String> stringList = null;
        if (corrects != null) {
            stringList = new ArrayList<>();
        /*拆分答案*/
            for (int i = 0; i < corrects.length(); i++) {
                stringList.add(corrects.substring(i, i + 1));
            }
        }
        /*拆分选项*/
        String[] options = content.split("∷");
        final MyRadioGroup radioGroup = new MyRadioGroup(getActivity());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        radioGroup.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.setBackgroundColor(getResources().getColor(R.color.white));
        if (options.length > 0) {
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                final LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setPadding(0, 0, mSpaceing, mSpaceing);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final RadioButton rb = new RadioButton(getActivity());
                rb.setLayoutParams(llp);
                rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                rb.setClickable(clickable);
                rb.setGravity(Gravity.CENTER);
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                rb.setText(option.substring(0, 1));
                rb.setTextColor(getResources().getColor(R.color.white));
                rb.setTag(option.substring(0, 1));
                switch (option.substring(0, 1)) {
                    case "A":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "A":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "B":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "B":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "C":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "C":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "D":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "D":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "E":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "E":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "F":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "F":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "G":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "G":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                    case "H":
                        if (corrects != null) {
                            for (String correct : stringList) {
                                switch (correct) {
                                    case "H":
                                        rb.setChecked(true);
                                        break;
                                }
                            }
                        }
                        rb.setBackgroundResource(R.drawable.radio_btn_shap);
                        break;
                }
                ll.addView(rb);
                if (clickable) {
                    rb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, TYPE_SINGLE_CHOICE, rb.getTag().toString(), "", "", "", "", "");
                            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                LinearLayout ll = (LinearLayout) radioGroup.getChildAt(i);
                                RadioButton rb = (RadioButton) ll.getChildAt(0);
                                if (rb.getTag().toString().equals(v.getTag().toString())) {
                                    rb.setChecked(true);
                                } else {
                                    rb.setChecked(false);
                                }
                                rb.setBackgroundResource(R.drawable.radio_btn_shap);
                            }
                        }
                    });
                }
                //移除RichText,修复选项中出现很长公式显示截断的问题
                WebView webView = new WebView(getActivity());
                LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                mWebViewLP.gravity = Gravity.CENTER_VERTICAL;
                webView.setLayoutParams(mWebViewLP);
                setContentToWebView(webView, option.substring(2));
                ll.addView(webView);
                radioGroup.addView(ll);
            }
        }
        mLinearLayout.addView(radioGroup);
        addDividerLine();
    }

    private void setContentToWebView(final WebView webView, String html) {
        mImageList = HtmlUtils.filterImages(html);
        if (mImageList != null && mImageList.size() > 0) {
            webView.addJavascriptInterface(new JsInteraction(mImageList), "control");
            webView.getSettings().setJavaScriptEnabled(true);
            WebViewUtils.setContentToWebView(webView, HtmlUtils.constructExecActionJs(html));
        } else {
            WebViewUtils.setContentToWebView(webView, html);
        }
        webView.setPadding(mSpaceing, 0, mSpaceing, mSpaceing);
    }

    public class JsInteraction {
        private List<String> imageList = new ArrayList<>();

        public JsInteraction(List<String> imageList) {
            this.imageList = imageList;
        }

        @JavascriptInterface
        public void showImage(int position, String url) {
            // TODO: 16-5-31 展示图片的GalleryView
            List<SubjectMaterialPicture> subjectMaterialPictures = new ArrayList<>();
            for (int i = 0; i < imageList.size(); i++) {
                SubjectMaterialPicture smp = new SubjectMaterialPicture();
                smp.setId("" + i);
                smp.setUrl(imageList.get(i));
                subjectMaterialPictures.add(smp);
            }
            SubjectMaterialPicturesActivity.start(getActivity(), subjectMaterialPictures, position);
        }
    }

    /**
     * 添加问答题/计算题/文本题学生回答框
     */
    protected void addAnswerLayout(String mTaskStatus, String answer) {
        TextView titleTv = new TextView(getActivity());
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.answer_title));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleTv.setText(spannableString);
        titleTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleTv.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(titleTv);
        final EditText readEt = new EditText(getActivity());
        readEt.setGravity(Gravity.TOP | Gravity.LEFT);
        readEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        readEt.setMaxLines(5);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(getActivity(), 100f));
        editParams.setMargins(mSpaceing, 0, mSpaceing, 0);
        readEt.setLayoutParams(editParams);
        readEt.setPadding(mSpaceing, 0, mSpaceing, 0);
        readEt.setBackgroundResource(R.drawable.drawale_fill_in_comment_edittext);
        if (mQuestionInfo != null && !TextUtils.isEmpty(mQuestionInfo.getHint())) {
            readEt.setHint(mQuestionInfo.getHint());
        } else {
            readEt.setHint(getString(R.string.input_solution));
        }
        readEt.setText(answer);
        readEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});//最多500个字符
        readEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTaskItemType.equals(TYPE_TEXT)) {//当为作业的文本题时，无taskitemid,但可以用taskid代替，一个作业仅一个文本题
                    mTaskItemId = mTaskId;
                    mStudentTextAnswer = readEt.getText().toString();
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskId, mTaskItemType, "", mStudentTextAnswer, mCurrentResourceName, mCurrentResourceId == null ? "" : mCurrentResourceId, mCurrentResourceLocalPath, mCurrentResourceType);
                } else {
                    mStudentAnswer = readEt.getText().toString();
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, mTaskItemType, mTaskItemType.equals(TYPE_MULTI_CHOICE) ? sortAnswer(mStudentAnswer) : mStudentAnswer, "", mCurrentResourceName, mCurrentResourceId == null ? "" : mCurrentResourceId, mCurrentResourceLocalPath, mCurrentResourceType);
                }
            }
        });
        mLinearLayout.addView(readEt);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_layout_element, null);
        view.setBackgroundColor(Color.TRANSPARENT);
        mBtnPicContainer = (LinearLayout) view.findViewById(R.id.line_select_pic);
        mBtnAudioContainer = (LinearLayout) view.findViewById(R.id.line_select_audio);
        mBtnVideoContainer = (LinearLayout) view.findViewById(R.id.line_select_video);
        if (mTaskItemType.equals(TYPE_TEXT)) {
            mBtnAudioContainer.setVisibility(View.INVISIBLE);
            mBtnVideoContainer.setVisibility(View.INVISIBLE);
        }
        //选择图片
        mBtnPicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
                if (picInfoList.size() < 8) {
                    Intent intent = new Intent(getActivity(), MMSelectorActivity.class);
                    intent.putExtra("EXTRA_TYPE", "IMAGE");
                    intent.putExtra("EXTRA_SIZE", picInfoList.size());
                    startActivityForResult(intent, REQUEST_CODE);
                    UIUtils.addEnterAnim(getActivity());
                } else {
                    ToastUtil.showToast(getActivity(), getString(R.string.tips_have_eight_pics));
                }
            }
        });
        //选择音频
        mBtnAudioContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInsertAudio = false;//是否存在相关音视频数据
                mTaskAnswer = mTaskAnswerDao.queryItemInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
                if (mTaskAnswer == null) {
                    isInsertAudio = false;
                }
                if (mTaskAnswer != null && TYPE_VIDEO.equals(mTaskAnswer.getResourceType())) {
                    isInsertAudio = true;
                }
                if (!isInsertAudio) {
                    Intent intent = new Intent(getActivity(), MMSelectorActivity.class);
                    intent.putExtra("EXTRA_TYPE", "AUDIO");
                    startActivityForResult(intent, REQUEST_CODE);
                    UIUtils.addEnterAnim(getActivity());
                } else {
                    ToastUtil.showToast(getActivity(), getString(R.string.work_answer_have_video));
                }
            }
        });
        //选择视频
        mBtnVideoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInsertVideo = false;//是否存在相关音视频数据
                mTaskAnswer = mTaskAnswerDao.queryItemInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
                if (mTaskAnswer == null) {
                    isInsertVideo = false;
                }
                if (mTaskAnswer != null && TYPE_AUDIO.equals(mTaskAnswer.getResourceType())) {
                    isInsertVideo = true;
                }
                if (!isInsertVideo) {
                    Intent intent = new Intent(getActivity(), MMSelectorActivity.class);
                    intent.putExtra("EXTRA_TYPE", "VIDEO");
                    startActivityForResult(intent, REQUEST_CODE);
                    UIUtils.addEnterAnim(getActivity());
                } else {
                    ToastUtil.showToast(getActivity(), getString(R.string.work_answer_have_audio));
                }
            }
        });
        if (mTaskStatus.equals(WorkItemDetailFragment.STATUS_STU_PROGRESS)) {//做题时添加
            mLinearLayout.addView(view);
        }
    }

    /**
     * 添加附件题题目
     *
     * @param fileName
     * @param fileSize
     */
    protected void addFileLayout(String fileName, String fileSize) {
        TextView contentTv = new TextView(getActivity());
        contentTv.setText(fileName.substring(0, fileName.length() > FILE_NAME_LENGTH_SHOW ? FILE_NAME_LENGTH_SHOW : fileName.length()) + "     " + fileSize);
        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        contentTv.setTextColor(getResources().getColor(R.color.work_file_tip_text_color));
        contentTv.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        contentTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLinearLayout.addView(contentTv);
    }

    protected void addFileTip() {
        TextView fileTipTv = new TextView(getActivity());
        fileTipTv.setText(getResources().getString(R.string.file_tip));
        fileTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        fileTipTv.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        fileTipTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fileTipTv.setBackgroundColor(getResources().getColor(R.color.white));
        mLinearLayout.addView(fileTipTv);
    }

    protected void addStuAnswerTip() {
        TextView fileTipTv = new TextView(getActivity());
        fileTipTv.setText(getResources().getString(R.string.work_item_stu_answer));
        fileTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        fileTipTv.setPadding(mSpaceing, mSpaceing * 2, mSpaceing, mSpaceing);
        fileTipTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fileTipTv.setBackgroundColor(getResources().getColor(R.color.white));
        mLinearLayout.addView(fileTipTv);
    }

    /**
     * 添加点评内容
     *
     * @param commentTitle 老师点评/学生点评
     * @param comment      点评内容
     */
    protected void addComment(String commentTitle, String comment) {
        TextView titleTv = new TextView(getActivity());
        SpannableString spannableString = new SpannableString(commentTitle);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleTv.setText(spannableString);
        titleTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleTv.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(titleTv);

/*        TextView commentTv = new TextView(getActivity());
        commentTv.setText(comment);
        commentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(mSpaceing, 0, mSpaceing, 0);
        commentTv.setLayoutParams(textParams);
        commentTv.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        commentTv.setBackgroundResource(R.drawable.drawale_fill_in_comment_edittext);
        commentTv.setBackgroundColor(Color.TRANSPARENT);
        mLinearLayout.addView(commentTv);*/
        addContent(comment);

    }


    /**
     * 语音按钮
     */
    private ImageView mAudioRecordIv;
    /**
     * 是否可录制语音
     */
    private boolean canRecord;

    /**
     * 添加批阅评论框，并本地保存评论内容
     *
     * @param commentTitle 老师点评/学生点评
     * @param localComment 本地还未上传的评论
     */
    protected void addCommentEditText(String commentTitle, String localComment) {
        TextView titleTv = new TextView(getActivity());
        SpannableString spannableString = new SpannableString(commentTitle);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleTv.setText(spannableString);
        titleTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleTv.setPadding(mSpaceing / 2, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(titleTv);

        View comment = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_read, null);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(getActivity(), 72f));
        //editParams.setMargins(mSpaceing, 0, mSpaceing, 0);
        comment.setLayoutParams(editParams);
        comment.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        final EditText readEt = (EditText) comment.findViewById(R.id.et_comment);
        readEt.setText(localComment);
        readEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});//最多500个字符
        //mAudioRecordIv = (ImageView) comment.findViewById(R.id.iv_audio_record);
        //mAudioRecordIv.setImageResource(R.drawable.audio_record_unselect);
        /*mAudioRecordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioRecordClickListener.OnImageClick();
                if (canRecord) {
                    mAudioRecordIv.setImageResource(R.drawable.audio_record_unselect);
                } else {
                    mAudioRecordIv.setImageResource(R.drawable.audio_record_select);
                }
                canRecord = !canRecord;
            }
        });*/

        readEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mComment = readEt.getText().toString();
                mTaskReadDao.insert(mTaskItemType, mStudentId, mTaskId, mTaskItemId, mScore, mComment);
            }
        });
        mLinearLayout.addView(comment);
    }

    private static AudioRecordClickListener mAudioRecordClickListener;

    public static void setAudioRecordClickListener(AudioRecordClickListener listener) {
        mAudioRecordClickListener = listener;
    }

    /**
     * 打分
     */
    protected void addScore(String score) {
        TextView titleTv = new TextView(getActivity());
        SpannableString spannableString = new SpannableString("得分");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleTv.setText(spannableString);
        titleTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleTv.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        mLinearLayout.addView(titleTv);
        final EditText readEt = new EditText(getActivity());
//        readEt.setHint(getResources().getString(R.string.work_read_comment_hint));
        readEt.setGravity(Gravity.TOP | Gravity.LEFT);
        readEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        readEt.setSingleLine();
        readEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(UIUtils.dip2px(getContext(), 50f), UIUtils.dip2px(getContext(), 38f));
        editParams.setMargins(mSpaceing, 0, mSpaceing, 0);
        readEt.setLayoutParams(editParams);
        readEt.setPadding(mSpaceing, mSpaceing, mSpaceing, mSpaceing);
        readEt.setBackgroundResource(R.drawable.drawale_fill_in_comment_edittext);
        readEt.setText(score);
        readEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isDigitsOnly(s) && !TextUtils.isEmpty(readEt.getText()) && Integer.parseInt(readEt.getText().toString()) > Integer.parseInt(mQuestionInfo.getQuestionScores())) {
                    readEt.setText(null);
                    ToastUtil.showToast(getContext(), getString(R.string.exam_do_read_max_score, mQuestionInfo.getQuestionScores()));
                    return;
                }
                mScore = readEt.getText().toString() + "∷" + mQuestionInfo.getQuestionScores();
                mTaskReadDao.insert(mTaskItemType, mStudentId, mTaskId, mTaskItemId, mScore, TextUtils.isEmpty(mComment) ? "" : mComment);
            }
        });
        mLinearLayout.addView(readEt);
    }

    /**
     * 上传视频失败时显示的感叹号
     */
    private ImageView mVideoUploadFailureTv;

    /**
     * 学生的音频
     *
     * @param playUrl   播放地址
     * @param canDelete 是否可删除 （当学生做题时，可删，其他情况下不能删）
     */
    protected void addServiceAnswerAudioBar(final String playUrl, final boolean canDelete, boolean isSave) {
        if (mLinearLayout.findViewWithTag("ServiceAnswerAudioBar") != null) {
            mLinearLayout.removeView(mLinearLayout.findViewWithTag("ServiceAnswerAudioBar"));
        }
        AudioBar audioBar = new AudioBar(getContext());
        audioBar.setTag("ServiceAnswerAudioBar");
        audioBar.setUrl(playUrl, WorkUtils.getUploadAudioUrl(mUserInfo), false);
        audioBar.setProgressVisible(isSave ? false : true);
        audioBar.setOnAudioLongClickListener(new AudioBar.OnAudioListener() {
            @Override
            public void longClick(String tag) {
                if (canDelete)
                    deleteAudioBar("ServiceAnswerAudioBar");
            }

            @Override
            public void insertAudioInfo(String originalName, String messageId) {
                if (mTaskReadDao != null) {
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, mTaskItemType,
                            mStudentAnswer, mStudentTextAnswer,
                            originalName, messageId, playUrl, TYPE_AUDIO);
                }
            }
        });
        mLinearLayout.addView(audioBar);
    }

    /**
     * 添加语音评论布局
     *
     * @param audioUrl
     * @param isSave   是否已保存
     */
    protected void addAudioCommentLayout(final String audioUrl, final boolean isSave) {
        int mostAudioCount;
        if (isSave) {
            mostAudioCount = AddOverallCommentFragment.MAX_AUDIO_COMMENT_COUNT + 1;
        } else {
            mostAudioCount = AddOverallCommentFragment.MAX_AUDIO_COMMENT_COUNT;
        }
        if (mTaskReadDao.queryCommentAudio(mStudentId, mTaskId, mTaskItemId, 0).size() >= mostAudioCount) {
            Toast.makeText(getContext(), "音频评论数量最多10条", Toast.LENGTH_SHORT).show();
            return;
        }
        final AudioBar audioBar = new AudioBar(getContext());
        audioBar.setTag(audioUrl);
        audioBar.setUrl(audioUrl, WorkUtils.getUploadAudioUrl(mUserInfo), true);
        audioBar.setProgressVisible(isSave ? false : true);
        audioBar.setOnAudioLongClickListener(new AudioBar.OnAudioListener() {
            @Override
            public void longClick(String tag) {
                deleteAudioCommentBar(tag);
            }

            @Override
            public void insertAudioInfo(String originalName, String messageId) {
                if (mTaskReadDao != null && !isSave) {
                    mTaskReadDao.insertCommentAudio(mStudentId, mTaskId, mTaskItemId, audioUrl, audioBar.getLength(), originalName, messageId, 0);
                }
            }
        });
        mLinearLayout.addView(audioBar);
    }

    private void deleteAudioBar(final String tag) {
        DialogUtils dialogUtils = new DialogUtils(getString(R.string.tip_delete_audio), getFragmentManager());
        dialogUtils.setOnRightClickListener(new DialogUtils.OnRightClickListener() {
            @Override
            public void onRightClick() {
                if (mLinearLayout.findViewWithTag(tag) != null) {
                    mLinearLayout.removeView(mLinearLayout.findViewWithTag(tag));
                    //如果在播放，停止播放
                    stopPlayingAudio();
                    stopUpload();
                }
                if (mTaskAnswerDao != null) {
                    queryDBAnswer();
                    mTaskAnswerDao.insert(mStudentId, mTaskId, mTaskItemId, mTaskItemType, mStudentAnswer, mStudentTextAnswer, "", "", "", "");
                }
            }
        });
    }

    /**
     * 删除语音评论条
     *
     * @param tag
     */
    private void deleteAudioCommentBar(String tag) {
        if (mLinearLayout.findViewWithTag(tag) != null) {
            mLinearLayout.removeView(mLinearLayout.findViewWithTag(tag));
            //如果在播放，停止播放
            MediaPlayer mp = WorkAnswerMediaPlayer.newInstance();
            if (mp != null && mp.isPlaying()) {
                mp.stop();
            }
        }
        if (mTaskReadDao != null) {
            mTaskReadDao.deleteCommentAudio(mStudentId, mTaskId, mTaskItemId, tag, 0);
        }
    }

    private void queryDBAnswer() {
        mTaskAnswer = mTaskAnswerDao.queryItemInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
        if (mTaskAnswer != null) {
            if (!"".equals(mTaskAnswer.getStudentAnswer()) || !"null".equals(mTaskAnswer.getStudentAnswer())) {
                mStudentAnswer = mTaskAnswer.getStudentAnswer();
            }
            if (!"".equals(mTaskAnswer.getStudentTextAnswer()) || !"null".equals(mTaskAnswer.getStudentAnswer())) {
                mStudentTextAnswer = mTaskAnswer.getStudentTextAnswer();
            }
            mCurrentResourceLocalPath = mTaskAnswer.getResourceLocalPath();
            mCurrentResourceType = mTaskAnswer.getResourceType();
        }
    }


    /**
     * 题目中出现的音频
     *
     * @param playUrl
     */
    protected void addServiceItemAudioBar(final String playUrl) {
        if (mLinearLayout.findViewWithTag("itemServiceAudioView") != null) {
            mLinearLayout.removeView(mLinearLayout.findViewWithTag("itemServiceAudioView"));
        }

        AudioBar audioBar = new AudioBar(getContext());
        audioBar.setTag("itemServiceAudioView");
        audioBar.setUrl(playUrl, WorkUtils.getUploadAudioUrl(mUserInfo), false);
        audioBar.setProgressVisible(false);
        mLinearLayout.addView(audioBar);
    }

    /**
     * 显示选择的图片列表
     *
     * @param imageDetailList
     */
    protected void addImageLayout(final List<ImageDetail> imageDetailList) {
        mSelectedImageUrlList = (ArrayList<ImageDetail>) imageDetailList;
        if (mLinearLayout.findViewWithTag("imageRecyclerView") != null) {
            mLinearLayout.removeView(mLinearLayout.findViewWithTag("imageRecyclerView"));
        }
        RecyclerView imageRecyclerView = new RecyclerView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);//UIUtils.dip2px(getActivity(), imageUrlList.size() > 4 ? 200 : 100)
        params.gravity = Gravity.CENTER;
        imageRecyclerView.setLayoutParams(params);
        mImagePreviewAdapter.setList(imageDetailList);
        imageRecyclerView.setAdapter(mImagePreviewAdapter);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        imageRecyclerView.setTag("imageRecyclerView");
        mLinearLayout.addView(imageRecyclerView);
    }

    private void deleteImageLayout() {
        if (mLinearLayout.findViewWithTag("imageRecyclerView") != null) {
            mLinearLayout.removeView(mLinearLayout.findViewWithTag("imageRecyclerView"));
        }
    }

    class ImagePreviewAdapter extends MMBaseRecyclerViewAdapter<ImageDetail> {

        private int mPosition = 0;
        private View view;
        private RecyclerView.ViewHolder holder;
        private AnimationDrawable drawable;

        public ImagePreviewAdapter(List<ImageDetail> list, Context context) {
            super(list, context);
        }

        public void setPosition(int position) {
            this.mPosition = position;
            notifyDataSetChanged();
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            view = LayoutInflater.from(context).inflate(R.layout.item_image_selected_task, parent, false);
            holder = new NormalRecyclerViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            ((NormalRecyclerViewHolder) holder).picSd.setImageURI(Uri.fromFile(new File(list.get(position).getPicUrl())));
            drawable = (AnimationDrawable) ((NormalRecyclerViewHolder) holder).progressIv.getBackground();
            switch (list.get(position).getLoadInfo()) {
                case ImageDetail.TYPE_LOADING:
                    drawable.start();
                    ((NormalRecyclerViewHolder) holder).container.setVisibility(View.VISIBLE);
                    break;
                case ImageDetail.TYPE_LOADED:
                    drawable.stop();
                    ((NormalRecyclerViewHolder) holder).container.setVisibility(View.INVISIBLE);
                    break;
                case ImageDetail.TYPE_LOAD_FAILED:
                    drawable.stop();
                    ((NormalRecyclerViewHolder) holder).container.setVisibility(View.INVISIBLE);
                    ((NormalRecyclerViewHolder) holder).failedIv.setVisibility(View.VISIBLE);
                    break;
            }


            /*((NormalRecyclerViewHolder) holder).container.setVisibility(list.get(position).getLoadInfo().equals(ImageDetail.TYPE_LOADED) ? View.INVISIBLE : View.VISIBLE);
            drawable = (AnimationDrawable) ((NormalRecyclerViewHolder) holder).progressIv.getBackground();
            refreshImageStatus(position,drawable);*/
        }

        private void refreshImageStatus(int position, AnimationDrawable dr) {
            if (list == null || list.size() == 0) return;
            if (dr == null) return;
            switch (list.get(position).getLoadInfo()) {
                case ImageDetail.TYPE_LOADING:
                    dr.start();
                    ((NormalRecyclerViewHolder) holder).container.setVisibility(View.VISIBLE);
                    break;
                case ImageDetail.TYPE_LOADED:
                    dr.stop();
                    ((NormalRecyclerViewHolder) holder).container.setVisibility(View.INVISIBLE);
                    break;
                case ImageDetail.TYPE_LOAD_FAILED:
                    dr.stop();
                    ((NormalRecyclerViewHolder) holder).container.setVisibility(View.INVISIBLE);
                    ((NormalRecyclerViewHolder) holder).failedIv.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView picSd;
            LinearLayout container;
            ImageView progressIv;
            ImageView failedIv;

            public NormalRecyclerViewHolder(View view) {
                super(view);
                picSd = (SimpleDraweeView) view.findViewById(R.id.sdv_headpic);
                container = (LinearLayout) view.findViewById(R.id.container_image_upload);
                progressIv = (ImageView) view.findViewById(R.id.iv_image_upload);
                failedIv = (ImageView) view.findViewById(R.id.iv_image_failed);
            }
        }
    }

    private View videoLayout;//视频缩略图
    public static final String FROM_LOACAL = "local";
    public static final String FROM_NET = "net";
    private MediaMetadataRetrieverTask mMediaRetrieverTask;

    /**
     * 添加视频缩略图，点播放跳转到新的播放界面
     *
     * @param videoUrl 播放地址
     * @param from     来自网络/本地
     * @param title    视频名称
     * @param isLocal  是否来自本地；若是，则可删，否则不可删
     */
    protected void addVideoThumbnail(final String videoUrl, final String from, final String title, boolean isLocal) {
        if (mLinearLayout.findViewWithTag("videoLayout") != null) {
            mLinearLayout.removeView(mLinearLayout.findViewWithTag("videoLayout"));
        }
        videoLayout = LayoutInflater.from(getActivity()).inflate(R.layout.item_video_thumbnail_view, null);
        videoLayout.setPadding(mSpaceing, 0, mSpaceing, 0);
        mVideoProgress = (ProgressCircle) videoLayout.findViewById(R.id.pc_video_progress);
        mVideoStartIv = (ImageView) videoLayout.findViewById(R.id.iv_controller);
        mVideoUploadFailureTv = (ImageView) videoLayout.findViewById(R.id.iv_video_upload_failure);
        mVideoUploadFailureTv.setOnClickListener(this);
        if (from.equals(FROM_LOACAL)) {
            videoLayout.setTag("videoLayout");
        } else if (from.equals(FROM_NET)) {
            videoLayout.setTag("itemVideoView");
        }

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) videoLayout.findViewById(R.id.sd_video_task_view);
        //ImageView controller = (ImageView) videoLayout.findViewById(R.id.iv_controller);
        if (isLocal) {
            simpleDraweeView.setImageURI(Uri.parse("file://" + videoUrl));
            mVideoStartIv.setVisibility(View.VISIBLE);
        } else {
            mMediaRetrieverTask = new MediaMetadataRetrieverTask(simpleDraweeView, mVideoStartIv, ScreenUtils.getScreenWidth(getContext()), UIUtils.dip2px(getContext(), 180f));
            mMediaRetrieverTask.execute(videoUrl);
        }

        mVideoStartIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止该页面中播放的音频
                stopPlayingAudio();
                VideoViewActivity.startActivity(getActivity(), videoUrl, from, title);
            }
        });
        if (isLocal) {
            mVideoStartIv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteAudioBar("videoLayout");
                    return false;
                }
            });

            mVideoProgress.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteAudioBar("videoLayout");
                    stopUpload();
                    return false;
                }
            });
        }
        mLinearLayout.addView(videoLayout);
    }

    private void stopPlayingAudio() {
        MediaPlayer mp = WorkAnswerMediaPlayer.newInstance();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                AudioBar.getAnimationDrawable().stop();
            }
        }
    }

    private void stopUpload() {
        if (mMyTask != null) {
            mMyTask.cancel(true);
            mMyTask.setCancel(true);
        }
    }


    /**
     * 获取视频任意一帧作为缩略图
     */
    class MediaMetadataRetrieverTask extends AsyncTask<String, Integer, Bitmap> {
        private SimpleDraweeView simpleDraweeView;
        private ImageView imageView;
        private int screenWidth;
        private int height;

        public MediaMetadataRetrieverTask(SimpleDraweeView simpleDraweeView, ImageView imageView, int screenWidth, int height) {
            super();
            this.simpleDraweeView = simpleDraweeView;
            this.imageView = imageView;
            this.screenWidth = screenWidth;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            MediaCheck check = new MediaCheck();
            Bitmap bitmap = null;
            if (check.isUsable(params[0]) && getActivity() != null) {
                Drawable drawable = getActivity().getResources().getDrawable(R.drawable.video_can_play);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmap = bd.getBitmap();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) return;
            if (bitmap == null && getActivity() != null) {
                Drawable drawable = getActivity().getResources().getDrawable(R.drawable.video_not_play);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmap = bd.getBitmap();
                simpleDraweeView.setImageBitmap(bitmap);
                imageView.setVisibility(View.GONE);
            } else {
                simpleDraweeView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Cog.d(TAG, isVisibleToUser);
        if (!isVisibleToUser) {
            stopPlayingAudio();
            if (mAudioRecordIv != null) {
                mAudioRecordIv.setImageResource(R.drawable.audio_record_unselect);
                canRecord = false;
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Cog.d(TAG, "onAttach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Cog.d(TAG, "onPause");

        //停止音乐
        stopPlayingAudio();
    }

    @Override
    public void onStop() {
        super.onStop();
        Cog.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Cog.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mMediaRetrieverTask != null)
            mMediaRetrieverTask.cancel(true);

        //关闭音频
        MediaPlayer mp = WorkAnswerMediaPlayer.newInstance();
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
        //停止传输
        stopUpload();
    }

    /**
     * @param type 题目类型
     */
    protected void addType(String type) {
        SpannableString spannableString = null;
        switch (type) {
            case TYPE_SINGLE_CHOICE:
                spannableString = new SpannableString(getString(R.string.type_single_choice));
                break;
            case TYPE_MULTI_CHOICE:
                spannableString = new SpannableString(getString(R.string.type_multi_choice));
                break;
            case TYPE_JUDGEMENT:
                spannableString = new SpannableString(getString(R.string.type_judgement));
                break;
            case TYPE_FILL_IN_BLANK:
                spannableString = new SpannableString(getString(R.string.type_fill_in_blank));
                break;
            case TYPE_ASK_ANSWER:
                spannableString = new SpannableString(getString(R.string.type_ask_answer));
                break;
            case TYPE_COMPUTING:
                spannableString = new SpannableString(getString(R.string.type_computing));
                break;
            case TYPE_TEXT:
                spannableString = new SpannableString(getString(R.string.type_text));
                break;
            case TYPE_FILE:
                spannableString = new SpannableString(getString(R.string.type_file));
                break;
            case TYPE_WORK_CONTENT:
                spannableString = new SpannableString(getString(R.string.type_work_content));
                break;
        }
        if (spannableString != null) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK),0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing / 2, mSpaceing, 0, mSpaceing);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        mLinearLayout.addView(textView);
    }

    /**
     * 添加题型，包括是否答对/答错
     *
     * @param type
     * @param isCorrect
     */
    protected void addType(String type, String isCorrect) {
        SpannableString spannableString = null;
        switch (type) {
            case TYPE_SINGLE_CHOICE:
                spannableString = new SpannableString(getString(R.string.type_single_choice) + "  [" + isCorrect + "]");
                break;
            case TYPE_MULTI_CHOICE:
                spannableString = new SpannableString(getString(R.string.type_multi_choice) + "  [" + isCorrect + "]");
                break;
            case TYPE_JUDGEMENT:
                spannableString = new SpannableString(getString(R.string.type_judgement) + "  [" + isCorrect + "]");
                break;
            case TYPE_FILL_IN_BLANK:
                spannableString = new SpannableString(getString(R.string.type_fill_in_blank) + "  [" + isCorrect + "]");
                break;
            case TYPE_ASK_ANSWER:
                spannableString = new SpannableString(getString(R.string.type_ask_answer) + "  [" + isCorrect + "]");
                break;
            case TYPE_COMPUTING:
                spannableString = new SpannableString(getString(R.string.type_computing) + "  [" + isCorrect + "]");
                break;
            case TYPE_TEXT:
                spannableString = new SpannableString(getString(R.string.type_text) + "  [" + isCorrect + "]");
                break;
            case TYPE_FILE:
                spannableString = new SpannableString(getString(R.string.type_file) + "  [" + isCorrect + "]");
                break;
        }
        if (spannableString != null) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(16, true), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (isCorrect.equals("答对")) {
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)),
                        spannableString.length() - 3, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)),
                        spannableString.length() - 3, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        TextView textView = new TextView(getActivity());
        textView.setText(spannableString);
        textView.setPadding(mSpaceing, mSpaceing, 0, mSpaceing);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        mLinearLayout.addView(textView);
    }

    /**
     * 将选择的图片转换成具有上传信息的数据
     *
     * @param mImageBeans
     * @return
     */
    private List<ImageDetail> getSelectedImagesPath(ArrayList<MMImageBean> mImageBeans) {
        List<ImageDetail> imagesPath = new ArrayList<>();
        for (MMImageBean mmImageBean : mImageBeans) {
            ImageDetail imageDetail = new ImageDetail();
            imageDetail.setLoadInfo(ImageDetail.TYPE_LOADING);
            imageDetail.setPicUrl(mmImageBean.getPath());
            imagesPath.add(imageDetail);
        }
        return imagesPath;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
            parent.removeView(mRootView);
        return mRootView;
    }

}
