package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.CommentAdapter;
import com.codyy.erpsportal.commons.utils.Check3GUtil;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.utils.WiFiBroadCastUtils;
import com.codyy.erpsportal.commons.widgets.BNVideoControlView;
import com.codyy.erpsportal.commons.widgets.BnVideoView2;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.Comment;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.entities.evaluation.EvaluationVideo;
import com.codyy.erpsportal.commons.models.entities.evaluation.EvaluationVideoParse;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.resource.models.entities.ResourceDetails;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 进入评课议课
 * Created by kmdai on 2015/4/21.
 */
public class EvaluationActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int RATE_TRUE = 0x001;
    /**
     * 获取评论
     */
    private final static int GET_COMMENT = 0x001;
    /**
     * 网络获取数据错误
     */
    private final static int HTTPCONNECT_ERROE = 0x002;
    /**
     * 发表评论成功
     */
    private final static int SEND_MSG_SUCESS = 0x003;
    /**
     * 星星打分成功
     */
    private final static int UPDATE_START_SUCESS = 0x004;

    private final static int GET_ASSESSMENT_DETAIL = 0x005;
    /**
     * 获取直播地址
     */
    private final static int GET_LIVE_URL = 0x006;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private Handler mHandler;
    private AssessmentDetails mAssessmentDetails;
    private UserInfo userInfo;
    private ArrayList<Comment> mComments;
    private CommentAdapter mCommentAdapter;

    private LinearLayout mLinearLayout;
    /**
     * 评论开始页
     */
    private int start = 0;
    /**
     * 每次加载的页数
     */
    private int cont = 10;
    /**
     * 结束位置
     */
    private int end = cont;

    /**
     * 星星评分 个人打分
     */
    private RatingBar mRatingBar;
    /**
     * 星星评分 全部
     */
    private RatingBar mRatingBarAll;

    /**
     * 打分按钮
     */
    private Button mGradeBtn;

    /**
     * 评论按钮
     */
    private TextView mCommentBtn;

    private Dialog mCommentDialog;

    private EditText mCommentText;

    private InputMethodManager mInputMethodManager;
    private RelativeLayout mRelativeLayoutAll;
    /**
     * 打分页面
     */
    private RelativeLayout mRelativeLayout;
    private DialogUtil mDialogUtil3G;

    /**
     * 进度控制
     */
    private BNVideoControlView mVideoControlView;

    /**
     * 顶部栏
     */
    private RelativeLayout mTitleLayout;

//    private Button mVideoListBtn;

    private ListView mVideoListView;

    private BnVideoView2 mBnVideoView;
