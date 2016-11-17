package com.codyy.erpsportal.homework.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.homework.implementclass.AudioRecorder;
import com.codyy.erpsportal.homework.controllers.activities.WorkItemDetailActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.homework.models.entities.student.ImageDetail;
import com.codyy.erpsportal.homework.widgets.PressBar;
import com.codyy.erpsportal.commons.models.dao.TaskAnswerDao;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
import com.codyy.erpsportal.homework.models.entities.task.TaskPicInfo;
import com.codyy.erpsportal.homework.utils.WorkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 习题详情页fragment
 * Created by ldh on 2016/1/15.
 */
public class WorkItemDetailFragment extends TaskFragment {
    private static final String TAG = WorkItemDetailFragment.class.getSimpleName();
    public static final String STATUS_TEACHER_VIEW = "STATUS_TEACHER_VIEW";//老师-查看
    public static final String STATUS_TEACHER_READ = "STATUS_TEACHER_READ";//老师-批阅
    public static final String STATUS_TEACHER_STATISTIC = "STATUS_TEACHER_STATISTIC";//老师-统计

    public static final String STATUS_STU_VIEW = "STATUS_STU_VIEW";//学生-我的批阅-查看
    public static final String STATUS_STU_READ = "STATUS_STU_READ";//学生-我的批阅-批阅
    public static final String STATUS_STU_READ_BROWSER = "STATUS_STU_READ_BROWSER";//学生-我的批阅-查看批阅

    public static final String STATUS_STU_PROGRESS = "STATUS_STU_PROGRESS";//学生-我的作业-未完成
    public static final String STATUS_STU_END = "STATUS_STU_END";//学生-我的作业-待批阅
    public static final String STATUS_STU_CHECKED = "STATUS_STU_CHECKED";//学生-我的作业-已完成

    public static final String STATUS_PAR_PROGRESS = "STATUS_PAR_PROGRESS";//家长-未完成
    public static final String STATUS_PAR_END = "STATUS_PAR_END";//家长-待批阅
    public static final String STATUS_PAR_CHECKED = "STATUS_PAR_CHECKED";//家长-已完成

    private String mTaskStatus;//显示状态

    /**
     * 语音录制后的文件路径
     */
    private String mRecordFileName;

