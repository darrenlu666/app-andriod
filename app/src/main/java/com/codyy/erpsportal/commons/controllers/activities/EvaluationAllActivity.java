package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.CommentAdapter;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.Comment;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 评课议课所有评论
 * Created by kmdai on 2015/4/30.
 */
public class EvaluationAllActivity extends Activity implements Handler.Callback {

    /**
     * 获取评论
     */
    private final static int GET_COMMENT = 0x001;
    /**
     * 网络获取数据错误
     */
    private final static int HTTPCONNECT_ERROE = 0x002;
    private PullToRefreshListView mPullToRefreshListView;
    private ArrayList<Comment> comments;
    private CommentAdapter mCommentAdapter;
    private AssessmentDetails assessmentDetails;
    private UserInfo userInfo;
    /**
     * 网络请求
     */
    private RequestSender mSender;
    private Handler mHandler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_all_layout);
        assessmentDetails = getIntent().getParcelableExtra("assessmentDetails");
        userInfo = getIntent().getParcelableExtra("userInfo");
        comments = getIntent().getParcelableArrayListExtra("mComments");
        init();
        mCommentAdapter = new CommentAdapter(this, comments, assessmentDetails.getScoreType(), userInfo.getBaseUserId(), assessmentDetails.isScoreVisible(), assessmentDetails.getStatus());
        mPullToRefreshListView.getRefreshableView().setAdapter(mCommentAdapter);
        if (comments.size() < end) {
            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
    }

    private void init() {
        mHandler = new Handler(this);
        mSender = new RequestSender(this);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_pull);
        mPullToRefreshListView.setOnRefreshListener(refreshListener2);
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
            if (comments.size() >= end) {
                start = comments.size();
                end = start + cont;
                getComment();
            }
        }
    };

    /**
     * 获取评论
     */
    private void getComment() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        data.put("start", String.valueOf(start));
        data.put("end", String.valueOf(end));
        httpConnect(URLConfig.GET_COMMENT, data, GET_COMMENT);
    }

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
                mHandler.sendMessage(mHandler.obtainMessage(msg, 0, 0, response));
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendMessage(mHandler.obtainMessage(HTTPCONNECT_ERROE, 0, 0));
            }
        }));
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
        }
        if (start == 0) {
            comments.clear();
        }
        Comment.getComment((JSONObject) msg.obj, comments);
        mCommentAdapter.notifyDataSetChanged();
        if (comments.size() < end) {
            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
        if (comments.size() == 0) {
            ToastUtil.showToast(this, "暂无评论");
        }
        return false;
    }

    public void onBackClick(View view) {
        this.finish();
        overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mSender.stop();
        super.onDestroy();
    }
}
