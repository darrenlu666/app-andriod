package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.ChannelAdapter;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.DeviceUtils;
import com.codyy.erpsportal.commons.widgets.CodyyViewPager;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.dao.ChatDataHelper;
import com.codyy.erpsportal.onlinemeetings.widgets.BGABadgeTextView;
import com.codyy.erpsportal.commons.widgets.SlidingTabLayout;
import butterknife.Bind;

/**
 * 聊天区fragment
 * Created by yangxinwu on 2015/7/30.
 */
public class OnLineChatFragment extends OnlineFragmentBase implements OnLineChatMessageFragment.GetViewPager ,Handler.Callback{
    private final static String TAG = OnLineChatFragment.class.getSimpleName();
    private static final int MSG_DB_ACTION＿DONE = 0x111;//删除数据结束 .

    public final static String INTENT_ACTION_NOTICE = "intent_action_notice";
    @Bind(R.id.viewpager) CodyyViewPager mViewPager;
    @Bind(R.id.sliding_tabs) SlidingTabLayout mSlidingTabLayout;
    private ChannelAdapter mAdapter;
    private BGABadgeTextView mGroupChatBGABadgeTextView, mSingleChatBGABadgeTextView, mMsgBGABadgeTextView;
    private ChatDataHelper mChatDataHelper;
    private boolean isObserver = false;
    private Handler mHandler = new Handler(this);

    //Tab标签右上角的小红点消息提示框
    private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(OnLineChatMessageFragment.CHAT_TYPE, 0) == OnLineChatMessageFragment.CHAT_TYPE_GROUP) {
                if ((mGroupChatBGABadgeTextView.getCurCount() + 1) > 99) { //群聊的提示红点
                    mGroupChatBGABadgeTextView.showTextBadge("99+");
                } else {
                    mGroupChatBGABadgeTextView.showTextBadge((mGroupChatBGABadgeTextView.getCurCount() + 1) + "");
                }
            } else if (intent.getIntExtra(OnLineChatMessageFragment.CHAT_TYPE, 0) == OnLineChatMessageFragment.CHAT_TYPE_SINGLE) {
                if ((mSingleChatBGABadgeTextView.getCurCount() + 1) > 99) {//单聊的提示红点
                    mSingleChatBGABadgeTextView.showTextBadge("99+");
                } else {
                    mSingleChatBGABadgeTextView.showTextBadge((mSingleChatBGABadgeTextView.getCurCount() + 1) + "");
                }
            } else {
                if ((mMsgBGABadgeTextView.getCurCount() + 1) > 99) {//系统消息的提示红点
                    mMsgBGABadgeTextView.showTextBadge("99+");
                } else {
                    mMsgBGABadgeTextView.showTextBadge((mMsgBGABadgeTextView.getCurCount() + 1) + "");
                }
            }
        }
    };

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        checkDataBaseValidity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.i(TAG," onCreate～ ");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Cog.i(TAG," onViewCreated ");
        getParentActivity().setTitle("聊天区");
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_chat;
    }

    private void initView() {
        IntentFilter filter = new IntentFilter(INTENT_ACTION_NOTICE);
        getActivity().registerReceiver(mNoticeReceiver, filter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPagingEnabled(false);
        mSlidingTabLayout.setCustomTabView(R.layout.msg_notify_layout, R.id.tab_text);
        if (mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3) {
            isObserver = true;
        }
        mAdapter = new ChannelAdapter(getActivity(), getChildFragmentManager(), mViewPager);
        Bundle bd = new Bundle();
        bd.putParcelable(Constants.USER_INFO,mUserInfo);
//        mAdapter.addTab(getResources().getString(R.string.group_chat), new OnLineChatMessageFragment(), bd);
        mAdapter.addTab(getResources().getString(R.string.group_chat), new OnlineGroupChatFragment(), bd);
        if (!isObserver) { //是观摩者 去除私聊
            mAdapter.addTab(getResources().getString(R.string.single_chat), new ContactsFragment(), bd);
        }
        mAdapter.addTab(getResources().getString(R.string.system_message), new SystemMessageFragment(), bd);
        mSlidingTabLayout.setViewPager(mViewPager);

        View view2 = mSlidingTabLayout.getChildAt(0);
        LinearLayout layout = (LinearLayout) view2;
        RelativeLayout rt0 = (RelativeLayout) layout.getChildAt(0);
        mGroupChatBGABadgeTextView = (BGABadgeTextView) rt0.findViewById(R.id.tab_text);
        if (!isObserver) {
            RelativeLayout rt1 = (RelativeLayout) layout.getChildAt(1);
            mSingleChatBGABadgeTextView = (BGABadgeTextView) rt1.findViewById(R.id.tab_text);
            RelativeLayout rt2 = (RelativeLayout) layout.getChildAt(2);
            mMsgBGABadgeTextView = (BGABadgeTextView) rt2.findViewById(R.id.tab_text);
        } else {
            RelativeLayout rt2 = (RelativeLayout) layout.getChildAt(1);
            mMsgBGABadgeTextView = (BGABadgeTextView) rt2.findViewById(R.id.tab_text);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    mGroupChatBGABadgeTextView.showTextBadge("0");
                    mGroupChatBGABadgeTextView.hiddenBadge();
                } else if (position == 1) {
                    DeviceUtils.hideSoftKeyboard(mViewPager);
                    if (!isObserver) {
                        mSingleChatBGABadgeTextView.showTextBadge("0");
                        mSingleChatBGABadgeTextView.hiddenBadge();
                    } else {
                        mMsgBGABadgeTextView.showTextBadge("0");
                        mMsgBGABadgeTextView.hiddenBadge();
                    }
                } else {
                    DeviceUtils.hideSoftKeyboard(mViewPager);
                    mMsgBGABadgeTextView.showTextBadge("0");
                    mMsgBGABadgeTextView.hiddenBadge();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 检查数据库中的聊天记录是否失效  默认保存24小时
     * 修改：不保存聊天记录直接删除
     *
     */
    private void checkDataBaseValidity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mChatDataHelper = new ChatDataHelper(getActivity());
                mChatDataHelper.open();
                mChatDataHelper.deleteAll();
                mChatDataHelper.close();
                mHandler.sendEmptyMessage(MSG_DB_ACTION＿DONE);
            }
        }).start();
    }

    /**
     * 返回当前的SlidingTabLayout实例
     *
     * @return
     */
    @Override
    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mNoticeReceiver);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_DB_ACTION＿DONE:
                initView();
                break;
        }
        return false;
    }
}
