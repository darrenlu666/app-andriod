package com.codyy.erpsportal.homework.controllers.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.dao.TaskReadDao;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.widgets.wheelview.LoopView;
import com.codyy.erpsportal.exam.widgets.wheelview.OnItemSelectedListener;
import com.codyy.erpsportal.homework.implementclass.AudioRecorder;
import com.codyy.erpsportal.homework.interfaces.AudioRecordClickListener;
import com.codyy.erpsportal.homework.models.entities.teacher.Comment;
import com.codyy.erpsportal.homework.utils.WorkUtils;
import com.codyy.erpsportal.homework.widgets.AudioBar;
import com.codyy.erpsportal.homework.widgets.PressBar;
import com.codyy.erpsportal.homework.widgets.WorkAnswerMediaPlayer;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加总体评价fragment
 * Created by ldh on 2016/1/24.
 */
public class AddOverallCommentFragment extends Fragment implements View.OnClickListener, AudioRecordClickListener, PressBar.RecordListener, RatingBar.OnRatingBarChangeListener {

    private static final String TAG = AddOverallCommentFragment.class.getSimpleName();
    public static final String ARG_ROLE = "arg_role";
    private List<Comment> mCommentList;
    private List<String> mCommentStringList = new ArrayList<>();
    private PopupWindow mCommentPopupWindow;
    private LoopView mCommentLoopView;
    private TextView mShowCommentTv;
    private EditText mShowCommentEditText;
    private int mRatingCount;
    private ImageView mIvAudioComment;
    private View mView = null;
    private TaskReadDao mTaskReadDao;
    private String mStudentId;
    private String mTaskId;
    private String mReadRole;
    /**
     * 底部语音录制按钮
     */
    private PressBar mPressBar;

