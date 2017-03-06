package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.StandardAdapter;
import com.codyy.erpsportal.commons.models.entities.AssessmentDetails;
import com.codyy.erpsportal.commons.models.entities.StandardDetails;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.DialogUtil;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.widgets.ListviewTouch;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据打分标准标准打分
 * Created by kmdai on 2015/4/23.
 */
public class RateActivity extends Activity implements Handler.Callback, OnClickListener {

    /**
     * 获取打分详情
     */
    private final static int GET_EVASTANDARD = 0x001;
    /**
     * 打分
     */
    private final static int UPDATE_STANDARD_SCORE = 0x003;
    /**
     * 打分改变
     */
    public final static int SCORE_CHANGE = 0x004;


    private final static int EXIT = 0x001;
    private final static int RATE = 0x002;

    private RequestQueue requestQueue;
    private Handler mHandler;
    private AssessmentDetails assessmentDetails;
    private UserInfo userInfo;

    private StandardAdapter mStandardAdapter;
    private List<StandardDetails> mStandardDetailsList;
    private ListviewTouch mListView;

    private TextView option;
    private TextView point;

    private DialogUtil dialogUtil;
    private int dialogClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_layout);
        assessmentDetails = getIntent().getParcelableExtra("assessmentDetails");
        userInfo = getIntent().getParcelableExtra("userInfo");
        init();
        getEvaStandardDetails();
    }

    private void init() {
        dialogUtil = new DialogUtil(this, left, right);
        mStandardDetailsList = new ArrayList<>();
        mHandler = new Handler(this);
        requestQueue = RequestManager.getRequestQueue();
        option = (TextView) findViewById(R.id.rate_text_all);
        point = (TextView) findViewById(R.id.rate_text_point);
        mListView = (ListviewTouch) findViewById(R.id.rate_layout_listview);
        mStandardAdapter = new StandardAdapter(this, mStandardDetailsList, mHandler);
        mListView.setAdapter(mStandardAdapter);
        findViewById(R.id.rate_layout_btn_submit).setOnClickListener(this);
    }

    private void getEvaStandardDetails() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        httpConnect(URLConfig.GET_EVASTANDARDDeETAILS, data, GET_EVASTANDARD);
    }

    private OnClickListener left = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogUtil.cancle();
        }
    };
    private OnClickListener right = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (dialogClick) {
                case EXIT:
                    finish();
                    overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
                    break;
                case RATE:
                    submitScore();
                    break;
            }
        }
    };

    /**
     * 网络连接
     *
     * @param data
     * @param msg
     */
    private void httpConnect(String url, Map<String, String> data, final int msg) {
        requestQueue.add(new NormalPostRequest(url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                switch (msg) {
                    case GET_EVASTANDARD:
                        StandardDetails.getStandardDetails(response, mStandardDetailsList);
                        if (mStandardDetailsList.size() != 0) {
                            option.setText("请根据下面" + mStandardDetailsList.size() + "个具体项进行打分");
                            point.setText("满分:" + mStandardDetailsList.get(0).getTotalScore() + " 我的打分：" + getMyScore());
                            mStandardAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(RateActivity.this, "没有数据");
                        }
                        break;
                    case UPDATE_STANDARD_SCORE:
                        if ("success".equals(response.optString("result"))) {
                            ToastUtil.showToast(RateActivity.this, "打分成功");
                            setResult(EvaluationActivity.RATE_TRUE);
                            finish();
                            overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
                        }
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(RateActivity.this, getResources().getString(R.string.net_error));
            }
        }));
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SCORE_CHANGE:
                point.setText("满分:" + mStandardDetailsList.get(0).getTotalScore() + " 我的打分：" + getMyScore());
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            rateBack(null);
        }
        return true;
    }

    /**
     * 返回键
     *
     * @param view
     */
    public void rateBack(View view) {
        dialogClick = EXIT;
        dialogUtil.showDialog("确定离开打分页面吗？");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_layout_btn_submit:
                dialogClick = RATE;
                dialogUtil.showDialog("我的打分:" + getMyScore() + "，确定提交吗？");
                break;
        }
    }

    private void submitScore() {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", userInfo.getUuid());
        data.put("evaluationId", assessmentDetails.getEvaluationId());
        String score = "";
        for (StandardDetails standardDetails : mStandardDetailsList) {
            if ("item".equals(standardDetails.getType())) {
                score = score + standardDetails.getItemId() + "," + standardDetails.getShowScore() + "," + standardDetails.getType() + ";";
            } else if ("itemChild".equals(standardDetails.getType())) {
                score = score + standardDetails.getItemChildId() + "," + standardDetails.getShowScore() + "," + standardDetails.getType() + ";";
            }
        }
        data.put("scores", score);
        httpConnect(URLConfig.UPDATE_STANDARD_SCORE, data, UPDATE_STANDARD_SCORE);
    }

    private int getMyScore() {
        int a = 0;
        for (StandardDetails standardDetails : mStandardDetailsList) {
            a += standardDetails.getShowScore();
        }
        return a;
    }
}
