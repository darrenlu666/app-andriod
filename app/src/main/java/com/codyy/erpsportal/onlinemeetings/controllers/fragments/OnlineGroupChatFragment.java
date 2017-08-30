package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.services.IMeeting;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.VideoDownloadUtils;
import com.codyy.erpsportal.commons.widgets.BlogComposeView;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.OnlineChatReceiverViewHolder;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleRecyclerView;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 视频会议－群聊
 * Created by poe on 16-10-11.
 */
public class OnlineGroupChatFragment extends OnlineFragmentBase implements BlogComposeView.OnComposeOperationDelegate {
    public static final String TAG = "OnlineGroupFragment";

    @Bind(R.id.compose_view)
    BlogComposeView mInputView;

    @Bind(R.id.recycler_view)
    SimpleRecyclerView mRecyclerView;

    private IMeeting mIMeeting;//发送消息的回调接口
    /**
     * 群聊－发送
     */
    public static final int TYPE_CHAT_GROUP_SEND = 0x010;
    /**
     * 群聊－接收　
     */
    public static final int TYPE_CHAT_GROUP_RECEIVER = 0x011;

    private BaseRecyclerAdapter<ChatMessage,OnlineChatReceiverViewHolder> mAdapter ;
    private List<ChatMessage> mData = new ArrayList<>();//本地数据缓存
    private String mToUserId ;//视频会议id
    private onSoftKeyListener mSoftListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIMeeting = (IMeeting) activity;
        mSoftListener = (onSoftKeyListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_group_chat;
    }

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        mToUserId = Integer.valueOf(mMeetingBase.getBaseMeetID().substring(0,6),16)+"";
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mInputView.setOperationDelegate(this);
        if (mMeetingBase != null && mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3) {
            mInputView.setVisibility(View.GONE);//观摩者隐藏消息输入框
        }
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mInputView.hideEmojiOptAndKeyboard();
                return false;
            }
        });
        mAdapter = new BaseRecyclerAdapter(new BaseRecyclerAdapter.ViewCreator<OnlineChatReceiverViewHolder>() {
            @Override
            public OnlineChatReceiverViewHolder createViewHolder(ViewGroup parent, int viewType) {
                OnlineChatReceiverViewHolder viewHolder = new OnlineChatReceiverViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.row_received_message),mUserInfo.getBaseUserId());
                if(viewType == TYPE_CHAT_GROUP_SEND){
                    viewHolder = new OnlineChatReceiverViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.row_sent_message),mUserInfo.getBaseUserId());
                }
                viewHolder.setAdapter(mAdapter);
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getBaseViewHoldType();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        //get data .
        List<ChatMessage> data = getParentActivity().getGroupMessageCache();

        if(data.size()>0){
            for(ChatMessage cm : data){
                mData.add(cm);
            }
        }
        mAdapter.setData(mData);
    }

    @Override
    public void onSendText(String text) {
        sendTextMessage(text);
    }

    @Override
    public void onSoftWareKeyOpen() {
        //打开软件盘，隐藏底部导航栏.
        if(null != mSoftListener) mSoftListener.onKeyOpen();
    }

    @Override
    public void onSoftWareKeyClose() {
        //关闭软件盘,展示底部导航栏
        if(null != mSoftListener) mSoftListener.onKeyClose();
    }

    @Override
    public void onEmojiPanOpen() {
        // TODO: 08/05/17 打开表情输入框.
        if(null != mSoftListener) mSoftListener.onKeyOpen();
    }

    @Override
    public void onEmojiPanClose() {
        // TODO: 08/05/17 关闭表情输入框.
        if(null != mSoftListener) mSoftListener.onKeyClose();
    }

    /**
     * 发送聊天消息
     *
     * @param text
     */
    private void sendTextMessage(String text) {
        if(null == mRecyclerView) return;
        if (mMeetingBase != null && (mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3)) {
            UIUtils.toast(EApplication.instance(), "抱歉,您只可观摩无法发言", Toast.LENGTH_SHORT);
            return;
        }
        if (!mMeetingBase.getBaseChat().equals("1")) {
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
        chatMessage.setBaseViewHoldType(TYPE_CHAT_GROUP_SEND);
        //替换表情
        String replaceMsg = PullXmlUtils.replaceMsg(receiveMsg);
        //url encode
        String sendMsg = StringUtils.urlEncode(receiveMsg);
        Cog.i(TAG,"sendMsg: "+sendMsg);
        chatMessage.setMsg(replaceMsg);
        chatMessage.setHeadUrl(mUserInfo.getHeadPic());
        chatMessage.setName(mUserInfo.getUserName());
        chatMessage.setFrom(mUserInfo.getBaseUserId());
        chatMessage.setTo(mToUserId);
        chatMessage.setTime(System.currentTimeMillis() + "");
//        if (mChatType == CHAT_TYPE_GROUP) {
            chatMessage.setChatType(ChatMessage.GROUP_CHAT);
            mData.add(chatMessage);
            mAdapter.notifyDataSetChanged();
        if(null != mRecyclerView){
            mRecyclerView.scrollToPosition(mData.size()-1);
        }
        mIMeeting.sendMsg(sendMsg);//发送群聊消息
      /*  } else {
            chatMessage.setChatType(ChatMessage.SINGLE_CHAT);
            mAdapter.addData(chatMessage);
            refreshUIWithNewMessage();
            mIMeeting.sendSignalMsg(sendMsg, mToChatUserId);//发送单聊消息
        }*/
    }


    /**
     * 禁言通知处理中心
     */
    public void onEventMainThread(CoCoAction action) {
        switch (action.getActionType()) {
            case PullXmlUtils.CHAT_IS_CLOSE_BACK:
                if (action.getActionResult().equals("true")) {
                    canSay(false);
                } else {
                    canSay(true);
                }
                break;
        }
    }

    /**
     * 判断用户是否被禁言了
     *
     * @param b
     */
    private void canSay(boolean b) {
        if (b) {
            mMeetingBase.setBaseChat("1");
            mInputView.setVisibility(View.VISIBLE);
            UIUtils.toast(EApplication.instance(), "呵呵,您被允许发言了", Toast.LENGTH_SHORT);
        } else {
            mMeetingBase.setBaseChat("0");
            mInputView.setVisibility(View.GONE);
            UIUtils.toast(EApplication.instance(), "呜呜,您被禁言了", Toast.LENGTH_SHORT);
        }

    }


    /**
     * 消息处理中心
     *
     * @throws RemoteException
     */
    public void onEventMainThread(ChatMessage msg) throws RemoteException {
        if(null == mRecyclerView) return;
        String userId = null;
        if (msg.getChatType() == ChatMessage.GROUP_CHAT) {
            //群聊的话就取coco消息中的 to=475487329537895387 作为发送此消息者的ID
            userId = msg.getTo();
        } else {
            //单聊的话就取coco消息中的 from=475487329537895387 作为发送此消息者的ID
            userId = msg.getFrom();
        }
        //如果是当前会话的消息，刷新聊天页面
        if (userId.equals(mToUserId) && !msg.getFrom().equals(mUserInfo.getBaseUserId())) {
//            if (msg.getChatType() == Ty) {
            final ChatMessage cm = msg ;
            cm.setBaseViewHoldType(TYPE_CHAT_GROUP_RECEIVER);
            getParentActivity().getUsers(new OnlineMeetingActivity.ILoader() {
                @Override
                public void onLoadUserSuccess(List<OnlineUserInfo> users) {

                    if(users !=null && users.size()>0) {
                        for (int i = 0; i < users.size(); i++) {
                            if (cm.getFrom().equals(users.get(i).getId())) {
                                cm.setName(users.get(i).getName());
                                cm.setHeadUrl(users.get(i).getIcon());
                                break;
                            }
                        }

                        mData.add(cm);
                        mAdapter.notifyDataSetChanged();
                        if(null != mRecyclerView){
                            mRecyclerView.scrollToPosition(mData.size()-1);
                        }
                    }
                }
            },false,true);

        } /*else {
                mAdapter.addData(findSingleMessageFromWho(msg));
            }*/
            /*mNotifyUtils.onNewMsg(msg);//铃声通知消息到了
            refreshUIWithNewMessage();//刷新界面*/
    }

    public interface onSoftKeyListener{
        /**
         * 打开软件盘
         */
        void onKeyOpen();

        /**
         * 关闭软件盘
         */
        void onKeyClose();
    }
}
