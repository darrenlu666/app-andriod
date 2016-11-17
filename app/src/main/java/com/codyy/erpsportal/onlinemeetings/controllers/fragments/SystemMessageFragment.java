package com.codyy.erpsportal.onlinemeetings.controllers.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.ListView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.controllers.adapters.SystemMessgaeAdapter;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.commons.models.entities.SystemMessage;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnLineChatFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnLineChatMessageFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnlineFragmentBase;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 视频会议-聊天-区系统消息fragment
 * Created by yangxinwu on 2015/8/28.
 */
public class SystemMessageFragment extends OnlineFragmentBase{
    private static int SYSTEM_MESSAGE = 0x04;
    @Bind(R.id.lv_message) ListView mListView;
    private SystemMessgaeAdapter mAdapter;
    private List<SystemMessage> mMessages = new ArrayList<SystemMessage>();
    private ViewPager mViewPager;
    private OnLineChatMessageFragment.GetViewPager mGetViewPager;
    private String[] mTabName = {"群聊", "私聊", "系统消息"};
    private List<OnlineUserInfo> mUsers = new ArrayList<>();

    /**
     * 消息处理中心
     *
     * @throws RemoteException
     */
    public void onEventMainThread(final SystemMessage msg) throws RemoteException {
        if(null == msg) return;
        Cog.d("xx-----------xx", "收到SystemMessage" + msg.getMsg());
        if(findMessageFromWho(msg , mUsers)){
            addMessage(msg);
        }else{
            ((OnlineMeetingActivity) getActivity()).getUsers(new OnlineMeetingActivity.ILoader() {
                @Override
                public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                    if(users!= null && mUsers.size() <users.size()){
                        mUsers = users;
                    }
                    boolean hasUser = findMessageFromWho(msg , users);
                    if(hasUser){
                        addMessage(msg);
                    }
                }
            }, true , false);
        }
    }

    private void addMessage(SystemMessage msg) {
        if(null == msg) return;
        mMessages.add(msg);
        mAdapter.notifyDataSetChanged();
        int index = mViewPager.getCurrentItem();
        sendNewNotice(mTabName[index]);
    }

    /**
     * 发送新消息通知
     * @param s
     */
    private void sendNewNotice(String s) {
        //系统消息tab上的数字提示信息
        if (!s.equals("系统消息")) {
            Intent intent = new Intent(OnLineChatFragment.INTENT_ACTION_NOTICE);
            intent.putExtra(OnLineChatMessageFragment.CHAT_TYPE, SYSTEM_MESSAGE);
            getActivity().sendBroadcast(intent);
        }
        //聊天tab上的数字提示信息
        Intent intent2 = new Intent();
        intent2.setAction(OnlineMeetingActivity.INTENT_ACTION_NOTICE2);
        getActivity().sendBroadcast(intent2);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGetViewPager = (OnLineChatMessageFragment.GetViewPager) getParentFragment();
        mViewPager = mGetViewPager.getViewPager();
    }


    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_system_message;
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_system_message, container, false);
            initViews(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }*/

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        mAdapter = new SystemMessgaeAdapter(getActivity(), mMessages);
        mListView.setAdapter(mAdapter);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                getAllOnLineUser();
            }
        }, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取所有用户列表
     */
    private void getAllOnLineUser() {
        if (getActivity() == null) {
            return;
        }

        ((OnlineMeetingActivity) getActivity()).getUsers(new OnlineMeetingActivity.ILoader() {
            @Override
            public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                mUsers.clear();
                mUsers.addAll(users);
            }
        }, false,false);
    }

    /**
     * 判断 群聊信息是哪个用户发的消息
     *
     * @param msg
     */
    private boolean findMessageFromWho(SystemMessage msg , List<OnlineUserInfo> userList) {
        boolean hasPartner = false;
        for (int i = 0; i < userList.size(); i++) {
            if (msg.getId().equals(userList.get(i).getId())) {
                hasPartner  =   true;
                msg.setId(userList.get(i).getName());
                msg.setNick(userList.get(i).getName());
                msg.setHeadUrl(userList.get(i).getIcon());
                break;
            }
        }

        return hasPartner;
    }
}

