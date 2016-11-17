/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codyy.erpsportal.onlinemeetings.controllers.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.WeakHandler;
import com.codyy.erpsportal.commons.widgets.AsyncTextView;
import com.codyy.erpsportal.commons.widgets.AvatarView;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private String userId;
    private LayoutInflater inflater;
    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;
    private ListView mLvMessage;
    private List<ChatMessage> messages;
    private MessageHandler handler = new MessageHandler(this);

    public MessageAdapter(Context context, ListView lv, String userId, List<ChatMessage> messages) {
        this.userId = userId;
        inflater = LayoutInflater.from(context);
        mLvMessage = lv;
        this.messages = messages;
    }

    private  void selectLast() {
        if (messages.size() > 0) {
            mLvMessage.setSelection(messages.size() - 1);
        }
    }

    private  void selectPos(Message message) {
        int position = message.arg1;
        mLvMessage.setSelection(position);
    }

    /**
     * 获取item数
     */
    public int getCount() {
        return messages.size();
    }


    public List<ChatMessage> getData(){
        return this.messages;
    }


    /**
     * 刷新页面
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    /**
     * 获取item类型数
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * 获取item类型
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message == null) {
            return -1;
        }
        //Cog.d("惺惺惜惺惺", "chatMessage.getId()" + message.getFrom());
        //Cog.d("惺惺惜惺惺", "userId" + userId);
        return message.getFrom().equals(userId) ? MESSAGE_TYPE_SENT_TXT : MESSAGE_TYPE_RECV_TXT;
    }

    private View createViewByMessage(ChatMessage chatMessage) {
        //Cog.d("MeetingService", "chatMessage.getId()" + chatMessage.getFrom());
       // Cog.d("MeetingService", "userId" + userId);
        return chatMessage.getFrom().equals(userId) ? inflater.inflate(
                R.layout.row_sent_message, null) : inflater.inflate(R.layout.row_received_message, null);
    }

    public ChatMessage getItem(int position) {
        if (messages != null && position < messages.size()) {
            return messages.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public void addData(ChatMessage msg) {
        messages.add(msg);
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChatMessage message = getItem(position);
        final ViewHolder holder;
        ChatMessage chatMessage = messages.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(chatMessage);
            holder.ivAvatar = (AvatarView) convertView.findViewById(R.id.iv_userhead);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            // 这里是文字内容
            holder.tvChatMsg = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            holder.tvUserName = (AsyncTextView) convertView.findViewById(R.id.tv_userid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvChatMsg.setText(chatMessage.getMsg());
     /*   if (!chatMessage.getFrom().equals(userId)) {
            holder.tvUserName.setText(chatMessage.getFrom());
        } else {
            holder.tvUserName.setText(chatMessage.getName());
        }*/

        if (chatMessage.getChatType() == ChatMessage.SINGLE_CHAT) {
            holder.tvUserName.setVisibility(View.GONE);
        }else {
            holder.tvUserName.setVisibility(View.VISIBLE);
        }

        //设置用户头像
        setUserAvatar(message, holder.tvUserName, holder.ivAvatar);

        if (position == 0) {
            holder.timestamp.setText(DateUtil.stringToDate(chatMessage.getTime()));
            holder.timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            ChatMessage prvChatMessage = messages.get(position - 1);
            if (prvChatMessage != null && DateUtil.isCloseEnough(Long.parseLong(message.getTime()), Long.parseLong(prvChatMessage.getTime()))) {
                holder.timestamp.setVisibility(View.GONE);
            } else {
                holder.timestamp.setText(DateUtil.stringToDate(chatMessage.getTime()));
                holder.timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }


    /**
     * 显示用户头像
     *
     * @param message
     * @param imageView
     */
    private void setUserAvatar(ChatMessage message, final AsyncTextView displayName,
                               final AvatarView imageView) {
            imageView.setAvatarUrl(message.getHeadUrl());
            if (displayName != null&&!message.getFrom().equals(userId)) {
                displayName.setText(message.getName());
            }else {
                displayName.setVisibility(View.GONE);
            }
    }

    public static class ViewHolder {
        TextView tvChatMsg;
        AvatarView ivAvatar;
        AsyncTextView tvUserName;
        TextView timestamp;
    }


    private static  class MessageHandler extends WeakHandler<MessageAdapter>{

        public MessageHandler(MessageAdapter owner) {
            super(owner);
        }

        @Override
        public void handleMessage(android.os.Message message) {
            if(null == getOwner()) return;
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    getOwner().notifyDataSetChanged();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    getOwner().selectLast();
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    getOwner().selectPos(message);
                    break;
                default:
                    break;
            }
        }
    }
}