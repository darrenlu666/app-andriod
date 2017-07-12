package com.codyy.erpsportal.weibo.controllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.BaseRefreshFragment;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.WeiBoPopuDialog;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoActivity;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoNewActivity;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoPrivateMessageActivity;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoTurnActivity;
import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoAdapter;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.codyy.url.URLConfig;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 15-12-23.
 */
public class WeiBoFragment extends BaseRefreshFragment<WeiBoListInfo> implements WeiBoAdapter.OnItemClick {

    /**
     * 微博点赞
     */
    private final static int AGREE_WEIBO = 0x0a1;
    /**
     * 微博点赞
     */
    private final static int UNAGREE_WEIBO = 0x0a2;
    /**
     * 删除微博
     */
    private final static int DELETE_WEIBO = 0x0a3;
    /**
     * 发微博
     */
    public final static int NEW_WEIBO = 0x001;
    /**
     * 转发微博
     */
    public final static int TURN_WEIBO = NEW_WEIBO + 1;
    private UserInfo mUserInfo;
    private int mStart;
    private final static int COUNT_NUMBER = 19;
    private int mEnd;
    /**
     * 点赞、取消点赞的微博信息缓存
     */
    private WeiBoListInfo mAgreeWeibo;
    private String mGroupId;
    private int mType;
    private String mUserId;
    private String mIsMy;
    private RequestSender mRequestSender;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestSender = new RequestSender(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        mGroupId = getArguments().getString(WeiBoActivity.GROUP_ID);
        mType = getArguments().getInt(WeiBoActivity.TYPE, WeiBoActivity.TYPE_VISITOR);
        mUserId = getArguments().getString(WeiBoActivity.USER_ID);
        if (mUserId == null) {
            mUserId = mUserInfo.getBaseUserId();
        }
        if (mGroupId == null) {
            setURL(URLConfig.SELECT_DYNAMIC_LIST);
        } else {
            setURL(URLConfig.WEIBO_LIST_BYGROUPID);
        }
    }

    @NonNull
    @Override
    public RefreshBaseAdapter<WeiBoListInfo> getAdapter(List<WeiBoListInfo> data) {
        WeiBoAdapter weiBoAdapter = new WeiBoAdapter(getActivity(), data, mType);
        weiBoAdapter.setOnItemClick(this);
        return weiBoAdapter;
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> param = new Hashtable<>();
        if (mUserInfo != null) {
            param.put("uuid", mUserInfo.getUuid());
            if (mGroupId != null) {
                param.put("groupId", mGroupId);
            } else {
                param.put("baseUserId", mUserId);
            }
        }
        switch (mType) {
            case WeiBoActivity.TYPE_PERSNOAL_MY:
                param.put("isMySelf", "Y");
                break;
        }
        switch (state) {
            case STATE_ON_DOWN_REFRESH:
                mStart = 0;
                mEnd = mStart + COUNT_NUMBER;
                break;
            case STATE_ON_UP_REFRESH:
                mStart = mDatas.size();
                mEnd = mStart + COUNT_NUMBER;
                break;
        }
        param.put("start", String.valueOf(mStart));
        param.put("end", String.valueOf(mEnd));
        return param;
    }

