package com.codyy.erpsportal.weibo.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by kmdai on 15-12-24.
 */
public class WeiBoActivity extends ToolbarActivity {
    private Integer mHashTag = this.hashCode();
    /**
     * 访客
     */
    public final static int TYPE_VISITOR = 0x001;
    /**
     * 个人微博
     */
    public final static int TYPE_PERSONAL = TYPE_VISITOR + 1;
    /**
     * 圈组微博
     */
    public final static int TYPE_GROUP = TYPE_PERSONAL + 1;
    /**
     * 圈组访客
     */
    public final static int TYPE_GROUP_VISITOR = TYPE_GROUP + 1;
    /**
     * 圈组管理员
     */
    public final static int TYPE_GROUP_MANAGER = TYPE_GROUP_VISITOR + 1;
    /**
     * 我的微博
     */
    public final static int TYPE_PERSNOAL_MY = TYPE_GROUP_MANAGER + 1;
    /**
     * 私信请求
     */
    public final static int REQUEST_MSG = 0x001;
    public static final String TAG = "WeiBoActivity:---";
    public final static String GROUP_ID = "group_id";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public final static String TYPE = "type";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTextView;
    @Bind(R.id.weibo_floatactionbutton)
    FloatingActionButton mSendWeiBoFloat;
    WeiBoFragment mWeiBoFragment;
    String mGroupId;
    UserInfo mUserInfo;
    private int mType;
    private RequestSender mRequestSender;
    private MenuItem mItem;
    private String mUserId;
    private MenuItem mFlowitem;
    private boolean mIsFlow;
    private boolean mCanFlow;
    private String mUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(this);
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        mGroupId = getIntent().getStringExtra(GROUP_ID);
        mUserId = getIntent().getStringExtra(USER_ID);
        mType = getIntent().getIntExtra(TYPE, TYPE_VISITOR);
        mUserName = getIntent().getStringExtra(USER_NAME);
        Bundle bundle = new Bundle();
        if (mGroupId != null) {
            bundle.putString(GROUP_ID, mGroupId);
        }
        if (mUserId != null) {
            bundle.putString(USER_ID, mUserId);
        }
        bundle.putInt(TYPE, mType);
        mWeiBoFragment = (WeiBoFragment) WeiBoFragment.instantiate(this, WeiBoFragment.class.getName(), bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.weibo_framelayout, mWeiBoFragment, "mWeiBoFragment").commitAllowingStateLoss();
        switch (mType) {
            case TYPE_VISITOR:
                if (!"AREA_USR".equals(mUserInfo.getUserType())) {
                    mSendWeiBoFloat.setVisibility(View.GONE);
                }
                mTextView.setText(mUserName + "的微博");
                isFlow();
                break;
            case WeiBoActivity.TYPE_PERSONAL:
                loadMSG();
                break;
            case TYPE_PERSNOAL_MY:
                mSendWeiBoFloat.setVisibility(View.GONE);
                mTextView.setText("我的微博");
                break;
            case TYPE_GROUP:
            case TYPE_GROUP_MANAGER:
            case TYPE_GROUP_VISITOR:
                mTextView.setText("圈组微博");
                break;
        }
        mSendWeiBoFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeiBoFragment.newWeiBo();
            }
        });
    }

    private void isFlow() {
        Map<String, String> parm = new HashMap<>();
        parm.put("baseUserId", mUserId);
        parm.put("uuid", mUserInfo.getUuid());
        parm.put("end", String.valueOf(0));
        parm.put("start", String.valueOf(0));
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.SELECT_DYNAMIC_LIST, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!isFinishing() && "success".equals(response.optString("result"))) {
                    mCanFlow = true;
                    mIsFlow = response.optBoolean("isflow");
                    if (mIsFlow) {
                        mFlowitem.setTitle("取消关注");
                    } else {
                        mFlowitem.setTitle("关注");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, mHashTag));
    }

    public void loadMSG() {
        if (mUserInfo == null) {
            mUserInfo = UserInfoKeeper.obtainUserInfo();
        }
        Map<String, String> parm = new HashMap<>();
        parm.put("uuid", mUserInfo.getUuid());
        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_MIBLOG_COUNT, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                if (!isFinishing() && "success".equals(response.optString("result")) && mItem != null) {
                    int size = response.optInt("unReadMiBlogCount");
                    if (size <= 0) {
                        mItem.setTitle("私信");
                    } else if (size > 0 && size < 99) {
                        mItem.setTitle("私信(" + size + ")");
                    } else if (size > 99) {
                        mItem.setTitle("私信(99+)");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, mHashTag));
    }

    public static void start(Activity context, int type, String groupID, String userId, String userName) {
        Intent intent = new Intent(context, WeiBoActivity.class);
        intent.putExtra(TYPE, type);
        if (groupID != null) {
            intent.putExtra(GROUP_ID, groupID);
        }
        if (userId != null) {
            intent.putExtra(USER_ID, userId);
        }
        if (userName != null) {
            intent.putExtra(USER_NAME, userName);
        }
        context.startActivity(intent);
        UIUtils.addEnterAnim(context);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_wei_bo_main;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        mTextView.setText("微博首页");
        setOverFlowIcon(mToolbar, R.drawable.weibo_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mType) {
            case WeiBoActivity.TYPE_PERSONAL:
                getMenuInflater().inflate(R.menu.menu_weibo, menu);
                mItem = menu.findItem(R.id.item_weibo_msg);
                break;
            case WeiBoActivity.TYPE_VISITOR:
                getMenuInflater().inflate(R.menu.menu_weibo_visitor, menu);
                mFlowitem = menu.findItem(R.id.item_weibo_visitor_follow);
                mFlowitem.setTitle("取消关注");
                break;
            case WeiBoActivity.TYPE_PERSNOAL_MY:
                getMenuInflater().inflate(R.menu.menu_search, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (mType) {
            case TYPE_PERSONAL:
            case TYPE_PERSNOAL_MY:
            case TYPE_GROUP_VISITOR:
            case WeiBoActivity.TYPE_VISITOR:
                if (!super.onOptionsItemSelected(item)) {
                    Bundle bundle = new Bundle();
                    switch (item.getItemId()) {
                        case R.id.item_weibo_fans:
                            bundle.putString(WeiBoUniversalActivity.KEY_TITLE, WeiBoUniversalActivity.MY_FANS);
                            break;
                        case R.id.item_weibo_follow:
                            bundle.putString(WeiBoUniversalActivity.KEY_TITLE, WeiBoUniversalActivity.MY_FOLLOW);
                            break;
                        case R.id.item_weibo_msg:
                            bundle.putString(WeiBoUniversalActivity.KEY_TITLE, WeiBoUniversalActivity.MY_MSG);
                            WeiBoUniversalActivity.start(this, bundle, REQUEST_MSG);
                            return true;
                        case R.id.item_weibo_search:
                            bundle.putString(WeiBoUniversalActivity.KEY_TITLE, WeiBoUniversalActivity.FIND_PEOPLE);
                            break;
                        case R.id.item_weibo_my:
                            WeiBoActivity.start(this, TYPE_PERSNOAL_MY, null, null, null);
                            return true;
                        case R.id.item_weibo_visitor_follow:
                            flow();
                            return true;
                        case R.id.item_weibo_visitor_msg:
                            WeiBoPrivateMessageActivity.start(this, mUserId, mUserName);
                            return true;
                    }
                    WeiBoUniversalActivity.start(this, bundle);
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void flow() {
        if (mCanFlow) {
            Map<String, String> parm = new HashMap<>();
            String url;
            if (mIsFlow) {
                url = URLConfig.DELETE_FRIEND;
                parm.put("unfollowId", mUserId);
            } else {
                url = URLConfig.ADD_FRIEND;
                parm.put("followId", mUserId);
            }

            parm.put("uuid", mUserInfo.getUuid());
            mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (!isFinishing() && "success".equals(response.optString("result"))) {
                        if (mIsFlow) {
                            mFlowitem.setTitle("关注");
                            mIsFlow = false;
                            Snackbar.make(mWeiBoFragment.getView(), "取消关注成功！", Snackbar.LENGTH_SHORT).show();
                        } else {
                            mFlowitem.setTitle("取消关注");
                            mIsFlow = true;
                            Snackbar.make(mWeiBoFragment.getView(), "关注成功！", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(mWeiBoFragment.getView(), "操作失败！", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, mHashTag));
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MSG:
                if (resultCode == RESULT_OK) {
                    loadMSG();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }
}