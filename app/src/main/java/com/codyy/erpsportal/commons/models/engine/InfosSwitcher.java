package com.codyy.erpsportal.commons.models.engine;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextSwitcher;

import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.utils.Info;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 资讯切换器
 * Created by gujiajia on 2017/5/9.
 */

public class InfosSwitcher implements Callback {

    /**
     * 资讯切换消息，用于定时翻上面的资讯
     */
    private final static int MSG_SWITCH = 0x47;

    /**
     * 资讯数据json数组
     */
    private JSONArray mInfoArray;

    private Handler mHandler;

    private TextSwitcher mInfoTitleTs;

    private Fragment mFragment;

    public InfosSwitcher(Fragment fragment, TextSwitcher infoTitleTs) {
        this.mFragment = fragment;
        mHandler = new Handler(this);
        mInfoTitleTs = infoTitleTs;
        mInfoTitleTs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onInfoClick();
            }
        });
        mInfoTitleTs.setFactory(new InfoTitleTextFactory(fragment.getActivity()));
    }

    public void onInfoClick() {
        if (mInfoArray != null && mInfoArray.length() != 0 && mInfoCurrentPos < mInfoArray.length()) {
            int position = mInfoCurrentPos;
            if (position < 0) position = 0;//只有一个时
            JSONObject infoObj = mInfoArray.optJSONObject(position);
            if (infoObj != null) {
                InfoDetailActivity.startFromChannel(mFragment.getActivity(), infoObj.optString("informationId"));
            }
        }
    }

    /**
     * 当期滚动咨询位置
     */
    private int mInfoCurrentPos;

    /**
     * 开始切换标题
     */
    public void startSwitch() {
        if (mInfoArray == null || mInfoArray.length() == 0){
            mHandler.removeMessages(MSG_SWITCH);
            return;
        }
        showInfo();
        if (mInfoArray.length() < 2) {
            mHandler.removeMessages(MSG_SWITCH);
            return;
        }
        switchInfo();
    }

    /**
     * 开始切换资讯
     */
    private void switchInfo() {
        //计算下一个位置
        int nextPos = mInfoCurrentPos + 1;
        if (nextPos >= mInfoArray.length())
            nextPos = nextPos % mInfoArray.length();
        mHandler.removeMessages(MSG_SWITCH);
        Message message = Message.obtain();
        message.what = MSG_SWITCH;
        message.arg1 = nextPos;
        mHandler.sendMessageDelayed(message, 3000);
    }

    /**
     * 显示咨询
     */
    private void showInfo() {
        String infoType = mInfoArray.optJSONObject(mInfoCurrentPos).optString("infoType");
        String infoTypeName;
        switch (infoType) {
            case Info.TYPE_NEWS:
                infoTypeName = Titles.sPagetitleIndexCompositeNew;
                break;
            case Info.TYPE_ANNOUNCEMENT:
                infoTypeName = Titles.sPagetitleIndexCompositeAnnouncement;
                break;
            default: // if (infoType.equals(Info.TYPE_NOTICE))
                infoTypeName = Titles.sPagetitleIndexCompositeNotice;
                break;
        }
        String infoTitleStr = mInfoArray.optJSONObject(mInfoCurrentPos).optString("title");
        String infoStr = String.format("(%s) %s", infoTypeName, infoTitleStr);
        mInfoTitleTs.setText(infoStr);
    }

    @Override
    public boolean handleMessage(Message msg) {
        mInfoCurrentPos = msg.arg1;//更新新位置
        if (msg.what == MSG_SWITCH) {
            if (mFragment.isDetached()) {//如果未附上Activity，直接返回不显示资讯
                mHandler.removeMessages(MSG_SWITCH);
                return true;
            }

            showInfo();
            switchInfo();
            return true;
        }
        return false;
    }

    public void resume() {
        if (mInfoArray != null && mInfoArray.length() > 1) {
            startSwitch();
        }
    }

    public void setInfoArray(JSONArray infoArray) {
        mInfoArray = infoArray;
    }

    public void release() {
        mHandler.removeMessages(MSG_SWITCH);
    }
}
