package com.codyy.erpsportal.exam.controllers.activities.teacher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.ScreenUtils;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.rethink.controllers.activities.SubjectMaterialPicturesActivity;
import com.codyy.erpsportal.commons.controllers.activities.TeacherReadByTopicActivity;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.utils.HtmlUtils;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.controllers.activities.student.StudentReadActivity;
import com.codyy.erpsportal.exam.controllers.fragments.dialogs.SwitchTopicDialog;
import com.codyy.erpsportal.exam.models.entities.QuestionInfo;
import com.codyy.erpsportal.exam.utils.MediaCheck;
import com.codyy.erpsportal.homework.controllers.activities.VideoViewActivity;
import com.codyy.erpsportal.homework.controllers.fragments.WorkItemDetailFragment;
import com.codyy.erpsportal.homework.models.entities.ItemInfoClass;
import com.codyy.erpsportal.homework.widgets.AudioBar;
import com.codyy.erpsportal.homework.widgets.SlidingFloatScrollView;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.perlcourseprep.models.entities.SubjectMaterialPicture;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.services.WeiBoMediaService;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.WebViewUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 教师-按习题批阅
 * Created by eachann on 2016-03-02 11:36:11.
 */
public class TeacherDoReadByTopicActivity extends TeacherReadByTopicActivity {
    private static final String TAG = TeacherDoReadByTopicActivity.class.getSimpleName();
    private ViewPager.OnPageChangeListener mListener;
    private ArrayList<ItemInfoClass> mData = new ArrayList<>();

    //    @Bind(R.id.wv_stu_answer)
    WebView mWvStuAnswer;//学生答案
    private DialogUtil mDialog;

    /**
     * 用于上滑后显示悬停的学生列表
     */
    @Bind(R.id.rcl_floating_stu_list)
    RecyclerView mFloatingStuList;

    @Bind(R.id.ll_floating_stu_list)
    LinearLayout mFloatingStuListLayout;