    /**
     * 底部语音录制按钮
     */
    private PressBar mPressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        if (mStudentAnswersByPerson != null) {
            for (int i = 0; i < mStudentAnswersByPerson.getNomalAnswerList().size(); i++) {
                if (mStudentAnswersByPerson.getNomalAnswerList().get(i).getWorkQuestionId().equals(mTaskItemId)) {
                    mNomalAnswerListEntity = mStudentAnswersByPerson.getNomalAnswerList().get(i);
                }
            }
            mDocAnswerList = getArguments().getParcelableArrayList(WorkItemDetailActivity.ARG_STU_ANSWER_FILE_INFO);
        }
        if (mItemInfoClass != null) {
            switch (mItemInfoClass.getWorkItemType()) {
                case TaskFragment.TYPE_WORK_CONTENT://作业内容
                    addType(mItemInfoClass.getWorkItemType());
                    addContent(mItemInfoClass.getWorkContent());
                    for (int i = 0; i < mItemInfoClass.getAttachmentList().size(); i++) {
                        addFileLayout(mItemInfoClass.getAttachmentList().get(i).getAttachmentName(), mItemInfoClass.getAttachmentList().get(i).getAttachmentSize());
                    }
                    addFileTip();

                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {
                        addAnswerLayout(mTaskStatus, mTaskAnswer != null ? mTaskAnswer.getStudentTextAnswer() : "");
                        List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, TYPE_TEXT);
                        if (picInfoList.size() > 0) {
                            addImageLayout(getImageUrlList(picInfoList));
                        }
                    }

                    if (isShowStuAnswer(mTaskStatus) && mNomalAnswerListEntity != null) {
                        addStudentAnswer(mNomalAnswerListEntity.getMyAnswer());
                        if (mDocAnswerList != null) {
                            for (int i = 0; i < mDocAnswerList.size(); i++) {
                                addFileLayout(mDocAnswerList.get(i).getDocName(), mDocAnswerList.get(i).getDocSize());
                            }
                            addFileTip();
                        }
                    }
                    break;
                case TaskFragment.TYPE_SINGLE_CHOICE://单选题
                    if (isShowCorrectInfo(mTaskStatus) && mNomalAnswerListEntity != null) {
                        addType(mItemInfoClass.getWorkItemType(), mNomalAnswerListEntity.getCorrectFlag().equals("Y") ? "答对" : "答错");
                    } else {
                        addType(mItemInfoClass.getWorkItemType());
                    }
                    addContent(mItemInfoClass.getWorkContent());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {
                        addOptions(mItemInfoClass.getWorkItemOptions(), true,
                                mTaskAnswer != null ? mTaskAnswer.getStudentAnswer() : null);
                    } else {
                        addOptions(mItemInfoClass.getWorkItemOptions(), mTaskStatus.equals(STATUS_STU_PROGRESS),
                                isShowItemAnswer(mTaskStatus) ? mItemInfoClass.getTrueAnswer() : isShowStuAnswer(mTaskStatus) ? (mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : null) : null);
                    }
                    if (isShowStuAnswer(mTaskStatus)) {//是否显示学生答案
                        addStudentAnswer(mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "");
                    }
                    if (isShowItemAnswer(mTaskStatus)) {//是否显示题目答案
                        addCorrectAnswer(mItemInfoClass.getTrueAnswer());
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getItemAnalysisResourceId(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getItemAnalysisResourceId());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    break;
                case TaskFragment.TYPE_MULTI_CHOICE://多选题
                    if (isShowCorrectInfo(mTaskStatus)) {
                        addType(mItemInfoClass.getWorkItemType(), mNomalAnswerListEntity != null ? (mNomalAnswerListEntity.getCorrectFlag().equals("Y") ? "答对" : "答错") : "");
                    } else {
                        addType(mItemInfoClass.getWorkItemType());
                    }
                    addContent(mItemInfoClass.getWorkContent());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {
                        addMultiOptions(mItemInfoClass.getWorkItemOptions(), true,
                                mTaskAnswer != null ? mTaskAnswer.getStudentAnswer() : null);
                    } else {
                        addMultiOptions(mItemInfoClass.getWorkItemOptions(), mTaskStatus.equals(STATUS_STU_PROGRESS),
                                isShowItemAnswer(mTaskStatus) ? mItemInfoClass.getTrueAnswer() : isShowStuAnswer(mTaskStatus) ? (mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "") : null);
                    }
                    if (isShowStuAnswer(mTaskStatus)) {
                        addStudentAnswer(mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "");
                    }
                    if (isShowItemAnswer(mTaskStatus)) {
                        addCorrectAnswer(mItemInfoClass.getTrueAnswer());
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getItemAnalysisResourceId(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getItemAnalysisResourceId());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    break;
                case TaskFragment.TYPE_JUDGEMENT://判断题
                    if (isShowCorrectInfo(mTaskStatus)) {
                        addType(mItemInfoClass.getWorkItemType(), mNomalAnswerListEntity != null ? (mNomalAnswerListEntity.getCorrectFlag().equals("Y") ? "答对" : "答错") : "");
                    } else {
                        addType(mItemInfoClass.getWorkItemType());
                    }
                    addContent(mItemInfoClass.getWorkContent());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {
                        addJudgeOptions(mItemInfoClass.getWorkItemOptions(), true,
                                mTaskAnswer != null ? mTaskAnswer.getStudentAnswer() : null);
                    } else {
                        addJudgeOptions(mItemInfoClass.getWorkItemOptions(), mTaskStatus.equals(STATUS_STU_PROGRESS),
                                isShowItemAnswer(mTaskStatus) ? mItemInfoClass.getTrueAnswer() : isShowStuAnswer(mTaskStatus) ? (mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "") : null);
                    }
                    if (isShowStuAnswer(mTaskStatus)) {
                        addStudentAnswer(mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "");
                    }
                    if (isShowItemAnswer(mTaskStatus)) {
                        addCorrectAnswer(mItemInfoClass.getTrueAnswer());
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getItemAnalysisResourceId(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getItemAnalysisResourceId());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    break;
                case TaskFragment.TYPE_FILL_IN_BLANK://填空题
                    if (isShowCorrectInfo(mTaskStatus)) {
                        addType(mItemInfoClass.getWorkItemType(), mNomalAnswerListEntity != null ? (mNomalAnswerListEntity.getCorrectFlag().equals("Y") ? "答对" : "答错") : "");
                    } else {
                        addType(mItemInfoClass.getWorkItemType());
                    }
                    addContent(mItemInfoClass.getWorkContent());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {//状态为学生-我的作业-未完成 时，才需要添加填空题的框框
                        if (mTaskAnswer != null && !mTaskAnswer.getStudentAnswer().equals("")) {
                            addAnswerFillInBlank(mItemInfoClass.getTrueFillAnswer().size(), mTaskAnswer.getStudentAnswer());
                        } else {
                            addAnswerFillInBlank(mItemInfoClass.getTrueFillAnswer().size());
                        }
                    }
                    if (isShowStuAnswer(mTaskStatus)) {
                        addStudentAnswer(WorkUtils.parserStuFillAnswer(mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : ""));
                    }
                    if (isShowItemAnswer(mTaskStatus)) {
                        String fillTrueAnswer = "";
                        for (int i = 0; i < mItemInfoClass.getTrueFillAnswer().size(); i++) {
                            fillTrueAnswer = fillTrueAnswer + (fillTrueAnswer.equals("") ? "" : " ; ") + mItemInfoClass.getTrueFillAnswer().get(i);
                        }
                        addCorrectAnswer(fillTrueAnswer);
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    break;
                case TaskFragment.TYPE_ASK_ANSWER://问答题
                    addType(mItemInfoClass.getWorkItemType());
                    addContent(mItemInfoClass.getWorkContent());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {//状态为学生答题时
                        addAnswerLayout(mTaskStatus, mTaskAnswer != null ? mTaskAnswer.getStudentAnswer() : "");
                        List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
                        if (picInfoList.size() > 0) {
                            addImageLayout(getImageUrlList(picInfoList));
                        }
                        addDoWorkResource();
                    }
                    if (isShowStuAnswer(mTaskStatus)) {
                        addStudentAnswer(mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "");
                        if (mNomalAnswerListEntity != null) {
                            if (mNomalAnswerListEntity.getAnswerVideo().contains(TYPE_VIDEO_STRING)) {//加载视频
                                addVideoThumbnail(mNomalAnswerListEntity.getAnswerVideo(), FROM_NET, "", false);
                            } else if (mNomalAnswerListEntity.getAnswerVideo().contains(TYPE_AUDIO_STRING)) {//加载音频
                                addServiceAnswerAudioBar(mNomalAnswerListEntity.getAnswerVideo() + "?phoneType=android", false, true);
                            }
                        }
                    }
                    if (isShowItemAnswer(mTaskStatus)) {
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getItemAnalysisResourceId(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getItemAnalysisResourceId());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    if (isShowCommentLayout(mTaskStatus) && mNomalAnswerListEntity != null) {//是否显示点评框
                        if ("Y".equals(mNomalAnswerListEntity.getReadOverFlag())) {
                            addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                    mNomalAnswerListEntity.getComment());
                        } else {
                            addCommentEditText(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT, getArguments().getString(TaskFragment.ARG_COMMENT));
                            addAudioBar();
                        }
                    }
                    if (isShowCommentContent(mTaskStatus) && mNomalAnswerListEntity != null) {
                        addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                mNomalAnswerListEntity.getComment().equals("") ? getResources().getString(R.string.work_item_comment_no) : mNomalAnswerListEntity.getComment());

                    }
                    break;
                case TaskFragment.TYPE_COMPUTING://计算题
                    addType(mItemInfoClass.getWorkItemType());
                    addContent(mItemInfoClass.getWorkContent());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {//状态为学生答题时
                        addAnswerLayout(mTaskStatus, mTaskAnswer != null ? mTaskAnswer.getStudentAnswer() : "");
                        List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);
                        if (picInfoList.size() > 0) {
                            addImageLayout(getImageUrlList(picInfoList));
                        }
                        addDoWorkResource();
                    }
                    if (isShowStuAnswer(mTaskStatus)) {
                        addStudentAnswer(mNomalAnswerListEntity != null ? mNomalAnswerListEntity.getMyAnswer() : "");
                        if (mNomalAnswerListEntity != null) {
                            if (mNomalAnswerListEntity.getAnswerVideo().contains(TYPE_VIDEO_STRING)) {//加载视频
                                addVideoThumbnail(mNomalAnswerListEntity.getAnswerVideo(), FROM_NET, "", false);
                            } else if (mNomalAnswerListEntity.getAnswerVideo().contains(TYPE_AUDIO_STRING)) {//加载音频
                                addServiceAnswerAudioBar(mNomalAnswerListEntity.getAnswerVideo() + "?phoneType=android", false, true);
                            }
                        }
                    }
                    if (isShowItemAnswer(mTaskStatus)) {
                        //addCorrectAnswer(mItemInfoClass.getTrueAnswer());//计算题没有正确答案
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getItemAnalysisResourceId(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getItemAnalysisResourceId());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    if (isShowCommentLayout(mTaskStatus) && mNomalAnswerListEntity != null) {//是否显示点评框
                        if ("Y".equals(mNomalAnswerListEntity.getReadOverFlag())) {
                            addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                    mNomalAnswerListEntity.getComment());
                        } else {
                            addCommentEditText(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT, getArguments().getString(TaskFragment.ARG_COMMENT));
                            addAudioBar();
                        }
                    }
                    if (isShowCommentContent(mTaskStatus) && mNomalAnswerListEntity != null) {
                        addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                mNomalAnswerListEntity.getComment().equals("") ? getResources().getString(R.string.work_item_comment_no) : mNomalAnswerListEntity.getComment());
                    }
                    break;
                case TaskFragment.TYPE_TEXT://文本题
                    addType(mItemInfoClass.getWorkItemType());
                    addContent(mItemInfoClass.getTextQestion());
                    if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_VIDEO_STRING)) {//加载视频
                        addVideoThumbnail(mItemInfoClass.getWorkItemResourceUrl(), FROM_NET, "", false);
                    } else if (mItemInfoClass.getWorkItemResourceUrl().contains(TYPE_AUDIO_STRING)) {//加载音频
                        addServiceItemAudioBar(mItemInfoClass.getWorkItemResourceUrl());
                    }
                    if (mTaskStatus.equals(STATUS_STU_PROGRESS)) {//状态为学生答题时
                        addAnswerLayout(mTaskStatus, mTaskAnswer != null ? mTaskAnswer.getStudentTextAnswer() : "");
                        List<TaskPicInfo> picInfoList = mTaskAnswerDao.queryPicInfo(mStudentId, mTaskId, mTaskItemId, TYPE_TEXT);
                        if (picInfoList.size() > 0) {
                            addImageLayout(getImageUrlList(picInfoList));
                        }
                    }
                    if (isShowStuAnswer(mTaskStatus)) {
                        addStudentAnswer(mStudentAnswersByPerson != null ? mStudentAnswersByPerson.getTextAnswer() : "");
                    }
                    if (isShowItemAnswer(mTaskStatus)) {
                        addResolve(mItemInfoClass.getItemAnalysis(), mItemInfoClass.getItemAnalysisResourceId());
                        if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_VIDEO_STRING)) {//加载视频
                            addVideoThumbnail(mItemInfoClass.getItemAnalysisResourceId(), FROM_NET, "", false);
                        } else if (mItemInfoClass.getItemAnalysisResourceId().contains(TYPE_AUDIO_STRING)) {//加载音频
                            addServiceItemAudioBar(mItemInfoClass.getItemAnalysisResourceId());
                        }
                        addKnowledgePoints(mItemInfoClass.getKnowledgePoint());
                        addDifficultyFactor(mItemInfoClass.getDifficulty(), true);
                    }
                    if (isShowCommentLayout(mTaskStatus)) {//是否显示点评框
                        if ("Y".equals(mStudentAnswersByPerson.getTextReadOverFlag())) {
                            addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                    mStudentAnswersByPerson.getTextAnswerComment().equals("") ? getResources().getString(R.string.work_item_comment_no) : mStudentAnswersByPerson.getTextAnswerComment());
                        } else {
                            addCommentEditText(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT, getArguments().getString(TaskFragment.ARG_COMMENT));
                        }
                    }
                    if (isShowCommentContent(mTaskStatus)) {
                        addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                mStudentAnswersByPerson.getTextAnswerComment().equals("") ? getResources().getString(R.string.work_item_comment_no) : mStudentAnswersByPerson.getTextAnswerComment());
                    }
                    break;
                case TaskFragment.TYPE_FILE://附件题
                    addType(mItemInfoClass.getWorkItemType());
                    for (int i = 0; i < mItemInfoClass.getAttachmentList().size(); i++) {
                        addFileLayout(mItemInfoClass.getAttachmentList().get(i).getAttachmentName(), mItemInfoClass.getAttachmentList().get(i).getAttachmentSize());
                    }
                    addFileTip();

                    if (isShowStuAnswer(mTaskStatus)) {
                        addStuAnswerTip();
                        if (mDocAnswerList != null) {
                            for (int i = 0; i < mDocAnswerList.size(); i++) {
                                addFileLayout(mDocAnswerList.get(i).getDocName(), mDocAnswerList.get(i).getDocSize());
                            }
                            addFileTip();
                        }
                    }

                    if (isShowCommentContent(mTaskStatus) && mStudentAnswersByPerson != null) {
                        addComment(mIsTeacherRead.equals(WorkUtils.READ_TYPE_TEACHER) ? TaskFragment.TITLE_COMMENT_TEACHER : TaskFragment.TITLE_COMMENT_STUDENT,
                                mStudentAnswersByPerson.getDocAnswerComment().equals("") ? getResources().getString(R.string.work_item_comment_no) : mStudentAnswersByPerson.getDocAnswerComment());
                    }
                    break;
            }
        }
    }


    /**
     * 添加已录制的语音评论
     */
    private void addAudioBar() {
        List<TaskReadDao.TaskItemReadAudioInfo> list = mTaskReadDao.queryCommentAudio(mStudentId, mTaskId, mTaskItemId, 0);
        if (list == null || list.size() == 0) return;
        for (int i = 0; i < list.size(); i++) {
            addAudioCommentLayout(list.get(i).getAudioUrl(), true);
        }
    }

    /**
     * 添加已做过的题的音视频
     */
    private void addDoWorkResource() {
        if (mTaskAnswer != null && mTaskAnswer.getResourceType().equals(TYPE_AUDIO)) {
            addServiceAnswerAudioBar(mTaskAnswer.getResourceLocalPath(), true, true);
        } else if (mTaskAnswer != null && mTaskAnswer.getResourceType().equals(TYPE_VIDEO)) {
            addVideoThumbnail(mTaskAnswer.getResourceLocalPath(), FROM_LOACAL, "", true);
        }
    }

    /**
     * 初始化题目相关信息
     */
    private void init() {
        mItemInfoClass = getArguments().getParcelable(WorkItemDetailActivity.ARG_ITEM_INFO);//题目信息
        mTaskStatus = getArguments().getString(ARG_TASK_STATUS);//获取作业状态（12种）
        mTaskId = getArguments().getString(ARG_WORK_ID);
        mStudentId = getArguments().getString(ARG_STUDENT_ID);
        mIsTeacherRead = getArguments().getString(ARG_IS_TEACHER_READ);
        mTaskItemType = mItemInfoClass.getWorkItemType();
        mTaskItemId = mItemInfoClass.getWorkItemId();
        mStudentAnswersByPerson = getArguments().getParcelable(WorkItemDetailActivity.ARG_STU_ANSWER_INFO);
        mTaskType = TaskFragment.TYPE_WORK;//任务类型：作业/测试
        mTaskReadDao = TaskReadDao.newInstance(getActivity());
        mTaskAnswerDao = TaskAnswerDao.getInstance(getActivity());
        mTaskAnswer = mTaskAnswerDao.queryItemInfo(mStudentId, mTaskId, mTaskItemId, mTaskItemType);

        mPressBar = (PressBar) getActivity().findViewById(R.id.pb_dialog);
        mPressBar.setAudioRecorder(new AudioRecorder(getContext()));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mPressBar == null) return;
        if (isVisibleToUser) {
            mPressBar.setRecordListener(new PressBar.RecordListener() {
                @Override
                public void RecordEnd(String recordFilePath, int duration) {
                    mRecordFileName = recordFilePath;
                    addAudioCommentLayout(recordFilePath, false);
                    pullToDown();
                }
            });
        } else {
            mPressBar.setRecordListener(null);
        }
    }

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

    /**
     * 是否显示学生答案
     *
     * @param taskStatus
     * @return
     */
    public static boolean isShowStuAnswer(String taskStatus) {
        return (taskStatus.equals(STATUS_PAR_END) ||
                taskStatus.equals(STATUS_TEACHER_READ) || taskStatus.equals(STATUS_STU_READ) ||
                taskStatus.equals(STATUS_STU_END) || taskStatus.equals(STATUS_TEACHER_STATISTIC) ||
                taskStatus.equals(STATUS_STU_READ_BROWSER) || taskStatus.equals(STATUS_PAR_CHECKED) || taskStatus.equals(STATUS_STU_CHECKED));
    }


    /**
     * 是否显示题目答案
     *
     * @param taskStatus
     * @return
     */
    private boolean isShowItemAnswer(String taskStatus) {
        return (taskStatus.equals(STATUS_TEACHER_VIEW) || taskStatus.equals(STATUS_TEACHER_STATISTIC) || taskStatus.equals(STATUS_TEACHER_READ) || taskStatus.equals(STATUS_STU_READ) ||
                taskStatus.equals(STATUS_STU_READ_BROWSER) || taskStatus.equals(STATUS_PAR_CHECKED) || taskStatus.equals(STATUS_STU_CHECKED) || taskStatus.equals(STATUS_STU_VIEW));
    }

    /**
     * 是否显示点评框
     *
     * @param taskStatus
     * @return
     */
    private boolean isShowCommentLayout(String taskStatus) {
        return (taskStatus.equals(STATUS_TEACHER_READ) || taskStatus.equals(STATUS_STU_READ));
    }

    /**
     * 是否显示点评内容
     *
     * @param taskStatus
     * @return
     */
    private boolean isShowCommentContent(String taskStatus) {
        return (taskStatus.equals(STATUS_TEACHER_STATISTIC) || taskStatus.equals(STATUS_STU_READ_BROWSER) ||
                taskStatus.equals(STATUS_PAR_CHECKED) || taskStatus.equals(STATUS_STU_CHECKED));
    }

    /**
     * 是否显示答对答错
     *
     * @param taskStatus
     * @return
     */
    private boolean isShowCorrectInfo(String taskStatus) {
        return (taskStatus.equals(STATUS_TEACHER_READ) || taskStatus.equals(STATUS_TEACHER_STATISTIC) || taskStatus.equals(STATUS_STU_CHECKED) || taskStatus.equals(STATUS_STU_READ)
                || taskStatus.equals(STATUS_STU_READ_BROWSER) || taskStatus.equals(STATUS_PAR_CHECKED));
    }
}
