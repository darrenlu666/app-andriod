package com.codyy.erpsportal.onlinemeetings.controllers.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.widgets.SwipeMenuListView.SwipeMenu;
import com.codyy.erpsportal.commons.widgets.SwipeMenuListView.SwipeMenuCreator;
import com.codyy.erpsportal.commons.widgets.SwipeMenuListView.SwipeMenuItem;
import com.codyy.erpsportal.commons.widgets.SwipeMenuListView.SwipeMenuListView;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.commons.controllers.activities.SingleChatActivity;
import com.codyy.erpsportal.commons.controllers.adapters.CantactsAdapter;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.commons.models.entities.LoginOut;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.commons.models.entities.UpdateCantacts;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.onlinemeetings.models.dao.ChatDataHelper;
import com.codyy.erpsportal.commons.utils.NotifyUtils;
import com.codyy.erpsportal.commons.utils.UIUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 视频会议-聊天区-一对一
 * Created by yangxinwu on 2015/7/31.
 */
public class ContactsFragment extends OnlineFragmentBase {
    private final static String TAG = "ContactsFragment";
    public final static String INTENT_ACTION_FINISH = "intent_action_finish";
    private String[] mTabName = {"群聊", "私聊", "系统消息"};
    @Bind(R.id.sml_cantacts) SwipeMenuListView mListView;
    private CantactsAdapter mAdapter;
    private LinkedList<ChatMessage> mMessages = new LinkedList<>();
    private ViewPager mViewPager;
    private OnLineChatMessageFragment.GetViewPager mGetViewPager;
    private NotifyUtils mNotifyUtils;
    private ChatDataHelper mChatDataHelper;
    private QueryAllOnLineUser mQueryAllOnLineUser;
    private String mChatId = "";//当前用户和哪个人聊天
    private int index = -1;
    private ArrayList<OnlineUserInfo> mUsers = new ArrayList<>();
    private MeetingBase mMeetingBase = null;

    /**
     * 消息处理中心
     *
     * @throws RemoteException
     */
    public void onEventMainThread(ChatMessage msg) throws RemoteException {
        Cog.d(TAG, "收到服务器返回的ChatMessage");
        if (msg.getChatType() == ChatMessage.SINGLE_CHAT) {
            updateMsg(msg);
            int index = mViewPager.getCurrentItem();
            if (!mTabName[index].equals("私聊")) {
                mNotifyUtils.viberateAndPlayTone(msg);
                Intent intent = new Intent(OnLineChatFragment.INTENT_ACTION_NOTICE);
                intent.putExtra(OnLineChatMessageFragment.CHAT_TYPE, ChatMessage.SINGLE_CHAT);
                getActivity().sendBroadcast(intent);
            }
            Intent intent2 = new Intent();
            intent2.setAction(OnlineMeetingActivity.INTENT_ACTION_NOTICE2);
            getActivity().sendBroadcast(intent2);
        }
    }

    public void onEventMainThread(String[] onLineUserId) {
        Cog.d(TAG, "收到服务器返回的在线用户" + onLineUserId.toString());
       // this.onLineUserId = onLineUserId;
        if (onLineUserId.length > 0) {
            loadOnLineUser(onLineUserId);
        }
    }


