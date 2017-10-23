package com.codyy.erpsportal.commons.controllers.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.IMeetingService;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.ContactsFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.fragments.OnLineChatMessageFragment;
import com.codyy.erpsportal.commons.services.ChatService;
import com.codyy.erpsportal.commons.services.IMeeting;
import com.codyy.erpsportal.commons.utils.UIUtils;

/**
 * 单聊页面
 * Created by yangxinwu on 2015/8/20.
 */
public class SingleChatActivity extends AppCompatActivity implements IMeeting {
    private final static String TAG = SingleChatActivity.class.getSimpleName();
    public static String CHAT_TYPE = "chat_type";
    public static String HAS_UNREAD = "has_unread";
    public static String CHAT_MESSAGE = "chat_message";

    public static String EXTRA_FLAG_ALL_SAY = "chat_message";//全局禁止聊天 0:正常 1:禁止.
    public static String EXTRA_FLAG_BASE_CHAT = "base_chat";//全局禁止聊天 0:禁言 1:允许发言.


    public static int CHAT_TYPE_SINGLE = 0x01;
    public static final String KEY_TO_CHAT_ID = "key_to_chat_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_HEAD_URL = "user_head_url";
    public static final String MEETING_ID = "meeting_id";
    private IMeetingService mIMeetingService;
    private Intent mIntent;
    private TextView mTitle;
    private Button mBtnBack;
    private int mCanSay = 0;
   //private ChatMessage mChatMessage;

    private ServiceConnection mMeetingServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMeetingService = IMeetingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_chat_activity);
        initView();
    }


    private void initView() {
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setText(getIntent().getStringExtra(USER_NAME));

        mIntent = new Intent(this, ChatService.class);
        bindService(mIntent, mMeetingServiceConn, BIND_AUTO_CREATE);
        OnLineChatMessageFragment onLineChatMessageFragment = new OnLineChatMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CHAT_TYPE, getIntent().getIntExtra(CHAT_TYPE, 0));
        bundle.putBoolean(HAS_UNREAD, getIntent().getBooleanExtra(HAS_UNREAD, false));
        bundle.putString(KEY_TO_CHAT_ID, getIntent().getStringExtra(KEY_TO_CHAT_ID));
        bundle.putString(USER_NAME, getIntent().getStringExtra(USER_NAME));
        bundle.putString(USER_HEAD_URL, getIntent().getStringExtra(USER_HEAD_URL));
        bundle.putString(MEETING_ID, getIntent().getStringExtra(MEETING_ID));
        bundle.putInt(EXTRA_FLAG_ALL_SAY,getIntent().getIntExtra(EXTRA_FLAG_ALL_SAY,0));
        bundle.putInt(EXTRA_FLAG_BASE_CHAT,getIntent().getIntExtra(EXTRA_FLAG_BASE_CHAT,0));
        onLineChatMessageFragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.container, onLineChatMessageFragment);
        trans.commit();
    }

    private void finishActivity() {
        Intent intent = new Intent(ContactsFragment.INTENT_ACTION_FINISH);
        sendBroadcast(intent);
        finish();
    }


    @Override
    public void sendMsg(String msg) {
        try {
            mIMeetingService.sendMsg(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMeetingServiceConn);
    }

    @Override
    public void sendSignalMsg(String msg, String id) {
        try {
            mIMeetingService.sendSignalMsg(msg, id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void start(Activity context,String from , String name ,String headUrl , String meetId ,boolean hasUnread){
        Intent intent = new Intent(context, SingleChatActivity.class);
        intent.putExtra(SingleChatActivity.CHAT_TYPE, SingleChatActivity.CHAT_TYPE_SINGLE);
        intent.putExtra(SingleChatActivity.KEY_TO_CHAT_ID, from);
        intent.putExtra(SingleChatActivity.USER_NAME, name);
        intent.putExtra(SingleChatActivity.USER_HEAD_URL, headUrl);
        intent.putExtra(SingleChatActivity.MEETING_ID, meetId);
        intent.putExtra(SingleChatActivity.HAS_UNREAD, hasUnread);
        context.startActivity(intent);
    }

    public static void start(Activity context,String meetId , ChatMessage message,int canAllSay,int baseChat){
        Intent intent = new Intent(context, SingleChatActivity.class);
        intent.putExtra(SingleChatActivity.CHAT_TYPE, SingleChatActivity.CHAT_TYPE_SINGLE);
        intent.putExtra(SingleChatActivity.KEY_TO_CHAT_ID, message.getFrom());
        intent.putExtra(SingleChatActivity.USER_NAME, message.getName());
        intent.putExtra(SingleChatActivity.USER_HEAD_URL, message.getHeadUrl());
        intent.putExtra(SingleChatActivity.MEETING_ID, meetId);
        intent.putExtra(SingleChatActivity.HAS_UNREAD, message.isHasUnReadMsg());
        intent.putExtra(SingleChatActivity.EXTRA_FLAG_ALL_SAY,canAllSay);
        intent.putExtra(SingleChatActivity.EXTRA_FLAG_BASE_CHAT,baseChat);
        context.startActivity(intent);
        UIUtils.addEnterAnim(context);
    }
}