    /**
     * 动态加载的音频评论条
     */
    private LinearLayout mAudioBarLayout;
    /**
     * 该界面唯一的音频播放器对象
     */
    private MediaPlayer mMediaPlayer;
    /**
     * 音频评论集合
     */
    private List<TaskReadDao.TaskItemReadAudioInfo> mAudioInfoList;
    /**
     * 音频评论的数量最多为10条
     */
    public static final int MAX_AUDIO_COMMENT_COUNT = 10;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskReadDao = TaskReadDao.newInstance(getActivity());
        mStudentId = getArguments().getString(TaskFragment.ARG_STUDENT_ID);
        mTaskId = getArguments().getString(TaskFragment.ARG_WORK_ID);
        mReadRole = getArguments().getString(ARG_ROLE);
        mMediaPlayer = new MediaPlayer();
        loadComment();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_overall_comment_editable, container, false);
        mAudioBarLayout = (LinearLayout) mView.findViewById(R.id.ll_audio_comment);
        TextView mInsertCommentTv = (TextView)mView.findViewById(R.id.insert_comment_tv);
        loadLocalAudioComment();
        initAudioCommentView();
        RatingBar mRating = (RatingBar) mView.findViewById(R.id.rb_rating_total);
        mRating.setOnRatingBarChangeListener(this);
        mIvAudioComment = (ImageView) mView.findViewById(R.id.iv_total_audio_comment);
        mIvAudioComment.setImageResource(R.drawable.audio_record_unselect);
        mIvAudioComment.setOnClickListener(this);
        mShowCommentTv = (TextView) mView.findViewById(R.id.comment_list_tv);
        mShowCommentEditText = (EditText) mView.findViewById(R.id.overall_comment_et);
        mShowCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        LinearLayout mCommentLinearLayout = (LinearLayout) mView.findViewById(R.id.line_comment);
        mCommentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentStringList.size() > 0) {
                    showPopWindow(v, inflater);
                } else {
                    ToastUtil.showToast(getActivity(), getResources().getString(R.string.work_read_comment_failure_tip));
                }
            }
        });
        if(!mReadRole.equals(WorkUtils.READ_TYPE_TEACHER)){
            mCommentLinearLayout.setVisibility(View.GONE);
            mInsertCommentTv.setVisibility(View.GONE);
        }
        return mView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mIvAudioComment != null) {//当不可见时
            mIvAudioComment.setImageResource(R.drawable.audio_record_unselect);
            canRecord = false;
            mPressBar.setRecordListener(null);
        } else {
            if (mPressBar != null)
                mPressBar.setRecordListener(this);
        }
    }

    /**
     * 初始化“按住说话按钮”  和 音频评论条
     */
    private void initAudioCommentView() {
        mPressBar = (PressBar) getActivity().findViewById(R.id.pb_dialog);
        mPressBar.setAudioRecorder(new AudioRecorder(getContext()));
    }

    private void loadComment() {
        Map<String, String> params = new HashMap<>();
        params.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        params.put("baseUserId", UserInfoKeeper.obtainUserInfo().getBaseUserId());
        RequestSender requestSender = new RequestSender(getActivity());
        requestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_COMMENT_TEMP, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result")) && mShowCommentEditText != null) {
                    mCommentList = Comment.parseResponse(response);
                    if (mCommentList != null) {
                        mCommentStringList.add("请选择插入点评");
                        for (int i = 0; i < mCommentList.size(); i++) {
                            mCommentStringList.add(mCommentList.get(i).getCommentsStr());
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {
                Log.e(TAG, "数据获取失败！");
            }
        }));
    }

    private void showPopWindow(View parent, LayoutInflater inflater) {
        if (mCommentPopupWindow == null) {
            View view = inflater.inflate(R.layout.dialog_comment_list, null);
            view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommentPopupWindow.dismiss();
                }
            });
            mCommentLoopView = (LoopView) view.findViewById(R.id.lv_comment_list);
            mCommentLoopView.setNotLoop();
            mCommentLoopView.setInitPosition(0);
            mCommentLoopView.setTextSize(UIUtils.dip2px(getActivity(), 5));
            mCommentLoopView.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    if (index == 0) {
                        mShowCommentTv.setText("");
                        mShowCommentEditText.setText("");
                    } else {
                        String currentComment = mCommentStringList.get(index);
                        mCommentLoopView.setItems(mCommentStringList);
                        mShowCommentTv.setText(currentComment);
                        mShowCommentEditText.setText(currentComment);
                    }
                }
            });
            mCommentLoopView.setItems(mCommentStringList);
            mCommentPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mCommentPopupWindow.setTouchable(true);
        mCommentPopupWindow.setOutsideTouchable(true);
        mCommentPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mCommentPopupWindow.showAtLocation(getView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public EditText getCommentEditTextView() {
        if (mView == null) {
            mView = getActivity().getLayoutInflater().inflate(R.layout.fragment_overall_comment_editable, null, false);
        }
        return (EditText) mView.findViewById(R.id.overall_comment_et);
    }

    private static AudioRecordClickListener mAudioRecordClickListener;

    public static void setAudioRecordClickListener(AudioRecordClickListener listener) {
        mAudioRecordClickListener = listener;
    }

    @Override
    public void OnImageClick() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_total_audio_comment:
                if (mAudioRecordClickListener != null) {
                    mAudioRecordClickListener.OnImageClick();
                    if (canRecord) {
                        mIvAudioComment.setImageResource(R.drawable.audio_record_unselect);
                    } else {
                        mIvAudioComment.setImageResource(R.drawable.audio_record_select);
                    }
                    canRecord = !canRecord;
                }
                break;
        }
    }

    /**
     * 是否显示“可录制”状态
     */
    private boolean canRecord;

    /**
     * 录制语音后，到此处处理
     *
     * @param recordFilePath
     */
    @Override
    public void RecordEnd(String recordFilePath, int duration) {
        if (mTaskReadDao.queryCommentAudio(mStudentId, mTaskId, "", 1).size() < MAX_AUDIO_COMMENT_COUNT) {
            addAudioBar(recordFilePath, setMediaRecord(recordFilePath), false);
        } else {
            Toast.makeText(getContext(), "音频评论数量最多10条", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化音频播放器
     *
     * @param recordFilePath
     * @return
     */
    private String setMediaRecord(String recordFilePath) {
        String audioLength = "";
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getContext(), Uri.fromFile(new File(recordFilePath)));
            mMediaPlayer.prepare();
            audioLength = WorkUtils.formatCommentTime(mMediaPlayer.getDuration(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioLength;
    }

    /**
     * 添加音频评论条
     */
    private void addAudioBar(final String url, final String audioLength, final boolean isSave) {
        AudioBar audioBar = new AudioBar(getContext());
        audioBar.setTag(url);
        audioBar.setUrl(url, WorkUtils.getUploadAudioUrl(UserInfoKeeper.obtainUserInfo()), true);
        audioBar.setProgressVisible(!isSave);
        audioBar.setOnAudioLongClickListener(new AudioBar.OnAudioListener() {
            @Override
            public void longClick(String tag) {
                deleteAudioCommentBar(tag);
            }

            @Override
            public void insertAudioInfo(String originalName, String messageId) {
                if (mTaskReadDao != null && !isSave) {
                    mTaskReadDao.insertCommentAudio(mStudentId, mTaskId, "", url, audioLength, originalName, messageId, 1);
                }
            }
        });

        mAudioBarLayout.addView(audioBar);
    }


    private void deleteAudioCommentBar(String tag) {
        if (mAudioBarLayout.findViewWithTag(tag) != null) {
            mAudioBarLayout.removeView(mAudioBarLayout.findViewWithTag(tag));
            //如果在播放，停止播放
            MediaPlayer mp = WorkAnswerMediaPlayer.newInstance();
            if (mp != null && mp.isPlaying()) {
                mp.stop();
            }
        }
        deleteAudioDBInfo(tag);
    }

    /**
     * 删除音频信息
     *
     * @param audioUrl
     */
    private void deleteAudioDBInfo(String audioUrl) {
        if (mTaskReadDao != null) {
            mTaskReadDao.deleteCommentAudio(mStudentId, mTaskId, "", audioUrl, 1);
        }
    }

    /**
     * 加载已有的音频评论
     */
    private void loadLocalAudioComment() {
        mAudioInfoList = mTaskReadDao.queryCommentAudio(mStudentId, mTaskId, "", 1);
        mAudioBarLayout.removeAllViews();
        for (TaskReadDao.TaskItemReadAudioInfo audioInfo : mAudioInfoList) {
            addAudioBar(audioInfo.getAudioUrl(), audioInfo.getAudioLength(), true);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        mRatingCount = (int) rating;
    }

    public int getRating() {
        return mRatingCount;
    }
}
