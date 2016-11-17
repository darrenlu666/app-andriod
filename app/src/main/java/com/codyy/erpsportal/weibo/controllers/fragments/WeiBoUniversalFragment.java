package com.codyy.erpsportal.weibo.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.BaseRefreshFragment;
import com.codyy.erpsportal.commons.widgets.RefreshRecycleView;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.weibo.controllers.adapters.WeiBoUniversalAdapter;
import com.codyy.erpsportal.weibo.models.entities.WeiBoMessage;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by kmdai on 15-12-25.
 */
public class WeiBoUniversalFragment extends BaseRefreshFragment implements WeiBoUniversalAdapter.OnItemClick {

    /**
     * 搜索
     */
    public static final int TYPE_FIND_PEOPLE = RefreshEntity.REFRESH_TYPE_LASTVIEW + 1;
    /**
     * 粉丝
     */
    public static final int TYPE_MY_FANS = TYPE_FIND_PEOPLE + 1;
    /**
     * 我的关注
     */
    public static final int TYPE_MY_FOLLOW = TYPE_MY_FANS + 1;
    /**
     * 私信
     */
    public static final int TYPE_MY_MSG = TYPE_MY_FOLLOW + 1;
    /**
     * 删除私信
     */
    public static final int DELETE_MY_MSG = TYPE_MY_MSG + 1;
    private int mType;
    private UserInfo mUserInfo;
    /**
     * 搜索关键字
     */
    private String mKey;

    private int mStart;
    private final static int COUNT_NUMBER = 9;
    private int mEnd;