//    private BNPlayerFactory mBnPlayerFactory;

    private ImageView mFullScreenBtn;

    private int type;

    //增加网络监测
    private WiFiBroadCastUtils mWiFiBroadCastUtils;

    private boolean isRate = false;

    /**
     * lsit头部view
     */
    private View mHeadView;
    private TextView mNoAssessment;
    private TextView mTextNoVideo;

    //3G提示框
    private DialogUtil mDialogUtil;
    private RecyclerView mRecyclerView;
    private ResourceDetails mResourceDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_details);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mAssessmentDetails = getIntent().getParcelableExtra("assessmentDetails");
        userInfo = getIntent().getParcelableExtra(Constants.USER_INFO);
        type = getIntent().getIntExtra("type", 0);
        init();
        mResourceDetails = new ResourceDetails();
        if (userInfo != null) {
            getComment();
        }
    }

    private void init() {
        mComments = new ArrayList<>();
        mHandler = new Handler();
        mDialogUtil = new DialogUtil(this, left, right);
        mSender = new RequestSender(this);
        mCommentAdapter = new CommentAdapter(this, mComments, mAssessmentDetails.getScoreType(), userInfo.getBaseUserId(), mAssessmentDetails.isScoreVisible(), mAssessmentDetails.getStatus());
        mHeadView = LayoutInflater.from(this).inflate(R.layout.evaluation_lsit_heardview, null);
        mRecyclerView = (RecyclerView) mHeadView.findViewById(R.id.evaluation_list_heardview_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRelativeLayoutAll = (RelativeLayout) mHeadView.findViewById(R.id.all_rate_layout);
        mTextNoVideo = (TextView) findViewById(R.id.evaluation_textview_novideo);
        mNoAssessment = (TextView) findViewById(R.id.evaluation_layout_textview);
        mNoAssessment.setOnClickListener(this);
        mRelativeLayout = (RelativeLayout) mHeadView.findViewById(R.id.my_rate_layout);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.evaluation_layout_listview);
        mRatingBar = (RatingBar) mHeadView.findViewById(R.id.evaluation_list_ratingbar);
        mRatingBarAll = (RatingBar) mHeadView.findViewById(R.id.evaluation_list_all_ratingbar);
        mLinearLayout = (LinearLayout) mHeadView.findViewById(R.id.evaluation_list_heardview_mark_linear);
        mGradeBtn = (Button) mHeadView.findViewById(R.id.evaluation_list_mark);
        mCommentBtn = (TextView) mHeadView.findViewById(R.id.evaluation_list_item_comment);
        mCommentBtn.setOnClickListener(this);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.addHeaderView(mHeadView);
        mListView.setAdapter(mCommentAdapter);
        mPullToRefreshListView.setOnRefreshListener(refreshListener2);

        commentDialogInit();

        mFullScreenBtn = (ImageView) findViewById(R.id.evaluation_details_fullscreen);
        mFullScreenBtn.setOnClickListener(this);
        mBnVideoView = (BnVideoView2) findViewById(R.id.evaluation_details_bnVideoView);
        //开启声音
        mBnVideoView.setVolume(100);

        mVideoControlView = (BNVideoControlView) findViewById(R.id.evaluation_widgets_VideoControlView);
        mTitleLayout = (RelativeLayout) findViewById(R.id.evaluation_details_title);


        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    mDialogUtil.showDialog("已选择" + mRatingBar.getRating() + "颗星");
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    Intent intent = new Intent(EvaluationActivity.this, EvaluationAllActivity.class);
                    intent.putParcelableArrayListExtra("mComments", mComments);
                    intent.putExtra("userInfo", userInfo);
                    intent.putExtra("assessmentDetails", mAssessmentDetails);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
                }
            }
        });
        findViewById(R.id.evaluation_title_btn_down).setOnClickListener(this);
        headViewSet(mHeadView);
    }


    /**
     * 评论弹框初始化
     */
    private void commentDialogInit() {
        View popu = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        mCommentText = (EditText) popu.findViewById(R.id.input_layout_editext);
        mCommentText.addTextChangedListener(mCommentTextWatcher);
        popu.findViewById(R.id.input_layout_btn).setOnClickListener(this);
        mCommentDialog = new Dialog(this, R.style.input_dialog);
        mCommentDialog.setContentView(popu);
        Window window = mCommentDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setGravity(Gravity.BOTTOM);
    }

    /**
     * 播放录播视频
     */
    private void playRecoidVideo() {
        mFullScreenBtn.setVisibility(View.GONE);
        if (mAssessmentDetails.getVideoIds().size() > 0) {
            videoIndex = 0;
            mVideoControlView.setVisibility(View.GONE);
            mVideoControlView.bindVideoView(mBnVideoView, getSupportFragmentManager());
            mVideoControlView.setVideoPath(mAssessmentDetails.getVideoIds().get(videoIndex).getFilePath(), BnVideoView2.BN_URL_TYPE_RTMP_HISTORY, false);
            mVideoControlView.setDisplayListener(new BNVideoControlView.DisplayListener() {
                @Override
                public void show() {
                    mTitleLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void hide() {
                    if (mVideoControlView.isPlaying()) {
                        mTitleLayout.setVisibility(View.GONE);
                    }
                }
            });
            mVideoControlView.setOnCompleteListener(new BnVideoView2.OnBNCompleteListener() {
                @Override
                public void onComplete() {
                    if (++videoIndex < mAssessmentDetails.getVideoIds().size()) {
                        mVideoControlView.post(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.postDelayed(mRunnable, 3000);
                                mVideosAdapter.notifyDataSetChanged();
                                mVideoControlView.setVideoPath(mAssessmentDetails.getVideoIds().get(videoIndex).getFilePath(), BnVideoView2.BN_URL_TYPE_HTTP, false);
                            }
                        });
                    } else {
                        --videoIndex;
                    }
                }
            });
        } else {
            ToastUtil.showToast(this, "很抱歉，视频资源未上传。", Toast.LENGTH_LONG);
            mTextNoVideo.setVisibility(View.VISIBLE);
            mVideoControlView.setVisibility(View.GONE);
            mTextNoVideo.setClickable(false);
        }
    }

    /**
     * 播放直播
     */
    private void playDirectVideo() {
        mFullScreenBtn.setVisibility(View.VISIBLE);
        mBnVideoView.setOnClickListener(EvaluationActivity.this);

        mVideoControlView.setVisibility(View.GONE);
        Map<String, String> params = new HashMap<>();
        params.put("id", mAssessmentDetails.getScheduleDetailId());
        params.put("uuid", userInfo.getUuid());
        httpConnect(URLConfig.SPECIAL_DELIVERY_CLASSROOM_VIDEOS, params, GET_LIVE_URL);
        registerWiFiListener();
    }

    /**
     * @param view
     */
    private void headViewSet(View view) {
        mVideosAdapter = new DownLoadAdapter();
        mRecyclerView.setAdapter(mVideosAdapter);
        ((TextView) view.findViewById(R.id.evaluation_list_heardview_text_video_number)).setText("共" + String.valueOf(mAssessmentDetails.getVideoIds().size()) + "段>");
        TextView meRateText = (TextView) view.findViewById(R.id.evaluation_rate_me_text);
        TextView allRateText = (TextView) view.findViewById(R.id.evaluation_rate_all_text);

        if (mAssessmentDetails.getVideoIds().size() <= 1) {
            mRecyclerView.setVisibility(View.GONE);
            view.findViewById(R.id.evaluation_list_heardview_recycleview_line).setVisibility(View.GONE);
            view.findViewById(R.id.evaluation_title_btn_down).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.evaluation_list_heardview_recycleview_line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.evaluation_title_btn_down).setVisibility(View.GONE);
        }
        if (mAssessmentDetails.getVideoIds().size() == 0) {
            findViewById(R.id.evaluation_list_heardview_btn_down).setVisibility(View.GONE);
            view.findViewById(R.id.evaluation_title_btn_down).setVisibility(View.GONE);
        } else {
            findViewById(R.id.evaluation_list_heardview_btn_down).setOnClickListener(this);
            findViewById(R.id.evaluation_list_heardview_btn_down).setVisibility(View.VISIBLE);
        }
        if ("VIDEO".equals(mAssessmentDetails.getEvaType())) {//点播
            playRecoidVideo();
        } else if ("LIVE".equals(mAssessmentDetails.getEvaType())) {//直播
            if ("END".equals(mAssessmentDetails.getStatus())) {
                playRecoidVideo();
            } else {
                Check3GUtil.instance().CheckNetType(this, new Check3GUtil.OnWifiListener() {
                    @Override
                    public void onNetError() {

                    }

                    @Override
                    public void onContinue() {
                        playDirectVideo();
                    }
                });
            }
        }
        if (!"TEACHER".equals(userInfo.getUserType()) || "N".equals(mAssessmentDetails.getIsAttend())) {
            mCommentBtn.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.GONE);
            if ("END".equals(mAssessmentDetails.getStatus())) {
                mRelativeLayoutAll.setVisibility(View.VISIBLE);
                if ("star".equals(mAssessmentDetails.getScoreType())) {
                    mRatingBarAll.setVisibility(View.VISIBLE);
                    allRateText.setVisibility(View.GONE);
                    double a = Math.ceil(mAssessmentDetails.getAverageScore()) / 2;
                    mRatingBarAll.setRating((float) a);
                } else {
                    double a = mAssessmentDetails.getAverageScore();
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(1);
                    allRateText.setText(numberFormat.format(a < 0 ? 0 : a) + "分");
                    mRatingBarAll.setVisibility(View.GONE);
                    allRateText.setVisibility(View.VISIBLE);
                }
            } else {
                mRelativeLayoutAll.setVisibility(View.GONE);
            }
        } else {
            mCommentBtn.setVisibility(View.VISIBLE);
            mRelativeLayoutAll.setVisibility(View.VISIBLE);
            mRelativeLayout.setVisibility(View.VISIBLE);
            if ("END".equals(mAssessmentDetails.getStatus())) {
                if ("star".equals(mAssessmentDetails.getScoreType())) {
                    mLinearLayout.setVisibility(View.GONE);
                    mRatingBar.setIsIndicator(true);
                    allRateText.setVisibility(View.GONE);
                    double a = Math.ceil(mAssessmentDetails.getAverageScore()) / 2;
                    mRatingBarAll.setRating((float) a);
                    double b = Math.ceil(mAssessmentDetails.getMyScore()) / 2;
                    mRatingBar.setRating((float) b);
                } else {
                    mRatingBar.setVisibility(View.GONE);
                    mRatingBarAll.setVisibility(View.GONE);
                    int a = (int) mAssessmentDetails.getMyScore();
                    meRateText.setText((a < 0 ? 0 : a) + "分");
                    double b = mAssessmentDetails.getAverageScore();
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(1);
                    allRateText.setText(numberFormat.format(b < 0 ? 0 : b) + "分");
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mGradeBtn.setVisibility(View.GONE);
                    meRateText.setVisibility(View.VISIBLE);
                    allRateText.setVisibility(View.VISIBLE);
                }
            } else {
                if ((int) mAssessmentDetails.getMyScore() >= 0) {
                    meRateText.setVisibility(View.VISIBLE);
                    meRateText.setText((int) mAssessmentDetails.getMyScore() + "分");
                } else {
                    meRateText.setVisibility(View.GONE);
                }
                mRelativeLayoutAll.setVisibility(View.GONE);
                if ("star".equals(mAssessmentDetails.getScoreType())) {
                    mLinearLayout.setVisibility(View.GONE);
                    mRatingBar.setVisibility(View.VISIBLE);
                } else {
                    mGradeBtn.setVisibility(View.VISIBLE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mGradeBtn.setOnClickListener(this);
                    mRatingBar.setVisibility(View.GONE);
                }
                if (mAssessmentDetails.getMyScore() >= 0) {
                    double a = Math.ceil(mAssessmentDetails.getMyScore()) / 2;
                    mRatingBar.setRating((float) a);
                } else {
                    mRatingBar.setRating(0);
                }
            }
        }
        if (type == AssessmentClassActivity.MASTER) {
            mRelativeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 直播隐藏标题和全屏按钮
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * 隐藏全屏按钮
     */
    private void hide() {
        mFullScreenBtn.setVisibility(View.GONE);
        mTitleLayout.setVisibility(View.GONE);
    }

    /**
     * 显示全屏按钮
     */
    private void show() {
        mFullScreenBtn.setVisibility(View.VISIBLE);
        mTitleLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取评论
     */
    private void getComment() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("evaluationId", mAssessmentDetails.getEvaluationId());
        data.put("start", String.valueOf(start));
        data.put("end", String.valueOf(end));
        httpConnect(URLConfig.GET_COMMENT, data, GET_COMMENT);
    }

    private EvaluationVideo mMainClassroom = null;

    /**
     * 网络连接
     *
     * @param data
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {
        mSender.sendRequest(new RequestSender.RequestData(url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                switch (msg) {
                    case GET_COMMENT:
                        if (mPullToRefreshListView.isRefreshing()) {
                            mPullToRefreshListView.onRefreshComplete();
                        }
                        if (start == 0) {
                            mComments.clear();
                        }
                        Comment.getComment(response, mComments);
                        mCommentAdapter.notifyDataSetChanged();
                        if (mComments.size() < end) {
                            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        if (mComments.size() == 0) {
                            mNoAssessment.setVisibility(View.VISIBLE);
                        } else {
                            mNoAssessment.setVisibility(View.GONE);
                        }
                        break;
                    case HTTPCONNECT_ERROE:
                        if (mPullToRefreshListView.isRefreshing()) {
                            mPullToRefreshListView.onRefreshComplete();
                        }
                        break;
                    case SEND_MSG_SUCESS:
                        if ("success".equals(response.optString("result"))) {
                            ToastUtil.showToast(EvaluationActivity.this, "评论成功");
                            mPullToRefreshListView.setRefreshing();
                            mCommentText.setText("");
                        } else {
                            ToastUtil.showToast(EvaluationActivity.this, "评论失败");
                            mCommentDialog.show();
                        }
                        break;
                    case UPDATE_START_SUCESS:
                        if ("success".equals(response.optString("result"))) {
                            ToastUtil.showToast(EvaluationActivity.this, "打分成功");
                            isRate = true;
                            mPullToRefreshListView.setRefreshing();
                            mAssessmentDetails.setMyScore(mRatingBar.getRating());
                        } else {
                            ToastUtil.showToast(EvaluationActivity.this, "打分失败");
                            mCommentDialog.show();
                        }
                        break;
                    case GET_ASSESSMENT_DETAIL:
                        AssessmentDetails.getAssessmentDetail(response, mAssessmentDetails);
                        headViewSet(mHeadView);
                        break;
                    case GET_LIVE_URL:
                        if ("success".equals(response.optString("result"))) {
                            EvaluationVideoParse parse = new Gson().fromJson(response.toString(), EvaluationVideoParse.class);
                            if (null != parse) {
                                List<EvaluationVideo> videos = parse.getWatchPath();
                                if (null != videos && videos.size() > 0) {
                                    for (int i = 0; i < videos.size(); i++) {
                                        if ("main".equals(videos.get(i).getRoomType())) {
                                            mMainClassroom = videos.get(i);
                                            playVideo(mMainClassroom.getStreamAddress());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(EvaluationActivity.this, getResources().getString(R.string.net_error));
            }
        }, this.toString()));
    }

    private void playVideo(String playUrl) {
        Cog.d("onResponse", playUrl);
        mBnVideoView.setUrl(playUrl, BnVideoView2.BN_URL_TYPE_RTMP_LIVE);
        mBnVideoView.setTimeOut(20);
        mBnVideoView.play(BnVideoView2.BN_PLAY_DEFAULT);
        mHandler.postDelayed(mRunnable, 3000);
    }

    /**
     * 刷新监听
     */
    private PullToRefreshBase.OnRefreshListener2 refreshListener2 = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            start = 0;
            end = start + cont;
            getComment();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            if (mComments.size() >= end) {
                start = mComments.size();
                end = start + cont;
                getComment();
            }
        }
    };


    /**
     * 返回键
     *
     * @param view
     */
    public void onBackClick(View view) {
        exit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mBnVideoView.setLayoutParams(lparam);
        } else {
            RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(this, 180));
            mBnVideoView.setLayoutParams(lparam);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出
     */
    private void exit() {
        if (isRate) {
            setResult(AssessmentDetailsActivity.REQUEST_CODE_SET_TEACHER);
        }
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    private boolean flag = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.evaluation_list_mark:
                Intent intent = new Intent(EvaluationActivity.this, RateActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.putExtra("assessmentDetails", mAssessmentDetails);
                startActivityForResult(intent, RATE_TRUE);
                overridePendingTransition(R.anim.slidemenu_show, R.anim.layout_hide);
                break;
            case R.id.evaluation_list_item_comment:
                if ("END".equals(mAssessmentDetails.getStatus())) {
                    ToastUtil.showToast(this, "评课已结束，不能发表评论");
                } else {
                    mInputMethodManager.showSoftInput(mCommentText, InputMethodManager.SHOW_FORCED);
                    mCommentDialog.show();
                }
                break;
            case R.id.input_layout_btn:
                if (mCommentDialog.isShowing()) {
                    sendComment();
                    mCommentDialog.cancel();
                }
                break;
            case R.id.evaluation_title_btn_down:
            case R.id.evaluation_list_heardview_btn_down:
                if (mAssessmentDetails.getVideoIds().size() > 0) {
                    downLoadVideo(mAssessmentDetails.getVideoIds().get(videoIndex).getId(), videoIndex + 1);
                }
                break;
            case R.id.evaluation_details_fullscreen:
                if (!flag) {
                    flag = true;
                    UIUtils.setLandscape(this);
                } else {
                    flag = false;
                    UIUtils.setPortrait(this);
                }
                break;
            case R.id.evaluation_details_bnVideoView:
                show();
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 3000);
                break;
            case R.id.evaluation_layout_textview://没有评论时刷新
                mPullToRefreshListView.setRefreshing();
                break;
        }
    }

    private DownLoadAdapter mVideosAdapter;


    /**
     * 评论输入框监听
     */
    private TextWatcher mCommentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() >= 200) {
                ToastUtil.showToast(EvaluationActivity.this, "字数达到上限");
            }
        }
    };

    //注册Wi-Fi类
    private void registerWiFiListener() {

        mWiFiBroadCastUtils = new WiFiBroadCastUtils(this, getSupportFragmentManager(), new WiFiBroadCastUtils.PlayStateListener() {
            @Override
            public void play() {
                mBnVideoView.play(BnVideoView2.BN_PLAY_DEFAULT);
            }

            @Override
            public void stop() {
                mBnVideoView.stop();
            }
        });
    }

    private CacheDao rDao;

    /**
     * 下载
     */
    private void downLoadVideo(String resId, int a) {
        mResourceDetails.setId(resId);
        mResourceDetails.setResourceName(mAssessmentDetails.getTitle() + (videoIndex + 1));
        VideoDownloadUtils.downloadVideo(mResourceDetails, mAssessmentDetails.getVideoIds().get(videoIndex).getDownloadUrl(), userInfo.getBaseUserId());
    }

    private void sendComment() {
        String msg = mCommentText.getText().toString();
        if (!"".equals(msg)) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", userInfo.getUuid());
            data.put("evaluationId", mAssessmentDetails.getEvaluationId());
            data.put("commentContent", msg);
            httpConnect(URLConfig.SEND_COMMENT, data, SEND_MSG_SUCESS);
        }
    }

    /**
     * 评分取消监听
     */
    private View.OnClickListener left = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDialogUtil.cancle();
            double a = Math.ceil(mAssessmentDetails.getMyScore()) / 2;
            mRatingBar.setRating((float) a);
        }
    };
    /**
     * 评分确定监听
     */
    private View.OnClickListener right = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Map<String, String> data = new HashMap<>();
            data.put("uuid", userInfo.getUuid());
            data.put("evaluationId", mAssessmentDetails.getEvaluationId());
            data.put("star", String.valueOf(mRatingBar.getRating() * 2));
            httpConnect(URLConfig.UPDATE_START, data, UPDATE_START_SUCESS);
            mDialogUtil.cancle();
        }
    };
    private int videoIndex = 0;

    /**
     * 视频列表list adapter
     */
    class videosAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAssessmentDetails.getVideoIds().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(EvaluationActivity.this).inflate(R.layout.evaluation_video_list_item, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.evaluation_video_item_iamge);
            TextView textView = (TextView) convertView.findViewById(R.id.evaluation_video_item_text);
            Button button = (Button) convertView.findViewById(R.id.evaluation_video_item_btn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    downLoadVideo(mAssessmentDetails.getVideoIds()[position]);
                }
            });
            textView.setText("视频文件 " + (position + 1));
            if (videoIndex == position) {
                imageView.setVisibility(View.VISIBLE);
                TextPaint tp = textView.getPaint();
                tp.setFakeBoldText(true);
            } else {
                imageView.setVisibility(View.GONE);
                TextPaint tp = textView.getPaint();
                tp.setFakeBoldText(false);
            }
            return convertView;
        }
    }

    class DownLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DownLoadHolder(getLayoutInflater().inflate(R.layout.horizontal_list_item_video_view, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            DownLoadHolder holder1 = (DownLoadHolder) holder;
            if (position == videoIndex) {
                holder1.mTitle.setTextColor(getResources().getColor(R.color.video_text_selected));
                holder1.mVideoCountView.setBackgroundResource(R.drawable.video_count_bg_selected);
            } else {
                holder1.mTitle.setTextColor(getResources().getColor(R.color.video_text_unselected));
                holder1.mVideoCountView.setBackgroundResource(R.drawable.video_count_bg_unselected);
            }
            holder1.mTitle.setText(String.valueOf(position + 1));
            holder1.mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.postDelayed(mRunnable, 3000);
                    videoIndex = position;
                    notifyDataSetChanged();
                    mBnVideoView.stop();
//                    String url=URLConfig.GET_EVAVIDEO_BYVIDEO_BYID + "?uuid=" + userInfo.getUuid() + "&videoId=" + mAssessmentDetails.getVideoIds()[videoIndex];
//                    mVideoControlView.setVideoPath(URLConfig.GET_EVAVIDEO_BYVIDEO_BYID + "?uuid=" + userInfo.getUuid() + "&videoId=" + mAssessmentDetails.getVideoIds()[videoIndex]);
                    mVideoControlView.setVideoPath(mAssessmentDetails.getVideoIds().get(position).getFilePath(), BnVideoView2.BN_URL_TYPE_RTMP_HISTORY, false);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mAssessmentDetails.getVideoIds().size();
        }
    }

    class DownLoadHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_video_count)
        TextView mTitle;
        @Bind(R.id.ll_video_count_view)
        RelativeLayout mVideoCountView;

        public DownLoadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RATE_TRUE) {
            isRate = true;
            Map<String, String> data = new HashMap<>();
            data.put("uuid", userInfo.getUuid());
            if (type == AssessmentClassActivity.INVITED) {
                data.put("isInvited", "Y");
            }
            data.put("evaluationId", mAssessmentDetails.getEvaluationId());
            httpConnect(URLConfig.GET_EVALUATIONDETAIL, data, GET_ASSESSMENT_DETAIL);
        }
    }

    @Override
    protected void onDestroy() {

        //解除广播接收器
        if (null != mWiFiBroadCastUtils) {
            mWiFiBroadCastUtils.destroy();
        }
        mSender.stop(this.toString());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (mBnVideoView != null && !mBnVideoView.isPlaying()) {
                    if (!mBnVideoView.isUrlEmpty()) {
                        mBnVideoView.playNow();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBnVideoView != null && mBnVideoView.isPlaying()) {
            mBnVideoView.stop();
        }
    }

}
