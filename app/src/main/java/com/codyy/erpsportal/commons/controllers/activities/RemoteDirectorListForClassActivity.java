package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.RemoteDirectorClassViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.ClassRoomItem;
import com.codyy.erpsportal.commons.models.entities.RemoteDirectorConfig;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专递课堂-远程导播列表
 * Created by yangxinwu on 2015/7/28.
 */
public class RemoteDirectorListForClassActivity extends AppCompatActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {
    private final static String EXTRA_USER_INFO = "user_info";
    private String TAG = "RemoteDirectorListActivity";
    private PullToRefreshListView mListView;
    private EmptyView mEmptyView;
    private ObjectsAdapter<ClassRoomItem, RemoteDirectorClassViewHolder> mAdapter;
    private static final int mLoadCount = 10;
    private int mStart;
    private RequestSender mRequestSender;
    private UserInfo mUserInfo;
    private AppCompatDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_director_list);
        initView();
        loadData(true);
    }

    private void initView() {
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mRequestSender = new RequestSender(this);
        mAdapter = new ObjectsAdapter<>(this, RemoteDirectorClassViewHolder.class);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText(Titles.sWorkspaceSpeclassBroadcast);
        }
        Button btnBack = (Button) findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(this);
        }
        mListView = (PullToRefreshListView) findViewById(R.id.ptrl_remote_director_list);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyView);
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(true);
            }
        });
        initPullToRefresh(mListView);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    private void loadData(final boolean refresh) {
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + mLoadCount;

        Map<String, String> params = new HashMap<>();
        params.put("uuid", mUserInfo.getUuid());
        params.put("directorId", mUserInfo.getBaseUserId());
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_DIRECTOR_FOR_CLASS, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {
                    int total = response.optInt("total");
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();
                        mListView.onRefreshComplete();
                        mEmptyView.setLoading(false);
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        List<ClassRoomItem> models = ClassRoomItem.parseList(response);
                        if (refresh) {
                            mAdapter.setData(models);
                        } else {
                            mAdapter.addData(models);
                        }
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount()) {
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    }
                    mStart = mAdapter.getCount();
                } else {
                    mListView.onRefreshComplete();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (refresh && mAdapter.getCount() == 0) {
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
            }
        }));
        mEmptyView.setLoading(true);
    }

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.pull_to_refresh));
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.loading));
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.release_to_refresh));
        view.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                this.finish();
                overridePendingTransition(R.anim.layout_show, R.anim.slidemenu_hide);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cog.d(TAG, "------position------" + position);
        showDialog();
        ClassRoomItem classRoomItem = mAdapter.getItem(position - 1);
        fetchRemoteDirectorConfig(classRoomItem.getMeetingId(),
                classRoomItem.getSubject(),
                classRoomItem.getTeacher(),
                "PROGRESS".equals(classRoomItem.getStatus()));
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(false);
    }

    /**
     * 获取远程导播配置
     *
     * @param mid 视频会话id
     */
    private void fetchRemoteDirectorConfig(String mid, final String subject, final String teacher, final boolean isProgressing) {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", mUserInfo.getUuid());
        param.put("mid", mid);
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.REMOTE_DIRECTOR_CONFIG, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Cog.d(TAG, "onResponse:" + jsonObject);
                String result = jsonObject.optString("result");
                if ("success".equals(result)) {
                    RemoteDirectorConfig config = RemoteDirectorConfig.parse(jsonObject.optJSONObject("data"));
                    dismissDialog();
                    if (TextUtils.isEmpty(config.getPmsRemoteUrl())) {
                        UIUtils.toast(getApplicationContext(), "无法获取直播服务地址！", Toast.LENGTH_SHORT);
                    } else {
                        RemoteDirectorActivity.start(RemoteDirectorListForClassActivity.this, config, subject, teacher, isProgressing);
                    }
                } else if ("error".equals(result)) {
                    dismissDialog();
                    UIUtils.toast(getApplicationContext(), "抱歉，无法连接远程导播！", Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Cog.e(TAG, "+fetchRemoteDirectorConfig:onErrorResponse:" + volleyError);
                dismissDialog();
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
            }
        }));
    }


    /**
     * 启动远程导播列表
     *
     * @param activity 原界面
     * @param userInfo 用户登录信息
     */
    public static void start(Activity activity, UserInfo userInfo) {
        Intent intent = new Intent(activity, RemoteDirectorListForClassActivity.class);
        intent.putExtra(EXTRA_USER_INFO, userInfo);
        activity.startActivity(intent);
    }

    private void showDialog() {
        createDialog();
        mLoadingDialog.show();
    }

    private void dismissDialog() {
        mLoadingDialog.dismiss();
    }

    private void createDialog() {
        if (mLoadingDialog == null) {
            Cog.d("createDialog", "createDialog");
            mLoadingDialog = new AppCompatDialog(this, R.style.NoTitleDialogStyle);
            mLoadingDialog.setTitle("请稍等…");
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            View view = LayoutInflater.from(this).inflate(R.layout.remote_loading_layout, null);
            mLoadingDialog.setContentView(view);
            ProgressBar loadingProgressBar = (ProgressBar) view.findViewById(R.id.loadingProgressBar);
            Drawable drawable = getResources().getDrawable(R.drawable.progress_loading);
            loadingProgressBar.setIndeterminateDrawable(drawable);
        }

    }

}