    @Override
    public void loadData() {
        if (getView() != null) {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    mRefreshRecycleView.setRefreshing(true);
                }
            });
        }
        httpConnect(getURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    protected boolean hasData() {
        if (mDatas.size() >= mEnd) {
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public List<WeiBoListInfo> getDataOnJSON(JSONObject object) {
        List<WeiBoListInfo> mDatas = WeiBoListInfo.getWeiBoList(object);
        return mDatas;
    }

    /**
     * @param object
     * @param msg
     */
    protected boolean onRequestSuccess(JSONObject object, int msg) {
        switch (msg) {
            case AGREE_WEIBO:
                mAgreeIsRequest = false;
                if (null != getView()) {
                    Snackbar.make(getView(), object.optString("message"), Snackbar.LENGTH_SHORT).show();
                }
                if ("success".equals(object.optString("result"))) {
                    if (mAgreeWeibo != null) {
                        mAgreeWeibo.setAgreeFlag("N");
                        mAgreeWeibo.setAgreeCount(mAgreeWeibo.getAgreeCount() + 1);

                        if (getView() != null) {
                            getView().post(new Runnable() {
                                @Override
                                public void run() {
                                    mRefreshBaseAdapter.notifyItemChanged(mDatas.indexOf(mAgreeWeibo));
                                }
                            });
                        }
                    }
                }
                return true;
            case UNAGREE_WEIBO:
                mAgreeIsRequest = false;
                if (null != getView()) {
                    Snackbar.make(getView(), object.optString("message"), Snackbar.LENGTH_SHORT).show();
                }
                if ("success".equals(object.optString("result"))) {
                    if (mAgreeWeibo != null) {
                        mAgreeWeibo.setAgreeFlag("Y");
                        mAgreeWeibo.setAgreeCount(mAgreeWeibo.getAgreeCount() - 1);
                        if (getView() != null) {
                            getView().post(new Runnable() {
                                @Override
                                public void run() {
                                    mRefreshBaseAdapter.notifyItemChanged(mDatas.indexOf(mAgreeWeibo));
                                }
                            });
                        }
                    }
                }
                return true;
            case DELETE_WEIBO:
                if ("success".equals(object.optString("result"))) {
                    if (null != getView()) {
                        Snackbar.make(getView(), object.optString("message"), Snackbar.LENGTH_SHORT).show();
                    }
                    int size = mDatas.indexOf(mAgreeWeibo);
                    mDatas.remove(mAgreeWeibo);
                    if (mDatas.size() > 0) {
                        mRefreshBaseAdapter.notifyItemRemoved(size);
                    } else {
                        mRefreshBaseAdapter.notifyDataSetChanged();
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onRequestError(Throwable error, int msg) {
        switch (msg) {
            case AGREE_WEIBO:
            case UNAGREE_WEIBO:
                mAgreeIsRequest = false;
                break;
        }
        mEnd = mDatas.size();
        super.onRequestError(error, msg);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        ((WeiBoActivity) getActivity()).loadMSG();
    }

    private boolean mAgreeIsRequest = false;

    @Override
    public void agreeWeibo(WeiBoListInfo weiBoListInfo) {
        if (!mAgreeIsRequest) {
            mAgreeIsRequest = true;
            mAgreeWeibo = weiBoListInfo;
            Map<String, String> parm = new Hashtable<>();
            parm.put("uuid", mUserInfo.getUuid());
            String url = "";
            switch (mType) {
                case WeiBoActivity.TYPE_GROUP:
                case WeiBoActivity.TYPE_GROUP_VISITOR:
                case WeiBoActivity.TYPE_GROUP_MANAGER:
                    url = URLConfig.INSERT_MIBLOG_AGREE_GROUP;
                    parm.put("groupMiblogId", weiBoListInfo.getGroupMiblogId());
                    break;
                case WeiBoActivity.TYPE_PERSONAL:
                case WeiBoActivity.TYPE_VISITOR:
                case WeiBoActivity.TYPE_PERSNOAL_MY:
                    url = URLConfig.INSERT_MIBLOG_AGREE;
                    parm.put("weiBoId", weiBoListInfo.getMiblogId());
                    break;
            }
            httpConnect(url, parm, AGREE_WEIBO);
        }
    }

    @Override
    public void unAgreeWeibo(WeiBoListInfo weiBoListInfo) {
        if (!mAgreeIsRequest) {
            mAgreeIsRequest = true;
            mAgreeWeibo = weiBoListInfo;
            Map<String, String> parm = new Hashtable<>();
            parm.put("uuid", mUserInfo.getUuid());
            String url = "";
            switch (mType) {
                case WeiBoActivity.TYPE_GROUP:
                case WeiBoActivity.TYPE_GROUP_VISITOR:
                case WeiBoActivity.TYPE_GROUP_MANAGER:
                    url = URLConfig.CANCEL_MIBLOG_AGREE_GROUP;
                    parm.put("groupMiblogId", weiBoListInfo.getGroupMiblogId());
                    break;
                case WeiBoActivity.TYPE_PERSONAL:
                case WeiBoActivity.TYPE_VISITOR:
                case WeiBoActivity.TYPE_PERSNOAL_MY:
                    url = URLConfig.CANCEL_MIBLOG_AGREE;
                    parm.put("weiBoId", weiBoListInfo.getMiblogId());
                    break;
            }
            httpConnect(url, parm, UNAGREE_WEIBO);
        }
    }

    public void newWeiBo() {
//        WeiBoNewActivity.start(getActivity(), NEW_WEIBO);
        Intent intent = new Intent(getActivity(), WeiBoNewActivity.class);
        intent.putExtra(WeiBoActivity.TYPE, mType);
        intent.putExtra(WeiBoActivity.GROUP_ID, mGroupId);
        startActivityForResult(intent, NEW_WEIBO);
        UIUtils.addEnterAnim(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_WEIBO:
                if (resultCode == Activity.RESULT_OK) {
                    mRefreshRecycleView.setRefreshing(true);
                    onRefresh();
                }
                break;
            case TURN_WEIBO:
                if (resultCode == Activity.RESULT_OK) {
                    mRefreshRecycleView.setRefreshing(true);
                    onRefresh();
                }
                break;
        }
    }

    @Override
    public void onMenuClick(final WeiBoListInfo weiBoListInfo) {
        int type;
        mAgreeWeibo = weiBoListInfo;
        WeiBoPopuDialog popuMenuDialog;
        if (mType == WeiBoActivity.TYPE_GROUP_MANAGER && !mUserId.equals(weiBoListInfo.getBaseUserId())) {
            if ("SELF".equals(weiBoListInfo.getIsfollow())) {
                type = WeiBoPopuDialog.TYPE_ALL;
            } else if ("YES".equals(weiBoListInfo.getIsfollow())) {
                type = WeiBoPopuDialog.TYPE_ALL_UNFLOW;
            } else {
                type = WeiBoPopuDialog.TYPE_ALL_FLOW;
            }
        } else if (mUserInfo.getBaseUserId().equals(weiBoListInfo.getBaseUserId())) {
            type = WeiBoPopuDialog.TYPE_DELETE_ONLY;
        } else {
            if (mType == WeiBoActivity.TYPE_PERSONAL) {//我的微博
                type = WeiBoPopuDialog.TYPE_MESSAGE_ONLY;
            } else if ("YES".equals(weiBoListInfo.getIsfollow())) {//已关注
                type = WeiBoPopuDialog.TYPE_MESSAGE_UNFLOW;
            } else {//没有关注
                type = WeiBoPopuDialog.TYPE_MESSAGE_FLOW;
            }
        }
        popuMenuDialog = WeiBoPopuDialog.newInstence(type);
        popuMenuDialog.setOnItemClick(new WeiBoPopuDialog.OnItemClick() {
            @Override
            public void onDeletecleck() {
                Map<String, String> parm = new Hashtable<>();
                parm.put("uuid", mUserInfo.getUuid());
                String url = "";
                switch (mType) {
                    case WeiBoActivity.TYPE_GROUP:
                    case WeiBoActivity.TYPE_GROUP_VISITOR:
                    case WeiBoActivity.TYPE_GROUP_MANAGER:
                        url = URLConfig.DELETE_DYNAMIC_BLOG_GROUP;
                        parm.put("groupMiblogId", weiBoListInfo.getGroupMiblogId());
                        break;
                    case WeiBoActivity.TYPE_PERSONAL:
                    case WeiBoActivity.TYPE_VISITOR:
                    case WeiBoActivity.TYPE_PERSNOAL_MY:
                        url = URLConfig.DELETE_DYNAMIC_BLOG;
                        parm.put("weiBoId", weiBoListInfo.getMiblogId());
                        break;
                }
                httpConnect(url, parm, DELETE_WEIBO);
            }

            @Override
            public void onMsgClick() {
                WeiBoPrivateMessageActivity.start(getContext(), weiBoListInfo.getBaseUserId(), weiBoListInfo.getRealName());
            }

            @Override
            public void onFlowClick() {
                String url;
                Map<String, String> parm = new Hashtable<>();
                parm.put("uuid", mUserInfo.getUuid());
                if ("YES".equals(weiBoListInfo.getIsfollow())) {
                    url = URLConfig.DELETE_FRIEND;
                    parm.put("unfollowId", weiBoListInfo.getBaseUserId());
                } else {
                    url = URLConfig.ADD_FRIEND;
                    parm.put("followId", weiBoListInfo.getBaseUserId());
                }
                mRequestSender.sendRequest(new RequestSender.RequestData(url, parm, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (isRemoving()) {
                            return;
                        }
                        if ("success".equals(response.optString("result")) && weiBoListInfo != null) {
                            if ("YES".equals(weiBoListInfo.getIsfollow())) {
                                for (WeiBoListInfo weiBoListInfo1 : mDatas) {
                                    if (weiBoListInfo.getBaseUserId().equals(weiBoListInfo1.getBaseUserId())) {
                                        weiBoListInfo1.setIsfollow("NO");
                                    }
                                }
                                if (getView() != null) {
                                    Snackbar.make(getView(), "取消关注成功！", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                for (WeiBoListInfo weiBoListInfo1 : mDatas) {
                                    if (weiBoListInfo.getBaseUserId().equals(weiBoListInfo1.getBaseUserId())) {
                                        weiBoListInfo1.setIsfollow("YES");
                                    }
                                }
                                if (getView() != null) {
                                    Snackbar.make(getView(), "关注成功！", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } else if (getView() != null) {
                            Snackbar.make(getView(), "操作失败！", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(Throwable error) {

                    }
                }));
            }

            @Override
            public void onDismiss() {

            }
        });
        popuMenuDialog.show(getFragmentManager(), "onMenuClick");
    }

    @Override
    public void onDestroy() {
        mRequestSender.stop();
        ((WeiBoAdapter) mRefreshBaseAdapter).onDestroy();
        super.onDestroy();
    }

    @Override
    public void turnWeiBo(WeiBoListInfo weiBoListInfo) {
        Intent intent = new Intent(getActivity(), WeiBoTurnActivity.class);
        intent.putExtra(WeiBoTurnActivity.TURN_DATA, weiBoListInfo);
        startActivityForResult(intent, TURN_WEIBO);
    }
}