    private RefreshEntity mRefreshEntityCache;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mStart = 0;
        mKey = "";
        mEnd = mStart + COUNT_NUMBER;
        mUserInfo = UserInfoKeeper.getInstance().getUserInfo();
        setLastVisibleNB(COUNT_NUMBER + 1);
    }

    public void setType(int type) {
        mType = type;
        switch (mType) {
            case TYPE_FIND_PEOPLE:
                setEnable(false);
                setmURL(URLConfig.GET_SEARCH_PEOPLE);
                break;
            case TYPE_MY_FANS:
                setEnable(false);
                setmURL(URLConfig.GET_MY_FANS_LIST);
                httpConnect(getmURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
                break;
            case TYPE_MY_FOLLOW:
                setEnable(false);
                setmURL(URLConfig.GET_MY_FRIENDLIST);
                httpConnect(getmURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
                break;
            case TYPE_MY_MSG:
                setEnable(false);
                setmURL(URLConfig.GET_MIBLOG_MESSAGE_LIST);
                httpConnect(getmURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
                break;
        }
    }

    public void setOnTouch(RefreshRecycleView.OnTouch onTouch) {
        if (mRefreshRecycleView != null) {
            mRefreshRecycleView.setOnTouch(onTouch);
        }
    }

    @Override
    public void loadData() {
    }

    @Override
    protected boolean hasData() {
        if (mDatas.size() < mEnd) {
            return false;
        }
        return true;
    }

    @Override
    protected void onRequestError(VolleyError error, int msg) {
        mEnd = mDatas.size();
        super.onRequestError(error, msg);
    }

    @NonNull
    @Override
    public RefreshBaseAdapter getAdapter(List data) {
        WeiBoUniversalAdapter adapter = new WeiBoUniversalAdapter(getActivity(), data);
        adapter.setOnItemClick(this);
        return adapter;
    }

    public void searchInPut(String key) {
        mKey = key;
        httpConnect(getmURL(), getParam(STATE_ON_DOWN_REFRESH), STATE_ON_DOWN_REFRESH);
    }

    @NonNull
    @Override
    public Map<String, String> getParam(int state) {
        Map<String, String> param = new Hashtable<>();
        if (mUserInfo != null) {
            param.put("uuid", mUserInfo.getUuid());
        }
        param.put("key", mKey);
        addIndex(state, param);
        return param;
    }

    private void addIndex(int state, Map<String, String> param) {
        switch (state) {
            case STATE_ON_DOWN_REFRESH:
                mStart = 0;
                mEnd = mStart + COUNT_NUMBER;
                param.put("start", String.valueOf(mStart));
                param.put("end", String.valueOf(mEnd));
                break;
            case STATE_ON_UP_REFRESH:
                mStart = mDatas.size();
                mEnd = mStart + COUNT_NUMBER;
                param.put("start", String.valueOf(mStart));
                param.put("end", String.valueOf(mEnd));
                break;
        }
    }

    @NonNull
    @Override
    public List getDataOnJSON(JSONObject object) {
        switch (mType) {
            case TYPE_FIND_PEOPLE:
            case TYPE_MY_FANS:
            case TYPE_MY_FOLLOW:
                return WeiBoSearchPeople.getWeiBoSearchPeople(object, mType);
            case TYPE_MY_MSG:
                return WeiBoMessage.getWeiBoMessage(object);
        }
        return new ArrayList();
    }


    @Override
    protected boolean onRequestSuccess(JSONObject object, int msg) {
        switch (msg) {
            case TYPE_FIND_PEOPLE:
                if ("success".equals(object.optString("result"))) {
                    final WeiBoSearchPeople searchPeople = (WeiBoSearchPeople) mRefreshEntityCache;
                    if (searchPeople.isFollowFlag()) {
                        if (null != getView()) {
                            Snackbar.make(getView(), getString(R.string.weibo_has_delete_friend) + searchPeople.getRealName(), Snackbar.LENGTH_SHORT).show();
                        }
                        searchPeople.setFollowFlag(false);
                    } else {
                        if (null != getView()) {
                            if (null != getView()) {
                                Snackbar.make(getView(), getString(R.string.weibo_has_add_friend) + searchPeople.getRealName(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        searchPeople.setFollowFlag(true);
                    }
                    getView().post(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshBaseAdapter.notifyItemChanged(mDatas.indexOf(searchPeople));
                        }
                    });
                }
                return true;
            case TYPE_MY_FOLLOW:
                if ("success".equals(object.optString("result"))) {
                    final WeiBoSearchPeople searchPeople = (WeiBoSearchPeople) mRefreshEntityCache;
                    if (null != getView()) {
                        Snackbar.make(getView(), getString(R.string.weibo_has_delete_friend) + searchPeople.getRealName(), Snackbar.LENGTH_SHORT).show();
                    }
                    mDatas.remove(mRefreshEntityCache);
                    if (mDatas.size() <= 0) {
                        mRefreshBaseAdapter.notifyDataSetChanged();
                        emptyViewState();
                    } else {
                        mRefreshBaseAdapter.notifyItemRemoved(mRefreshEntityCache.getPosition());
                    }
                }
                return true;
            case TYPE_MY_FANS:
                if ("success".equals(object.optString("result"))) {
                    final WeiBoSearchPeople searchPeople = (WeiBoSearchPeople) mRefreshEntityCache;
                    if (null != getView()) {
                        Snackbar.make(getView(), getString(R.string.weibo_has_remove_fans) + searchPeople.getRealName(), Snackbar.LENGTH_SHORT).show();
                    }
                    mDatas.remove(mRefreshEntityCache);
                    if (mDatas.size() <= 0) {
                        mRefreshBaseAdapter.notifyDataSetChanged();
                        emptyViewState();
                    } else {
                        mRefreshBaseAdapter.notifyItemRemoved(mRefreshEntityCache.getPosition());
                    }
                }
                return true;
            case TYPE_MY_MSG:
                if ("success".equals(object.optString("result"))) {
                    WeiBoMessage myMsg = (WeiBoMessage) mRefreshEntityCache;
                    if (!TextUtils.isEmpty(myMsg.getBlackUserId())) {
                        myMsg.setBlackUserId("");
                        if (null != getView()) {
                            Snackbar.make(getView(), getString(R.string.weibo_has_delete_shield_msg) + myMsg.getTargetRealName(), Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        myMsg.setBlackUserId(myMsg.getTargetUserId());
                        if (null != getView()) {
                            Snackbar.make(getView(), getString(R.string.weibo_has_shield_msg) + myMsg.getTargetRealName(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    getView().post(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshBaseAdapter.notifyItemChanged(mDatas.indexOf(mRefreshEntityCache));
                        }
                    });
                } else {
                    if (null != getView()) {
                        Snackbar.make(getView(), "操作失败！", Snackbar.LENGTH_SHORT).show();
                    }
                }
                return true;
            case DELETE_MY_MSG:
                if ("success".equals(object.optString("result"))) {
                    final WeiBoMessage searchPeople = (WeiBoMessage) mRefreshEntityCache;
                    if (null != getView()) {
                        Snackbar.make(getView(), getString(R.string.weibo_has_remove_msg) + searchPeople.getTargetRealName(), Snackbar.LENGTH_SHORT).show();
                    }
                    mDatas.remove(mRefreshEntityCache);
                    if (mDatas.size() <= 0) {
                        mRefreshBaseAdapter.notifyDataSetChanged();
                        emptyViewState();
                    } else {
                        mRefreshBaseAdapter.notifyItemRemoved(mRefreshEntityCache.getPosition());
                    }
                }
                return true;
        }
        return super.onRequestSuccess(object, msg);
    }

    @Override
    public void onItemClick(RefreshEntity refreshEntity, int type, int position) {
        mRefreshEntityCache = refreshEntity;
        mRefreshEntityCache.setPosition(position);
        switch (type) {
            case TYPE_FIND_PEOPLE:
                WeiBoSearchPeople searchPeople = (WeiBoSearchPeople) refreshEntity;
                if (searchPeople.isFollowFlag()) {
                    Map<String, String> parm = new HashMap<>();
                    parm.put("uuid", mUserInfo.getUuid());
                    parm.put("unfollowId", searchPeople.getUserId());
                    httpConnect(URLConfig.DELETE_FRIEND, parm, TYPE_FIND_PEOPLE);
                } else {
                    Map<String, String> parm = new HashMap<>();
                    parm.put("uuid", mUserInfo.getUuid());
                    parm.put("followId", searchPeople.getUserId());
                    httpConnect(URLConfig.ADD_FRIEND, parm, TYPE_FIND_PEOPLE);
                }
                break;
            case TYPE_MY_FANS:
                WeiBoSearchPeople myfans = (WeiBoSearchPeople) refreshEntity;
                Map<String, String> parm1 = new HashMap<>();
                parm1.put("uuid", mUserInfo.getUuid());
                parm1.put("removeFollowedId", myfans.getUserId());
                httpConnect(URLConfig.DELETE_FANS, parm1, TYPE_MY_FANS);
                break;
            case TYPE_MY_FOLLOW:
                WeiBoSearchPeople myfollow = (WeiBoSearchPeople) refreshEntity;
                Map<String, String> parm = new HashMap<>();
                parm.put("uuid", mUserInfo.getUuid());
                parm.put("unfollowId", myfollow.getUserId());
                httpConnect(URLConfig.DELETE_FRIEND, parm, TYPE_MY_FOLLOW);
                break;
            case TYPE_MY_MSG:
                WeiBoMessage message = (WeiBoMessage) refreshEntity;
                if (!TextUtils.isEmpty(message.getBlackUserId())) {
                    WeiBoMessage myMsg = (WeiBoMessage) refreshEntity;
                    Map<String, String> parm2 = new HashMap<>();
                    parm2.put("uuid", mUserInfo.getUuid());
                    parm2.put("blackUserId", myMsg.getBlackUserId());
                    httpConnect(URLConfig.DELETE_MI_BLOG_BLACK, parm2, TYPE_MY_MSG);
                } else {
                    WeiBoMessage myMsg = (WeiBoMessage) refreshEntity;
                    Map<String, String> parm2 = new HashMap<>();
                    parm2.put("uuid", mUserInfo.getUuid());
                    parm2.put("blackUserId", myMsg.getTargetUserId());
                    httpConnect(URLConfig.ADD_MI_BLOG_BLACK, parm2, TYPE_MY_MSG);
                }
                break;
            case DELETE_MY_MSG:
                WeiBoMessage message1 = (WeiBoMessage) refreshEntity;
                Map<String, String> parm2 = new HashMap<>();
                parm2.put("uuid", mUserInfo.getUuid());
                parm2.put("targetUserId", message1.getTargetUserId());
                httpConnect(URLConfig.DELETE_MI_MIBLOG_MESSAGE, parm2, DELETE_MY_MSG);
                break;
        }
    }

}