    public void onEventMainThread(LoginOut loginOut) throws RemoteException {
        Cog.d(TAG, "收到服务器返回的loginOut" + loginOut.getFrom());
        for (int i = 0; i < mMessages.size(); i++) {
            if (loginOut.getFrom().equals(mMessages.get(i).getFrom())) {
                mMessages.remove(i);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onEventMainThread(UpdateCantacts updateCantacts) throws RemoteException {
        Cog.i(TAG,"updateContacts content ! ");
        if(index < 0) return;
        mMessages.get(index).setMsg(updateCantacts.getContent());
        mMessages.get(index).setTime(updateCantacts.getTime());
        mAdapter.notifyDataSetChanged();
        index = -1;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mChatId = "";
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mQueryAllOnLineUser = (QueryAllOnLineUser) getActivity();
        mGetViewPager = (OnLineChatMessageFragment.GetViewPager) getParentFragment();
        mViewPager = mGetViewPager.getViewPager();
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_cantacts;
    }

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        IntentFilter filter = new IntentFilter(INTENT_ACTION_FINISH);
        getActivity().registerReceiver(mReceiver, filter);
        if (getActivity()!=null) {
            mMeetingBase = ((OnlineMeetingActivity) getActivity()).getMeetingBase();
        }
        mChatDataHelper = new ChatDataHelper(getActivity());
        mNotifyUtils = new NotifyUtils();
        mNotifyUtils = mNotifyUtils.init(getActivity());
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "Delete" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));//Color.rgb(0xC9, 0xC9,0xCE)));
                // set item width
                openItem.setWidth(UIUtils.dip2px(EApplication.instance(),90));
                // set item title
                openItem.setTitle("Delete");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        mListView.setMenuCreator(creator);
        mAdapter = new CantactsAdapter(getActivity(), mMessages);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMeetingBase != null&&(mMeetingBase.getBaseRole()==MeetingBase.BASE_MEET_ROLE_3)){
                    UIUtils.toast(getActivity(), "抱歉,您只可观摩无法私聊", Toast.LENGTH_SHORT);
                    return;
                }
                if (mMessages.get(position).isHasUnReadMsg()) {
                    mMessages.get(position).setHasUnReadMsg(false);
                    mAdapter.notifyDataSetChanged();
                }
                mChatId = mMessages.get(position).getFrom();
                index = position;
                SingleChatActivity.start(getActivity(),
                        ((OnlineMeetingActivity) getActivity()).getMeetingID(),
                        mMessages.get(position),
                        mMeetingBase.getBaseSay(),
                        mMeetingBase.getBaseChat());
            }
        });

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                mChatDataHelper.open();
                mChatDataHelper.removeData(mMessages.get(position),getChatKey(mMessages.get(position).getFrom()));
                mChatDataHelper.close();
                mMessages.remove(position);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

        if (mQueryAllOnLineUser != null) {
            Cog.d(TAG, "查询所有在线用户");
            //查询所有在线用户
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mQueryAllOnLineUser.queryAllOnLineUser();
                }
            }, 2000);
        }
    }

    /**
     * 加载在线用户
     *
     * @param data
     */
    private void loadOnLineUser(final String[] data) {
        if (mUsers.size() > 0) {
            new GetOnLineUserTask(data, mUsers).execute();
        } else {
            if ( getActivity()==null){
                return;
            }
            Log.i(TAG,"getUsers...");
            ((OnlineMeetingActivity) getActivity()).getUsers(new OnlineMeetingActivity.ILoader() {
                @Override
                public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                    mUsers = (ArrayList<OnlineUserInfo>) users;
                    new GetOnLineUserTask(data, mUsers).execute();
                }
            }, false,true);
        }
    }

    /**
     * 刷新在线用户列表
     *
     * @param msg
     */
    private void updateMsg(final ChatMessage msg) {
        boolean isExist = false ;
        for (int i = 0; i < mMessages.size(); i++) {
            String id = msg.getFrom();
            if(msg.getFrom().equals(mUserInfo.getBaseUserId())){
                id = msg.getTo();
            }
            if (id.equals(mMessages.get(i).getFrom())) {
//                mMessages.get(i).setFrom(msg.getFrom());
                if(TextUtils.isEmpty(msg.getHeadUrl())){
                    msg.setHeadUrl(mMessages.get(i).getHeadUrl());
                }
                mMessages.get(i).setMsg(msg.getMsg());
                if (msg.getFrom().equals(mChatId)) {
                    mMessages.get(i).setHasUnReadMsg(false);
                } else {
                    mMessages.get(i).setHasUnReadMsg(true);
                    mChatDataHelper.open();
                    mChatDataHelper.addChat(msg, getChatKey(id));
                    mChatDataHelper.close();
                }
                mMessages.get(i).setTime(msg.getTime());
                mMessages.get(i).setChatType(msg.getChatType());
                isExist = true;
                break;
            }
        }

        if(!isExist){//新来的信息
           getParentActivity().getUsers(new OnlineMeetingActivity.ILoader() {
               @Override
               public void onLoadUserSuccess(List<OnlineUserInfo> users) {

                   String id = msg.getFrom();
                   if(msg.getFrom().equals(mUserInfo.getBaseUserId())){
                       id = msg.getTo();
                   }

                   for(int i = 0 ; i< users.size() ; i++){

                       if(users.get(i).getId().equals(id)){
                           msg.setName(users.get(i).getName());
                           msg.setHeadUrl(users.get(i).getIcon());
                       }
                   }

                   mChatDataHelper.open();
                   mChatDataHelper.addChat(msg,getChatKey(id));
                   mChatDataHelper.close();
                   mMessages.add(msg);
               }
           },false,true);

        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mReceiver);
    }


    /**
     * 加载在线用户任务
     */
    public class GetOnLineUserTask extends AsyncTask {
        String[] data;
        List<OnlineUserInfo> users;

        public GetOnLineUserTask(String[] data, List<OnlineUserInfo> users) {
            this.data = data;
            this.users = users;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Object[] params) {
            mChatDataHelper.open();
            mMessages.clear();
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < users.size(); j++) {
                    if (null != mUserInfo && !data[i].equals(mUserInfo.getBaseUserId()) && data[i].equals(users.get(j).getId())) {
                        ChatMessage chatMessage = new ChatMessage();
                        ChatMessage chatMessage1 = mChatDataHelper.queryChatById(getChatKey(data[i]));
                        if (chatMessage1 != null) {
                            chatMessage.setMsg(chatMessage1.getMsg());
                            chatMessage.setTime(chatMessage1.getTime());
                        } else {
                            chatMessage.setMsg("");
                            chatMessage.setTime(System.currentTimeMillis() + "");
                        }
                        chatMessage.setName(users.get(j).getName());
                        chatMessage.setHeadUrl(users.get(j).getIcon());
                        chatMessage.setChatType(SingleChatActivity.CHAT_TYPE_SINGLE);
                        chatMessage.setFrom(data[i]);
                        chatMessage.setHasUnReadMsg(false);
                        mMessages.add(chatMessage);
                        break;
                    }
                }
            }
            mChatDataHelper.close();
            return null;
        }
    }

    /**
     * 获取聊天记录的key
     *
     * @param id
     * @return
     */
    private String getChatKey(String id) {
        StringBuilder mDbKey = new StringBuilder();//聊天记录的key
        mDbKey.append(mMeetID).append(id);
        return mDbKey.toString();
    }

    public interface QueryAllOnLineUser {
        /**
         * 查询所有在线用户
         */
        void queryAllOnLineUser();
    }

}
