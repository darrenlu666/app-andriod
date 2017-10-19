package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.SingleChatActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.commons.models.entities.UpdateCantacts;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.services.IMeeting;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.NotifyUtils;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.ComposeView;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.controllers.adapters.MessageAdapter;
import com.codyy.erpsportal.onlinemeetings.models.dao.ChatDataHelper;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.onlinemeetings.models.entities.coco.MeetingCommand;
import com.codyy.erpsportal.onlinemeetings.utils.EmojiUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 聊天区fragment
 * Created by yangxinwu on 2015/8/20.
 */
public class OnLineChatMessageFragment extends Fragment implements ComposeView.OnComposeOperationDelegate {
    private final static String TAG = OnLineChatMessageFragment.class.getSimpleName();

    public static final String CHAT_TYPE = "chat_type";
    public static final int CHAT_TYPE_SINGLE = 0x01;//单聊
    public static final int CHAT_TYPE_GROUP = 0x02;//群聊
    private int mChatType = CHAT_TYPE_GROUP;//聊天类型
    private int mChatCount;
    private boolean isCanSay = true;//是否可以说话
    private String mCurUserId;//发言人ID
    private String mToChatUserId;//发送给那个人的ID
    private String mName;//发言人的名字
    private String mHeadUrl;//发言人的头像
    private String mMyMeetingID;//会议id
    private String[] str = {"群聊", "私聊", "系统消息"};
    private List<ChatMessage> messages = new ArrayList<>();
    private ListView mLvMessage;
    private ComposeView mComposeView;//聊天输入框
    private MessageAdapter mAdapter;//聊天Adapter
    private IMeeting mIMeeting;//发送消息的回调接口
    private NotifyUtils mNotifyUtils;//声音通知工具类
    private ViewPager mViewPager;
    private GetViewPager mGetViewPager;//获取当前ViewPager的回调接口
    private View mView;//缓存Fragment view
    private ChatDataHelper mChatDataHelper;//保存聊天内容的数据库
    private UserInfo mUserInfo;
    private List<OnlineUserInfo> mUsers = new ArrayList<>();//加载在线用户信息
    private StringBuilder mDbKey = new StringBuilder();//查询数据库中聊天记录的key
    private MeetingBase mMeetingBase = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIMeeting = (IMeeting) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if(null != getArguments()){
            mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
        }
        if(null == mUserInfo){
            mUserInfo   = UserInfoKeeper.getInstance().getUserInfo();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_chat_message, container, false);
            initViews(mView);
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        return mView;
    }


    /**
     * 初始化布局
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View view) {

        if (getArguments() != null) {
            //单聊的初始化
            mToChatUserId = getArguments().getString(SingleChatActivity.KEY_TO_CHAT_ID);//要发送给谁的ID
            mCurUserId = mUserInfo.getBaseUserId();//当前自己的ID
            mChatType = getArguments().getInt(CHAT_TYPE);//当前的聊天类型
            mMyMeetingID = getArguments().getString(SingleChatActivity.MEETING_ID);//当前会议ID
            mName = getArguments().getString(SingleChatActivity.USER_NAME);//当前自己的名字
            mHeadUrl = getArguments().getString(SingleChatActivity.USER_HEAD_URL);//当前自己的头像
            mDbKey = mDbKey.append(mMyMeetingID).append(mToChatUserId);//当前key，用于查询数据库中的聊天信息记录 以会议ID+自己的userID作为查询数据库的key

        } else {
            //群聊的初始化
            getAllOnLineUser();
            if (getActivity() != null) {
                mMeetingBase = ((OnlineMeetingActivity) getActivity()).getMeetingBase();
            }

            mMyMeetingID = getActivity().getIntent().getStringExtra(OnlineMeetingActivity.EXTRA_MEETING_ID);//当前会议ID
            mCurUserId = mUserInfo.getBaseUserId();//当前自己的ID
            mToChatUserId = Integer.valueOf(mMyMeetingID.substring(0,6),16)+"";
            mDbKey = mDbKey.append(mMyMeetingID);//当前key，用于查询数据库中的聊天信息记录 以会议ID作为查询数据库的key
            mGetViewPager = (GetViewPager) getParentFragment();//获得ViewPager，用来指出当前Tab在哪个标签下，主要是为了通知未读消息的数量（红色小点）
            mViewPager = mGetViewPager.getViewPager();
        }

        mNotifyUtils = new NotifyUtils();
        mNotifyUtils = mNotifyUtils.init(EApplication.instance());//初始化消息通知类
        mChatDataHelper = new ChatDataHelper(EApplication.instance());//初始化数据库
        mLvMessage = (ListView) view.findViewById(R.id.lv_message);
        mLvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mComposeView.hideEmojiOptAndKeyboard();//点击屏幕隐藏输入框
                return false;
            }
        });
        mComposeView = (ComposeView) view.findViewById(R.id.compose);//消息输入框控件
        mComposeView.setOperationDelegate(this);
        if (mMeetingBase != null && mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3) {
            mComposeView.setVisibility(View.GONE);//观摩者隐藏消息输入框
        }
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mAdapter = new MessageAdapter(getActivity(), mLvMessage, mCurUserId, messages);
        // 显示消息
        mLvMessage.setAdapter(mAdapter);
        //加载聊天记录，只保存当天的聊天记录，每次只取最新的30条记录
        new QueryChatMessageTask().execute();
    }

    /**
     * 获取所有用户列表
     */
    private void getAllOnLineUser() {
        if (getActivity() == null) {
            return;
        }
        Log.i(TAG, "getUsers...");
        ((OnlineMeetingActivity) getActivity()).getUsers(new OnlineMeetingActivity.ILoader() {
            @Override
            public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                if(null != users){
                    mUsers.clear();
                    mUsers.addAll(users);
                }
            }
        }, false,true);
    }

    /**
     * 判断 群聊信息是哪个用户发的消息
     *
     * @param msg
     */
    private ChatMessage findMessageFromWho(final ChatMessage msg) {
        if (mUsers.size() == 0) {
            return null;
        }

        ChatMessage chatMessage = null;
        chatMessage = msg;
        for (int i = 0; i < mUsers.size(); i++) {
            if (chatMessage.getFrom().equals(mUsers.get(i).getId())) {
                chatMessage.setName(mUsers.get(i).getName());
                chatMessage.setHeadUrl(mUsers.get(i).getIcon());
                break;
            }
        }
        return chatMessage;
    }

    /**
     * 判断 单聊信息是哪个用户发的消息
     */
    private ChatMessage findSingleMessageFromWho(final ChatMessage msg) {
        ChatMessage chatMessage = null;
        chatMessage = msg;
        chatMessage.setName(mName);
        chatMessage.setHeadUrl(mHeadUrl);
        return chatMessage;
    }


    /**
     * 消息来了刷新页面
     */
    private void refreshUIWithNewMessage() {
        if (mAdapter == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.refreshSelectLast();
            }
        });
    }


    /**
     * 如果是群聊就进行通知
     *
     * @param msg
     */
    private void noticeForGroupChat(ChatMessage msg) {

        int index = mViewPager.getCurrentItem();
        if (!str[index].equals("群聊")) {
            mNotifyUtils.viberateAndPlayTone(msg);
            Intent intent = new Intent();
            intent.setAction(OnLineChatFragment.INTENT_ACTION_NOTICE);
            intent.putExtra(CHAT_TYPE, CHAT_TYPE_GROUP);
            getActivity().sendBroadcast(intent);
        }

        Intent intent2 = new Intent();
        intent2.setAction(OnlineMeetingActivity.INTENT_ACTION_NOTICE2);
        getActivity().sendBroadcast(intent2);
    }

    /**
     * 发送聊天消息
     *
     * @param text
     */
    private void sendTextMessage(String text) {

        if (mMeetingBase != null && (mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3)) {
            UIUtils.toast(EApplication.instance(), "抱歉,您只可观摩无法发言", Toast.LENGTH_SHORT);
            return;
        }
        if (!isCanSay) {
            UIUtils.toast(EApplication.instance(), "您目前无权发话", Toast.LENGTH_SHORT);
            return;
        }
        if ("".equals(text)) {
            UIUtils.toast(EApplication.instance(), "无法发送空内容", Toast.LENGTH_SHORT);
            return;
        }
        if (!VideoDownloadUtils.isConnected(EApplication.instance())) {
            UIUtils.toast(EApplication.instance(), "您的网络异常哦", Toast.LENGTH_SHORT);
            return;
        }
        String receiveMsg = URLDecoder.decode(text);
        if (receiveMsg.length() > 120) {
            UIUtils.toast(EApplication.instance(), "您的话不能超过120个字", Toast.LENGTH_SHORT);
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        //替换表情
        String replaceMsg = EmojiUtils.replaceMsg(receiveMsg);
        //url encode
        String sendMsg = StringUtils.urlEncode(receiveMsg);
        Cog.i(TAG,"sendMsg: "+sendMsg);
        chatMessage.setMsg(replaceMsg);
        chatMessage.setHeadUrl(mUserInfo.getHeadPic());
        chatMessage.setName(mUserInfo.getUserName());
        chatMessage.setFrom(mCurUserId);
        chatMessage.setTo(mToChatUserId);
        chatMessage.setTime(System.currentTimeMillis() + "");
        if (mChatType == CHAT_TYPE_GROUP) {
            chatMessage.setChatType(ChatMessage.GROUP_CHAT);
            mAdapter.addData(chatMessage);
            refreshUIWithNewMessage();
            mIMeeting.sendMsg(sendMsg);//发送群聊消息
        } else {
            chatMessage.setChatType(ChatMessage.SINGLE_CHAT);
            EventBus.getDefault().post(chatMessage);
            mAdapter.addData(chatMessage);
            refreshUIWithNewMessage();
            mIMeeting.sendSignalMsg(sendMsg, mToChatUserId);//发送单聊消息
        }
    }

    @Override
    public void onSendText(String text) {
        sendTextMessage(text);
    }

    @Override
    public void onStop() {
        super.onStop();
        new InsertChatMessageTask().execute();//保存最新的聊天记录
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //单聊的时候，当关闭单聊页面时，联系人页面中的item的内容要更新
        if (mChatType == CHAT_TYPE_SINGLE && messages.size() > 0) {
            UpdateCantacts updateCantacts = new UpdateCantacts();
            updateCantacts.setContent(messages.get(messages.size() - 1).getMsg());
            updateCantacts.setTime(messages.get(messages.size() - 1).getTime());
            EventBus.getDefault().post(updateCantacts);
        }
        EventBus.getDefault().unregister(this);
    }


    /**
     * 禁言通知处理中心
     */
    public void onEventMainThread(CoCoAction action) {
        switch (action.getActionType()) {
            case MeetingCommand.WEB_CHAT_IS_CLOSE_BACK:
                if (action.getActionResult().equals("true")) {
                    canSay(false);
                } else {
                    canSay(true);
                }
                break;
        }
    }

    /**
     * 消息处理中心
     *
     * @throws RemoteException
     */
    public void onEventMainThread(ChatMessage msg) throws RemoteException {
        String userId = null;
        if (msg.getChatType() == CHAT_TYPE_GROUP) {
            //群聊的话就取coco消息中的 to=475487329537895387 作为发送此消息者的ID
            userId = msg.getTo();
        } else {
            //单聊的话就取coco消息中的 from=475487329537895387 作为发送此消息者的ID
            userId = msg.getFrom();
        }
        //如果是当前会话的消息，刷新聊天页面
        if (userId.equals(mToChatUserId) && !msg.getFrom().equals(mCurUserId)) {
            if (msg.getChatType() == CHAT_TYPE_GROUP) {
                ChatMessage message = findMessageFromWho(msg);// 判断群聊信息是哪个用户发的消息
                if (message == null) {
                    return;
                } else {
                    mAdapter.addData(message);
                }
                noticeForGroupChat(msg);//通知有消息来了，就是显示在群聊Tab标签右上角的红色小红点,用于表示未读消息的数量
            } else {
                mAdapter.addData(findSingleMessageFromWho(msg));
            }
            mNotifyUtils.onNewMsg(msg);//铃声通知消息到了
            refreshUIWithNewMessage();//刷新界面
        }
    }


    public interface GetViewPager {
        ViewPager getViewPager();
    }

    /**
     * 判断用户是否被禁言了
     *
     * @param b
     */
    private void canSay(boolean b) {
        if (b) {
            isCanSay = true;
            mComposeView.setVisibility(View.VISIBLE);
            UIUtils.toast(EApplication.instance(), "呵呵,您被允许发言了", Toast.LENGTH_SHORT);
        } else {
            isCanSay = false;
            mComposeView.setVisibility(View.GONE);
            UIUtils.toast(EApplication.instance(), "呜呜,您被禁言了", Toast.LENGTH_SHORT);
        }

    }

    //刚刚打开页面时，查询数据库中的历史聊天信息
    public class QueryChatMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            mChatDataHelper.open();
            messages.addAll(mChatDataHelper.queryAllChat(mDbKey.toString()));
            mChatCount = messages.size();//数据库中保存的聊天记录的个数
            mChatDataHelper.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshUIWithNewMessage();
        }
    }
    //往数据库中插入最新的消息
    public class InsertChatMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if (mAdapter.getData().size() > mChatCount) {
                mChatDataHelper.open();
                mChatDataHelper.addChat(mAdapter.getData(), mDbKey.toString(), mChatCount);
                mChatDataHelper.close();
            }
            return null;
        }

    }

}