    /**
     * 学生列表数据适配器
     */
    private ListAdapter mAdapter;
    /**
     * 是否处于悬停状态
     */
    private boolean isFloating;
    private LinearLayout mStuListContainer;//学生列表布局

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRvStuHead.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTitle.setText(getIntent().getStringExtra(EXTRA_TITLE));
        mEtScore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                judgeScore();
            }
        });
        //提交学生答案
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (judgeScore()) {
                    if (mDialog == null)
                        mDialog = new DialogUtil(TeacherDoReadByTopicActivity.this);
                    mDialog.showDialog();
                    sendStuAnswer();
                } else {
                    ToastUtil.showToast(TeacherDoReadByTopicActivity.this, getResources().getString(R.string.exam_no_score_tip));
                }
            }
        });
        mAdapter = new ListAdapter(new ArrayList<StudentBean>(), this);
        mAdapter.setOnInViewClickListener(R.id.layout_user_icon, new MMBaseRecyclerViewAdapter.onInternalClickListener<StudentBean>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, StudentBean values) {
                mAdapter.setPosition(position);
                getStuAnswer(position, values);
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, StudentBean values) {

            }
        });
        mRvStuHead.setItemAnimator(new DefaultItemAnimator());
        mRvStuHead.setAdapter(mAdapter);
        Intent intent = new Intent(this, WeiBoMediaService.class);
        this.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mStuListContainer = (LinearLayout) findViewById(R.id.container_stu_list);
        mStuAnswerInfoSfsv = (SlidingFloatScrollView) findViewById(R.id.sfsv_stu_answer_info);
        mStuAnswerInfoSfsv.setOnScrollListener(this);
    }

    private void sendStuAnswer() {
        if (mStuAnswerBean == null) {
            mDialog.cancel();
            ToastUtil.showToast(TeacherDoReadByTopicActivity.this, "请先获取学生答案!");
            return;
        }
        if (TextUtils.isEmpty(mEtScore.getText())) {
            mDialog.cancel();
            ToastUtil.showToast(TeacherDoReadByTopicActivity.this, "请先进行打分!");
            return;
        }
        RequestSender sender = new RequestSender(this);
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        map.put("examQuestionResultId", mStuAnswerBean.getExamQuestionResultId());
        map.put("score", mEtScore.getText().toString());
        map.put("teacherComment", TextUtils.isEmpty(mEtComment.getText()) ? "" : StringUtils.replaceHtml2(mEtComment.getText().toString()));
        map.put("result", mEtScore.getText().toString().equals(mQuestionInfoMap.get(mLastPosition).getQuestionScores()) ? "1" : "0");
        map.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        map.put("studentId", mStudentBeanMap.get(mLastPosition).get(mAdapter.getPosition()).getBaseUserId());
        map.put("classId", getIntent().getStringExtra("classId"));
        map.put("questionId", mStuAnswerBean.getExamQuestionId());
        map.put("classLevelId", getIntent().getStringExtra("classlevelId"));
        sender.sendRequest(new RequestSender.RequestData(URLConfig.SAVE_READ_BY_QUESTION_COMMENT, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mDialog.cancel();
                if (response != null) {
                    if ("success".equals(response.optString("result")) && mToolbar != null) {
                        mTvWaitToReadBy.setText(getSpannableStringBuilder(response.optInt("questionCount"), "题待批阅"));
                        mTvPeopleWait.setText(getSpannableStringBuilder(response.optInt("studentCount"), "人待批阅"));
                        if (response.optInt("studentCount") == 0) {
                            mLlScore.setVisibility(View.GONE);
                            mEtScore.setVisibility(View.GONE);
                            mTvMaxScore.setVisibility(View.GONE);
                            mTvComment.setVisibility(View.GONE);
                            mEtComment.setVisibility(View.GONE);
                            mRlSubmit.setVisibility(View.GONE);
                            mSubmit.setVisibility(View.GONE);
                        }
                        mWvStuAnswer.loadData(" ", "text/html; charset=UTF-8", null);//这种写法可以正确解码
                        mEtScore.setText(null);
                        mEtComment.setText(null);
                        (mStudentBeanMap.get(mLastPosition)).remove(mAdapter.getPosition());
                        mAdapter.remove(mAdapter.getPosition());
                        if (mStudentBeanMap.get(mLastPosition).size() == 0) {
                            if (mLastPosition + 1 <= mData.size())
                                mPager.setCurrentItem(mLastPosition + 1);
                        }
                        if (mQuestionInfoMap.size() == 0) {
                            EventBus.getDefault().post(StudentReadActivity.class.getSimpleName());
                            finish();
                        }
                    } else {
                        ToastUtil.showToast(TeacherDoReadByTopicActivity.this, "提交失败!");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.cancel();
                ToastUtil.showToast(TeacherDoReadByTopicActivity.this, "提交失败!");
            }
        }));
    }

    private void getStuAnswer(Integer position, StudentBean values) {
        RequestSender sender = new RequestSender(this);
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        map.put("classId", getIntent().getStringExtra("classId"));
        map.put("questionId", mQuestionInfoMap.get(mLastPosition).getQuestionId());
        map.put("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
        map.put("studentId", values.getBaseUserId());
        sender.sendRequest(new RequestSender.RequestData(URLConfig.GET_QUESTION_ANSWER, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseDataToView(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    private StuAnswerBean mStuAnswerBean;

    @OnClick(R.id.iv_controller)
    public void onVideoViewClick(View view) {
        if (mStuAnswerBean.getAnswerVideo() != null) {
            stopPlayingAudio();
            VideoViewActivity.startActivity(this,
                    mStuAnswerBean.getAnswerVideo().toString(), WorkItemDetailFragment.FROM_NET, "");
        }
    }

    private void stopPlayingAudio() {
        if (mMediaBinder != null) {
            mMediaBinder.stopTask();
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    private void parseDataToView(JSONObject response) {
        if (!response.isNull("question") && mToolbar != null) {
            try {
                JSONObject object = response.getJSONObject("question");
                mStuAnswerBean = new Gson().fromJson(object.toString(), StuAnswerBean.class);
                //mWvStuAnswer.loadData(mStuAnswerBean.getAnswer(), "text/html; charset=UTF-8", null);//这种写法可以正确解码
                setContentToWebView(mWvStuAnswer, mStuAnswerBean.getAnswer());
                addExamMedia(mStuAnswerBean.getAnswerVideo() == null ? "" : mStuAnswerBean.getAnswerVideo().toString());
                if (!mStuAnswerBean.getResult().equals("")) {//Integer.valueOf(mStuAnswerBean.getResult() == null ? "1" : mStuAnswerBean.getResult().toString()) == 0
                    mLlScore.setVisibility(View.GONE);
                    mEtScore.setVisibility(View.GONE);
                    mTvMaxScore.setVisibility(View.GONE);
                    mTvComment.setVisibility(View.GONE);
                    mEtComment.setVisibility(View.GONE);
                    mRlSubmit.setVisibility(View.GONE);
                    mSubmit.setVisibility(View.GONE);
                } else {
                    mLlScore.setVisibility(View.VISIBLE);
                    mEtScore.setVisibility(View.VISIBLE);
                    mTvMaxScore.setVisibility(View.VISIBLE);
                    mTvComment.setVisibility(View.VISIBLE);
                    mEtComment.setVisibility(View.VISIBLE);
                    mRlSubmit.setVisibility(View.VISIBLE);
                    mSubmit.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 视频地址包含字段
     */
    public static final String TYPE_VIDEO_STRING = "viewVideo";
    /**
     * 音频地址包含字段
     */
    public static final String TYPE_AUDIO_STRING = "viewAudio";
    private MediaMetadataRetrieverTask mMediaRetrieverTask;

    private void addExamMedia(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(TYPE_VIDEO_STRING)) {//加载视频
                mFlVideoView.setVisibility(View.VISIBLE);
                mLayoutAudioAnswer.setVisibility(View.GONE);
                mMediaRetrieverTask = new MediaMetadataRetrieverTask(sdVideoTaskView, ivController, ScreenUtils.getScreenWidth(this), UIUtils.dip2px(this, 230f));
                mMediaRetrieverTask.execute(url);
            } else if (url.contains(TYPE_AUDIO_STRING)) {//加载音频
                mFlVideoView.setVisibility(View.GONE);
                mLayoutAudioAnswer.setVisibility(View.VISIBLE);
                //setAudio(R.id.iv_audio_bar_answer, R.id.container_audio_answer, R.id.tv_audio_time_answer,url);
                setAudio(url);
            }
        } else {
            mFlVideoView.setVisibility(View.GONE);
            mLayoutAudioAnswer.setVisibility(View.GONE);
        }
    }

    /**
     * 显示学生列表浮层
     */
    private void showFloatingStuList() {
        if (!isFloating) {
            initFloatingRecyclerStuList();
            mFloatingStuListLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏学生列表浮层
     */
    protected void hideFloatingStuList() {
        if (isFloating) {
            initRecyclerStuList();
            mFloatingStuListLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 初始化学生列表浮层
     */
    private void initFloatingRecyclerStuList() {
        mFloatingStuList.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setHeaderInvisible(false);
        mFloatingStuList.setAdapter(mAdapter);
        mFloatingStuList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * 初始化学生列表
     */
    private void initRecyclerStuList() {
        mRvStuHead.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setHeaderInvisible(true);
        mRvStuHead.setAdapter(mAdapter);
        mRvStuHead.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onScroll(int t) {
        if (t >= mPager.getHeight() + mStuListContainer.getHeight()) {
            showFloatingStuList();
            isFloating = true;
        } else {
            hideFloatingStuList();
            isFloating = false;
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

        public MediaMetadataRetrieverTask(SimpleDraweeView simpleDraweeView, ImageView ivController, int screenWidth, int height) {
            super();
            this.imageView = ivController;
            this.simpleDraweeView = simpleDraweeView;
            this.screenWidth = screenWidth;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            MediaCheck check = new MediaCheck();
            Bitmap bitmap = null;
            if (check.isUsable(params[0])) {
                Drawable drawable = getResources().getDrawable(R.drawable.video_can_play);
                BitmapDrawable bd = (BitmapDrawable) drawable;
                bitmap = bd.getBitmap();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) return;
            if (bitmap == null) {
                Drawable drawable = getResources().getDrawable(R.drawable.video_not_play);
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
    protected void onPause() {
        super.onPause();
        if (mMediaBinder != null) {
            mMediaBinder.stop();
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    private WeiBoMediaService.MediaBinder mMediaBinder;
    private WeiBoMediaService mWeiBoMediaService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (WeiBoMediaService.MediaBinder) service;
            mWeiBoMediaService = ((WeiBoMediaService.MediaBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mWeiBoMediaService = null;
        }
    };

    //设置音频
    private AnimationDrawable animationDrawable;//音频播放条动画

    private void setAudio(int barResId, int autoItemResId, final int timeResId, final String playUrl) {
        ImageView audioBar = (ImageView) findViewById(barResId);
        animationDrawable = (AnimationDrawable) audioBar.getBackground();
        LinearLayout audioItem = (LinearLayout) findViewById(autoItemResId);
        final TextView timeTv = (TextView) findViewById(timeResId);
        audioItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaBinder != null) {
                    mMediaBinder.playTask(playUrl + "?phoneType=android", animationDrawable, timeTv);
                }
            }
        });
    }

    private void setAudio(String playUrl) {
        AudioBar audioBar = new AudioBar(this);
        audioBar.setTag("itemServiceAudioView");
        audioBar.setUrl(playUrl, "uploadUrl", false);
        audioBar.setProgressVisible(false);
        mLayoutAudioAnswer.addView(audioBar);
    }

    /**
     * 判断得分
     */
    private boolean judgeScore() {
        if (TextUtils.isEmpty(mEtScore.getText())) return false;
        if (Integer.parseInt(mEtScore.getText().toString()) > Integer.parseInt(mQuestionInfoMap.get(mLastPosition).getQuestionScores())) {
            ToastUtil.showToast(this, getString(R.string.exam_do_read_max_score, mQuestionInfoMap.get(mLastPosition).getQuestionScores()));
            return false;
        }
        return true;
    }


    @Override
    protected void onViewBound() {
        super.onViewBound();
        mListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String str = position + 1 + "/" + mData.size();
                mTvTaskTitle.setText(str);
                if (mLastPosition != position) {
                    mData.get(mLastPosition).setColor(0);
                }
                mLastPosition = position;
                mData.get(mLastPosition).setColor(Color.parseColor("#ff69be40"));
                bindDataToAdapter(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        setViewAnim(false, mTitle);
        setCustomTitle(getIntent().getStringExtra(EXTRA_TITLE));
        mPager.addOnPageChangeListener(mListener);

    }

    private void bindDataToAdapter(int position) {
        mAdapter.setPosition(0);
        mStuAnswerBean = null;
        if (mAnswerLinearLayout.getChildCount() > 1)
            mAnswerLinearLayout.removeViewAt(1);
        mWvStuAnswer = new WebView(this);
        mWvStuAnswer.setBackgroundColor(0); // 设置背景色
        setContentToWebView(mWvStuAnswer, "");
        mAnswerLinearLayout.addView(mWvStuAnswer);
        mAdapter.setList(mStudentBeanMap.get(position));
        mTvMaxScore.setHint(getString(R.string.exam_do_read_max_score, mQuestionInfoMap.get(position).getQuestionScores()));
        mTvPeopleWait.setText(getSpannableStringBuilder(mStudentBeanMap.get(position).size(), "人待批阅"));
        if (mStudentBeanMap.get(position).size() > 0)
            getStuAnswer(position, mStudentBeanMap.get(position).get(0));
    }

    @Override
    protected void onDestroy() {
        if (mListener != null) {
            mPager.removeOnPageChangeListener(mListener);
        }
        this.unbindService(mServiceConnection);
        if (mMediaRetrieverTask != null) {
            mMediaRetrieverTask.cancel(true);
        }
        super.onDestroy();
    }

    public static void start(Activity activity, String title, String examTaskId, String classId, String classlevelId) {
        Intent intent = new Intent(activity, TeacherDoReadByTopicActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_EXAM_TASK_ID, examTaskId);
        intent.putExtra("classId", classId);
        intent.putExtra("classlevelId", classlevelId);
        activity.startActivity(intent);
        UIUtils.addEnterAnim(activity);
    }


    @Override
    protected void addParams(Map<String, String> params) {
        addParam("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        addParam("classId", getIntent().getStringExtra("classId"));
        addParam("classlevelId", getIntent().getStringExtra("classlevelId"));
        addParam("examTaskId", getIntent().getStringExtra(EXTRA_EXAM_TASK_ID));
    }

    private Map<Integer, QuestionInfo> mQuestionInfoMap;
    private Map<Integer, List<StudentBean>> mStudentBeanMap;

    @Override
    protected void addFragments(JSONObject response) {
        try {
            if ("error".equals(response.optString("result"))) {
                finish();
                ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
                return;
            }
            JSONArray jsonArray = response.getJSONArray("questionList");
            if (jsonArray.length() == 0 || response.getJSONArray("studentList").length() == 0) {
                mTvTaskTitle.setVisibility(View.INVISIBLE);//当没有数据时隐藏
                ToastUtil.showToast(getString(R.string.exam_no_subjective));
                finish();
                return;
            } else {
                mStuAnswerInfoSfsv.setVisibility(View.VISIBLE);//当没有数据时隐藏
                mTvTaskTitle.setVisibility(View.VISIBLE);
            }
            mStudentBeanMap = new HashMap<>();
            mQuestionInfoMap = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                //item筛选
                ItemInfoClass itemInfoClass = new ItemInfoClass();
                itemInfoClass.setWorkItemIndex(i + 1);
                itemInfoClass.setWorkItemId(o.isNull("examQuestionId") ? "" : o.getString("examQuestionId"));
                if (i == 0) {
                    itemInfoClass.setColor(Color.parseColor("#ff69be40"));
                }
                itemInfoClass.setWorkItemType(o.isNull("type") ? "" : o.getString("type"));
                mData.add(itemInfoClass);
                String str = 1 + "/" + mData.size();
                mTvTaskTitle.setText(str);
                QuestionInfo questionInfo = getQuestionInfo(o, i + 1);
                mQuestionInfoMap.put(i, questionInfo);
                List<StudentBean> listStuBeans = new ArrayList<>();
                JSONArray stuArray = response.getJSONArray("studentList");
                for (int j = 0; j < stuArray.length(); j++) {
                    JSONObject oo = stuArray.getJSONObject(j);
                    StudentBean studentBean = getmStudentBean(oo);
                    listStuBeans.add(studentBean);
                }
                mStudentBeanMap.put(i, listStuBeans);
                Bundle bundle = new Bundle();
                bundle.putParcelable(TaskFragment.ARG_TASK_DATA, questionInfo);
                bundle.putString(TaskFragment.ARG_TASK_TYPE, TaskFragment.TYPE_EXAM);
                bundle.putString(TaskFragment.ARG_TASK_STATUS, TaskFragment.STATUS_DO_READ_BY);
                addFragment(i + "", TaskFragment.class, bundle);
            }
            bindDataToAdapter(0);
            mTvWaitToReadBy.setText(getSpannableStringBuilder(mData.size(), "题待批阅"));
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            ToastUtil.showToast(getApplicationContext(), getString(R.string.refresh_state_loade_error));
        }
    }

    @NonNull
    private SpannableStringBuilder getSpannableStringBuilder(int count, String suffix) {
        //设置字体颜色
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(String.valueOf(count));
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff69be40")), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置背景色为青色
        builder.append(suffix);
        return builder;
    }

    private StudentBean getmStudentBean(JSONObject o) {
        StudentBean bean = null;
        try {
            bean = new StudentBean();
            bean.setBaseUserId(o.isNull("baseUserId") ? "" : o.getString("baseUserId"));
            bean.setUsername(o.isNull("username") ? "" : o.getString("username"));
            bean.setRealName(o.isNull("realName") ? "" : o.getString("realName"));
            bean.setHeadPic(o.isNull("headPic") ? "" : o.getString("headPic"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    private QuestionInfo getQuestionInfo(JSONObject o, int i) {
        QuestionInfo info = null;
        try {
            info = new QuestionInfo();
            info.setQuestionId(o.isNull("examQuestionId") ? "" : o.getString("examQuestionId"));
            info.setQuestionType(o.isNull("type") ? "" : o.getString("type"));
            info.setQuestionContent((o.isNull("content") ? "" : o.getString("content")));
            info.setQuestionMediaUrl(o.isNull("contentVideo") ? "" : o.getString("contentVideo"));
            info.setQuestionScores(o.isNull("score") ? "0" : String.valueOf(o.optInt("score")));
            info.setQuestionResolveVideo(o.isNull("resolveVideo") ? "" : o.getString("resolveVideo"));
            info.setQuestionCorrectAnswer(o.isNull("resolve") ? "" : o.getString("resolve"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    private TextView mTvTaskTitle;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        mTvTaskTitle = (TextView) linearLayout.findViewById(R.id.task_title);
        mTvTaskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.size(); i++) {
                    for (int j = 0; j < mStudentBeanMap.size(); j++) {
                        if (mStudentBeanMap.get(j).size() == 0 && mLastPosition != i) {
                            mData.get(i).setColor(Color.parseColor("#FFE86153"));
                        }
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(SwitchTopicDialog.ARG_KEY, mData);
                if (mData == null || mData.size() == 0) return;
                final SwitchTopicDialog dialog = new SwitchTopicDialog();
                dialog.setArguments(bundle);
                dialog.setOnItemClickListener(new SwitchTopicDialog.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        dialog.dismiss();
                        bindDataToAdapter(position - 1);
                        if (mLastPosition != position - 1) {
                            mData.get(mLastPosition).setColor(0);
                        }
                        mLastPosition = position - 1;
                        mData.get(mLastPosition).setColor(Color.parseColor("#ff69be40"));
                        mPager.setCurrentItem(position - 1, true);
                    }
                });
                dialog.show(getSupportFragmentManager(), SwitchTopicDialog.TAG);

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    @Override
    protected String getUrl() {
        return URLConfig.GET_READ_BY_QUESTIONPRE;
    }

    /**
     * 图片点击放大浏览的图片地址集合，从富文本中获取得来
     */
    private List<String> mImageList;

    private void setContentToWebView(WebView webView, String html) {
        mImageList = HtmlUtils.filterImages(html);
        if (mImageList != null && mImageList.size() > 0) {
            webView.addJavascriptInterface(new JsInteraction(mImageList), "control");
            webView.getSettings().setJavaScriptEnabled(true);
            WebViewUtils.setContentToWebView(webView, HtmlUtils.constructExecActionJs(html));
        } else {
            WebViewUtils.setContentToWebView(webView, html);
        }
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
            SubjectMaterialPicturesActivity.start(TeacherDoReadByTopicActivity.this, subjectMaterialPictures, position);
        }
    }

    private int mLastPosition = 0;

    class ListAdapter extends MMBaseRecyclerViewAdapter<StudentBean> {
        private int mPosition = 0;
        private boolean isShowHeader = true;

        public ListAdapter(List<StudentBean> list, Context context) {
            super(list, context);
        }

        public void setPosition(int position) {
            this.mPosition = position;
            notifyDataSetChanged();
        }

        public void setHeaderInvisible(boolean isShowHeader) {
            this.isShowHeader = isShowHeader;
            notifyDataSetChanged();
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            final View view = LayoutInflater.from(context).inflate(R.layout.item_student_list_work_read
                    , parent, false);
            return new NormalRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (mPosition == position)
                ((NormalRecyclerViewHolder) holder).tvName.setTextColor(getResources().getColor(R.color.main_color));
            else
                ((NormalRecyclerViewHolder) holder).tvName.setTextColor(getResources().getColor(R.color.exam_normal_color));
            ((NormalRecyclerViewHolder) holder).tvName.setText(list.get(position).getRealName());
            ((NormalRecyclerViewHolder) holder).simpleDraweeView.setImageURI(Uri.parse(list.get(position).getHeadPic()));
        }

        public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            SimpleDraweeView simpleDraweeView;

            public NormalRecyclerViewHolder(View view) {
                super(view);
                tvName = (TextView) view.findViewById(R.id.tv_student_name);
                simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.useritem_icon);
                if (isShowHeader) {
                    simpleDraweeView.setVisibility(View.VISIBLE);
                } else {
                    simpleDraweeView.setVisibility(View.GONE);
                }
            }
        }
    }

    class StudentBean {
        private String username;
        private String realName;
        private String baseUserId;
        private String headPic;

        public String getBaseUserId() {
            return baseUserId;
        }

        public void setBaseUserId(String baseUserId) {
            this.baseUserId = baseUserId;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
