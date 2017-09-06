package com.codyy.erpsportal.commons.models.engine;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextSwitcher;

import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.mainpage.Announce;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.utils.Info;

import java.util.List;

/**
 * 资讯切换器
 * Created by poe on 2017/8/30.
 */

public class AnnounceSwitcher implements Callback {
    private static final String TAG = "AnnounceSwitcher";
    /**
     * 资讯切换消息，用于定时翻上面的资讯
     */
    private final static int MSG_SWITCH = 0x47;

    /**
     * 资讯数据json数组
     */
    private List<Announce> mInfoArray;

    private Handler mHandler;

    private TextSwitcher mInfoTitleTs;

    public AnnounceSwitcher(TextSwitcher infoTitleTs) {
        mHandler = new Handler(this);
        mInfoTitleTs = infoTitleTs;
        mInfoTitleTs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onInfoClick();
            }
        });
        mInfoTitleTs.setFactory(new InfoTitleTextFactory(mInfoTitleTs.getContext()));
    }

    public void onInfoClick() {
        if (mInfoArray != null && mInfoArray.size() != 0 && mInfoCurrentPos < mInfoArray.size()) {
            int position = mInfoCurrentPos;
            if (position < 0) position = 0;//只有一个时
            Announce infoObj = mInfoArray.get(position);
            if (infoObj != null) {
                InfoDetailActivity.startFromChannel(mInfoTitleTs.getContext(), infoObj.getInformationId());
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
        if (mInfoArray == null || mInfoArray.size() == 0){
            mHandler.removeMessages(MSG_SWITCH);
            return;
        }
        showInfo();
        if (mInfoArray.size() < 2) {
            mHandler.removeMessages(MSG_SWITCH);
            return;
        }
        switchInfo();
    }

    /**
     * 开始切换资讯
     */
    private void switchInfo() {
        Log.i(TAG,"switchInfo()");
        //计算下一个位置
        int nextPos = mInfoCurrentPos + 1;
        if (nextPos >= mInfoArray.size())
            nextPos = nextPos % mInfoArray.size();
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
        String infoType = mInfoArray.get(mInfoCurrentPos).getInfoType();
        String infoTypeName;
        switch (infoType) {
            case Info.TYPE_NEWS:
                infoTypeName = Titles.sPagetitleIndexGroupSchoolNew;
                break;
            case Info.TYPE_ANNOUNCEMENT:
                infoTypeName = Titles.sPagetitleIndexGroupSchoolAnnouncement;
                break;
            default: // if (infoType.equals(Info.TYPE_NOTICE))
                infoTypeName = Titles.sPagetitleIndexGroupSchoolNotice;
                break;
        }
        String infoTitleStr = mInfoArray.get(mInfoCurrentPos).getTitle();
        String infoStr = String.format("(%s) %s", infoTypeName, infoTitleStr);
        mInfoTitleTs.setText(infoStr);
    }

    @Override
    public boolean handleMessage(Message msg) {
        mInfoCurrentPos = msg.arg1;//更新新位置
        if (msg.what == MSG_SWITCH) {
            if (mInfoTitleTs.getContext() == null) {//如果未附上Activity，直接返回不显示资讯
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
        if (mInfoArray != null && mInfoArray.size() > 1) {
            startSwitch();
        }
    }

    public void setInfoArray(List<Announce> infoArray) {
        mInfoArray = infoArray;
    }

    public void release() {
        mHandler.removeMessages(MSG_SWITCH);
    }
}
